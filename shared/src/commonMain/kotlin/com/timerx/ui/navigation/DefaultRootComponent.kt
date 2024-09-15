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
import com.arkivanov.decompose.router.stack.webhistory.WebHistoryController
import com.arkivanov.decompose.value.Value
import com.timerx.ui.create.DefaultCreateComponent
import com.timerx.ui.main.DefaultMainComponent
import com.timerx.ui.run.DefaultRunComponent
import kotlinx.serialization.Serializable

class DefaultRootComponent @OptIn(ExperimentalDecomposeApi::class) constructor(
    componentContext: ComponentContext,
    webHistoryController: WebHistoryController? = null
) : RootComponent,
    ComponentContext by componentContext {

    private val nav = StackNavigation<Config>()

    private val _stack =
        childStack(
            source = nav,
            serializer = Config.serializer(),
            initialStack = { listOf(Config.Main) },
            childFactory = ::child,
        )

    override val stack: Value<ChildStack<*, RootComponent.Child>> = _stack

    init {
//        webHistoryController?.attach(
//            navigator = nav,
//            serializer = Config.serializer(),
//            stack = _stack,
//            getPath = ::getPathForConfig,
//            getConfiguration = ::getConfigForPath,
//        )
    }

    @OptIn(DelicateDecomposeApi::class)
    private fun child(config: Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            is Config.Main ->
                RootComponent.Child.MainChild(
                    DefaultMainComponent(
                        componentContext = componentContext,
                        onRunTimer = { nav.push(Config.Run(it)) },
                        onSettings = { nav.push(Config.Settings) },
                        onCreate = { nav.push(Config.Create(it)) }
                    )
                )

            is Config.Settings ->
                RootComponent.Child.SettingsChild(
                    DefaultSettingsComponent(
                        componentContext = componentContext,
                        onBack = { nav.pop() }
                    )
                )

            is Config.Create ->
                RootComponent.Child.CreateChild(
                    DefaultCreateComponent(
                        componentContext = componentContext,
                        timerId = config.timerId ?: -1L,
                        onBack = { nav.pop() }
                    )
                )

            is Config.Run ->
                RootComponent.Child.RunChild(
                    DefaultRunComponent(
                        componentContext,
                        config.timerId,
                        onBack = { nav.pop() }
                    )
                )
        }

    override fun onBackClicked() {
        nav.pop()
    }

    override fun onBackClicked(toIndex: Int) {
        nav.popTo(index = toIndex)
    }

    @OptIn(DelicateDecomposeApi::class)
    override fun navigateTo(config: Config) {
        nav.push(config)
    }

    // TODO consider keeping this private, this was made public to allow android intents to work
    @Serializable
    sealed interface Config {
        @Serializable
        data object Main : Config

        @Serializable
        data object Settings : Config

        @Serializable
        data class Create(val timerId: Long?) : Config

        @Serializable
        data class Run(val timerId: Long) : Config
    }
}


