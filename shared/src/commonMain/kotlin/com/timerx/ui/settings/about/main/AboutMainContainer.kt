package com.timerx.ui.settings.about.main

import com.timerx.contact.ContactProvider
import com.timerx.platform.platformCapabilities
import com.timerx.settings.TimerXSettings
import com.timerx.ui.settings.about.main.AboutMainIntent.ContactSupport
import com.timerx.ui.settings.about.main.AboutMainIntent.UpdateCollectAnalytics
import com.timerx.util.assert
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

class AboutMainContainer(
    private val timerXSettings: TimerXSettings,
    private val contactProvider: ContactProvider
) : Container<AboutMainState, AboutMainIntent, Nothing> {
    override val store = store(AboutMainState(hasAnalytics = platformCapabilities.hasAnalytics)) {
        whileSubscribed {
            timerXSettings.collectAnalytics.collect {
                updateState {
                    copy(collectAnalytics = it)
                }
            }
        }

        reduce {
            when (it) {
                is UpdateCollectAnalytics -> {
                    assert(platformCapabilities.hasAnalytics) {
                        "Analytics not supported on this platform"
                    }
                    timerXSettings.setCollectAnalytics(it.collectAnalytics)
                }

                ContactSupport -> contactProvider.contact()
            }
        }
    }
}
