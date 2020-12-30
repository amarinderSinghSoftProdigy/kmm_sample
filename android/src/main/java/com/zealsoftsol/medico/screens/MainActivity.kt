package com.zealsoftsol.medico.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.setContent
import androidx.core.content.FileProvider
import com.zealsoftsol.medico.AppTheme
import com.zealsoftsol.medico.core.mvi.UiNavigator
import com.zealsoftsol.medico.core.mvi.scope.EnterNewPasswordScope
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.scope.OtpScope
import com.zealsoftsol.medico.core.mvi.scope.SearchScope
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.screens.auth.AuthAddressData
import com.zealsoftsol.medico.screens.auth.AuthAwaitVerificationScreen
import com.zealsoftsol.medico.screens.auth.AuthDetailsAadhaar
import com.zealsoftsol.medico.screens.auth.AuthDetailsTraderData
import com.zealsoftsol.medico.screens.auth.AuthEnterNewPasswordScreen
import com.zealsoftsol.medico.screens.auth.AuthLegalDocuments
import com.zealsoftsol.medico.screens.auth.AuthPersonalData
import com.zealsoftsol.medico.screens.auth.AuthPhoneNumberInputScreen
import com.zealsoftsol.medico.screens.auth.AuthScreen
import com.zealsoftsol.medico.screens.auth.AuthUserType
import com.zealsoftsol.medico.screens.auth.Welcome
import com.zealsoftsol.medico.screens.auth.WelcomeOption
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
import java.text.SimpleDateFormat

class MainActivity : ComponentActivity(), DIAware {

    override val di: DI by closestDI()
    private val navigator by instance<UiNavigator>()
    private val dateFormat by lazy { SimpleDateFormat("mm:ss") }

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
        setContent {
            AppTheme {
                val currentScope = navigator.scope.flow.collectAsState()
                Crossfade(currentScope.value, animation = tween(durationMillis = 200)) {
                    when (it) {
                        is LogInScope -> AuthScreen(it)
                        is OtpScope.PhoneNumberInput -> AuthPhoneNumberInputScreen(it)
                        is OtpScope.AwaitVerification -> AuthAwaitVerificationScreen(
                            scope = it,
                            dateFormat = dateFormat,
                        )
                        is EnterNewPasswordScope -> AuthEnterNewPasswordScreen(it)
                        is SignUpScope.SelectUserType -> AuthUserType(it)
                        is SignUpScope.PersonalData -> AuthPersonalData(it)
                        is SignUpScope.AddressData -> AuthAddressData(it)
                        is SignUpScope.Details.TraderData -> AuthDetailsTraderData(it)
                        is SignUpScope.Details.Aadhaar -> AuthDetailsAadhaar(it)
                        is SignUpScope.LegalDocuments -> AuthLegalDocuments(it)
                        is SignUpScope.Welcome -> Surface {
                            Welcome(
                                fullName = it.fullName,
                                option = WelcomeOption.Thanks { it.accept() }
                            )
                        }
                        is MainScope -> MainView(it)
                        is SearchScope -> Surface { SearchQueryScreen(it) }
                    }
                }
                val isInProgress = currentScope.value.isInProgress.flow.collectAsState()
                if (isInProgress.value) IndefiniteProgressBar()
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