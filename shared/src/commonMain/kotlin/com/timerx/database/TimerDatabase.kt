package com.timerx.database

import androidx.compose.ui.graphics.Color
import com.timerx.beep.Beep
import com.timerx.domain.FinalCountDown
import com.timerx.domain.Timer
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import com.timerx.vibration.Vibration
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.mongodb.kbson.ObjectId
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.copy_parenthesis

interface ITimerRepository {
    suspend fun getTimers(): List<Timer>
    suspend fun insertTimer(timer: Timer)
    suspend fun updateTimer(timer: Timer)
    suspend fun deleteTimer(timer: Timer)
    suspend fun duplicate(timer: Timer)
    suspend fun getTimer(timerId: String): Timer
    suspend fun swapTimers(from: Int, to: Int)
}

private class RealmColor : RealmObject {
    var red: Float = 0f
    var green: Float = 0f
    var blue: Float = 0f
    var alpha: Float = 0f
}

private class RealmTimerContainer : RealmObject {
    var timers: RealmList<RealmTimer> = realmListOf()
}

private class RealmTimer : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var name: String = ""
    var sets: RealmList<RealmSet> = realmListOf()
    var finishColor: RealmColor? = null
    var finishBeepId: Int = Beep.Alert.ordinal
    var finishVibration: Int = Vibration.Heavy.ordinal
}

private class RealmSet : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var repetitions: Int = 1
    var intervals: RealmList<RealmInterval> = realmListOf()
}

private class RealmInterval : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var name: String = ""
    var duration: Int = 1
    var color: RealmColor? = null
    var skipOnLastSet: Boolean = false
    var countUp: Boolean = false
    var manualNext: Boolean = false
    var beepId: Int = Beep.Alert.ordinal
    var vibrationId: Int = Vibration.Medium.ordinal
    var finalCountDown: RealmFinalCountDown? = null
}

private class RealmFinalCountDown : EmbeddedRealmObject {
    var duration: Int = 3
    var beepId: Int = Beep.Alert.ordinal
    var vibrationId = Vibration.Soft.ordinal
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

private fun RealmFinalCountDown?.toFinalCountDown(): FinalCountDown {
    if (this == null) return FinalCountDown()
    return FinalCountDown(duration, Beep.entries[beepId], Vibration.entries[vibrationId])
}

class RealmTimerRepository : ITimerRepository {
    private val configuration = RealmConfiguration.create(
        schema = setOf(
            RealmTimerContainer::class,
            RealmTimer::class,
            RealmSet::class,
            RealmInterval::class,
            RealmColor::class,
            RealmFinalCountDown::class
        )
    )

    private val realm = Realm.open(configuration)

    init {
        val query = realm.query<RealmTimerContainer>().find()
        if (query.isEmpty()) {
            realm.writeBlocking {
                copyToRealm(RealmTimerContainer())
            }
        } else if (query.size > 1) {
            throw IllegalStateException("More than 1 RealmTimerContainer ${query.size}")
        }
    }

    override suspend fun getTimers(): List<Timer> {
        return getRealmTimers().map(::realmTimerToTimer)
    }

    private fun getRealmTimers(): List<RealmTimer> {
        return realm.query<RealmTimerContainer>().find().first().timers
    }


    override suspend fun insertTimer(timer: Timer) {
        val realmTimer = RealmTimer()
        realmTimer.setTimer(timer)
        realm.writeBlocking {
            val realmTimerContainer = this.query<RealmTimerContainer>().find().first()
            realmTimerContainer.timers.add(realmTimer)
            copyToRealm(realmTimerContainer)
        }
    }

    override suspend fun updateTimer(timer: Timer) {
        realm.writeBlocking {
            val realmTimerContainer = this.query<RealmTimerContainer>().find().first()
            val index = realmTimerContainer.timers.first { it.id.toHexString() == timer.id }
            index.setTimer(timer)
            copyToRealm(index)
        }
    }

    override suspend fun deleteTimer(timer: Timer) {
        realm.writeBlocking {
            val realmTimerContainer = this.query<RealmTimerContainer>().find().first()
            realmTimerContainer.timers.remove(realmTimerContainer.timers.first { it.id.toHexString() == timer.id })
            copyToRealm(realmTimerContainer)
        }
    }

    override suspend fun duplicate(timer: Timer) {
        runBlocking {
            insertTimer(timer.copy(name = timer.name + getString(Res.string.copy_parenthesis)))
        }
    }

    override suspend fun getTimer(timerId: String): Timer {
        return realmTimerToTimer(realm.query<RealmTimer>().find().first { it.id.toHexString() == timerId })
    }

    override suspend fun swapTimers(from: Int, to: Int) {
        realm.writeBlocking {
            val realmTimerContainer = this.query<RealmTimerContainer>().find().first()
            val fromTimer = realmTimerContainer.timers[from]
            val toTimer = realmTimerContainer.timers[to]
            realmTimerContainer.timers[from] = toTimer
            realmTimerContainer.timers[to] = fromTimer
            copyToRealm(realmTimerContainer)
        }
    }

    private fun RealmTimer.setTimer(timer: Timer) {
        this.apply {
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
                            this.beepId = it.beep.ordinal
                            this.vibrationId = it.vibration.ordinal
                            this.finalCountDown = RealmFinalCountDown().apply {
                                this.duration = it.finalCountDown.duration
                                this.beepId = it.finalCountDown.beep.ordinal
                                this.vibrationId = it.vibration.ordinal
                            }
                        }
                    }.toRealmList()
                }
            }.toRealmList()

            this.finishColor = timer.finishColor.toRealmColor()
            this.finishBeepId = timer.finishBeep.ordinal
            this.finishVibration = timer.finishVibration.ordinal
        }
    }

    private fun realmTimerToTimer(realmTimer: RealmTimer): Timer {
        return Timer(
            realmTimer.id.toHexString(),
            realmTimer.name,
            realmTimer.sets.map { realmSet ->
                TimerSet(
                    realmSet.id.toHexString(),
                    realmSet.repetitions,
                    realmSet.intervals.map { realmInterval ->
                        TimerInterval(
                            realmInterval.id.toHexString(),
                            realmInterval.name,
                            realmInterval.duration,
                            realmInterval.color.toComposeColor(),
                            realmInterval.skipOnLastSet,
                            realmInterval.countUp,
                            realmInterval.manualNext,
                            Beep.entries[realmInterval.beepId],
                            Vibration.entries[realmInterval.vibrationId],
                            realmInterval.finalCountDown.toFinalCountDown()
                        )
                    }.toPersistentList()
                )
            }.toPersistentList(),
            realmTimer.finishColor.toComposeColor(),
            Beep.entries[realmTimer.finishBeepId],
            Vibration.entries[realmTimer.finishVibration]
        )
    }
}
