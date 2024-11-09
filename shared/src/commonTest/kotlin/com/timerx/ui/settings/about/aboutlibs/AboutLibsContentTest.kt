package com.timerx.ui.settings.about.aboutlibs

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.timerx.analytics.TimerXAnalytics
import com.timerx.testutil.DisabledCondition
import com.timerx.testutil.setContentWithLocals
import com.timerx.ui.logging.LocalTimerXAnalytics
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.resetAnswers
import dev.mokkery.verify
import dev.mokkery.verifyNoMoreCalls
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.runBlocking
import timerx.shared.generated.resources.Res

private const val TAG_ANDROIDX_ACTIVITY = "androidx.activity:activity"
private const val NAME_ANDROIDX_ACTIVITY = "Activity"
private const val VERSION_ANDROIDX_ACTIVITY = "1.10.0-alpha03"
private const val AUTHOR_ANDROIDX_ACTIVITY = "The Android Open Source Project"

private const val TAG_ANDROIDX_COMPOSE = "androidx.activity:activity-compose"
private const val NAME_ANDROIDX_COMPOSE = "Activity Compose"
private const val VERSION_ANDROIDX_COMPOSE = "1.10.0-alpha02"
private const val AUTHOR_ANDROIDX_COMPOSE = "The Android Open Source Project 2"

private const val TAG_REORDERABLE = "sh.calvin.reorderable:reorderable-android-debug"
private const val NAME_REORDERABLE = "Reorderable"
private const val VERSION_REORDERABLE = "2.3.2"
private const val AUTHOR_REORDERABLE = "Calvin Liang"

private const val ASKDL_LICENSE_URL = "https://developer.android.com/studio/terms.html"

@Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")
@EnabledIf(DisabledCondition::class)
@OptIn(ExperimentalTestApi::class)
class AboutLibsContentTest : FreeSpec({
    val data = runBlocking { Res.readBytes(FILES_ABOUT_LIBRARIES_JSON).decodeToString() }
    val libsDataProvider = object : LibsLoader {
        override suspend fun load() = data
    }
    val emptyDataProvider = object : LibsLoader {
        override suspend fun load() = ""
    }

    val timerXAnalytics = mock<TimerXAnalytics> { }
    beforeTest { every { timerXAnalytics.logScreen(any()) } returns Unit }
    afterTest { resetAnswers(timerXAnalytics) }
    "logs screen view" {
        runComposeUiTest {
            setContent {
                CompositionLocalProvider(LocalTimerXAnalytics provides timerXAnalytics) {
                    AboutLibsContent(libsLoader = libsDataProvider)
                }
            }
        }
    }
    "renders all 3 libraries when empty" {
        runComposeUiTest {
            setContentWithLocals { AboutLibsContent(emptyDataProvider) }
            waitForIdle()
            onNodeWithTag(TAG_ANDROIDX_ACTIVITY).assertDoesNotExist()
            onNodeWithText(NAME_ANDROIDX_ACTIVITY).assertDoesNotExist()
            onNodeWithText(VERSION_ANDROIDX_ACTIVITY).assertDoesNotExist()
            onNodeWithText(AUTHOR_ANDROIDX_ACTIVITY).assertDoesNotExist()

            onNodeWithTag(TAG_ANDROIDX_COMPOSE).assertDoesNotExist()
            onNodeWithText(NAME_ANDROIDX_COMPOSE).assertDoesNotExist()
            onNodeWithText(VERSION_ANDROIDX_COMPOSE).assertDoesNotExist()
            onNodeWithText(AUTHOR_ANDROIDX_COMPOSE).assertDoesNotExist()

            onNodeWithTag(TAG_REORDERABLE).assertDoesNotExist()
            onNodeWithText(NAME_REORDERABLE).assertDoesNotExist()
            onNodeWithText(VERSION_REORDERABLE).assertDoesNotExist()
            onNodeWithText(AUTHOR_REORDERABLE).assertDoesNotExist()
        }
    }
    "renders all 3 libraries" {
        runComposeUiTest {
            setContentWithLocals { AboutLibsContent(libsDataProvider) }
            waitForIdle()
            onNodeWithTag(TAG_ANDROIDX_ACTIVITY).assertIsDisplayed()
            onNodeWithText(NAME_ANDROIDX_ACTIVITY).assertIsDisplayed()
            onNodeWithText(VERSION_ANDROIDX_ACTIVITY).assertIsDisplayed()
            onNodeWithText(AUTHOR_ANDROIDX_ACTIVITY).assertIsDisplayed()

            onNodeWithTag(TAG_ANDROIDX_COMPOSE).assertIsDisplayed()
            onNodeWithText(NAME_ANDROIDX_COMPOSE).assertIsDisplayed()
            onNodeWithText(VERSION_ANDROIDX_COMPOSE).assertIsDisplayed()
            onNodeWithText(AUTHOR_ANDROIDX_COMPOSE).assertIsDisplayed()

            onNodeWithTag(TAG_REORDERABLE).assertIsDisplayed()
            onNodeWithText(NAME_REORDERABLE).assertIsDisplayed()
            onNodeWithText(VERSION_REORDERABLE).assertDoesNotExist()
            onNodeWithText(AUTHOR_REORDERABLE).assertDoesNotExist()
        }
    }
    "on click invokes uri handler" {
        val uriHandler = mock<UriHandler> {
            every { openUri(any()) } returns Unit
        }
        runComposeUiTest {
            setContentWithLocals {
                CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                    AboutLibsContent(libsDataProvider)
                }
            }
            waitForIdle()
            onNodeWithTag(TAG_ANDROIDX_ACTIVITY).performClick()
            verify { uriHandler.openUri(ASKDL_LICENSE_URL) }
            onNodeWithTag(TAG_ANDROIDX_COMPOSE).performClick()
            verifyNoMoreCalls(uriHandler)
            onNodeWithTag(TAG_REORDERABLE).performClick()
            verifyNoMoreCalls(uriHandler)
        }
    }
    "on click handles uri error" {
        val illegalArgumentException = IllegalArgumentException("error")
        val uriHandler = mock<UriHandler> {
            every { openUri(any()) } throws illegalArgumentException
        }
        every { timerXAnalytics.logException(any()) } returns Unit
        runComposeUiTest {
            setContent {
                CompositionLocalProvider(LocalTimerXAnalytics provides timerXAnalytics) {
                    CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                        AboutLibsContent(libsDataProvider)
                    }
                }
            }
            waitForIdle()
            onNodeWithTag(TAG_ANDROIDX_ACTIVITY).performClick()
            verify { timerXAnalytics.logException(illegalArgumentException) }
        }
    }
})
