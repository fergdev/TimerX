package com.timer.di

import android.content.Context
import com.timerx.di.platformModule
import com.timerx.settings.AlertSettingsManager
import com.timerx.settings.TimerXSettings
import com.timerx.timermanager.TimerManager
import io.kotest.core.spec.style.FreeSpec
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class PlatformModuleTest : FreeSpec({
    "verify" {
        platformModule.verify(
            extraTypes = listOf(
                Context::class,
                TimerXSettings::class,
                AlertSettingsManager::class,
                TimerManager::class
            )
        )
    }
})
