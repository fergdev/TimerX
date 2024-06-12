package com.timerx.settings

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class TimerXSettingsTest {

    private lateinit var timerXSettings: TimerXSettings
    private val ISettingsManager: ISettingsManager = mock()

    @BeforeTest
    fun setup() {
        timerXSettings = TimerXSettings(ISettingsManager)
    }

    @Test
    fun `get volume - returns settings manager value`() {
        // given + then
        every { ISettingsManager.getFloat("volume", 1F) }
            .returns(0.5f)

        // when
        assertEquals(0.5f, timerXSettings.volume)
    }

    @Test
    fun `set volume - sets value in settings manager`() {
        // given
        every { ISettingsManager.putFloat("volume", 0.5f) }
            .returns(Unit)

        // when
        timerXSettings.volume = 0.5f

        // then
        verify { ISettingsManager.putFloat("volume", 0.5F) }
    }
}