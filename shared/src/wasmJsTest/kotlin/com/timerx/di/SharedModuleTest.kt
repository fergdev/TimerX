package com.timerx.di

import com.timerx.settings.TimerXSettings
import com.timerx.timermanager.TimerManager
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatform

class SharedModuleTest : FreeSpec({
    "get timer settings" {
        org.koin.core.context.startKoin {
            modules(sharedModule)
        }
        KoinPlatform.getKoin().get<TimerXSettings>().shouldNotBeNull()
        false shouldBe true
        stopKoin()
    }
    "get timer manager" {
        org.koin.core.context.startKoin {
            modules(sharedModule)
        }
        KoinPlatform.getKoin().get<TimerManager>().shouldNotBeNull()
        stopKoin()
    }
})
