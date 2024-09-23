package com.timerx.ui.settings.alerts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.timerx.ui.common.TScaffold
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.EnableNotifications
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.OpenAppSettings
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.UpdateVibration
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.UpdateVolume
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import pro.respawn.flowmvi.api.IntentReceiver
import pro.respawn.flowmvi.compose.dsl.DefaultLifecycle
import pro.respawn.flowmvi.compose.dsl.subscribe
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.alerts
import timerx.shared.generated.resources.app_os_settings
import timerx.shared.generated.resources.enable
import timerx.shared.generated.resources.enabled
import timerx.shared.generated.resources.notifications
import timerx.shared.generated.resources.vibration
import timerx.shared.generated.resources.volume

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsSettingsContent(rootComponent: AlertSettingsComponent) {
    with(koinInject<AlertsSettingsContainer>().store) {
        LaunchedEffect(Unit) { start(this).join() }
        val state by subscribe(DefaultLifecycle)
        TScaffold(
            title = stringResource(Res.string.alerts),
            onBack = rootComponent::onBackClicked
        ) { scaffoldPadding ->
            Column(
                modifier = Modifier.padding(
                    top = scaffoldPadding.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VolumeCard(state)
                VibrationCard(state)
                NotificationsCard(state)
            }
        }
    }
}

@Composable
private fun IntentReceiver<AlertsSettingsIntent>.NotificationsCard(state: AlertsSettingsState) {
    AlertCard {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(Res.string.notifications))
            Spacer(modifier = Modifier.weight(1f))
            if (state.notificationsEnabled) {
                Text(stringResource(Res.string.enabled))
            } else {
                Button(onClick = { intent(EnableNotifications) }) {
                    Text(stringResource(Res.string.enable))
                }
            }
        }

        Button(onClick = { intent(OpenAppSettings) }) {
            Text(text = stringResource(Res.string.app_os_settings))
        }
    }
}

@Composable
private fun IntentReceiver<UpdateVibration>.VibrationCard(state: AlertsSettingsState) {
    AlertCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(Res.string.vibration))
            Spacer(modifier = Modifier.weight(1f))
            val haptic = LocalHapticFeedback.current
            Switch(
                state.vibration,
                onCheckedChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    intent(UpdateVibration(it))
                }
            )
        }
    }
}

@Composable
private fun IntentReceiver<UpdateVolume>.VolumeCard(state: AlertsSettingsState) {
    AlertCard {
        Text(text = stringResource(Res.string.volume))
        Slider(
            value = state.volume,
            onValueChange = { intent(UpdateVolume(it)) }
        )
    }
}

@Composable
private fun AlertCard(content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard {
        Column(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}
