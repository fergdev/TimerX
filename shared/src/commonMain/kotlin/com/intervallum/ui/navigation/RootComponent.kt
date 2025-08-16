package com.intervallum.ui.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.intervallum.ui.create.CreateComponent
import com.intervallum.ui.main.MainComponent
import com.intervallum.ui.run.RunComponent
import com.intervallum.ui.settings.SettingsComponent
import com.intervallum.ui.splash.SplashComponent

interface RootComponent : BackHandlerOwner {

    val stack: Value<ChildStack<*, Child>>

    fun onBackClicked()
    fun onBackClicked(toIndex: Int)

    sealed class Child {
        class SplashChild(val component: SplashComponent) : Child()
        class MainChild(val component: MainComponent) : Child()
        class SettingsChild(val component: SettingsComponent) : Child()
        class CreateChild(val component: CreateComponent) : Child()
        class RunChild(val component: RunComponent) : Child()
    }

    fun navigateCreate()
    fun navigateRun(timerId: Long)
}
