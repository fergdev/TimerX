package com.timerx.ui.settings

import com.timerx.platform.Platform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel

class SettingsViewModel(val platform: Platform) : ViewModel(){

    private val _state = MutableStateFlow("")
    val state : StateFlow<String> = _state

    init {
        _state.update { platform.name }
    }
}