package com.timerx.ui.settings.alerts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
internal fun AlertsSettingsContent(rootComponent: AlertSettingsComponent) =
    with(rootComponent) {
        TScaffold(
            title = stringResource(Res.string.alerts),
            onBack = ::onBackClicked
        ) { scaffoldPadding ->
            Column(
                modifier = Modifier.padding(
                    top = scaffoldPadding.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
                    .widthIn(max = 600.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                with(subscribe(DefaultLifecycle).value) {
                    VolumeCard(volume)
                    if (canVibrate) {
                        VibrationCard(isVibrationEnabled)
                    }
                    NotificationsCard(isNotificationsEnabled)
                }
            }
        }
    }

@Composable
private fun IntentReceiver<AlertsSettingsIntent>.NotificationsCard(isNotificationsEnabled: Boolean) {
    AlertCard {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(Res.string.notifications))
            Spacer(modifier = Modifier.weight(1f))
            if (isNotificationsEnabled) {
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
private fun IntentReceiver<UpdateVibration>.VibrationCard(isVibrationEnabled: Boolean) {
    val haptic = LocalHapticFeedback.current
    val updateVibration = {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        intent(UpdateVibration(isVibrationEnabled.not()))
    }
    AlertCard(
        modifier = Modifier.clickable {
            updateVibration()
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(Res.string.vibration))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                isVibrationEnabled,
                onCheckedChange = {
                    updateVibration()
                }
            )
        }
    }
}

@Composable
private fun IntentReceiver<UpdateVolume>.VolumeCard(volume: Float) {
    AlertCard {
        Text(text = stringResource(Res.string.volume))
        Slider(
            value = volume,
            onValueChange = { intent(UpdateVolume(it)) }
        )
    }
}

@Composable
private fun AlertCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}
