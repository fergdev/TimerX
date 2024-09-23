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
import org.jetbrains.compose.resources.stringResource
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.alerts
import timerx.shared.generated.resources.alerts_subtitle
import timerx.shared.generated.resources.settings
import timerx.shared.generated.resources.theme
import timerx.shared.generated.resources.theme_subtitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainSettingsContent(component: MainSettingsComponent) {
    TScaffold(
        title = stringResource(Res.string.settings),
        onBack = component::onBackClicked
    ) { padding ->
        Column(
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            )
        ) {
            TMenuItem(
                title = stringResource(Res.string.alerts),
                color = rainbow[0],
                icon = CustomIcons.vibration,
                subtitle = stringResource(Res.string.alerts_subtitle),
                onClick = { component.onAlertClicked() }
            )
            TMenuItem(
                title = stringResource(Res.string.theme),
                color = rainbow[1],
                icon = CustomIcons.pause,
                subtitle = stringResource(Res.string.theme_subtitle),
                onClick = { component.onThemeClicked() }
            )
        }
    }
}