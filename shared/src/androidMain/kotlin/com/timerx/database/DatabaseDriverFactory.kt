package com.timerx.database

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.timerx.domain.Timer
import kotlinx.collections.immutable.toPersistentList
import sqldelight.AppDatabase
import com.timerx.domain.Timer as TimerX
import com.timerx.domain.TimerSet as TimerXSet
import com.timerx.domain.TimerInterval as TimerXInterval

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context, "test.db")
    }
}

class TimerDatabase(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    fun getTimers(): List<TimerX> {
        return dbQuery.selectTimers(::mapTimer).executeAsList()
    }

    fun insertTimer(timer: TimerX) {
        dbQuery.transaction {
            dbQuery.insertTimer(timer.name)
            val timerRowId = dbQuery.lastInsertRowId().executeAsOne()

            insertTimerSets(timer.sets, timerRowId)
        }
    }

    private fun insertTimerSets(sets: List<TimerXSet>, timerId: Long) {
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

    private fun mapTimer(timerId: Long, name: String): TimerX {
        val setIds = dbQuery.selectTimerSet(timerId).executeAsList()
        val sets = setIds.map { dbQuery.selectSet(it).executeAsOne() }

        val timerXSets = sets.map { (setId, repetitions) ->
            val intervalIds = dbQuery.selectSetInterval(setId).executeAsList()
            val intervals =
                intervalIds
                    .map { intervalId -> dbQuery.selectInterval(intervalId).executeAsOne() }
                    .map { (intervalId, name, duration) ->
                        TimerXInterval(
                            id = intervalId,
                            name = name,
                            duration = duration
                        )
                    }

            TimerXSet(
                id = setId,
                repetitions = repetitions,
                intervals = intervals.toPersistentList()
            )
        }

        return TimerX(timerId, name, timerXSets.toPersistentList())
    }

    fun updateTimer(timer: Timer) {
        dbQuery.transaction {
            val oldTimer = getTimer(timer.id)
            dbQuery.deleteTimerSet(timer.id)
            deleteTimerSets(oldTimer)

            dbQuery.updateTimer(timer.name, timer.id)
            insertTimerSets(timer.sets, timer.id)
        }
    }

    private fun deleteTimerSets(timer: TimerX) {
        timer.sets.forEach { (id, _, intervals) ->
            intervals.forEach {
                dbQuery.deleteInterval(it.id)
            }

            dbQuery.deleteSetInterval(id)
            dbQuery.deleteSet(id)
        }
        dbQuery.deleteTimerSet(timer.id)
    }

    fun deleteTimer(timer: Timer) {
        dbQuery.transaction {
            deleteTimerSets(timer)
            dbQuery.deleteTimer(timer.id)
        }
    }

    fun duplicate(timer: Timer) {
        insertTimer(timer)
    }

    fun getTimer(timerId: Long): TimerX {
        return dbQuery.selectTimer(timerId, ::mapTimer).executeAsOne()
    }
}