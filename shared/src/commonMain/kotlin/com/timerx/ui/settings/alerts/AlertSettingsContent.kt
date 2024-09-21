package com.timerx.ui.settings.alerts

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.timerx.ui.settings.SettingsScaffold
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import pro.respawn.flowmvi.compose.dsl.DefaultLifecycle
import pro.respawn.flowmvi.compose.dsl.subscribe
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.app_os_settings
import timerx.shared.generated.resources.enable
import timerx.shared.generated.resources.enabled
import timerx.shared.generated.resources.notifications
import timerx.shared.generated.resources.vibration
import timerx.shared.generated.resources.volume

@Composable
fun AlertsSettingsContent(rootComponent: AlertSettingsComponent) {
    with(koinInject<AlertsSettingsContainer>().store) {
        LaunchedEffect(Unit) { start(this).join() }
        val state by subscribe(DefaultLifecycle)
        SettingsScaffold(
            "Alerts", rootComponent::onBackClicked
        ) {
            Text(text = stringResource(Res.string.volume))
            Slider(
                value = state.volume,
                onValueChange = { intent(AlertsSettingsIntent.UpdateVolume(it)) }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(Res.string.vibration))
                Spacer(modifier = Modifier.weight(1f))
                val haptic = LocalHapticFeedback.current
                Switch(
                    state.vibration,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        intent(AlertsSettingsIntent.UpdateVibration(it))
                    }
                )
            }

            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(Res.string.notifications))
                Spacer(modifier = Modifier.weight(1f))
                if (state.notificationsEnabled) {
                    Text(stringResource(Res.string.enabled))
                } else {
                    Button(onClick = { intent(AlertsSettingsIntent.EnableNotifications) }) {
                        Text(stringResource(Res.string.enable))
                    }
                }
            }

            Button(onClick = { intent(AlertsSettingsIntent.OpenAppSettings) }) {
                Text(text = stringResource(Res.string.app_os_settings))
            }
        }
    }
}
