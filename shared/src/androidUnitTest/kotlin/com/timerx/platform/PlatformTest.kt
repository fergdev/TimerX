package com.timerx.platform

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import kotlin.test.Test

class PlatformTest {

    private lateinit var platform: Platform

    @Before
    fun setup(){
        platform = Platform()
    }

    @Test
    fun `platform name - returns correct name`(){
        assertThat(platform.name, `is`("Android 0"))
    }
}