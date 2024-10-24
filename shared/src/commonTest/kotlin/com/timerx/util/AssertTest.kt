package com.timerx.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlin.test.assertTrue

class AssertTest : FreeSpec({
    "predicate" - {
        "does not throw exception" {
            assert(true) { "this should not throw" }
            assertTrue { true }
        }
        "does throw exception" {
            val errorMessage = "this should throw"
            shouldThrow<IllegalStateException> {
                assert(false) { errorMessage }
            }.apply {
                message shouldBe errorMessage
            }
        }
    }
    "not null" - {
        "does not throw exception" {
            assertNotNull(Unit) { "this should not throw" }
            assertTrue { true }
        }
        "does throw exception" {
            val errorMessage = "this should throw"
            shouldThrow<IllegalStateException> {
                assertNotNull(null) { errorMessage }
            }.apply {
                message shouldBe errorMessage
            }
        }
    }
})
