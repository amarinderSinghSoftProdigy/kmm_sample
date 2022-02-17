package com.zealsoftsol.medico

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import com.github.tutorialsandroid.appxupdater.AppUpdaterUtils
import com.github.tutorialsandroid.appxupdater.enums.AppUpdaterError
import com.github.tutorialsandroid.appxupdater.objects.Update
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.zealsoftsol.medico.core.UiLink
import com.zealsoftsol.medico.core.mvi.UiNavigator
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.core.mvi.scope.regular.WelcomeScope
import com.zealsoftsol.medico.data.FileType
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
    private val isAppUpdateAvailable = mutableStateOf(false)

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
                        is TabBarScope -> TabBarScreen(it, coroutineScope,this)
                    }
                }
                val isInProgress = hostScope.value.isInProgress.flow.collectAsState()
                if (isInProgress.value) IndefiniteProgressBar()

                hostScope.value.showErrorAlert()
                hostScope.value.showBottomSheet(this, coroutineScope)
            }
            if (isAppUpdateAvailable.value) {
                ShowAppUpdate()
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
     * Show dialog to update app
     */
    @Composable
    fun ShowAppUpdate() {
        MaterialTheme {
            AlertDialog(
                title = {
                    Text(
                        stringResource(id = R.string.update_available),
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                ),
                text = {
                    Text(stringResource(id = R.string.update_app), color = Color.Black)
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = ConstColors.yellow),
                        onClick = {
                            try {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=$packageName")
                                    )
                                )
                            } catch (e: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                                    )
                                )
                            }
                        }
                    ) {
                        Text(stringResource(id = R.string.update), color = Color.Black)
                    }
                },
                onDismissRequest = {}
            )
        }
    }

    /**
     * check for app update
     */
    override fun onResume() {
        super.onResume()
        val appUpdaterUtils = AppUpdaterUtils(this)
            .withListener(object : AppUpdaterUtils.UpdateListener {
                override fun onSuccess(update: Update, isUpdateAvailable: Boolean) {
                    isAppUpdateAvailable.value = isUpdateAvailable
                }

                override fun onFailed(error: AppUpdaterError) {
                    Log.e("error", error.toString())
                }
            })
        appUpdaterUtils.start()
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
}