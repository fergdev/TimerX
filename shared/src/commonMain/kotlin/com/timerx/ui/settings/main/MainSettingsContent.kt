package com.timerx.ui.settings.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.timerx.ui.settings.SettingsScaffold

@Composable
internal fun MainSettingsContent(component: MainSettingsComponent) {
    SettingsScaffold("Settings", component::onBackClicked) {
        Column {
            Button(onClick = { component.onAlertClicked() }) {
                Text(text = "Alerts")
            }
            Button(onClick = { component.onThemeClicked() }) {
                Text(text = "Theme")
            }
        }
    }
}