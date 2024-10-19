package com.timerx.ui.settings.about.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.timerx.ui.settings.about.aboutlibs.AboutLibsComponent
import kotlinx.serialization.Serializable
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore

typealias AboutStore = Store<AboutState, Nothing, Nothing>

interface AboutMainComponent : AboutStore {

    val aboutLibsSlot: Value<ChildSlot<*, AboutLibsComponent>>

    fun back()

    fun onLibs()

    fun dismissLibs()
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
            serializer = AboutLibsConfig.serializer(),
            handleBackButton = true,
        ) { _, _ -> AboutLibsComponent }

    override fun back() {
        onBack()
    }

    override fun onLibs() {
        aboutLibsNavigation.activate(AboutLibsConfig)
    }

    override fun dismissLibs() {
        aboutLibsNavigation.dismiss()
    }

    @Serializable
    private object AboutLibsConfig
}
