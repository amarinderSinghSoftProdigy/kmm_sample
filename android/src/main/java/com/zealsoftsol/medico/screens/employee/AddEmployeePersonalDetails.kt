package com.zealsoftsol.medico.screens.employee

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.extensions.toast
import com.zealsoftsol.medico.core.mvi.scope.nested.EmployeeScope
import com.zealsoftsol.medico.screens.auth.ProgressItem
import com.zealsoftsol.medico.screens.common.InputField
import com.zealsoftsol.medico.screens.common.InputWithError
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.PasswordFormatInputField
import com.zealsoftsol.medico.screens.common.PhoneFormatInputFieldForRegister
import com.zealsoftsol.medico.screens.common.RectHolder
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.scrollOnFocus

@ExperimentalComposeUiApi
@Composable
fun AddEmployeeStepOneScreen(scope: EmployeeScope.PersonalData) {

    val registration = scope.registration.flow.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val isFirstNameError = registration.value.firstName.any { !it.isLetter() }
    val isLastNameError = registration.value.lastName.any { !it.isLetter() }
    val validEmail = scope.validEmail(registration.value.email)
    val validPhone = scope.validPhone(registration.value.phoneNumber)
    val password = scope.isValidPassword(registration.value.password)
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicAuthSignUpScreenWithButton(
        userType = registration.value.userType,
        progress = 1.0,//0.4,
        scrollState = scrollState,
        baseScope = scope,
        buttonText = stringResource(id = R.string.next),
        onButtonClick = { scope.validate(registration.value) },
        buttonExtraValid = !isFirstNameError && !isLastNameError,
        body = {
            InputWithError(
                errorText = if (isFirstNameError)
                    stringResource(R.string.letters_only)
                else
                    null
            ) {
                InputField(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.first_name),
                    text = registration.value.firstName,
                    isValid = !isFirstNameError,
                    onValueChange = { scope.changeFirstName(it) },
                    mandatory = true,
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_register),
                            contentDescription = null,
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }
            Space(dp = 12.dp)
            InputWithError(
                errorText = if (isLastNameError)
                    stringResource(R.string.letters_only)
                else
                    null
            ) {
                InputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.last_name),
                    text = registration.value.lastName,
                    isValid = !isLastNameError,
                    onValueChange = { scope.changeLastName(it) },
                    mandatory = true,
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_register),
                            contentDescription = null,
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = if (!validEmail) stringResource(id = R.string.email_validation) else null) {
                InputField(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.email),
                    text = registration.value.email,
                    onValueChange = { scope.changeEmail(it) },
                    mandatory = true,
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_email),
                            contentDescription = null,
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }
            Space(dp = 12.dp)
            InputWithError(errorText = if (!validPhone) stringResource(id = R.string.phone_validation) else null) {
                PhoneFormatInputFieldForRegister(
                    modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                    hint = stringResource(id = R.string.phone_number),
                    text = registration.value.phoneNumber,
                    onValueChange = { phoneNumber ->
                        scope.changePhoneNumber(phoneNumber.filter { it.isDigit() })
                    },
                    leadingIcon = {
                        Row(modifier = Modifier.padding(start = 16.dp, top = 12.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_phone_number),
                                contentDescription = null,
                            )
                            Space(dp = 4.dp)
                            Text(
                                text = "+91",
                                color = MaterialTheme.colors.background,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                            )
                        }
                    },
                    mandatory = true,
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
                //scope.setPhoneNumberValid(isValid)
            }
            Space(dp = 12.dp)
            InputWithError(errorText = if (!password) stringResource(id = R.string.password_requirement) else null) {
                val rectHolder = RectHolder()
                PasswordFormatInputField(
                    modifier = Modifier.scrollOnFocus(rectHolder, scrollState, coroutineScope),
                    hint = stringResource(id = R.string.password),
                    text = registration.value.password,
                    onValueChange = { scope.changePassword(it) },
                    onPositioned = { rectHolder.rect = it.boundsInParent() },
                    mandatory = true,
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_verify_password),
                            contentDescription = null,
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }
            Space(dp = 12.dp)
            val isValid =
                registration.value.verifyPassword.isEmpty() || registration.value.password == registration.value.verifyPassword
            InputWithError(
                errorText = if (!isValid) stringResource(id = R.string.password_doesnt_match) else null
            ) {
                val rectHolder = RectHolder()
                PasswordFormatInputField(
                    modifier = Modifier.scrollOnFocus(rectHolder, scrollState, coroutineScope),
                    hint = stringResource(id = R.string.repeat_password),
                    text = registration.value.verifyPassword,
                    isValid = isValid,
                    onValueChange = { scope.changeRepeatPassword(it) },
                    onPositioned = { rectHolder.rect = it.boundsInParent() },
                    mandatory = true,
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_verify_password),
                            contentDescription = null,
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }
            Space(dp = 12.dp)
            val context = LocalContext.current
            val isTermsAccepted = scope.isTermsAccepted.flow.collectAsState()

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = isTermsAccepted.value,
                    colors = CheckboxDefaults.colors(checkedColor = ConstColors.lightBlue),
                    onCheckedChange = { scope.changeTerms(it) },
                )
                Space(dp = 8.dp)
                Text(
                    text = stringResource(id = R.string.consent_terms),
                    color = ConstColors.lightBlue,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.W600,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.clickable(onClick = {
                        val tabs = CustomTabsIntent.Builder().build()
                        val uri = context.getString(R.string.tos_url).toUri()
                        runCatching {
                            tabs.launchUrl(context, uri)
                        }.getOrNull() ?: runCatching {
                            ContextCompat.startActivity(
                                context,
                                Intent(Intent.ACTION_VIEW).also {
                                    it.data = uri
                                },
                                null
                            )
                        }.getOrNull() ?: context.toast(R.string.something_went_wrong)
                    })
                )
            }
            Space(18.dp)
        },
    )
}


