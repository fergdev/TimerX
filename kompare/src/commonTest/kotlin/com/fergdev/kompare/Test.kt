package com.fergdev.kompare

import androidx.compose.material3.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import io.kotest.core.spec.style.FreeSpec

@OptIn(ExperimentalTestApi::class)
class Test : FreeSpec({
    "wowo" {
        runComposeUiTest {
            setContent {
                Text("wowowow")
                Text("wowowow 2")
            }
//            kompare()
        }
    }
})
