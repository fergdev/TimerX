package com.timerx.database

import com.timerx.domain.Timer
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

interface ITimerRepository {
    fun getTimers(): List<Timer>
    fun insertTimer(timer: Timer)
    fun updateTimer(timer: Timer)
    fun deleteTimer(timer: Timer)
    fun duplicate(timer: Timer)
    fun getTimer(timerId: Long): Timer
}

private class RealmTimer() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = ""
    var sets: List<RealmSet> = emptyList()
}

private class RealmSet() : RealmObject {
    var id: ObjectId = ObjectId()
    var repetitions: Long = 1
    var intervals: List<RealmInterval> = emptyList()
}

private class RealmInterval() : RealmObject {
    var id: ObjectId = ObjectId()
    var name: String = ""
    var duration: Long = 1
}

data class TimerInterval(
    val id: Long = -1,
    val name: String,
    val duration: Long
)

class TimerRepo : ITimerRepository {
    private val timerList: MutableList<Timer> = mutableListOf()
    override fun getTimers(): List<Timer> {
        return timerList
    }

    override fun insertTimer(timer: Timer) {
        timerList.add(timer)
    }

    override fun updateTimer(timer: Timer) {

    }

    override fun deleteTimer(timer: Timer) {
    }

    override fun duplicate(timer: Timer) {
    }

    override fun getTimer(timerId: Long): Timer {
        return timerList.first { it.id == timerId }
    }

}
