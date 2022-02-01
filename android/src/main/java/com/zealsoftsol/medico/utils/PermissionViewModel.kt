package com.zealsoftsol.medico.utils

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionViewModel : ViewModel() {
    private val _performAction: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _performTypeAction: MutableStateFlow<String> = MutableStateFlow("")
    val performAction = _performAction.asStateFlow()
    val performTypeAction = _performTypeAction.asStateFlow()


    fun setPerformLocationAction(request: Boolean, type: String) {
        _performAction.value = request
        _performTypeAction.value = type
    }

}