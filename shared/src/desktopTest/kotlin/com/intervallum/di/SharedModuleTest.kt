package com.intervallum.di

import com.intervallum.coroutines.TxDispatchers
import com.intervallum.platform.PlatformCapabilities
import com.intervallum.settings.IntervallumSettings
import com.intervallum.timermanager.TimerManager
import com.intervallum.ui.main.MainContainer
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.CoroutineDispatcher
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
                definition<MainContainer>(SharedFlow::class),
                definition<PlatformCapabilities>(Boolean::class),
                definition<TxDispatchers>(CoroutineDispatcher::class),
            )
        )
    }
    "get timer settings" {
        org.koin.core.context.startKoin {
            modules(sharedModule)
        }
        KoinPlatform.getKoin().get<IntervallumSettings>().shouldNotBeNull()
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
