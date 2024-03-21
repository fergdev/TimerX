package com.timerx.database

import com.timerx.domain.Timer
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import kotlinx.collections.immutable.toPersistentList
import org.mongodb.kbson.ObjectId

interface ITimerRepository {
    fun getTimers(): List<Timer>
    fun insertTimer(timer: Timer)
    fun updateTimer(timer: Timer)
    fun deleteTimer(timer: Timer)
    fun duplicate(timer: Timer)
    fun getTimer(timerId: String): Timer
}

private class RealmTimer() : RealmObject {
    var _id: ObjectId = ObjectId()
    var name: String = ""
    var sets: RealmList<RealmSet> = realmListOf()
}

private class RealmSet() : RealmObject {
    var _id: ObjectId = ObjectId()
    var repetitions: Long = 1
    var intervals: RealmList<RealmInterval> = realmListOf()
}

private class RealmInterval() : RealmObject {
    var _id: ObjectId = ObjectId()
    var name: String = ""
    var duration: Long = 1
}

class TimerRepo : ITimerRepository {
    private val configuration = RealmConfiguration.create(
        schema = setOf(
            RealmTimer::class, RealmSet::class, RealmInterval::class
        )
    )

    private val realm = Realm.open(configuration)

    override fun getTimers(): List<Timer> {
        return realm.query<RealmTimer>().find().map(::realmTimerToTimer)
    }

    private fun realmTimerToTimer(realmTimer: RealmTimer): Timer {
        return Timer(
            realmTimer._id.toHexString(), realmTimer.name, realmTimer.sets.map { realmSet ->
                TimerSet(realmSet._id.toHexString(), realmSet.repetitions, realmSet.intervals.map {
                    TimerInterval(it._id.toHexString(), it.name, it.duration)
                }.toPersistentList())
            }.toPersistentList()
        )
    }

    override fun insertTimer(timer: Timer) {
        val realmTimer = RealmTimer().apply {
            name = timer.name
            sets = timer.sets.map {
                RealmSet().apply {
                    this.repetitions = it.repetitions
                    this.intervals = it.intervals.map {
                        RealmInterval().apply {
                            this.duration = it.duration
                            this.name = it.name
                        }
                    }.toRealmList()
                }
            }.toRealmList()
        }
        realm.writeBlocking {
            copyToRealm(realmTimer)
        }
    }

    override fun updateTimer(timer: Timer) {
        deleteTimer(timer)
        insertTimer(timer)
    }

    override fun deleteTimer(timer: Timer) {
        val realmTimer = getRealmTimer(timer.id)
        realm.writeBlocking {
            this.findLatest(realmTimer)?.let {
                this.delete(it)
            }
        }
    }

    override fun duplicate(timer: Timer) {
        insertTimer(timer.copy(name = timer.name + "blah"))
    }

    override fun getTimer(timerId: String): Timer {
        return realmTimerToTimer(realm.query<RealmTimer>().find().first {
            it._id.toHexString() == timerId
        })
    }

    private fun getRealmTimer(timerId: String): RealmTimer {
        return realm.query<RealmTimer>().find().first {
            it._id.toHexString() == timerId
        }
    }
}
