package com.timerx.ui.settings.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.TMenuItem
import com.timerx.ui.common.TScaffold
import com.timerx.ui.common.rainbow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainSettingsContent(component: MainSettingsComponent) {
    TScaffold(
        title = "Settings",
        onBack = component::onBackClicked
    ) { padding ->
        Column(
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            )
        ) {
            TMenuItem(
                title = "Alerts",
                color = rainbow[0],
                icon = CustomIcons.vibration,
                subtitle = "Control how Tx interacts with you",
                onClick = { component.onAlertClicked() }
            )
            TMenuItem(
                title = "Theme",
                color = rainbow[1],
                icon = CustomIcons.pause,
                subtitle = "Control look and feel",
                onClick = { component.onThemeClicked() }
            )
        }
    }
}