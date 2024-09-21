package com.timerx.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.timerx.platform.PlatformCapabilities
import com.timerx.settings.DarkTheme
import com.timerx.settings.ITimerXSettings
import com.timerx.settings.ThemeSettings
import com.timerx.ui.common.isDarkTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun CustomizeThemeContent() {
    val settings = koinInject<ITimerXSettings>()
    val platformCapabilities: PlatformCapabilities = koinInject()
    val themeSettings = settings.themeSettings.collectAsState(ThemeSettings())
    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = isDarkTheme(themeSettings.value)

    Column(modifier = Modifier.fillMaxWidth()) {
        DarkModeRow(themeSettings, coroutineScope, settings)
        AnimatedVisibility(platformCapabilities.dynamicColor) {
            DynamicColorsRow(themeSettings.value.isSystemDynamic, coroutineScope, settings)
        }
        AnimatedVisibility(platformCapabilities.dynamicColor.not() || themeSettings.value.isSystemDynamic.not()) {
            Column {
                AnimatedVisibility(isDarkTheme) {
                    AmoledRow(themeSettings, coroutineScope, settings)
                }
                AnimatedVisibility(themeSettings.value.paletteStyle != PaletteStyle.Monochrome) {
                    HighFidelityRow(themeSettings.value.isHighFidelity, coroutineScope, settings)
                }
                ContrastRow(themeSettings.value.contrast, coroutineScope, settings)
                PaletteRow(themeSettings, coroutineScope, settings)
                AnimatedVisibility(themeSettings.value.paletteStyle != PaletteStyle.Monochrome) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        SeedColorRow(themeSettings.value.seedColor)
                        Spacer(modifier = Modifier.height(10.dp))
                        AppColors(coroutineScope, settings)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                ColorPreview()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HighFidelityRow(
    isHighFidelity: Boolean,
    coroutineScope: CoroutineScope,
    settings: ITimerXSettings
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
                onClick = {
                    coroutineScope.launch { settings.setIsHighFidelity(true) }
                },
            )
            FilterChip(
                label = { Text(text = "No") },
                selected = isHighFidelity.not(),
                onClick = {
                    coroutineScope.launch { settings.setIsHighFidelity(false) }
                },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContrastRow(
    contrast: Double,
    coroutineScope: CoroutineScope,
    settings: ITimerXSettings
) {
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
            onValueChange = {
                coroutineScope.launch { settings.setContrast(it.toDouble()) }
            },
            onValueChangeFinished = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DynamicColorsRow(
    isSystemDynamic: Boolean,
    coroutineScope: CoroutineScope,
    settings: ITimerXSettings
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
                    coroutineScope.launch { settings.setIsDynamicTheme(true) }
                },
            )
            FilterChip(
                label = { Text(text = "No") },
                selected = isSystemDynamic.not(),
                onClick = {
                    coroutineScope.launch { settings.setIsDynamicTheme(false) }
                },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AmoledRow(
    themeSettings: State<ThemeSettings>,
    coroutineScope: CoroutineScope,
    settings: ITimerXSettings
) {
    Text(text = "Is Amoled")
    FlowRow(
        modifier = Modifier.fillMaxWidth().wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            label = { Text(text = "Yes") },
            selected = themeSettings.value.isAmoled,
            onClick = {
                coroutineScope.launch { settings.setIsAmoled(true) }
            },
        )
        FilterChip(
            label = { Text(text = "No") },
            selected = themeSettings.value.isAmoled.not(),
            onClick = {
                coroutineScope.launch { settings.setIsAmoled(false) }
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DarkModeRow(
    themeSettings: State<ThemeSettings>,
    coroutineScope: CoroutineScope,
    settings: ITimerXSettings
) {
    Text(text = "Dark mode")
    FlowRow(
        modifier = Modifier.fillMaxWidth().wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            label = { Text(text = "User") },
            selected = themeSettings.value.darkTheme == DarkTheme.User,
            onClick = {
                coroutineScope.launch { settings.setDarkTheme(DarkTheme.User) }
            },
        )
        FilterChip(
            label = { Text(text = "Force Light") },
            selected = themeSettings.value.darkTheme == DarkTheme.ForceLight,
            onClick = {
                coroutineScope.launch { settings.setDarkTheme(DarkTheme.ForceLight) }
            },
        )
        FilterChip(
            label = { Text(text = "Force dark") },
            selected = themeSettings.value.darkTheme == DarkTheme.ForceDark,
            onClick = {
                coroutineScope.launch { settings.setDarkTheme(DarkTheme.ForceDark) }
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PaletteRow(
    themeSettings: State<ThemeSettings>,
    coroutineScope: CoroutineScope,
    settings: ITimerXSettings
) {
    Text(text = "Palette Style")
    FlowRow(
        modifier = Modifier.fillMaxWidth().wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PaletteStyle.entries.forEach { paletteStyle ->
            FilterChip(
                label = { Text(text = paletteStyle.name) },
                selected = themeSettings.value.paletteStyle == paletteStyle,
                onClick = {
                    coroutineScope.launch { settings.setPaletteStyle(paletteStyle) }
                },
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
private fun AppColors(
    coroutineScope: CoroutineScope,
    settings: ITimerXSettings
) {
    FlowRow(modifier = Modifier.fillMaxWidth().wrapContentWidth()) {
        presetColors.forEach { color ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(32.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(color)
                    .clickable {
                        coroutineScope.launch { settings.setSeedColor(color.toArgb()) }
                    }
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
