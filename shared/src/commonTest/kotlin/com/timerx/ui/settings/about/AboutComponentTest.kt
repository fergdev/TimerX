package com.timerx.ui.settings.about

import com.timerx.ui.settings.about.main.AboutMainComponentFake
import com.timerx.testutil.assertActiveInstance
import com.timerx.testutil.createComponent
import io.kotest.core.spec.style.FreeSpec

class AboutComponentTest : FreeSpec({
    "init sets sets active child to main" {
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
