package com.timerx.platform

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformTest {

    private lateinit var platform: Platform

    @BeforeTest
    fun setup() {
        platform = Platform()
    }

    @Test
    fun `platform name - returns correct name`() {
        assertEquals("iOS 17.5", platform.name)
    }
}