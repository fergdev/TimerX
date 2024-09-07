package com.timerx.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.koin.koinViewModel
import org.jetbrains.compose.resources.stringResource
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.app_os_settings
import timerx.shared.generated.resources.back
import timerx.shared.generated.resources.enable
import timerx.shared.generated.resources.enabled
import timerx.shared.generated.resources.notifications
import timerx.shared.generated.resources.settings
import timerx.shared.generated.resources.vibration
import timerx.shared.generated.resources.volume

private const val NINETY_DEGREES = 90f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navigateUp: () -> Unit) {
    val viewModel: SettingsViewModel =
        koinViewModel(vmClass = SettingsViewModel::class)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.settings)) },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.rotate(NINETY_DEGREES),
                        onClick = { navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                }
            )
        },
        content = { scaffoldPadding ->
            val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
            val cutoutPadding = WindowInsets.displayCutout.asPaddingValues()
            Column(
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = systemBarsPadding.calculateStartPadding(LocalLayoutDirection.current)
                        .coerceAtLeast(cutoutPadding.calculateStartPadding(LocalLayoutDirection.current))
                        .coerceAtLeast(16.dp),
                    end = systemBarsPadding.calculateEndPadding(LocalLayoutDirection.current)
                        .coerceAtLeast(cutoutPadding.calculateEndPadding(LocalLayoutDirection.current))
                        .coerceAtLeast(16.dp)
                )
            ) {
                Spacer(Modifier.height(scaffoldPadding.calculateTopPadding()))
                val state by viewModel.state.collectAsState()
                Text(text = stringResource(Res.string.volume))
                Slider(
                    value = state.volume,
                    onValueChange = viewModel.interactions.updateVolume
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(Res.string.vibration))
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        state.vibration,
                        onCheckedChange = { viewModel.interactions.updateVibration(it) })
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
                        Button(onClick = { viewModel.interactions.enableNotifications() }) {
                            Text(stringResource(Res.string.enable))
                        }
                    }
                }

                Button(onClick = { viewModel.interactions.openAppSettings() }) {
                    Text(text = stringResource(Res.string.app_os_settings))
                }
            }
        }
    )
}
