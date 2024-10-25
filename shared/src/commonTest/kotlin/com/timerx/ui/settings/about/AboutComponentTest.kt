package com.timerx.ui.settings.about

import com.timerx.ui.settings.about.main.AboutMainComponentFake
import com.timerx.util.assertActiveInstance
import com.timerx.util.createComponent
import io.kotest.core.spec.style.FreeSpec
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

class AboutComponentTest : FreeSpec({
    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher() as CoroutineDispatcher)
    }
    afterTest {
        Dispatchers.resetMain()
    }
    "init" {
        val defaultAboutComponent = createComponent {
            DefaultAboutComponent(
                componentContext = it,
                back = {},
                aboutMainComponentFactory = { _, _ -> AboutMainComponentFake() }
            )
        }
        defaultAboutComponent.stack.value.assertActiveInstance<AboutComponent.Child.Main>()
    }
})
