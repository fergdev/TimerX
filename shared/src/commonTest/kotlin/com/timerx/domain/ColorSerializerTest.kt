package com.timerx.domain

import androidx.compose.ui.graphics.Color
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.json.Json

class ColorSerializerTest : FreeSpec({
    "descriptor" {
        ColorSerializer.descriptor shouldBe
            PrimitiveSerialDescriptor("Color", PrimitiveKind.INT)
    }
    "serialize" {
        Json.encodeToString(ColorSerializer, Color.Blue) shouldBe "-16776961"
    }
    "deserialize" {
        Json.decodeFromString(ColorSerializer, "-16776961") shouldBe Color.Blue
    }
})
