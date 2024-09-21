package com.timerx.ui.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.timerx.ui.settings.alerts.AlertSettingsComponent
import com.timerx.ui.settings.alerts.DefaultAlertSettingsComponent
import com.timerx.ui.settings.main.DefaultMainSettingsComponent
import com.timerx.ui.settings.main.MainSettingsComponent
import com.timerx.ui.settings.theme.DefaultThemeSettingsComponent
import com.timerx.ui.settings.theme.ThemeSettingsComponent
import kotlinx.serialization.Serializable

interface SettingsComponent : BackHandlerOwner {

    val stack: Value<ChildStack<*, Child>>

    fun onBackClicked()
    fun onBackClicked(toIndex: Int)

    sealed class Child {
        class Main(val component: MainSettingsComponent) : Child()
        class Alerts(val component: AlertSettingsComponent) : Child()
        class Theme(val component: ThemeSettingsComponent) : Child()
    }
}

@OptIn(DelicateDecomposeApi::class)
class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit
) : SettingsComponent, ComponentContext by componentContext {
    private val nav = StackNavigation<SettingsConfig>()
    private val _stack = childStack(
        source = nav,
        serializer = SettingsConfig.serializer(),
        initialStack = { listOf(SettingsConfig.Main) },
        childFactory = ::child,
    )
    override val stack: Value<ChildStack<*, SettingsComponent.Child>> = _stack

    private fun child(
        config: SettingsConfig,
        componentContext: ComponentContext
    ): SettingsComponent.Child =
        when (config) {
            is SettingsConfig.Main -> SettingsComponent.Child.Main(DefaultMainSettingsComponent(
                backClicked = { onBackClicked() },
                alertClicked = { nav.push(SettingsConfig.Alerts) },
                themeClicked = { nav.push(SettingsConfig.Theme) }
            ))

            is SettingsConfig.Alerts -> SettingsComponent.Child.Alerts(DefaultAlertSettingsComponent(
                backClicked = { nav.pop() }
            ))

            is SettingsConfig.Theme -> SettingsComponent.Child.Theme(
                DefaultThemeSettingsComponent(
                    backClicked = { nav.pop() }
                )
            )
        }

    override fun onBackClicked() {
        if (stack.value.backStack.isEmpty()) {
            onBack()
        } else {
            nav.pop()
        }
    }

    override fun onBackClicked(toIndex: Int) {
        nav.popTo(index = toIndex)
    }
}

@Serializable
private sealed interface SettingsConfig {
    @Serializable
    data object Main : SettingsConfig

    @Serializable
    data object Alerts : SettingsConfig

    @Serializable
    data object Theme : SettingsConfig
}
