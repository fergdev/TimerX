package com.timerx.ui.settings.about.main

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.runComposeUiTest
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.timerx.BuildFlags
import com.timerx.testutil.NotAndroidCondition
import com.timerx.testutil.asUnconfined
import com.timerx.testutil.setContentWithLocals
import com.timerx.ui.settings.about.aboutlibs.AboutLibsComponent
import com.timerx.ui.settings.about.changelog.ChangeLogComponent
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")
@OptIn(ExperimentalTestApi::class)
@EnabledIf(NotAndroidCondition::class)
class AboutMainContentTest : FreeSpec({
    asUnconfined()
    val aboutMainComponent = mock<AboutMainComponent> {
        every { onBack } returns {}
        every { state } returns MutableValue(AboutMainState.AnalyticsNotSupported())
        every { aboutLibsSlot } returns MutableValue(ChildSlot<Any, AboutLibsComponent>(child = null))
        every { changeLogSlot } returns MutableValue(ChildSlot<Any, ChangeLogComponent>(child = null))
        every { contactSupport() } returns Unit
        every { onAboutLibsClicked() } returns Unit
    }

    "displays version information" {
        runComposeUiTest {
            setContentWithLocals { AboutMainContent(aboutMainComponent) }
            onNodeWithText("TimerX v${BuildFlags.versionName}").assertIsDisplayed()
        }
    }

    "developer message displayed" {
        runComposeUiTest {
            setContentWithLocals { AboutMainContent(aboutMainComponent) }
            onNodeWithText(text = "Hey", substring = true).assertIsDisplayed()
        }
    }

    "privacy policy invokes uri handles" {
        val uriHandler = mock<UriHandler> {
            every { openUri(any()) } returns Unit
        }
        runComposeUiTest {
            setContentWithLocals {
                CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                    AboutMainContent(aboutMainComponent)
                }
            }
            onNodeWithTag(PRIVACY_POLICY_TEST_TAG)
                .performClick()
            verify { uriHandler.openUri(BuildFlags.privacyPolicyUrl) }
        }
    }

    "analytics" - {
        "not supported does not display card" {
            runComposeUiTest {
                setContentWithLocals { AboutMainContent(aboutMainComponent) }
                onNodeWithTag(COLLECT_ANALYTICS_TEST_TAG).assertDoesNotExist()
            }
        }

        "supported" - {
            "displays card" {
                every { aboutMainComponent.state } returns MutableValue(
                    AboutMainState.AnalyticsSupported(
                        collectAnalyticsEnable = false,
                        updateCollectAnalytics = {}
                    )
                )
                runComposeUiTest {
                    setContentWithLocals { AboutMainContent(aboutMainComponent) }
                    onNodeWithTag(COLLECT_ANALYTICS_TEST_TAG)
                        .performScrollTo()
                        .assertIsDisplayed()
                }
            }

            "click invokes callback" {
                var invoked = false
                every { aboutMainComponent.state } returns MutableValue(
                    AboutMainState.AnalyticsSupported(
                        collectAnalyticsEnable = false,
                        updateCollectAnalytics = { invoked = true }
                    )
                )
                runComposeUiTest {
                    setContentWithLocals { AboutMainContent(aboutMainComponent) }
                    onNodeWithTag(COLLECT_ANALYTICS_TEST_TAG)
                        .performScrollTo()
                        .performClick()
                    invoked shouldBe true
                }
            }
        }

        "about libs invokes component" {
            runComposeUiTest {
                setContentWithLocals { AboutMainContent(aboutMainComponent) }
                onNodeWithTag(ABOUT_LIBS_TEST_TAG)
                    .performScrollTo()
                    .performClick()
                verify { aboutMainComponent.onAboutLibsClicked() }
            }
        }

        "contact support invokes component" {
            runComposeUiTest {
                setContentWithLocals { AboutMainContent(aboutMainComponent) }
                waitForIdle()
                onNodeWithTag(CONTACT_SUPPORT_TEST_TAG)
                    .performScrollTo()
                    .assertIsDisplayed()
                    .performClick()
                verify { aboutMainComponent.contactSupport() }
            }
        }
    }
    "about libs" - {
        "is does not exist when slots is empty" {
            runComposeUiTest {
                setContentWithLocals { AboutMainContent(aboutMainComponent) }
                waitForIdle()
                onNodeWithTag(ABOUT_LIBS_SHEET_TEST_TAG).assertDoesNotExist()
            }
        }
        "is displayed when slot is set" {
            runComposeUiTest {
                every { aboutMainComponent.aboutLibsSlot } returns MutableValue(
                    ChildSlot<Any, AboutLibsComponent>(
                        child = Child.Created(AboutLibsComponent, AboutLibsComponent)
                    )
                )
                setContentWithLocals { AboutMainContent(aboutMainComponent) }
                waitForIdle()
                onNodeWithTag(ABOUT_LIBS_SHEET_TEST_TAG).assertIsDisplayed()
            }
        }
    }
    "change log" - {
        "is does not exist when slots is empty" {
            runComposeUiTest {
                setContentWithLocals {
                    AboutMainContent(aboutMainComponent)
                }
                waitForIdle()
                onNodeWithTag(CHANGE_LOG_SHEET_TEST_TAG).assertDoesNotExist()
            }
        }
        "is displayed when slot is set" {
            runComposeUiTest {
                every { aboutMainComponent.changeLogSlot } returns MutableValue(
                    ChildSlot<Any, ChangeLogComponent>(
                        child = Child.Created(AboutLibsComponent, ChangeLogComponent)
                    )
                )
                setContentWithLocals { AboutMainContent(aboutMainComponent) }
                waitForIdle()
                onNodeWithTag(CHANGE_LOG_SHEET_TEST_TAG).assertExists()
            }
        }
    }
})
