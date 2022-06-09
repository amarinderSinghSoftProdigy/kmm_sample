package com.zealsoftsol.medico.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.OutlinedInputField
import com.zealsoftsol.medico.screens.common.ShowToastGlobal
import com.zealsoftsol.medico.screens.common.Space
import com.zealsoftsol.medico.screens.common.clickable
import com.zealsoftsol.medico.screens.common.scrollOnFocus
import com.zealsoftsol.medico.screens.common.stringResourceByName

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AuthScreen(scope: LogInScope) {

    val showLoginView = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.bg_auth), contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        AnimatedVisibility(
            visible = showLoginView.value,
            enter = slideInVertically(
                initialOffsetY = { 300 },
                animationSpec = tween(
                    durationMillis = 200,
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { 1000 },
                animationSpec = tween(
                    durationMillis = 200,
                    easing = LinearEasing
                )
            ),
        ) {
            AuthTab(scope, showLoginView)
        }

        AnimatedVisibility(
            visible = !showLoginView.value,
            enter = slideInVertically(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = LinearEasing
                )
            ),
            exit = ExitTransition.None
        ) {
            PreAuthTab(scope, showLoginView)
        }

        if (scope.showToast.flow.collectAsState().value) {
            ShowToastGlobal(msg = stringResourceByName(name = scope.errorCode.flow.collectAsState().value))
            scope.hideErrorToast()
        }
    }
}

@Composable
private fun PreAuthTab(scope: LogInScope, showLoginView: MutableState<Boolean>) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Image(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth(),
            painter = painterResource(id = R.drawable.ic_logo_new),
            contentDescription = null
        )
        Space(20.dp)
        Text(
            text = stringResource(id = R.string.about_medico1),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center
        )
        Space(30.dp)
        Text(
            text = stringResource(id = R.string.about_medico2),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Center
        )
        Space(100.dp)
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MedicoButton(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.log_in),
                isEnabled = true
            ) {
                showLoginView.value = true
            }
            Space(dp = 20.dp)
            MedicoButton(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.register),
                isEnabled = true,
                txtColor = Color.White,
                color = ConstColors.lightBlue
            ) {
                scope.goToSignUp()
            }
        }
        Space(40.dp)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AuthTab(scope: LogInScope, showLoginView: MutableState<Boolean>) {
    val credentialsState = scope.credentials.flow.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val isValidPhone = scope.isValidPhone(credentialsState.value.phoneNumberOrEmail)
    val isValidPassword = scope.isValidPassword(credentialsState.value.password)
    val showCredentialError = scope.showCredentialError.flow.collectAsState()
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                Space(20.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.log_in),
                        color = MaterialTheme.colors.background,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W900,
                    )

                    Text(
                        text = stringResource(id = R.string.forgot_password),
                        color = ConstColors.lightBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W700,
                        modifier = Modifier
                            .clickable { scope.goToForgetPassword() },
                    )
                }
                Space(25.dp)
                val formatted = credentialsState.value.phoneNumberOrEmail
                OutlinedInputField(
                    modifier = Modifier
                        .scrollOnFocus(scrollState, coroutineScope)
                        .focusRequester(
                            focusRequester
                        ),
                    hint = stringResource(id = R.string.phone_number),
                    text = if (formatted.isDigitsOnly()) formatted else "",
                    isValid = isValidPhone && !showCredentialError.value,
                    maxLines = 1,
                    onValueChange = {
                        if (it.isDigitsOnly()) {
                            scope.updateAuthCredentials(
                                it,
                                credentialsState.value.password
                            )
                        }
                    },
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(
                            focusDirection = FocusDirection.Down,
                        )
                    }),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number,
                    ),
                )
                if (!isValidPhone) {
                    Space(10.dp)
                    Text(
                        modifier = Modifier.align(Start),
                        text = stringResource(id = R.string.phone_validation),
                        color = ConstColors.lightBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W500
                    )
                }
                Space(12.dp)
                Box(
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    val isPasswordHidden = remember { mutableStateOf(true) }
                    OutlinedInputField(
                        modifier = Modifier.scrollOnFocus(scrollState, coroutineScope),
                        hint = stringResource(id = R.string.password),
                        text = credentialsState.value.password,
                        isValid = isValidPassword && !showCredentialError.value,
                        visualTransformation = if (isPasswordHidden.value) PasswordVisualTransformation() else VisualTransformation.None,
                        maxLines = 1,
                        onValueChange = {
                            scope.updateAuthCredentials(
                                credentialsState.value.phoneNumberOrEmail,
                                it
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    )
                    Icon(
                        imageVector = Icons.Default.RemoveRedEye,
                        contentDescription = null,
                        tint = if (isPasswordHidden.value) ConstColors.gray else ConstColors.lightBlue,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(42.dp)
                            .clickable(indication = rememberRipple(radius = 15.dp)) {
                                isPasswordHidden.value = !isPasswordHidden.value
                            }
                            .padding(12.dp),
                    )
                }
                if (!isValidPassword) {
                    Space(10.dp)
                    Text(
                        modifier = Modifier.align(Start),
                        text = stringResource(id = R.string.password_requirement),
                        color = ConstColors.lightBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W500
                    )
                }
                Space(25.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = ConstColors.lightBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W700,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                keyboardController?.hide()
                                showLoginView.value = false
                            },
                    )
                    Space(20.dp)
                    MedicoButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.log_in),
                        isEnabled = isValidPhone && isValidPassword && credentialsState.value.phoneNumberOrEmail.isNotEmpty() &&
                                credentialsState.value.password.isNotEmpty(),
                        elevation = null,
                        onClick = { scope.tryLogIn() },
                        txtColor = MaterialTheme.colors.background,
                    )
                }
                Space(24.dp)
            }
        }
    }
}