@Composable
fun BasicAuthSignUpScreenWithButton(
    userType: String,
    progress: Double,
    baseScope: EmployeeScope,
    scrollState: ScrollState = rememberScrollState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    body: @Composable ColumnScope.() -> Unit,
    buttonText: String,
    buttonExtraValid: Boolean? = null,
    onSkip: (() -> Unit)? = null,
    onButtonClick: () -> Unit,
    padding: Dp = 16.dp,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White)
    ) {
        val isEnabled = baseScope.canGoNext.flow.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier.padding(end = 16.dp, start = 16.dp, top = 16.dp)
            ) {
                LazyRow {
                    itemsIndexed(
                        items = baseScope.inputProgress,
                        itemContent = { _, item ->
                            ProgressItem(count = item, progress, limit = 4, width = 0.27)
                        },
                    )
                }
            }
            if (userType.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.user_type),
                        fontSize = 14.sp,
                        color = ConstColors.gray.copy(alpha = 0.5f),
                    )
                    Space(dp = 4.dp)
                    Text(
                        text = userType,
                        fontSize = 16.sp,
                        color = ConstColors.darkBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(
                            PaddingValues(
                                top = padding,
                                start = padding,
                                end = padding,
                                bottom = padding + 60.dp
                            )
                        ),
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment,
                ) {
                    body()
                }

            }

        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom
        ) {
            MedicoButton(
                modifier = Modifier.padding(start = padding, end = padding, bottom = padding),
                text = buttonText,
                isEnabled = (buttonExtraValid ?: true) && isEnabled.value,
                onClick = onButtonClick,
            )
            onSkip?.let {
                Text(
                    text = stringResource(id = R.string.skip_for_now),
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    color = ConstColors.lightBlue,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = padding)
                        .clickable(onClick = it)
                )
            }
        }
    }
}