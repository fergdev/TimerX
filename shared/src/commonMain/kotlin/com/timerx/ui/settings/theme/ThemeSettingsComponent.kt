package com.timerx.ui.settings.theme

interface ThemeSettingsComponent {
    fun onBackClicked()
}

class DefaultThemeSettingsComponent(
    private val backClicked: () -> Unit,
) : ThemeSettingsComponent {
    override fun onBackClicked() {
        backClicked()
    }
}
