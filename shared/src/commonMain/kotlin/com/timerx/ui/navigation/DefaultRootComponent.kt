@file:Suppress("OPT_IN_USAGE")

package com.timerx.ui.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.router.stack.webhistory.WebHistoryController
import com.arkivanov.decompose.value.Value
import com.timerx.platform.platformCapabilities
import com.timerx.ui.create.DefaultCreateComponent
import com.timerx.ui.main.DefaultMainComponent
import com.timerx.ui.run.DefaultRunComponent
import com.timerx.ui.settings.DefaultSettingsComponent
import com.timerx.ui.splash.DefaultSplashComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.Serializable
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform

class DefaultRootComponent @OptIn(ExperimentalDecomposeApi::class) constructor(
    componentContext: ComponentContext,
    webHistoryController: WebHistoryController? = null
) : RootComponent,
    ComponentContext by componentContext {

    private val koin by lazy { KoinPlatform.getKoin() }

    private val nav = StackNavigation<Config>()

    private val updatedTimerFlow = MutableSharedFlow<Long>(extraBufferCapacity = Int.MAX_VALUE)

    private val _stack =
        childStack(
            source = nav,
            serializer = Config.serializer(),
            initialStack = {
                if (platformCapabilities.hasOwnSplashScreen) {
                    listOf(Config.Main)
                } else {
                    listOf(Config.Splash)
                }
            },
            childFactory = ::child,
        )

    override val stack: Value<ChildStack<*, RootComponent.Child>> = _stack

    init {
        webHistoryController?.attach(
            navigator = nav,
            serializer = Config.serializer(),
            stack = _stack,
            getPath = { "" },
            getConfiguration = { Config.Main }
        )
    }

    @OptIn(DelicateDecomposeApi::class)
    private fun child(config: Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            Config.Main ->
                RootComponent.Child.MainChild(
                    DefaultMainComponent(
                        componentContext = componentContext,
                        onRun = { nav.push(Config.Run(it)) },
                        onSettings = { nav.push(Config.Settings) },
                        onCreate = { nav.push(Config.Create(it)) },
                        factory = { koin.get(parameters = { parametersOf(updatedTimerFlow) }) },
                    )
                )

            Config.Settings ->
                RootComponent.Child.SettingsChild(
                    DefaultSettingsComponent(
                        componentContext = componentContext,
                        onBack = { nav.pop() }
                    )
                )

            is Config.Create -> {
                RootComponent.Child.CreateChild(
                    DefaultCreateComponent(
                        componentContext = componentContext,
                        onBack = {
                            nav.pop()
                        },
                        timerUpdated = {
                            nav.pop()
                            updatedTimerFlow.tryEmit(it)
                        },
                        factory = {
                            koin.get(parameters = { parametersOf(config.timerId ?: -1L) })
                        },
                    )
                )
            }

            is Config.Run ->
                RootComponent.Child.RunChild(
                    DefaultRunComponent(
                        componentContext = componentContext,
                        factory = { koin.get(parameters = { parametersOf(config.timerId) }) },
                        onBack = { nav.pop() }
                    )
                )

            Config.Splash -> RootComponent.Child.SplashChild(
                DefaultSplashComponent(
                    finishSplash = { nav.replaceAll(Config.Main) }
                )
            )
        }

    override fun onBackClicked() {
        nav.pop()
    }

    override fun onBackClicked(toIndex: Int) {
        nav.popTo(index = toIndex)
    }

    override fun navigateRun(timerId: Long) {
        nav.replaceAll(Config.Main, Config.Run(timerId))
    }

    override fun navigateCreate() {
        nav.replaceAll(Config.Main, Config.Create())
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Splash : Config

        @Serializable
        data object Main : Config

        @Serializable
        data object Settings : Config

        @Serializable
        data class Create(val timerId: Long? = null) : Config

        @Serializable
        data class Run(val timerId: Long) : Config
    }
}
