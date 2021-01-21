package com.zealsoftsol.medico.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.setContent
import androidx.core.content.FileProvider
import com.zealsoftsol.medico.AppTheme
import com.zealsoftsol.medico.core.UiLink
import com.zealsoftsol.medico.core.mvi.UiNavigator
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.regular.SearchScope
import com.zealsoftsol.medico.core.mvi.scope.regular.WelcomeScope
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.screens.auth.AuthScreen
import com.zealsoftsol.medico.screens.auth.WelcomeOption
import com.zealsoftsol.medico.screens.auth.WelcomeScreen
import com.zealsoftsol.medico.screens.search.SearchQueryScreen
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UiLink.setStartingScope()
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val searchList = rememberLazyListState()
            AppTheme {
                val hostScope = navigator.scope.flow.collectAsState()
                Crossfade(hostScope.value, animation = tween(durationMillis = 200)) {
                    when (it) {
                        is LogInScope -> AuthScreen(it)
                        is WelcomeScope -> Surface {
                            WelcomeScreen(
                                fullName = it.fullName,
                                option = WelcomeOption.Thanks { it.accept() }
                            )
                        }
                        is SearchScope -> Surface { SearchQueryScreen(it, searchList) }
                        is Scope.Host.TabBar -> TabBarScreen(it)
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

    private object GetSpecificContent : ActivityResultContracts.GetContent() {
        var supportedTypes: Array<String> = emptyArray()

        override fun createIntent(context: Context, input: String): Intent {
            return super.createIntent(context, input)
                .putExtra(Intent.EXTRA_MIME_TYPES, supportedTypes)
        }
    }
}