package com.intervallum.ui.settings.about.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.intervallum.contact.ContactProvider
import com.intervallum.coroutines.TxDispatchers
import com.intervallum.settings.AnalyticsSettings.Available
import com.intervallum.settings.AnalyticsSettings.NotAvailable
import com.intervallum.settings.IntervallumSettings
import com.intervallum.ui.settings.about.aboutlibs.AboutLibsComponent
import com.intervallum.ui.settings.about.changelog.ChangeLogComponent
import com.intervallum.ui.settings.about.main.AboutMainState.AnalyticsNotSupported
import com.intervallum.ui.settings.about.main.AboutMainState.AnalyticsSupported
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

interface AboutMainComponent {

    val onBack: () -> Unit

    val state: Value<AboutMainState>

    val aboutLibsSlot: Value<ChildSlot<*, AboutLibsComponent>>

    val changeLogSlot: Value<ChildSlot<*, ChangeLogComponent>>

    fun contactSupport()

    fun onAboutLibsClicked()

    fun onDismissLibs()

    fun onChangeLog()

    fun onDismissChangeLog()
}

class DefaultAboutMainComponent(
    componentContext: ComponentContext,
    override val onBack: () -> Unit,
    private val intervallumSettings: IntervallumSettings,
    private val contactProvider: ContactProvider,
    txDispatchers: TxDispatchers
) : ComponentContext by componentContext,
    AboutMainComponent {

    private val _state = MutableValue<AboutMainState>(AnalyticsNotSupported())

    override val state = _state

    private val coroutineScope = coroutineScope(txDispatchers.default)

    init {
        coroutineScope.launch {
            intervallumSettings.analytics.collect { analyticsSettings ->
                _state.update {
                    when (analyticsSettings) {
                        is Available -> AnalyticsSupported(
                            collectAnalyticsEnable = analyticsSettings.enabled,
                            updateCollectAnalytics = {
                                coroutineScope.launch {
                                    intervallumSettings.setCollectAnalytics(it)
                                }
                            }
                        )

                        is NotAvailable -> AnalyticsNotSupported()
                    }
                }
            }
        }
    }

    private val aboutLibsNavigation = SlotNavigation<AboutLibsConfig>()
    override val aboutLibsSlot: Value<ChildSlot<*, AboutLibsComponent>> =
        childSlot(
            source = aboutLibsNavigation,
            key = "aboutLibs",
            serializer = AboutLibsConfig.serializer(),
            handleBackButton = true,
        ) { _, _ -> AboutLibsComponent }

    private val changeLogNavigation = SlotNavigation<ChangeLogConfig>()
    override val changeLogSlot: Value<ChildSlot<*, ChangeLogComponent>> =
        childSlot(
            source = changeLogNavigation,
            key = "changeLog",
            serializer = ChangeLogConfig.serializer(),
            handleBackButton = true,
        ) { _, _ -> ChangeLogComponent }

    override fun contactSupport() {
        contactProvider.contactSupport()
    }

    override fun onAboutLibsClicked() {
        aboutLibsNavigation.activate(AboutLibsConfig)
    }

    override fun onDismissLibs() {
        aboutLibsNavigation.dismiss()
    }

    override fun onChangeLog() {
        changeLogNavigation.activate(ChangeLogConfig)
    }

    override fun onDismissChangeLog() {
        changeLogNavigation.dismiss()
    }

    @Serializable
    private object AboutLibsConfig

    @Serializable
    private object ChangeLogConfig
}
