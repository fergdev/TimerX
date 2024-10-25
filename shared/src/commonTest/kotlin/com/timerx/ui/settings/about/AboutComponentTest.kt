package com.timerx.ui.settings.about

import com.timerx.ui.settings.about.main.AboutMainIntent
import com.timerx.ui.settings.about.main.AboutMainState
import com.timerx.util.assertActiveInstance
import com.timerx.util.createComponent
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.kotest.core.spec.style.FreeSpec
import kotlinx.coroutines.Job
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pro.respawn.flowmvi.api.Container

class AboutComponentTest : FreeSpec({
    startKoin {
        modules(
            module {
                single { mock<Container<AboutMainState, AboutMainIntent, Nothing>> {
                    every { store } returns mock{
                        everySuspend { start(any()) } returns Job()
                    }
                } }
            }
        )
    }
    val defaultAboutComponent = createComponent {
        DefaultAboutComponent(
            componentContext = it,
            back = {}
        )
    }
    "init" {
        defaultAboutComponent.stack.value.assertActiveInstance<AboutComponent.Child.Main>()
    }
})
