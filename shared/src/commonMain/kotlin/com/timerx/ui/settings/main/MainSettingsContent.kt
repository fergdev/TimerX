package com.timerx.ui.settings.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.TMenuItem
import com.timerx.ui.common.TScaffold
import com.timerx.ui.common.rainbow
import com.timerx.ui.settings.main.MainSettingsIntent.KeepScreenOn
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import pro.respawn.flowmvi.compose.dsl.DefaultLifecycle
import pro.respawn.flowmvi.compose.dsl.subscribe
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.alerts
import timerx.shared.generated.resources.alerts_subtitle
import timerx.shared.generated.resources.keep_screen_on
import timerx.shared.generated.resources.keep_screen_on_subtitle
import timerx.shared.generated.resources.settings
import timerx.shared.generated.resources.theme
import timerx.shared.generated.resources.theme_subtitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainSettingsContent(
    component: MainSettingsComponent
) {
    with(koinInject<MainSettingsContainer>().store) {
        LaunchedEffect(Unit) { start(this).join() }
        val state by subscribe(DefaultLifecycle)
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
                    icon = Icons.Filled.Face,
                    subtitle = stringResource(Res.string.theme_subtitle),
                    onClick = { component.onThemeClicked() }
                )
                TMenuItem(
                    title = stringResource(Res.string.keep_screen_on),
                    color = rainbow[2],
                    icon = Icons.Filled.Lock,
                    subtitle = stringResource(Res.string.keep_screen_on_subtitle),
                    onClick = {
                        intent(KeepScreenOn(state.isKeepScreenOn.not()))
                    },
                    trailing = {
                        Switch(
                            checked = state.isKeepScreenOn,
                            onCheckedChange = {
                                intent(KeepScreenOn(state.isKeepScreenOn.not()))
                            }
                        )
                    }
                )
            }
        }
    }
}