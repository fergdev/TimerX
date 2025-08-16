package com.intervallum.ui.settings.about.main

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.intervallum.ui.settings.about.aboutlibs.AboutLibsComponent
import com.intervallum.ui.settings.about.changelog.ChangeLogComponent

class AboutMainComponentFake :
    AboutMainComponent {
    override val onBack: () -> Unit
        get() = {}
    override val state: Value<AboutMainState>
        get() = error("should not be called")
    override val aboutLibsSlot: Value<ChildSlot<*, AboutLibsComponent>>
        get() = error("Should not be called")

    override val changeLogSlot: Value<ChildSlot<*, ChangeLogComponent>>
        get() = error("Should not be called")

    override fun contactSupport() {}

    override fun onAboutLibsClicked() {}

    override fun onDismissLibs() {}

    override fun onChangeLog() {}

    override fun onDismissChangeLog() {}
}
