package com.timerx.di

import com.timerx.settings.TimerXSettings
import com.timerx.timermanager.TimerManager
import com.timerx.ui.main.MainContainer
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.flow.SharedFlow
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatform
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class SharedModuleTest : FreeSpec({
    "shared module verify" {
        sharedModule.verify(
            injections = injectedParameters(
                definition<MainContainer>(SharedFlow::class)
            )
        )
    }
    "get timer settings" {
        org.koin.core.context.startKoin {
            modules(sharedModule)
        }
        KoinPlatform.getKoin().get<TimerXSettings>().shouldNotBeNull()
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
