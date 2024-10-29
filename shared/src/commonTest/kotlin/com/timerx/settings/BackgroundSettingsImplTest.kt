package com.timerx.settings

import app.cash.turbine.test
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.timerx.testutil.awaitAndExpectNoMore
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalSettingsApi::class)
class BackgroundSettingsImplTest : FreeSpec({
    val settings = MapSettings()
    val backgroundSettingsManagerFactory = {
        BackgroundSettingsManagerImpl(
            flowSettings = settings.makeObservable().toFlowSettings(Dispatchers.Unconfined),
        )
    }
    afterTest { settings.clear() }

    "default" {
        backgroundSettingsManagerFactory().backgroundSettings.test {
            awaitAndExpectNoMore() shouldBe BackgroundSettings()
        }
    }
    "alpha" {
        val backgroundSettings = backgroundSettingsManagerFactory()
        val backgroundAlpha = BackgroundAlpha(0.5f)
        backgroundSettings.setBackgroundAlpha(backgroundAlpha)
        backgroundSettings.backgroundSettings.test {
            awaitAndExpectNoMore() shouldBe BackgroundSettings(
                backgroundAlpha = backgroundAlpha
            )
        }
    }
    "pattern" {
        val backgroundSettings = backgroundSettingsManagerFactory()
        val pattern = Pattern.Rectangles
        backgroundSettings.setPattern(pattern)
        backgroundSettings.backgroundSettings.test {
            awaitAndExpectNoMore() shouldBe BackgroundSettings(
                pattern = pattern
            )
        }
    }
})
