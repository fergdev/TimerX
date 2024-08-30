package com.timerx.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationProvider {

    private val _navigationFlow = MutableStateFlow<Screen?>(null)
    val navigationFlow: StateFlow<Screen?> = _navigationFlow

    fun navigationTo(screen: Screen?) {
        _navigationFlow.value = screen
    }
}