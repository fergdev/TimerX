package com.timerx.ui.settings.alerts

interface AlertSettingsComponent {
    fun onBackClicked()
}

class DefaultAlertSettingsComponent(
    private val backClicked: () -> Unit
) : AlertSettingsComponent {
    override fun onBackClicked() {
        backClicked()
    }
}
