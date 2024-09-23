package com.timerx.ui.settings.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.timerx.ui.common.TScaffold
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateContrast
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateDarkTheme
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateIsAmoled
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateIsHighFidelity
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateIsSystemDynamic
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdatePaletteStyle
import com.timerx.ui.settings.theme.ThemeSettingsIntent.UpdateSeedColor
import com.timerx.ui.settings.theme.ThemeSettingsState.LoadedState
import com.timerx.ui.theme.isDarkTheme
import com.timerx.ui.theme.presetColors
import org.koin.compose.koinInject
import pro.respawn.flowmvi.api.IntentReceiver
import pro.respawn.flowmvi.compose.dsl.DefaultLifecycle
import pro.respawn.flowmvi.compose.dsl.subscribe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemeSettingsContent(themeSettingsComponent: ThemeSettingsComponent) {
    TScaffold(title = "Theme", onBack = themeSettingsComponent::onBackClicked) { padding ->
        with(koinInject<ThemeSettingsContainer>().store) {
            LaunchedEffect(Unit) { start(this).join() }
            val state by subscribe(DefaultLifecycle)

            when (state) {
                ThemeSettingsState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is LoadedState -> {
                    LoadedContent(state as LoadedState, padding)
                }
            }
        }
    }
}

@Composable
private fun IntentReceiver<ThemeSettingsIntent>.LoadedContent(
    state: LoadedState,
    scaffoldPadding: PaddingValues
) {
    val systemBarPadding = WindowInsets.systemBars.asPaddingValues()
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            .padding(
                top = scaffoldPadding.calculateTopPadding(),
                bottom = scaffoldPadding.calculateBottomPadding() + systemBarPadding.calculateBottomPadding()
            )
    ) {
        val isDarkTheme = isDarkTheme(state.settingsDarkTheme)
        DarkModeRow(state.settingsDarkTheme)
        if (state.isDynamicThemeSupported) {
            DynamicColorsRow(state.isSystemDynamic)
        }
        AnimatedVisibility(state.isSystemDynamic.not()) {
            Column {
                AnimatedVisibility(isDarkTheme) {
                    AmoledRow(state.isAmoled)
                }
                AnimatedVisibility(state.paletteStyle != PaletteStyle.Monochrome) {
                    HighFidelityRow(state.isHighFidelity)
                }
                ContrastRow(state.contrast)
                PaletteRow(state.paletteStyle)
                AnimatedVisibility(state.paletteStyle != PaletteStyle.Monochrome) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        SeedColorRow(state.seedColor)
                        Spacer(modifier = Modifier.height(10.dp))
                        AppColors()
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        ColorPreview()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntentReceiver<UpdateIsHighFidelity>.HighFidelityRow(
    isHighFidelity: Boolean,
) {
    Column {
        Text(text = "High fidelity")
        FlowRow(
            modifier = Modifier.fillMaxWidth().wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                label = { Text(text = "Yes") },
                selected = isHighFidelity,
                onClick = { intent(UpdateIsHighFidelity(true)) },
            )
            FilterChip(
                label = { Text(text = "No") },
                selected = isHighFidelity.not(),
                onClick = { intent(UpdateIsHighFidelity(false)) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntentReceiver<UpdateContrast>.ContrastRow(contrast: Double) {
    Text(text = "Contrast")
    FlowRow(
        modifier = Modifier.fillMaxWidth().wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val haptic = LocalHapticFeedback.current
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = contrast.toFloat(),
            valueRange = -1f..1f,
            onValueChange = { intent(UpdateContrast(it.toDouble())) },
            onValueChangeFinished = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntentReceiver<UpdateIsSystemDynamic>.DynamicColorsRow(
    isSystemDynamic: Boolean
) {
    Column {
        Text(text = "System dynamic colors")
        FlowRow(
            modifier = Modifier.fillMaxWidth().wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                label = { Text(text = "Yes") },
                selected = isSystemDynamic,
                onClick = {
                    intent(UpdateIsSystemDynamic(true))
                },
            )
            FilterChip(
                label = { Text(text = "No") },
                selected = isSystemDynamic.not(),
                onClick = {
                    intent(UpdateIsSystemDynamic(false))
                },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntentReceiver<UpdateIsAmoled>.AmoledRow(
    isAmoled: Boolean
) {
    Text(text = "Is Amoled")
    FlowRow(
        modifier = Modifier.fillMaxWidth().wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            label = { Text(text = "Yes") },
            selected = isAmoled,
            onClick = {
                intent(UpdateIsAmoled(true))
            },
        )
        FilterChip(
            label = { Text(text = "No") },
            selected = isAmoled.not(),
            onClick = {
                intent(UpdateIsAmoled(false))
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntentReceiver<UpdateDarkTheme>.DarkModeRow(
    settingsDarkTheme: SettingsDarkTheme,
) {
    Text(text = "Dark mode")
    FlowRow(
        modifier = Modifier.fillMaxWidth().wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            label = { Text(text = "User") },
            selected = settingsDarkTheme == SettingsDarkTheme.User,
            onClick = {
                intent(UpdateDarkTheme(SettingsDarkTheme.User))
            },
        )
        FilterChip(
            label = { Text(text = "Force Light") },
            selected = settingsDarkTheme == SettingsDarkTheme.ForceLight,
            onClick = {
                intent(UpdateDarkTheme(SettingsDarkTheme.ForceLight))
            },
        )
        FilterChip(
            label = { Text(text = "Force dark") },
            selected = settingsDarkTheme == SettingsDarkTheme.ForceDark,
            onClick = {
                intent(UpdateDarkTheme(SettingsDarkTheme.ForceDark))
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntentReceiver<UpdatePaletteStyle>.PaletteRow(
    paletteStyle: PaletteStyle,
) {
    Text(text = "Palette Style")
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

@Composable
private fun ColorPreview() {
    Text(text = "Preview", style = MaterialTheme.typography.headlineSmall)
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntentReceiver<UpdateSeedColor>.AppColors() {
    FlowRow(modifier = Modifier.fillMaxWidth().wrapContentWidth()) {
        presetColors.forEach { color ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(32.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(color)
                    .clickable { intent(UpdateSeedColor(color)) }
            )
        }
    }
}

@Composable
fun ColorBox(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val textColor = if (color.luminance() < 0.5f) Color.White else Color.Black
    Box(modifier = modifier.background(color)) {
        Text(
            text = text,
            color = animateColorAsState(targetValue = textColor).value,
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Composable
private fun SeedColorRow(seedColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "Seed Color")
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(height = 32.dp, width = 80.dp)
                .clip(MaterialTheme.shapes.small)
                .background(seedColor)
        )
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
