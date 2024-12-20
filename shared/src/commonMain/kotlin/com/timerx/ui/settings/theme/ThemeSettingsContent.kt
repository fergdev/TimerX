package com.timerx.ui.settings.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.timerx.settings.SettingsDarkTheme
import com.timerx.settings.ThemeContrast
import com.timerx.ui.common.DefaultLoading
import com.timerx.ui.common.TCard
import com.timerx.ui.common.TScaffold
import com.timerx.ui.common.rainbow
import com.timerx.ui.logging.LogScreen
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateContrast
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateDarkTheme
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateIsAmoled
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateIsHighFidelity
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateIsSystemDynamic
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdatePaletteStyle
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateSeedColor
import com.timerx.ui.settings.theme.ThemeSettingsState.LoadedState
import com.timerx.ui.settings.theme.ThemeSettingsState.Loading
import com.timerx.ui.theme.Animation
import com.timerx.ui.theme.Size
import com.timerx.ui.theme.isDarkTheme
import org.jetbrains.compose.resources.stringResource
import pro.respawn.flowmvi.api.IntentReceiver
import pro.respawn.flowmvi.compose.dsl.DefaultLifecycle
import pro.respawn.flowmvi.compose.dsl.subscribe
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.amoled
import timerx.shared.generated.resources.contrast_level
import timerx.shared.generated.resources.dark
import timerx.shared.generated.resources.dark_mode
import timerx.shared.generated.resources.high_fidelity
import timerx.shared.generated.resources.light
import timerx.shared.generated.resources.palette_style
import timerx.shared.generated.resources.preview
import timerx.shared.generated.resources.system_dynamic_colors
import timerx.shared.generated.resources.theme
import timerx.shared.generated.resources.user

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemeSettingsContent(themeSettingsComponent: ThemeSettingsComponent) =
    with(themeSettingsComponent) {
        LogScreen("Settings:Theme")
        TScaffold(
            title = stringResource(Res.string.theme),
            onBack = themeSettingsComponent.backClicked
        ) { padding ->
            val state by subscribe(DefaultLifecycle)
            when (state) {
                Loading -> DefaultLoading()
                is LoadedState -> LoadedContent(
                    modifier = Modifier.widthIn(max = Size.maxWidth).align(Alignment.TopCenter),
                    state = state as LoadedState,
                    scaffoldPadding = padding
                )
            }
        }
    }

@Composable
private fun IntentReceiver<ThemeSettingsIntent>.LoadedContent(
    scaffoldPadding: PaddingValues,
    state: LoadedState,
    modifier: Modifier = Modifier,
) {
    val systemBarPadding = WindowInsets.systemBars.asPaddingValues()
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(
                top = scaffoldPadding.calculateTopPadding().plus(8.dp),
                bottom = scaffoldPadding.calculateBottomPadding() + systemBarPadding.calculateBottomPadding(),
                start = 16.dp,
                end = 16.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DarkModeRow(state.settingsDarkTheme)
        if (state.isDynamicThemeSupported) {
            DynamicColorsRow(state.isSystemDynamic)
        }
        AnimatedVisibility(state.isSystemDynamic.not()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val isDarkTheme = isDarkTheme(state.settingsDarkTheme)
                AnimatedVisibility(isDarkTheme) {
                    AmoledRow(state.isAmoled)
                }
                AnimatedVisibility(state.paletteStyle != PaletteStyle.Monochrome) {
                    HighFidelityRow(state.isHighFidelity)
                }
                ContrastRow(state.contrast)
                PaletteRow(state.paletteStyle)
                AnimatedVisibility(state.paletteStyle != PaletteStyle.Monochrome) {
                    SeedColorRow(state.seedColor)
                }
            }
        }
        ColorPreview()
    }
}

@Composable
private fun IntentReceiver<UpdateIsHighFidelity>.HighFidelityRow(
    isHighFidelity: Boolean,
) {
    val updateIsHighFidelity = {
        intent(UpdateIsHighFidelity(isHighFidelity.not()))
    }
    TCard(modifier = Modifier.clickable { updateIsHighFidelity() }) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(Res.string.high_fidelity))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isHighFidelity,
                onCheckedChange = {
                    intent(UpdateIsHighFidelity(it))
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntentReceiver<UpdateContrast>.ContrastRow(contrast: ThemeContrast) {
    TCard {
        Text(text = stringResource(Res.string.contrast_level))
        FlowRow(
            modifier = Modifier.fillMaxWidth().wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val haptic = LocalHapticFeedback.current
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = contrast.value.toFloat(),
                valueRange = ThemeContrast.range,
                onValueChange = { intent(UpdateContrast(ThemeContrast(it.toDouble()))) },
                onValueChangeFinished = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
        }
    }
}

@Composable
private fun IntentReceiver<UpdateIsSystemDynamic>.DynamicColorsRow(isSystemDynamic: Boolean) {
    val updateIsSystemDynamic = {
        intent(UpdateIsSystemDynamic(isSystemDynamic.not()))
    }
    TCard(
        modifier = Modifier.clickable {
            updateIsSystemDynamic()
        }
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(Res.string.system_dynamic_colors))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isSystemDynamic,
                onCheckedChange = {
                    updateIsSystemDynamic()
                }
            )
        }
    }
}

