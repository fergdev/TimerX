package com.timer.di

import android.content.Context
import com.timerx.di.sharedModule
import com.timerx.ui.main.MainContainer
import io.kotest.core.spec.style.FreeSpec
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
            )
        )
    }
})
