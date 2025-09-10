package com.intervallum.di

import com.intervallum.analytics.IntervallumAnalytics
import com.intervallum.analytics.IntervallumAnalyticsImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val nonMobileModule = module {
    singleOf(::IntervallumAnalyticsImpl) {
        bind<IntervallumAnalytics>()
    }
}
