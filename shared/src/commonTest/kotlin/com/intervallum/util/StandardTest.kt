package com.intervallum.util

import androidx.compose.ui.text.AnnotatedString
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class StandardTest : FreeSpec({
    "let type" - {
        "lets if the type is same type" {
            "asdf".letType<String, _> {
                this shouldBe "asdf"
            }
        }
        "lets if the type is subtype" {
            "asdf".letType<CharSequence, _> {
                this shouldBe "asdf"
            }
        }
        "does not let if the type is wrong" {
            "asdf".letType<AnnotatedString, _> {
                true shouldBe false
            }
        }
    }
})
