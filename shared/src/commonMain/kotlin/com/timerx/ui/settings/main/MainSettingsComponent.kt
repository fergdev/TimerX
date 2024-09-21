package com.timerx.ui.settings.main

interface MainSettingsComponent {
    fun onBackClicked()
    fun onAlertClicked()
    fun onThemeClicked()
}

class DefaultMainSettingsComponent(
    val backClicked: () -> Unit,
    val alertClicked: () -> Unit,
    val themeClicked: () -> Unit
) : MainSettingsComponent {
    override fun onBackClicked() {
        backClicked()
    }

    override fun onAlertClicked() {
        alertClicked()
    }

    override fun onThemeClicked() {
        themeClicked()
    }
}
