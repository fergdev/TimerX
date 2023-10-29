package com.timerx.database

import com.timerx.domain.Timer
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import kotlinx.collections.immutable.toPersistentList
import sqldelight.AppDatabase

interface ITimerDatabase {
    fun getTimers(): List<Timer>
    fun insertTimer(timer: Timer)
    fun updateTimer(timer: Timer)
    fun deleteTimer(timer: Timer)
    fun duplicate(timer: Timer)
    fun getTimer(timerId: Long): Timer
}

class TimerDatabase(databaseDriverFactory: DatabaseDriverFactory) : ITimerDatabase {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    override fun getTimers(): List<Timer> {
        return dbQuery.selectTimers(::mapTimer).executeAsList()
    }

    override fun insertTimer(timer: Timer) {
        dbQuery.transaction {
            dbQuery.insertTimer(timer.name)
            val timerRowId = dbQuery.lastInsertRowId().executeAsOne()

            insertTimerSets(timer.sets, timerRowId)
        }
    }

    private fun insertTimerSets(sets: List<TimerSet>, timerId: Long) {
        sets.forEach { (_, repetitions, intervals) ->

            dbQuery.insertSet(repetitions)
            val setRowId = dbQuery.lastInsertRowId().executeAsOne()
            dbQuery.insertTimerSet(timerId, setRowId)

            intervals.forEach {
                dbQuery.insertInterval(it.name, it.duration)
                val intervalRowId = dbQuery.lastInsertRowId().executeAsOne()
                dbQuery.insertSetInterval(setRowId, intervalRowId)
            }
        }
    }

    private fun mapTimer(timerId: Long, name: String): Timer {
        val setIds = dbQuery.selectTimerSet(timerId).executeAsList()
        val sets = setIds.map { dbQuery.selectSet(it).executeAsOne() }

        val timerXSets = sets.map { (setId, repetitions) ->
            val intervalIds = dbQuery.selectSetInterval(setId).executeAsList()
            val intervals =
                intervalIds
                    .map { intervalId -> dbQuery.selectInterval(intervalId).executeAsOne() }
                    .map { (intervalId, name, duration) ->
                        TimerInterval(
                            id = intervalId,
                            name = name,
                            duration = duration
                        )
                    }

            TimerSet(
                id = setId,
                repetitions = repetitions,
                intervals = intervals.toPersistentList()
            )
        }

        return Timer(timerId, name, timerXSets.toPersistentList())
    }

    override fun updateTimer(timer: Timer) {
        dbQuery.transaction {
            val oldTimer = getTimer(timer.id)
            dbQuery.deleteTimerSet(timer.id)
            deleteTimerSets(oldTimer)

            dbQuery.updateTimer(timer.name, timer.id)
            insertTimerSets(timer.sets, timer.id)
        }
    }

    private fun deleteTimerSets(timer: Timer) {
        timer.sets.forEach { (id, _, intervals) ->
            intervals.forEach {
                dbQuery.deleteInterval(it.id)
            }

            dbQuery.deleteSetInterval(id)
            dbQuery.deleteSet(id)
        }
        dbQuery.deleteTimerSet(timer.id)
    }

    override fun deleteTimer(timer: Timer) {
        dbQuery.transaction {
            deleteTimerSets(timer)
            dbQuery.deleteTimer(timer.id)
        }
    }

    override fun duplicate(timer: Timer) {
        insertTimer(timer)
    }

    override fun getTimer(timerId: Long): Timer {
        return dbQuery.selectTimer(timerId, ::mapTimer).executeAsOne()
    }
}
