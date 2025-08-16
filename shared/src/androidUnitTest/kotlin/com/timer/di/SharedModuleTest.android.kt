package com.timer.di

import android.content.Context
import com.intervallum.coroutines.TxDispatchers
import com.intervallum.di.sharedModule
import com.intervallum.platform.PlatformCapabilities
import com.intervallum.ui.main.MainContainer
import io.kotest.core.spec.style.FreeSpec
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharedFlow
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class SharedModuleTest : FreeSpec({
    "shared module verify" {
        sharedModule.verify(
            extraTypes = listOf(Context::class),
            injections = injectedParameters(
                definition<MainContainer>(SharedFlow::class),
                definition<PlatformCapabilities>(Boolean::class),
                definition<TxDispatchers>(CoroutineDispatcher::class),
            )
        )
    }
})