@Composable
private fun IntentReceiver<UpdateIsAmoled>.AmoledRow(isAmoled: Boolean) {
    val updateIsAmoled = {
        intent(UpdateIsAmoled(isAmoled.not()))
    }
    TCard(
        modifier = Modifier.clickable { updateIsAmoled() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(Res.string.amoled))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isAmoled,
                onCheckedChange = { updateIsAmoled() }
            )
        }
    }
}

private fun SettingsDarkTheme.label() = when (this) {
    SettingsDarkTheme.User -> Res.string.user
    SettingsDarkTheme.ForceLight -> Res.string.light
    SettingsDarkTheme.ForceDark -> Res.string.dark
}

@Composable
private fun IntentReceiver<UpdateDarkTheme>.DarkModeRow(settingsDarkTheme: SettingsDarkTheme) {
    TCard(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(Res.string.dark_mode))
        SingleChoiceSegmentedButtonRow {
            SettingsDarkTheme.entries.forEach {
                SegmentedButton(
                    label = { Text(text = stringResource(it.label())) },
                    selected = settingsDarkTheme == it,
                    shape = SegmentedButtonDefaults.itemShape(
                        it.ordinal,
                        SettingsDarkTheme.entries.size
                    ),
                    onClick = {
                        intent(UpdateDarkTheme(it))
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntentReceiver<UpdatePaletteStyle>.PaletteRow(paletteStyle: PaletteStyle) {
    TCard {
        Text(text = stringResource(Res.string.palette_style))
        FlowRow(
            modifier = Modifier.fillMaxWidth().wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PaletteStyle.entries.forEach {
                FilterChip(
                    label = { Text(text = it.name) },
                    selected = it == paletteStyle,
                    onClick = { intent(UpdatePaletteStyle(it)) },
                )
            }
        }
    }
}

@Composable
private fun ColorPreview() {
    TCard {
        Text(text = stringResource(Res.string.preview))
        Card {
            Column {
                colorSchemePairs().forEach { (name, colors) ->
                    val (color, onColor) = colors
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ColorBox(text = name, color = color, modifier = Modifier.weight(1f))
                        ColorBox(
                            text = "On$name",
                            color = onColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorBox(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val textColor = if (color.luminance() < 0.5f) Color.White else Color.Black
    Box(modifier = modifier.background(color)) {
        Text(
            text = text,
            maxLines = 1,
            color = animateColorAsState(targetValue = textColor).value,
            modifier = Modifier.padding(8.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntentReceiver<UpdateSeedColor>.SeedColorRow(seedColor: Color) {
    TCard {
        FlowRow(modifier = Modifier.fillMaxWidth().wrapContentWidth()) {
            rainbow.forEach { color ->
                val alpha by animateFloatAsState(
                    targetValue = if (seedColor == color) 1.0f else 0f,
                    animationSpec = tween(durationMillis = Animation.fast)
                )

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = 4.dp,
                            color = Color.White.copy(alpha = alpha),
                            shape = CircleShape
                        )
                        .clickable { intent(UpdateSeedColor(color)) }
                )
            }
        }
    }
}

@Composable
private fun colorSchemePairs() = listOf(
    "Primary" to (colorScheme.primary to colorScheme.onPrimary),
    "PrimaryContainer" to (colorScheme.primaryContainer to colorScheme.onPrimaryContainer),
    "Secondary" to (colorScheme.secondary to colorScheme.onSecondary),
    "SecondaryContainer" to (colorScheme.secondaryContainer to colorScheme.onSecondaryContainer),
    "Tertiary" to (colorScheme.tertiary to colorScheme.onTertiary),
    "TertiaryContainer" to (colorScheme.tertiaryContainer to colorScheme.onTertiaryContainer),
    "Error" to (colorScheme.error to colorScheme.onError),
    "ErrorContainer" to (colorScheme.errorContainer to colorScheme.onErrorContainer),
    "Background" to (colorScheme.background to colorScheme.onBackground),
    "Surface" to (colorScheme.surface to colorScheme.onSurface),
    "SurfaceVariant" to (colorScheme.surfaceVariant to colorScheme.onSurfaceVariant),
)
