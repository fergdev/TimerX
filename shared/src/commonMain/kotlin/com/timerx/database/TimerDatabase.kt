@file:OptIn(ExperimentalResourceApi::class)

package com.timerx.database

import androidx.compose.ui.graphics.Color
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
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.mongodb.kbson.ObjectId
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.copy_parenthesis

interface ITimerRepository {
    fun getTimers(): List<Timer>
    fun insertTimer(timer: Timer)
    fun updateTimer(timer: Timer)
    fun deleteTimer(timer: Timer)
    fun duplicate(timer: Timer)
    fun getTimer(timerId: String): Timer
}

private class RealmColor() : RealmObject {
    var red: Float = 0f
    var green: Float = 0f
    var blue: Float = 0f
    var alpha: Float = 0f
}

private class RealmTimer() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = ""
    var sets: RealmList<RealmSet> = realmListOf()
    var finishColor: RealmColor? = null
}

private class RealmSet() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var repetitions: Int = 1
    var intervals: RealmList<RealmInterval> = realmListOf()
}

private class RealmInterval() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = ""
    var duration: Int = 1
    var color: RealmColor? = null
    var skipOnLastSet: Boolean = false
    var countUp: Boolean = false
    var manualNext: Boolean = false
}

private fun Color.toRealmColor(): RealmColor {
    return RealmColor().also {
        it.red = this.red
        it.green = this.green
        it.blue = this.blue
        it.alpha = this.alpha
    }
}

private fun RealmColor?.toComposeColor(): Color {
    if (this == null) return Color.Blue
    return Color(this.red, this.green, this.blue, this.alpha)
}

class TimerRepo : ITimerRepository {
    private val configuration = RealmConfiguration.create(
        schema = setOf(
            RealmTimer::class,
            RealmSet::class,
            RealmInterval::class,
            RealmColor::class
        )
    )

    private val realm = Realm.open(configuration)

    override fun getTimers(): List<Timer> {
        return realm.query<RealmTimer>().find().map(::realmTimerToTimer)
    }

    private fun realmTimerToTimer(realmTimer: RealmTimer): Timer {
        return Timer(
            realmTimer._id.toHexString(),
            realmTimer.name,
            realmTimer.sets.map { realmSet ->
                TimerSet(
                    realmSet._id.toHexString(),
                    realmSet.repetitions,
                    realmSet.intervals.map {
                        TimerInterval(
                            it._id.toHexString(),
                            it.name,
                            it.duration,
                            it.color.toComposeColor(),
                            it.skipOnLastSet,
                            it.countUp,
                            it.manualNext
                        )
                    }.toPersistentList()
                )
            }.toPersistentList(),
            realmTimer.finishColor.toComposeColor()
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
                            this.name = it.name
                            this.duration = it.duration
                            this.color = it.color.toRealmColor()
                            this.skipOnLastSet = it.skipOnLastSet
                            this.countUp = it.countUp
                            this.manualNext = it.manualNext
                        }
                    }.toRealmList()
                }
            }.toRealmList()
            finishColor = timer.finishColor.toRealmColor()
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
        runBlocking {
            insertTimer(timer.copy(name = timer.name + getString(Res.string.copy_parenthesis)))
        }
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
