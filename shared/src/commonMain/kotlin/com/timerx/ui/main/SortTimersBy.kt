package com.timerx.ui.main

import androidx.compose.ui.graphics.vector.ImageVector
import com.timerx.database.RoomTimer
import com.timerx.ui.common.CustomIcons

enum class SortTimersBy {
    SORT_ORDER {
        override fun sort(timers: List<RoomTimer>): List<RoomTimer> {
            return timers.sortedBy { it.sortOrder }
        }
    },
    RUN_DATE_ASC {
        override fun sort(timers: List<RoomTimer>): List<RoomTimer> {
            return timers.sortedBy { it.lastRun?.toEpochMilliseconds() ?: 0 }
        }
    },
    RUN_DATE_DESC {
        override fun sort(timers: List<RoomTimer>): List<RoomTimer> {
            return timers.sortedByDescending { it.lastRun?.toEpochMilliseconds() ?: 0 }
        }
    },
    NAME_ASC {
        override fun sort(timers: List<RoomTimer>): List<RoomTimer> {
            return timers.sortedBy { it.name }
        }
    },
    NAME_DESC {
        override fun sort(timers: List<RoomTimer>): List<RoomTimer> {
            return timers.sortedByDescending { it.name }
        }
    },
    LENGTH_DESC {
        override fun sort(timers: List<RoomTimer>): List<RoomTimer> {
            return timers.sortedByDescending { it.duration }
        }
    },
    LENGTH_ASC {
        override fun sort(timers: List<RoomTimer>): List<RoomTimer> {
            return timers.sortedBy { it.duration }
        }
    };

    abstract fun sort(timers: List<RoomTimer>): List<RoomTimer>

}

internal fun SortTimersBy.imageVector(): ImageVector {
    return when (this) {
        SortTimersBy.SORT_ORDER -> CustomIcons.sortOrder
        SortTimersBy.RUN_DATE_ASC -> CustomIcons.calendarPlus
        SortTimersBy.RUN_DATE_DESC -> CustomIcons.calendarMinus
        SortTimersBy.NAME_ASC -> CustomIcons.sortAlphaDown
        SortTimersBy.NAME_DESC -> CustomIcons.sortAlphaUpAlt
        SortTimersBy.LENGTH_ASC -> CustomIcons.sortNumericUp
        SortTimersBy.LENGTH_DESC -> CustomIcons.sortNumericDownAlt
    }
}

internal fun SortTimersBy.next(): SortTimersBy {
    if (this.ordinal == SortTimersBy.entries.size - 1) {
        return SortTimersBy.entries[0]
    }
    return SortTimersBy.entries[this.ordinal + 1]
}

fun Int.toSortTimersBy(): SortTimersBy {
    if (this in 0 until SortTimersBy.entries.size) return SortTimersBy.entries[this]
    return SortTimersBy.entries[0]
}
