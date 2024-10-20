package com.timerx.ui.settings.about.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.timerx.ui.settings.about.aboutlibs.AboutLibsComponent
import com.timerx.ui.settings.about.changelog.ChangeLogComponent
import kotlinx.serialization.Serializable
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias AboutStore = Store<AboutState, AboutIntent, Nothing>

interface AboutMainComponent : AboutStore {

    val aboutLibsSlot: Value<ChildSlot<*, AboutLibsComponent>>

    fun onBackClicked()

    fun onLibsClicked()

    fun onDismissLibs()

    fun onChangeLog()

    fun onDismissChangeLog()
    val changeLogSlot: Value<ChildSlot<*, ChangeLogComponent>>
}

class DefaultAboutMainComponent(
    componentContext: ComponentContext,
    val onBack: () -> Unit,
    factory: () -> AboutMainContainer
) : ComponentContext by componentContext,
    AboutStore by componentContext.retainedStore(factory = factory),
    AboutMainComponent {

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

    override fun onBackClicked() {
        onBack()
    }

    override fun onLibsClicked() {
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
