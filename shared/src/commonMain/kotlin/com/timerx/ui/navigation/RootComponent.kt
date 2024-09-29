package com.timerx.ui.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.timerx.ui.create.CreateComponent
import com.timerx.ui.main.MainComponent
import com.timerx.ui.run.RunComponent
import com.timerx.ui.settings.SettingsComponent

interface RootComponent : BackHandlerOwner {

    val stack: Value<ChildStack<*, Child>>

    fun onBackClicked()
    fun onBackClicked(toIndex: Int)

    sealed class Child {
        class MainChild(val component: MainComponent) : Child()
        class SettingsChild(val component: SettingsComponent) : Child()
        class CreateChild(val component: CreateComponent) : Child()
        class RunChild(val component: RunComponent) : Child()
    }

    fun navigateTo(config: DefaultRootComponent.Config)
}
