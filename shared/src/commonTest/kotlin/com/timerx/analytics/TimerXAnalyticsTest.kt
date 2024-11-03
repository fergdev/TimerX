package com.timerx.analytics

import com.timerx.coroutines.TxDispatchers
import com.timerx.settings.AnalyticsSettings
import com.timerx.settings.TimerXSettings
import com.timerx.testutil.idleAll
import com.timerx.testutil.testDispatchers
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flowOf

private class TestTimerXAnalytics(
    timerXSettings: TimerXSettings,
    txDispatchers: TxDispatchers
) : TimerXAnalytics(timerXSettings, txDispatchers) {

    val logEvents = mutableListOf<Pair<String, Map<String, Any>>>()

    override fun doLogEvent(eventName: String, params: Map<String, Any>) {
        logEvents.add(Pair(eventName, params))
    }
}

class TimerXAnalyticsTest : FreeSpec({
    val timerXSettings = mock<TimerXSettings>()
    val testDispatchers = testDispatchers()
    val factory = { TestTimerXAnalytics(timerXSettings, testDispatchers) }
    "default logs analytics" - {
        "no data" {
            val analytics = factory()
            analytics.logEvent("test")
            analytics.logEvents shouldBe listOf(Pair("test", mapOf()))
        }
        "with data" {
            val analytics = factory()
            analytics.logEvent("test", mapOf("a" to "b"))
            analytics.logEvents shouldBe listOf(Pair("test", mapOf("a" to "b")))
        }
    }
    "analytics off does not log" - {
        "no data" {
            every { timerXSettings.analytics } returns flowOf(AnalyticsSettings.NotAvailable)
            val analytics = factory()
            testDispatchers.idleAll()
            analytics.logEvent("test")
            analytics.logEvents shouldBe listOf()
        }
        "with data" {
            val analytics = factory()
            testDispatchers.idleAll()
            analytics.logEvent("test", mapOf("a" to "b"))
            analytics.logEvents shouldBe listOf()
        }
    }
})
