package com.timerx.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.timerx.beep.Beep
import com.timerx.vibration.Vibration
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal const val NO_SORT_ORDER = -1L

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
)

@Serializable
data class TimerSet(
    val id: Long = 0L,
    val repetitions: Int = 1,
    @Serializable(with = MyPersistentListSerializer::class)
    val intervals: PersistentList<TimerInterval>
)

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
    val beep: Beep = Beep.Alert,
    val vibration: Vibration = Vibration.Medium,
    val finalCountDown: FinalCountDown = FinalCountDown()
)

@Serializable
data class FinalCountDown(
    val duration: Long = 3,
    val beep: Beep = Beep.Alert,
    val vibration: Vibration = Vibration.Light
)

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Color", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeInt(value.toArgb())
    }

    override fun deserialize(decoder: Decoder) = Color(decoder.decodeInt())
}

@OptIn(ExperimentalSerializationApi::class)
class MyPersistentListSerializer(
    private val serializer: KSerializer<TimerInterval>,
) : KSerializer<PersistentList<TimerInterval>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<TimerInterval>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<TimerInterval>) =
        ListSerializer(serializer).serialize(encoder, value)

    override fun deserialize(decoder: Decoder): PersistentList<TimerInterval> =
        ListSerializer(serializer).deserialize(decoder).toPersistentList()
}


fun Long.timeFormatted(): String {
    val hours = this / SECONDS_IN_HOUR
    val hoursString = if (hours == 0L) {
        ""
    } else {
        "$hours"
    }

    val minutes = this / MINUTES_IN_HOUR - hours * MINUTES_IN_HOUR
    val minutesString = if (minutes == 0L) {
        "00"
    } else if (minutes < SINGLE_DIGIT_MODULO) {
        "0$minutes"
    } else {
        "$minutes"
    }

    val seconds = this % SECONDS_IN_MINUTE
    val secondsString = if (seconds == 0L) {
        "00"
    } else if (seconds < SINGLE_DIGIT_MODULO) {
        "0$seconds"
    } else {
        "$seconds"
    }
    return if (hours == 0L) "$minutesString:$secondsString"
    else "$hoursString:$minutesString:$secondsString"
}

private const val SECONDS_IN_MINUTE = 60L
private const val MINUTES_IN_HOUR = 60L
private const val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR
private const val SINGLE_DIGIT_MODULO = 10L

fun Timer.length() = sets.fold(0L) { acc, i ->
    acc + i.length()
}

fun List<TimerSet>.length() =
    fold(0L) { acc, i ->
        acc + i.length()
    }

fun TimerSet.length() =
    intervals.fold(0L) { acc, i ->
        acc + i.length()
    } * repetitions

fun TimerInterval.length() =
    duration
