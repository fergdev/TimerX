package com.timerx.ui.settings.about.main

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store

class AboutMainContainer : Container<AboutState, Nothing, Nothing> {
    override val store = store(AboutState()) {}
}