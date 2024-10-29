package com.timerx.ui.settings.about.main

import androidx.compose.runtime.Stable
import com.timerx.BuildFlags

interface AboutMainState {
    val versionName: String
    val privacyPolicyUri: String

    @Stable
    data class AnalyticsSupported(
        override val versionName: String = BuildFlags.versionName,
        override val privacyPolicyUri: String = BuildFlags.privacyPolicyUrl,
        val collectAnalyticsEnable: Boolean = false,
        val updateCollectAnalytics: (Boolean) -> Unit
    ) : AboutMainState

    @Stable
    data class AnalyticsNotSupported(
        override val versionName: String = BuildFlags.versionName,
        override val privacyPolicyUri: String = BuildFlags.privacyPolicyUrl,
    ) : AboutMainState
}
