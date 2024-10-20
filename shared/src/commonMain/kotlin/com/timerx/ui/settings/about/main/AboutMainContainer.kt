package com.timerx.ui.settings.about.main

import com.timerx.contact.ContactProvider
import com.timerx.platform.PlatformCapabilities
import com.timerx.settings.TimerXSettings
import com.timerx.util.assert
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed

class AboutMainContainer(
    private val timerXSettings: TimerXSettings,
    private val platformCapabilities: PlatformCapabilities,
    private val contactProvider: ContactProvider
) :
    Container<AboutState, AboutIntent, Nothing> {
    override val store = store(AboutState(hasAnalytics = platformCapabilities.hasAnalytics)) {
        whileSubscribed {
            timerXSettings.collectAnalytics.collect {
                updateState {
                    copy(collectAnalytics = it)
                }
            }
        }

        reduce {
            when (it) {
                is AboutIntent.UpdateCollectAnalytics -> {
                    assert(platformCapabilities.hasAnalytics.not()) {
                        "Analytics not supported on this platform"
                    }
                    timerXSettings.setCollectAnalytics(it.collectAnalytics)
                }

                AboutIntent.ContactSupport -> contactProvider.contact()
            }
        }
    }
}
