package com.timerx.ui.settings.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.TMenuItem
import com.timerx.ui.common.TScaffold
import com.timerx.ui.common.rainbow
import com.timerx.ui.settings.main.MainSettingsIntent.KeepScreenOn
import org.jetbrains.compose.resources.stringResource
import pro.respawn.flowmvi.compose.dsl.DefaultLifecycle
import pro.respawn.flowmvi.compose.dsl.subscribe
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.about_libs
import timerx.shared.generated.resources.about_libs_subtitle
import timerx.shared.generated.resources.alerts
import timerx.shared.generated.resources.alerts_subtitle
import timerx.shared.generated.resources.background
import timerx.shared.generated.resources.background_subtitle
import timerx.shared.generated.resources.colors
import timerx.shared.generated.resources.colors_subtitle
import timerx.shared.generated.resources.keep_screen_on
import timerx.shared.generated.resources.keep_screen_on_subtitle
import timerx.shared.generated.resources.privacy_policy
import timerx.shared.generated.resources.privacy_policy_message
import timerx.shared.generated.resources.settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainSettingsContent(component: MainSettingsComponent) =
    with(component) {
        TScaffold(
            title = stringResource(Res.string.settings),
            onBack = component::onBack
        ) { padding ->
            val state by subscribe(DefaultLifecycle)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TMenuItem(
                    title = stringResource(Res.string.alerts),
                    color = rainbow[0],
                    icon = CustomIcons.vibration,
                    subtitle = stringResource(Res.string.alerts_subtitle),
                    onClick = { component.onAlert() }
                )
                TMenuItem(
                    title = stringResource(Res.string.colors),
                    subtitle = stringResource(Res.string.colors_subtitle),
                    color = rainbow[1],
                    icon = Icons.Filled.Face,
                    onClick = { component.onTheme() }
                )
                TMenuItem(
                    title = stringResource(Res.string.background),
                    color = rainbow[2],
                    icon = Icons.Filled.Star,
                    subtitle = stringResource(Res.string.background_subtitle),
                    onClick = { component.onBackgroundSettings() }
                )
                TMenuItem(
                    title = stringResource(Res.string.keep_screen_on),
                    color = rainbow[3],
                    icon = Icons.Filled.Lock,
                    subtitle = stringResource(Res.string.keep_screen_on_subtitle),
                    onClick = { intent(KeepScreenOn(state.isKeepScreenOn.not())) },
                    trailing = {
                        Switch(
                            checked = state.isKeepScreenOn,
                            onCheckedChange = {
                                intent(KeepScreenOn(state.isKeepScreenOn.not()))
                            }
                        )
                    }
                )
                TMenuItem(
                    title = stringResource(Res.string.about_libs),
                    color = rainbow[4],
                    icon = Icons.Filled.Info,
                    subtitle = stringResource(Res.string.about_libs_subtitle),
                    onClick = { component.onAboutLibs() }
                )
                val uriHandler = LocalUriHandler.current
                TMenuItem(
                    title = stringResource(Res.string.privacy_policy),
                    color = rainbow[5],
                    icon = Icons.Filled.Lock,
                    subtitle = stringResource(Res.string.privacy_policy_message),
                    onClick = { uriHandler.openUri(state.privacyPolicyUri)}
                )
            }
        }
    }
