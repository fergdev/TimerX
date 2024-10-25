package com.timerx.ui.settings.about.main

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.timerx.ui.settings.about.aboutlibs.AboutLibsComponent
import com.timerx.ui.settings.about.changelog.ChangeLogComponent
import pro.respawn.flowmvi.dsl.store

class AboutMainComponentFake :
    AboutMainComponent,
    AboutStore by store(AboutMainState(), {}) {
    override val aboutLibsSlot: Value<ChildSlot<*, AboutLibsComponent>>
        get() = error("Should not be called")

    override val changeLogSlot: Value<ChildSlot<*, ChangeLogComponent>>
        get() = error("Should not be called")

    override fun onBackClicked() {
    }

    override fun onLibsClicked() {
    }

    override fun onDismissLibs() {
    }

    override fun onChangeLog() {
    }

    override fun onDismissChangeLog() {
    }
}
