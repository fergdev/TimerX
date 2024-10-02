package com.timerx.ui.settings.aboutlibs

import com.arkivanov.decompose.ComponentContext

interface AboutLibsComponent {
    fun onBack()
}

class DefaultAboutLibsComponent(
    private val backClicked: () -> Unit,
    context: ComponentContext
) : ComponentContext by context, AboutLibsComponent {
    override fun onBack() {
        backClicked()
    }
}
