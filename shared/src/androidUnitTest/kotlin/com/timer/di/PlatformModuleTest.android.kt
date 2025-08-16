package com.timer.di

import android.content.Context
import com.intervallum.coroutines.TxDispatchers
import com.intervallum.di.platformModule
import com.intervallum.settings.AlertSettingsManager
import com.intervallum.settings.IntervallumSettings
import com.intervallum.timermanager.TimerManager
import io.kotest.core.spec.style.FreeSpec
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class PlatformModuleTest : FreeSpec({
    "verify" {
        platformModule.verify(
            extraTypes = listOf(
                Context::class,
                IntervallumSettings::class,
                AlertSettingsManager::class,
                TimerManager::class,
                TxDispatchers::class
            )
        )
    }
})
