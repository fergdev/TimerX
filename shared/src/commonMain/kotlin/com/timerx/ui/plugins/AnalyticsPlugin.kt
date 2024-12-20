package com.timerx.ui.plugins

import com.timerx.analytics.TimerXAnalytics
import org.koin.mp.KoinPlatform
import pro.respawn.flowmvi.api.LazyPlugin
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.dsl.lazyPlugin

fun <S : MVIState, I : MVIIntent, A : MVIAction> analyticsErrorPlugin(): LazyPlugin<S, I, A> = lazyPlugin {
    val analytics = KoinPlatform.getKoin().get<TimerXAnalytics>()
    onException { it.also { analytics.logException(it) } }
    onStop { e ->
        if (e == null) return@onStop
        analytics.logException(e)
    }
}
