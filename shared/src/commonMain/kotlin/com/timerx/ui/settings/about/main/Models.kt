package com.timerx.ui.settings.about.main

import com.timerx.BuildFlags
import com.timerx.settings.AnalyticsSettings
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

data class AboutMainState(
    val versionName: String = BuildFlags.versionName,
    val privacyPolicyUri: String = BuildFlags.privacyPolicyUrl,
    val analyticsSettings: AnalyticsSettings = AnalyticsSettings.NotAvailable
) : MVIState

sealed interface AboutMainIntent : MVIIntent {
    data class UpdateCollectAnalytics(val collectAnalytics: Boolean) : AboutMainIntent
    data object ContactSupport : AboutMainIntent
}
