package com.timerx.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.timerx.testutil.NotAndroidCondition
import com.timerx.vibration.Vibration
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")
@EnabledIf(NotAndroidCondition::class)
@OptIn(ExperimentalTestApi::class)
class VibrationSelectorTest : FreeSpec({
    "displays vibrations with in width creates others" {
        runComposeUiTest {
            setContent {
                VibrationSelector(
                    selected = Vibration.None,
                    onSelect = {}
                )
            }
            Vibration.entries.forEach {
                val vibrationNode = onNodeWithTag(it.name)
                if (it.shouldBeDisplayed()) {
                    vibrationNode.assertIsDisplayed()
                } else {
                    vibrationNode.assertIsNotDisplayed()
                        .assertExists()
                }
                if (it == Vibration.None) {
                    vibrationNode.assertIsSelected()
                } else {
                    vibrationNode.assertIsNotSelected()
                }
            }
        }
    }
    "on click invokes callback" {
        runComposeUiTest {
            setContent {
                VibrationSelector(
                    selected = Vibration.Soft,
                    onSelect = {
                        it shouldBe Vibration.SoftX2
                    }
                )
            }
            onNodeWithTag(Vibration.SoftX2.name)
                .performClick()
        }
    }
    "on click invokes callback with modifier" {
        runComposeUiTest {
            setContent {
                VibrationSelector(
                    modifier = Modifier.fillMaxSize(),
                    selected = Vibration.Soft,
                    onSelect = {
                        it shouldBe Vibration.SoftX2
                    }
                )
            }
            onNodeWithTag(Vibration.SoftX2.name)
                .performClick()
        }
    }
})

private val shouldBeDisplayed = Vibration.entries.takeWhile {
    it != Vibration.Heavy
}

private fun Vibration.shouldBeDisplayed() = this in shouldBeDisplayed
