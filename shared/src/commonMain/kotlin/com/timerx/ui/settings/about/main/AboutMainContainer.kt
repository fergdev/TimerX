package com.timerx.ui.settings.about.main

import com.timerx.contact.ContactProvider
import com.timerx.settings.TimerXSettings
import com.timerx.ui.settings.about.main.AboutMainIntent.ContactSupport
import com.timerx.ui.settings.about.main.AboutMainIntent.UpdateCollectAnalytics
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

class AboutMainContainer(
    private val timerXSettings: TimerXSettings,
    private val contactProvider: ContactProvider,
) : Container<AboutMainState, AboutMainIntent, Nothing> {
    override val store = store(AboutMainState()) {
        whileSubscribed {
            timerXSettings.analytics.collect {
                updateState { copy(analyticsSettings = it) }
            }
        }

        reduce {
            when (it) {
                is UpdateCollectAnalytics -> timerXSettings.setCollectAnalytics(it.collectAnalytics)
                ContactSupport -> contactProvider.contact()
            }
        }
    }
}
