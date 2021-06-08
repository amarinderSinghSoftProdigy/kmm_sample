package com.zealsoftsol.medico.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.extra.AadhaarDataComponent
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space

@Composable
fun WelcomeScreen(
    fullName: String,
    option: WelcomeOption,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "${stringResource(R.string.welcome)}, $fullName!",
            fontSize = 20.sp,
            fontWeight = FontWeight.W500,
        )
        Space(50.dp)
        val resource = when (option) {
            is WelcomeOption.Thanks -> R.drawable.ic_welcome
            is WelcomeOption.Upload -> R.drawable.ic_upload
        }
        Icon(
            painter = painterResource(id = resource),
            contentDescription = null,
            tint = Color.Unspecified,
        )
        Space(30.dp)
        when (option) {
            is WelcomeOption.Thanks -> ThanksForRegistration(option)
            is WelcomeOption.Upload -> UploadDocuments(option)
        }
    }
}

@Composable
private fun ThanksForRegistration(option: WelcomeOption.Thanks) {
    Text(
        text = stringResource(id = R.string.thanks_for_registration),
        textAlign = TextAlign.Center,
    )
    Space(18.dp)
    Text(
        text = stringResource(id = R.string.documents_under_review),
        textAlign = TextAlign.Center,
    )
    option.onAccept?.let {
        Space(50.dp)
        MedicoButton(
            text = stringResource(id = R.string.okay),
            isEnabled = true,
            onClick = it
        )
    }
}

@Composable
private fun UploadDocuments(option: WelcomeOption.Upload) {
    Text(
        text = stringResource(id = option.hintStringResource),
        textAlign = TextAlign.Center,
        color = ConstColors.gray,
    )
    val isEnabled = if (option is WelcomeOption.Upload.Aadhaar) {
        Space(50.dp)
        AadhaarInputFields(
            aadhaarData = option.dataComponent.aadhaarData,
            onCardChange = { option.dataComponent.changeCard(it) },
            onCodeChange = { option.dataComponent.changeShareCode(it) },
        )
        option.dataComponent.isVerified.flow.collectAsState()
    } else {
        remember { mutableStateOf(true) }
    }
    Space(50.dp)
    MedicoButton(
        text = stringResource(id = option.buttonStringResource),
        isEnabled = isEnabled.value,
        onClick = option.onUpload
    )
}

sealed class WelcomeOption {
    sealed class Upload : WelcomeOption() {
        abstract val onUpload: () -> Unit
        abstract val buttonStringResource: Int
        abstract val hintStringResource: Int

        data class DrugLicense(
            override val buttonStringResource: Int = R.string.upload_new_document,
            override val hintStringResource: Int = R.string.provide_drug_license_hint,
            override val onUpload: () -> Unit,
        ) : Upload()

        data class Aadhaar(
            val dataComponent: AadhaarDataComponent,
            override val buttonStringResource: Int = R.string.upload_aadhaar,
            override val hintStringResource: Int = R.string.provide_aadhaar_hint,
            override val onUpload: () -> Unit
        ) : Upload()
    }

    data class Thanks(val onAccept: (() -> Unit)?) : WelcomeOption()
}