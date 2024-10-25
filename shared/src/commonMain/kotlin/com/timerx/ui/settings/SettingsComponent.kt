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
import com.timerx.ui.settings.SettingsComponent.Child.Alerts
import com.timerx.ui.settings.SettingsComponent.Child.Background
import com.timerx.ui.settings.SettingsComponent.Child.Main
import com.timerx.ui.settings.SettingsComponent.Child.Theme
import com.timerx.ui.settings.about.AboutComponent
import com.timerx.ui.settings.about.DefaultAboutComponent
import com.timerx.ui.settings.alerts.AlertSettingsComponent
import com.timerx.ui.settings.alerts.DefaultAlertSettingsComponent
import com.timerx.ui.settings.background.BackgroundSettingsComponent
import com.timerx.ui.settings.background.DefaultBackgroundSettingsComponent
import com.timerx.ui.settings.main.DefaultMainSettingsComponent
import com.timerx.ui.settings.main.MainSettingsComponent
import com.timerx.ui.settings.theme.DefaultThemeSettingsComponent
import com.timerx.ui.settings.theme.ThemeSettingsComponent
import kotlinx.serialization.Serializable
import org.koin.mp.KoinPlatform

interface SettingsComponent : BackHandlerOwner {

    val stack: Value<ChildStack<*, Child>>

    fun onBackClicked()
    fun onBackClicked(toIndex: Int)

    sealed class Child {
        class Main(val component: MainSettingsComponent) : Child()
        class Alerts(val component: AlertSettingsComponent) : Child()
        class Theme(val component: ThemeSettingsComponent) : Child()
        class Background(val component: BackgroundSettingsComponent) : Child()
        class About(val component: AboutComponent) : Child()
    }
}

@OptIn(DelicateDecomposeApi::class)
internal class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit
) : SettingsComponent, ComponentContext by componentContext {
    private val koin = KoinPlatform.getKoin()
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
            is SettingsConfig.Main -> Main(
                DefaultMainSettingsComponent(
                    onBackClicked = ::onBackClicked,
                    onAlertsClicked = { nav.push(SettingsConfig.Alerts) },
                    onThemeClicked = { nav.push(SettingsConfig.Theme) },
                    onBackgroundClicked = { nav.push(SettingsConfig.Background) },
                    onAboutClicked = { nav.push(SettingsConfig.About) },
                    context = componentContext,
                    factory = koin::get
                )
            )

            is SettingsConfig.Alerts -> Alerts(
                DefaultAlertSettingsComponent(
                    onBackClicked = nav::pop,
                    factory = koin::get,
                    context = componentContext
                )
            )

            is SettingsConfig.Theme -> Theme(
                DefaultThemeSettingsComponent(
                    backClicked = { nav.pop() },
                    factory = koin::get,
                    context = componentContext
                )
            )

            is SettingsConfig.Background -> Background(
                DefaultBackgroundSettingsComponent(
                    backClicked = { nav.pop() },
                    factory = koin::get,
                    context = componentContext
                )
            )

            is SettingsConfig.About -> SettingsComponent.Child.About(
                DefaultAboutComponent(
                    back = { nav.pop() },
                    componentContext = componentContext
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

    @Serializable
    data object Background : SettingsConfig

    @Serializable
    data object About : SettingsConfig
}
