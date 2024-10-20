package com.timerx.ui.settings.about

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.timerx.ui.settings.about.main.AboutMainComponent
import com.timerx.ui.settings.about.main.DefaultAboutMainComponent
import kotlinx.serialization.Serializable
import org.koin.mp.KoinPlatform

interface AboutComponent : BackHandlerOwner {

    val stack: Value<ChildStack<*, Child>>

    fun onBackClicked()

    sealed class Child {
        class Main(val component: AboutMainComponent) : Child()
    }
}

internal class DefaultAboutComponent(
    componentContext: ComponentContext,
    private val back: () -> Unit,
) : ComponentContext by componentContext,
    AboutComponent {

    private val koin = KoinPlatform.getKoin()

    private val nav = StackNavigation<AboutConfig>()

    private val _stack = childStack(
        source = nav,
        serializer = AboutConfig.serializer(),
        initialStack = { listOf(AboutConfig.Main) },
        childFactory = ::child,
    )

    override val stack: Value<ChildStack<*, AboutComponent.Child>> = _stack

    private fun child(
        config: AboutConfig,
        componentContext: ComponentContext
    ): AboutComponent.Child =
        when (config) {
            is AboutConfig.Main -> AboutComponent.Child.Main(
                DefaultAboutMainComponent(
                    componentContext = componentContext,
                    onBack = ::onBackClicked,
                    factory = koin::get
                )
            )
        }

    override fun onBackClicked() {
        if (stack.value.backStack.isEmpty()) {
            back()
        } else {
            nav.pop()
        }
    }

    @Serializable
    private sealed interface AboutConfig {
        @Serializable
        data object Main : AboutConfig
    }
}
