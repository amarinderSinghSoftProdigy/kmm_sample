package com.zealsoftsol.medico

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.zealsoftsol.medico.core.UiLink
import com.zealsoftsol.medico.core.data.FileType
import com.zealsoftsol.medico.core.mvi.UiNavigator
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.core.mvi.scope.regular.WelcomeScope
import com.zealsoftsol.medico.screens.TabBarScreen
import com.zealsoftsol.medico.screens.auth.AuthScreen
import com.zealsoftsol.medico.screens.auth.WelcomeOption
import com.zealsoftsol.medico.screens.auth.WelcomeScreen
import com.zealsoftsol.medico.screens.common.IndefiniteProgressBar
import com.zealsoftsol.medico.screens.common.showErrorAlert
import com.zealsoftsol.medico.screens.showBottomSheet
import com.zealsoftsol.medico.utils.FileUtil
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class MainActivity : ComponentActivity(), DIAware {

    override val di: DI by closestDI()
    private val navigator by instance<UiNavigator>()
    private val UPDATE_REQUEST_CODE = 1000

    private var cameraCompletion: CompletableDeferred<Boolean> = CompletableDeferred()
    private val camera by lazy {
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            cameraCompletion.complete(isSuccess)
            cameraCompletion = CompletableDeferred()
        }
    }
    private var pickerCompletion: CompletableDeferred<Uri?> = CompletableDeferred()
    private val picker by lazy {
        registerForActivityResult(GetSpecificContent) { uri: Uri? ->
            pickerCompletion.complete(uri)
            pickerCompletion = CompletableDeferred()
        }
    }
    private val messaging by lazy { (application as MedicoApp).messaging }
    private var permissionCompletion: CompletableDeferred<Boolean> = CompletableDeferred()
    private val requestPermission by lazy {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isSuccess ->
            permissionCompletion.complete(isSuccess)
            permissionCompletion = CompletableDeferred()
        }
    }

    @ExperimentalComposeUiApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.FLAVOR == "dev" && !BuildConfig.DEBUG) { // devRelease
            handleCrashes()
        }
        UiLink.setStartingScope()
        setContent {
            val coroutineScope = rememberCoroutineScope()
            AppTheme {
                val hostScope = navigator.scope.flow.collectAsState()
                Crossfade(hostScope.value, animationSpec = tween(durationMillis = 200)) {
                    when (it) {
                        is LogInScope -> AuthScreen(it)
                        is WelcomeScope -> Surface {
                            WelcomeScreen(
                                fullName = it.fullName,
                                option = WelcomeOption.Thanks { it.accept() }
                            )
                        }
                        is TabBarScope -> TabBarScreen(it, coroutineScope, this)
                    }
                }
                val isInProgress = hostScope.value.isInProgress.flow.collectAsState()
                if (isInProgress.value) IndefiniteProgressBar()

                hostScope.value.showErrorAlert()
                hostScope.value.showBottomSheet(this, coroutineScope)
            }
        }
        camera
        picker

        intent?.let(::handleIntent)

        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) task.result?.let {
                messaging.handleNewToken(it)
            }
        }

    }


    /**
     * check for app update
     */
    override fun onResume() {
        super.onResume()
        val appUpdateManager = AppUpdateManagerFactory.create(this)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    UPDATE_REQUEST_CODE
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let(::handleIntent)
    }

    override fun onBackPressed() {
        if (!navigator.handleBack())
            super.onBackPressed()
    }

    suspend fun openFilePicker(fileTypes: Array<FileType>): File? {
        GetSpecificContent.supportedTypes = fileTypes.map { it.mimeType }.toTypedArray()
        picker.launch("*/*")
        val uri = pickerCompletion.await()
        return if (uri != null) FileUtil.getTempFile(this, uri) else null
    }

    suspend fun takePicture(name: String?): File? = withContext(Dispatchers.IO) {
        val photos = File(filesDir, "photos")
        if (!photos.exists()) photos.mkdirs()
        val image = File(photos, "image_$name.jpg")
        if (image.exists()) image.delete()
        image.createNewFile()
        camera.launch(
            FileProvider.getUriForFile(
                this@MainActivity, "$packageName.provider", image
            )
        )
        cameraCompletion.await()
        image.takeIf { image.length() > 0 }
    }

    fun openMaps(latitude: Double, longitude: Double) {
        val uri = String.format("geo:%f,%f", latitude, longitude).toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    fun openDialer(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun onClickWhatsApp(phone: String) {
        try {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.action = Intent.ACTION_VIEW
            sendIntent.setPackage("com.whatsapp")
            val url =
                "https://api.whatsapp.com/send?phone=$phone&text="
            sendIntent.data = Uri.parse(url)
            startActivity(sendIntent)
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun playVideo(url: String) {
        val file = File(url)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), "video/*")
        startActivity(intent)
    }

    fun sendMail(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$email")
        startActivity(intent)
    }

    fun openUrl(url: String) {
        CustomTabsIntent.Builder()
            .setToolbarColor("#0084D4".toColorInt())
            .build()
            .launchUrl(this, url.toUri())
    }

    suspend fun saveInvoice(url: String, invoiceName: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveFileUsingMediaStore(this@MainActivity, url, "invoice_$invoiceName")
                } else {
                    requestPermission.launch(WRITE_EXTERNAL_STORAGE)
                    if (permissionCompletion.await()) {
                        val target = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            invoiceName
                        )
                        URL(url).openStream().use { input ->
                            FileOutputStream(target).use { output ->
                                input.copyTo(output)
                            }
                        }
                        true
                    } else {
                        false
                    }
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileUsingMediaStore(context: Context, url: String, fileName: String): Boolean {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        return if (uri != null) {
            URL(url).openStream().use { input ->
                resolver.openOutputStream(uri).use { output ->
                    input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                }
            }
            true
        } else {
            false
        }
    }

    private fun handleIntent(intent: Intent) {
        intent.extras?.getString(NotificationCenter.DISMISS_NOTIFICATION_ID)?.let {
            messaging.messageClicked(it)
        }
    }

    private fun handleCrashes() {
        val default = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val intent = Intent(this, CrashActivity::class.java)
            intent.putExtra(CrashActivity.TRACE, throwable.stackTraceToString())
            startActivity(intent)
            default?.uncaughtException(thread, throwable)
        }
    }

    private object GetSpecificContent : ActivityResultContracts.GetContent() {
        var supportedTypes: Array<String> = emptyArray()

        override fun createIntent(context: Context, input: String): Intent {
            return super.createIntent(context, input)
                .putExtra(Intent.EXTRA_MIME_TYPES, supportedTypes)
        }
    }

    fun shareTextContent(content: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    /**
     * start process of recognising image from text
     */
    fun startOcr(value: String) {
        val image = InputImage.fromFilePath(this, Uri.fromFile(File(value)))
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { texts ->
                //scope.updateRecognisedList(processText(texts))
            }
            .addOnFailureListener { e -> // Task failed with an exception
                e.printStackTrace()
            }
    }


    /**
     * create a list of recognised text
     */
    private fun processText(result: Text): List<String> {
        val listOfText = mutableListOf<String>()
        for (block in result.textBlocks) {
            for (line in block.lines) {
                listOfText.add(line.text)
            }
        }
        return listOfText
    }
}