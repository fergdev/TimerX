package com.timerx.ui.settings.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.timerx.settings.VibrationSetting
import com.timerx.sound.VoiceInformation
import com.timerx.sound.Volume
import com.timerx.ui.common.TCard
import com.timerx.ui.common.TScaffold
import com.timerx.ui.common.thenIf
import com.timerx.ui.logging.LogScreen
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.EnableNotifications
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.OpenAppSettings
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.SetTTSVoice
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.UpdateVibration
import com.timerx.ui.settings.alerts.AlertsSettingsIntent.UpdateVolume
import com.timerx.util.letType
import kotlinx.collections.immutable.ImmutableSet
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
import timerx.shared.generated.resources.voice
import timerx.shared.generated.resources.volume

private const val LOG_SCREEN_TAG = "Settings:Alert"

internal const val TEST_TAG_VOLUME_SLIDER = "VolumeSlider"
internal const val TEST_TAG_VIBRATION_SWITCH = "VibrationSwitch"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlertsSettingsContent(alertSettingsComponent: AlertSettingsComponent) =
    with(alertSettingsComponent) {
        LogScreen(LOG_SCREEN_TAG)
        TScaffold(
            title = stringResource(Res.string.alerts),
            onBack = onBackClicked
        ) { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .padding(
                        top = scaffoldPadding.calculateTopPadding().plus(8.dp),
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    )
                    .widthIn(max = 600.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                with(subscribe(DefaultLifecycle).value) {
                    VolumeCard(volume)
                    vibration.letType<VibrationSetting.CanVibrate, _> {
                        VibrationCard(enabled)
                    }
                    NotificationsCard(areNotificationsEnabled, canOpenOsSettings)
                    VoiceCard(selectedVoice, availableVoices)
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IntentReceiver<AlertsSettingsIntent>.VoiceCard(
    selectedVoice: VoiceInformation,
    availableVoices: ImmutableSet<VoiceInformation>
) {
    var voiceSelectorVisible by remember { mutableStateOf(false) }
    TCard(onClick = { voiceSelectorVisible = true }) {
        Row {
            Text(text = stringResource(Res.string.voice))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = selectedVoice.name)
        }
    }
    if (voiceSelectorVisible) {
        ModalBottomSheet(onDismissRequest = { voiceSelectorVisible = false }) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                availableVoices.forEach {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { intent(SetTTSVoice(it)) }
                            .thenIf(it.id == selectedVoice.id) {
                                Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
                            },
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = it.name
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IntentReceiver<AlertsSettingsIntent>.NotificationsCard(
    isNotificationsEnabled: Boolean,
    canOpenOsSettings: Boolean
) {
    TCard {
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

        if (canOpenOsSettings) {
            Button(onClick = { intent(OpenAppSettings) }) {
                Text(text = stringResource(Res.string.app_os_settings))
            }
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
    TCard(
        onClick = { updateVibration() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(Res.string.vibration))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                modifier = Modifier.testTag(TEST_TAG_VIBRATION_SWITCH),
                checked = isVibrationEnabled,
                onCheckedChange = {
                    updateVibration()
                }
            )
        }
    }
}

@Composable
private fun IntentReceiver<UpdateVolume>.VolumeCard(volume: Volume) {
    TCard {
        Text(text = stringResource(Res.string.volume))
        Slider(
            modifier = Modifier.testTag(TEST_TAG_VOLUME_SLIDER),
            value = volume.value,
            onValueChange = { intent(UpdateVolume(Volume(it))) }
        )
    }
}
