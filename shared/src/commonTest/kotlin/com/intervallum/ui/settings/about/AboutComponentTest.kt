package com.intervallum.ui.settings.about

import com.intervallum.testutil.assertActiveInstance
import com.intervallum.testutil.createComponent
import com.intervallum.ui.settings.about.main.AboutMainComponentFake
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
