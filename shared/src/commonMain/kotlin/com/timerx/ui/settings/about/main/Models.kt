package com.timerx.ui.settings.about.main

import com.timerx.BuildFlags
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

data class AboutState(
    val versionName: String = "TimerX v${BuildFlags.versionName}",
    val privacyPolicyUri: String = BuildFlags.privacyPolicyUrl,
    val hasAnalytics: Boolean = true,
    val collectAnalytics: Boolean = true
) : MVIState

sealed interface AboutIntent : MVIIntent {
    data class UpdateCollectAnalytics(val collectAnalytics: Boolean) : AboutIntent
}