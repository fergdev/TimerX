package com.timerx.ui.main

import androidx.compose.ui.graphics.vector.ImageVector
import com.timerx.ui.common.CustomIcons

enum class SortTimersBy {
    SORT_ORDER {
        override fun sort(timers: List<TimerInfo>): List<TimerInfo> {
            return timers.sortedBy { it.sortOrder }
        }
    },
    RUN_DATE_ASC {
        override fun sort(timers: List<TimerInfo>): List<TimerInfo> {
            return timers.sortedBy { it.lastRunMillis }
        }
    },
    RUN_DATE_DESC {
        override fun sort(timers: List<TimerInfo>): List<TimerInfo> {
            return timers.sortedByDescending { it.lastRunMillis }
        }
    },
    NAME_ASC {
        override fun sort(timers: List<TimerInfo>): List<TimerInfo> {
            return timers.sortedBy { it.name }
        }
    },
    NAME_DESC {
        override fun sort(timers: List<TimerInfo>): List<TimerInfo> {
            return timers.sortedByDescending { it.name }
        }
    },
    LENGTH_DESC {
        override fun sort(timers: List<TimerInfo>): List<TimerInfo> {
            return timers.sortedByDescending { it.duration }
        }
    },
    LENGTH_ASC {
        override fun sort(timers: List<TimerInfo>): List<TimerInfo> {
            return timers.sortedBy { it.duration }
        }
    };

    abstract fun sort(timers: List<TimerInfo>): List<TimerInfo>

}

internal fun SortTimersBy.imageVector(): ImageVector {
    return when (this) {
        SortTimersBy.SORT_ORDER -> CustomIcons.sortOrder
        SortTimersBy.RUN_DATE_DESC -> CustomIcons.calendarMinus
        SortTimersBy.RUN_DATE_ASC -> CustomIcons.calendarPlus
        SortTimersBy.NAME_DESC -> CustomIcons.sortAlphaUpAlt
        SortTimersBy.NAME_ASC -> CustomIcons.sortAlphaDown
        SortTimersBy.LENGTH_DESC -> CustomIcons.sortNumericDownAlt
        SortTimersBy.LENGTH_ASC -> CustomIcons.sortNumericUpAlt
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
