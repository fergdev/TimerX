package com.timerx.domain

import com.timerx.ui.common.CustomIcons

enum class SortTimersBy {
    SORT_ORDER {
        override fun sort(timers: List<ShallowTimer>) = timers.sortedBy { it.sortOrder }
    },
    RUN_DATE_ASC {
        override fun sort(timers: List<ShallowTimer>) =
            timers.sortedBy { it.lastRun?.toEpochMilliseconds() ?: 0 }
    },
    RUN_DATE_DESC {
        override fun sort(timers: List<ShallowTimer>) =
            timers.sortedByDescending { it.lastRun?.toEpochMilliseconds() ?: 0 }
    },
    NAME_ASC {
        override fun sort(timers: List<ShallowTimer>) = timers.sortedBy { it.name }
    },
    NAME_DESC {
        override fun sort(timers: List<ShallowTimer>) = timers.sortedByDescending { it.name }
    },
    LENGTH_DESC {
        override fun sort(timers: List<ShallowTimer>) =
            timers.sortedByDescending { it.duration }
    },
    LENGTH_ASC {
        override fun sort(timers: List<ShallowTimer>) =
            timers.sortedBy { it.duration }
    };

    abstract fun sort(timers: List<ShallowTimer>): List<ShallowTimer>
}

internal fun SortTimersBy.imageVector() =
    when (this) {
        SortTimersBy.SORT_ORDER -> CustomIcons.sortOrder
        SortTimersBy.RUN_DATE_ASC -> CustomIcons.calendarPlus
        SortTimersBy.RUN_DATE_DESC -> CustomIcons.calendarMinus
        SortTimersBy.NAME_ASC -> CustomIcons.sortAlphaDown
        SortTimersBy.NAME_DESC -> CustomIcons.sortAlphaUpAlt
        SortTimersBy.LENGTH_ASC -> CustomIcons.sortNumericUp
        SortTimersBy.LENGTH_DESC -> CustomIcons.sortNumericDownAlt
    }

internal fun SortTimersBy.next(): SortTimersBy {
    if (this.ordinal == SortTimersBy.entries.size - 1) {
        return SortTimersBy.entries[0]
    }
    return SortTimersBy.entries[this.ordinal + 1]
}
