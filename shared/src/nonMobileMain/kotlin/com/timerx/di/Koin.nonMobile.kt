package com.timerx.di

import com.timerx.analytics.ITimerXAnalytics
import com.timerx.analytics.TimerXAnalytics
import org.koin.dsl.module

val nonMobileModule = module {
    single<ITimerXAnalytics> { TimerXAnalytics }
}
