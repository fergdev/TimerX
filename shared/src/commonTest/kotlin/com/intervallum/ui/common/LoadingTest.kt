package com.intervallum.ui.common

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.intervallum.testutil.disableForAndroid
import io.kotest.core.spec.style.FreeSpec

@OptIn(ExperimentalTestApi::class)
class LoadingTest : FreeSpec({
    "is displayed".config(enabledIf = disableForAndroid) {
        runComposeUiTest {
            setContent {
                DefaultLoading()
            }
            onNodeWithTag("loading").assertIsDisplayed()
        }
    }
})
