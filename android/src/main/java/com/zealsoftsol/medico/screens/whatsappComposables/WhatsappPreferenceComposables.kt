package com.zealsoftsol.medico.screens.whatsappComposables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.ConstColors
import com.zealsoftsol.medico.R
import com.zealsoftsol.medico.core.mvi.scope.nested.WhatsappPreferenceScope
import com.zealsoftsol.medico.screens.common.MedicoButton
import com.zealsoftsol.medico.screens.common.Space

/**
 * @param scope current scope to get the current and updated state of views
 * show the view to update phone number and language for user
 */

@Composable
fun WhatsappPreference(scope: WhatsappPreferenceScope) {
    val phoneNumber = scope.phoneNumber.flow.collectAsState()
    val language = scope.language.flow.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Space(12.dp)
        Text(
            text = stringResource(id = R.string.preferred_language),
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            color = Color.Black,
        )
        Space(12.dp)
        LanguagePicker(scope)
        Space(20.dp)
        Text(
            text = stringResource(id = R.string.phone_number),
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            color = Color.Black,
        )
        Space(12.dp)
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = MaterialTheme.shapes.medium),
            value = phoneNumber.value,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black,
                placeholderColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                if (it.length < 11) // only 10 chars allowed for phone number
                    scope.changePhoneNumber(it)
            },
        )
        Space(20.dp)
        MedicoButton(
            text = stringResource(id = R.string.submit),
            onClick = { scope.submit() },
            isEnabled = language.value.isNotEmpty() && phoneNumber.value.length == 10, // submit only when language is selected and phone number is 10 chars
        )
    }
}

/**
 * @param scope current scope to get the current and updated state of views
 * open language picker for user to select language
 */

@Composable
private fun LanguagePicker(scope: WhatsappPreferenceScope) {
    val languagesList = mutableListOf("English", "Tamil")
    var expanded by remember { mutableStateOf(false) }

    val language = scope.language.flow.collectAsState()

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = MaterialTheme.shapes.medium)
                .clickable(onClick = { expanded = !expanded })
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = language.value,
                color = Color.Black,
                fontSize = 14.sp,
            )
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = ConstColors.gray,
            )
            DropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded, onDismissRequest = {
                    expanded = false
                }) {
                languagesList.forEach { language ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            scope.changeLanguage(language) // update language selected by user
                        }) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = language,
                            color = Color.Black,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}