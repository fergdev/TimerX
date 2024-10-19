package com.timerx.di

import com.timerx.analytics.TimerXAnalytics
import com.timerx.analytics.TimerXAnalyticsImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val nonMobileModule = module {
    singleOf(::TimerXAnalyticsImpl) {
        bind<TimerXAnalytics>()
    }
}
