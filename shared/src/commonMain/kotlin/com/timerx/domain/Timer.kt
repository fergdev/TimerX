package com.timerx.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.timerx.sound.Beep
import com.timerx.vibration.Vibration
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal const val NO_SORT_ORDER = -1L

@Serializable
data class ShallowTimer(
    val id: Long = 0L,
    val sortOrder: Long = NO_SORT_ORDER,
    val name: String,
    val duration: Long = 1L,
    val startedCount: Long = 0,
    val completedCount: Long = 0,
    val createdAt: Instant = Instant.DISTANT_PAST,
    val lastRun: Instant? = null
) {
    init {
        require(startedCount >= 0) { "startedCount=$startedCount cannot be negative" }
        require(completedCount >= 0) { "completedCount=$completedCount cannot be negative" }
        require(duration > 0) { "duration=$duration must be greater than 0" }
    }
}

@Serializable
data class Timer(
    val id: Long = 0L,
    val sortOrder: Long = NO_SORT_ORDER,
    val name: String,
    val sets: List<TimerSet>,
    @Serializable(with = ColorSerializer::class)
    val finishColor: Color = Color.Red,
    val finishBeep: Beep = Beep.Alert,
    val finishVibration: Vibration = Vibration.Heavy,
    val duration: Long = sets.length(),
    val startedCount: Long = 0,
    val completedCount: Long = 0,
    val createdAt: Instant,
    val lastRun: Instant? = null
) {
    init {
        require(sets.isNotEmpty()) { "Timer must have at least one set" }
        require(startedCount >= 0) { "Started count cannot be negative startedCount=$startedCount" }
        require(completedCount >= 0) { "Completed count cannot be negative completedCount=$completedCount" }
    }
}

@Serializable
data class TimerSet(
    val id: Long = 0L,
    val repetitions: Int = 1,
    val intervals: List<TimerInterval>
) {
    init {
        require(intervals.isNotEmpty()) {
            "Timer set must have at least one interval"
        }
    }
}

@Serializable
data class TimerInterval(
    val id: Long = 0L,
    val name: String,
    val duration: Long,
    @Serializable(with = ColorSerializer::class)
    val color: Color = Color.Blue,
    val skipOnLastSet: Boolean = false,
    val countUp: Boolean = false,
    val manualNext: Boolean = false,
    val textToSpeech: Boolean = true,
    val beep: Beep = Beep.Alert,
    val vibration: Vibration = Vibration.Medium,
    val finalCountDown: FinalCountDown = FinalCountDown()
) {
    init {
        require(duration > 0) {
            "Duration must be greater than zero duration=$duration"
        }
    }
}

@Serializable
data class FinalCountDown(
    val duration: Long = 3,
    val beep: Beep = Beep.Alert,
    val vibration: Vibration = Vibration.Light
) {
    init {
        require(duration >= 0) {
            "Final count down duration cannot be negative $duration"
        }
    }
}

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Color", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeInt(value.toArgb())
    }

    override fun deserialize(decoder: Decoder) = Color(decoder.decodeInt())
}
