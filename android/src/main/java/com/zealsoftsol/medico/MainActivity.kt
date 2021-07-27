package com.zealsoftsol.medico

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
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

class MainActivity : ComponentActivity(), DIAware {

    override val di: DI by closestDI()
    private val navigator by instance<UiNavigator>()

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
                        is TabBarScope -> TabBarScreen(it, coroutineScope)
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

    suspend fun takePicture(): File? = withContext(Dispatchers.IO) {
        val photos = File(filesDir, "photos")
        if (!photos.exists()) photos.mkdirs()
        val image = File(photos, "image.jpg")
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