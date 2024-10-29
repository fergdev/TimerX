package com.timerx.ui.splash

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.timerx.util.NotAndroidCondition
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.style.FreeSpec

@OptIn(ExperimentalTestApi::class)
@Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")
@EnabledIf(NotAndroidCondition::class)
class SplashContentTest : FreeSpec({
    val splashComponent = mock<SplashComponent> {
        every { finishSplash } returns {}
    }
    "is displayed" {
        runComposeUiTest {
            setContent {
                SplashContent(splashComponent)
            }
            onNodeWithTag("splash_image").assertIsDisplayed()
        }
    }
    "invokes callback when finished" {
        runComposeUiTest {
            setContent {
                SplashContent(splashComponent)
            }
        }
        verify { splashComponent.finishSplash }
    }
})
