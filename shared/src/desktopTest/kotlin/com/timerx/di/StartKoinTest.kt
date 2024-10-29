package com.timerx.di

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.mp.KoinPlatform

class StartKoinTest : FreeSpec({
    "start koin" - {
        "no app declaration" {
            startKoin(listOf(sharedModule))
            KoinPlatform.getKoin().shouldNotBeNull()
            stopKoin()
        }
        "with app declaration" {
            startKoin {
                modules(sharedModule)
            }
            KoinPlatform.getKoin().shouldNotBeNull()
            stopKoin()
        }
    }
})
