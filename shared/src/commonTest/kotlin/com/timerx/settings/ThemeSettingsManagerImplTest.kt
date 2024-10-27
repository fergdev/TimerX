package com.timerx.settings

import androidx.compose.ui.graphics.Color
import app.cash.turbine.test
import com.materialkolor.PaletteStyle
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.timerx.platform.PlatformCapabilities
import com.timerx.platform.platformCapabilitiesOf
import com.timerx.util.awaitAndExpectNoMore
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalSettingsApi::class)
class ThemeSettingsManagerImplTest : FreeSpec({
    val settings = MapSettings()
    val themeSettingsManagerFactory: (PlatformCapabilities) -> ThemeSettingsManager =
        { platformCapabilities ->
            ThemeSettingsManagerImpl(
                flowSettings = settings.makeObservable().toFlowSettings(
                    Dispatchers.Unconfined
                ),
                platformCapabilities = platformCapabilities
            )
        }
    val defaultThemeSettingsManagerFactory =
        { themeSettingsManagerFactory(platformCapabilitiesOf()) }
    afterTest { settings.clear() }

    "default" {
        defaultThemeSettingsManagerFactory().themeSettings.test {
            awaitAndExpectNoMore() shouldBe ThemeSettings()
        }
    }
    "isDynamicTheme" - {
        "throws when not supported" {
            shouldThrow<IllegalArgumentException> {
                defaultThemeSettingsManagerFactory().setIsDynamicTheme(true)
            }.apply {
                message shouldBe "Attempting to set dynamic theme when platform does not support it"
            }
        }
        "updates when supported" {
            val themeSettingsManager =
                themeSettingsManagerFactory(platformCapabilitiesOf(canSystemDynamicTheme = true))
            themeSettingsManager.setIsDynamicTheme(true)
            themeSettingsManager.themeSettings.test {
                awaitAndExpectNoMore() shouldBe ThemeSettings(isSystemDynamic = true)
            }
        }
    }
    "dark theme" - {
        "force light" {
            val themeSettingsManager = defaultThemeSettingsManagerFactory()
            themeSettingsManager.setDarkTheme(SettingsDarkTheme.ForceLight)
            themeSettingsManager.themeSettings.test {
                awaitAndExpectNoMore() shouldBe ThemeSettings(settingsDarkTheme = SettingsDarkTheme.ForceLight)
            }
        }
        "force dark" {
            val themeSettingsManager = defaultThemeSettingsManagerFactory()
            themeSettingsManager.setDarkTheme(SettingsDarkTheme.ForceDark)
            themeSettingsManager.themeSettings.test {
                awaitAndExpectNoMore() shouldBe ThemeSettings(settingsDarkTheme = SettingsDarkTheme.ForceDark)
            }
        }
    }
    "isAmoled" {
        val themeSettingsManager = defaultThemeSettingsManagerFactory()
        themeSettingsManager.setIsAmoled(true)
        themeSettingsManager.themeSettings.test {
            awaitAndExpectNoMore() shouldBe ThemeSettings(isAmoled = true)
        }
    }
    "seedColor" {
        val themeSettingsManager = defaultThemeSettingsManagerFactory()
        themeSettingsManager.setSeedColor(Color.Cyan)
        themeSettingsManager.themeSettings.test {
            awaitAndExpectNoMore() shouldBe ThemeSettings(seedColor = Color.Cyan)
        }
    }
    "palette style" {
        val themeSettingsManager = defaultThemeSettingsManagerFactory()
        themeSettingsManager.setPaletteStyle(PaletteStyle.Vibrant)
        themeSettingsManager.themeSettings.test {
            awaitAndExpectNoMore() shouldBe ThemeSettings(paletteStyle = PaletteStyle.Vibrant)
        }
    }
    "isHighFidelity" {
        val themeSettingsManager = defaultThemeSettingsManagerFactory()
        themeSettingsManager.setIsHighFidelity(isHighFidelity = true)
        themeSettingsManager.themeSettings.test {
            awaitAndExpectNoMore() shouldBe ThemeSettings(isHighFidelity = true)
        }
    }
    "contrast" {
        val themeSettingsManager = defaultThemeSettingsManagerFactory()
        val contrast = ThemeContrast(0.5)
        themeSettingsManager.setContrast(contrast)
        themeSettingsManager.themeSettings.test {
            awaitAndExpectNoMore() shouldBe ThemeSettings(contrast = contrast)
        }
    }
})
