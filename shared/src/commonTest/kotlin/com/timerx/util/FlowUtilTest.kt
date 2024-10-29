package com.timerx.util

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flowOf

class FlowUtilTest : FreeSpec({
    "map if null" - {
        "should map null to default" {
            flowOf(null).mapIfNull("default")
                .collect {
                    it shouldBe "default"
                }
        }
        "should map not map set to default" {
            flowOf("not null default").mapIfNull("default")
                .collect {
                    it shouldBe "not null default"
                }
        }
    }
})
