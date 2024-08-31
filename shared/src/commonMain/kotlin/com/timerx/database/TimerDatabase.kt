package com.timerx.database

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.ColumnInfo
import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import com.timerx.beep.Beep
import com.timerx.domain.FinalCountDown
import com.timerx.domain.NO_SORT_ORDER
import com.timerx.domain.Timer
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import com.timerx.domain.TimerStats
import com.timerx.domain.length
import com.timerx.vibration.Vibration
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

interface ITimerRepository {
    fun getShallowTimers(): Flow<List<RoomTimer>>
    suspend fun getTimers(): List<Timer>
    suspend fun insertTimer(timer: Timer)
    suspend fun updateTimerStats(timer: Timer, timerStats: TimerStats)
    suspend fun updateTimer(timer: Timer)
    suspend fun deleteTimer(timerId: Long)
    suspend fun duplicate(timerId: Long)
    suspend fun getTimer(timerId: Long): Timer
    suspend fun swapTimers(
        fromId: Long,
        fromSortOrder: Long,
        toId: Long,
        toSortOrder: Long
    )
}

@Dao
interface RoomTimerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: RoomTimer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: RoomTimerStats): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: RoomSet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timerSet: RoomTimerSet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(interval: RoomInterval): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setInterval: RoomSetInterval)

    @Transaction
    suspend fun insertTimerTransaction(
        roomTimer: RoomTimer,
        roomTimerSets: Map<RoomSet, List<RoomInterval>>,
    ) {
        val insertSortOrder = if (roomTimer.sortOrder == NO_SORT_ORDER) {
            getMaxSortOrder().first() + 1L
        } else {
            roomTimer.sortOrder
        }
        val timerPrimaryKey = insert(roomTimer.copy(sortOrder = insertSortOrder))
        roomTimerSets.map { entry ->
            val setPrimaryKey = insert(entry.key)
            insert(
                RoomTimerSet(
                    id = 0, timerId = timerPrimaryKey, setId = setPrimaryKey
                )
            )
            entry.value.forEach { roomInterval ->
                val intervalPrimaryKey = insert(roomInterval)
                insert(RoomSetInterval(0, setPrimaryKey, intervalPrimaryKey))
            }
        }
        insert(RoomTimerStats(timerId = timerPrimaryKey))
    }

    @Query("SELECT sort_order FROM RoomTimer ORDER BY sort_order DESC LIMIT 1")
    fun getMaxSortOrder(): Flow<Long>

    @Transaction
    suspend fun updateSortOrder(
        fromId: Long,
        fromSortOrder: Long,
        toId: Long,
        toSortOrder: Long
    ) {
        updateSortOrder(timerId = fromId, sortOrder = toSortOrder)
        updateSortOrder(timerId = toId, sortOrder = fromSortOrder)
    }

    @Query("UPDATE RoomTimer SET sort_order = :sortOrder WHERE id = :timerId")
    suspend fun updateSortOrder(timerId: Long, sortOrder: Long)

    @Query("UPDATE RoomTimerStats SET started_count = :startedCount, completed_count = :completedCount WHERE timer_id = :timerId")
    suspend fun updateTimerStats(timerId: Long, startedCount: Long, completedCount: Long)

    @Query(value = "DELETE FROM RoomTimer WHERE id IS (:id)")
    suspend fun deleteTimer(id: Long)

    @Query(value = "DELETE FROM RoomTimerStats WHERE timer_id IS (:id)")
    suspend fun deleteTimerStats(id: Long)

    @Query(value = "DELETE FROM RoomSet WHERE id IS (:id)")
    suspend fun deleteSet(id: Long)

    @Query(value = "DELETE FROM RoomInterval WHERE id IS (:id)")
    suspend fun deleteInterval(id: Long)

    @Transaction
    suspend fun deleteTimerTransaction(
        timerId: Long
    ) {
        val timerSet = getTimerSet(timerId)
        val setIds = timerSet.first().map { it.setId }

        setIds.forEach { setId ->
            val setIntervals = getSetInterval(setId).first()
            setIntervals.forEach {
                deleteInterval(it.intervalId)
            }
            deleteSet(setId)
        }

        deleteTimer(timerId)
        deleteTimerStats(timerId)
    }

    @Transaction
    suspend fun updateTimer(
        roomTimer: RoomTimer,
        roomTimerSets: Map<RoomSet, List<RoomInterval>>
    ) {
        deleteTimerTransaction(roomTimer.id)
        insertTimerTransaction(roomTimer, roomTimerSets)
    }

    @Query("SELECT * FROM RoomTimer ORDER BY sort_order ASC")
    fun getTimers(): Flow<List<RoomTimer>>

    @Query("SELECT * FROM RoomTimerStats WHERE timer_id IS (:timerId)")
    fun getTimerStats(timerId: Long): Flow<RoomTimerStats>

    @Query("SELECT * FROM RoomTimer WHERE id IS (:timerId)")
    fun getTimer(timerId: Long): Flow<RoomTimer>

    @Query("SELECT * FROM RoomTimerSet WHERE timer_id IS (:timerId)")
    fun getTimerSet(timerId: Long): Flow<List<RoomTimerSet>>

    @Query("SELECT * FROM RoomSet WHERE id IN (:ids)")
    fun getSets(ids: List<Long>): Flow<List<RoomSet>>

    @Query("SELECT * FROM RoomSetInterval WHERE set_id IS (:setId)")
    fun getSetInterval(setId: Long): Flow<List<RoomSetInterval>>

    @Query("SELECT * FROM RoomInterval WHERE id IN (:ids)")
    fun getInterval(ids: List<Long>): Flow<List<RoomInterval>>
}

@Database(
    entities = [
        RoomTimer::class,
        RoomTimerSet::class,
        RoomSet::class,
        RoomSetInterval::class,
        RoomInterval::class,
        RoomTimerStats::class
    ], version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase(), DB {
    abstract fun timerDao(): RoomTimerDao

    // See hack comment below
    @Suppress("RedundantOverride")
    override fun clearAllTables() {
        super.clearAllTables()
    }
}

// Added a hack to resolve below issue:
// Class 'AppDatabase_Impl' is not abstract and does not implement abstract base class member 'clearAllTables'.
interface DB {
    @Suppress("RedundantUnitReturnType")
    fun clearAllTables(): Unit {
    }
}

internal const val dbFileName = "timerx.db"

@Entity
data class RoomTimer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("finish_color")
    val finishColor: Long,
    @ColumnInfo("finish_beep_id")
    val finishBeepId: Int,
    @ColumnInfo("finish_vibration")
    val finishVibration: Int,
    @ColumnInfo("sort_order")
    val sortOrder: Long,
    @ColumnInfo("duration")
    val duration: Long
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomTimer::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("timer_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomSet::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("set_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class RoomTimerSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "timer_id", index = true) val timerId: Long,
    @ColumnInfo(name = "set_id", index = true) val setId: Long
)

@Entity
class RoomSet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("repetitions")
    val repetitions: Int = 1,
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomSet::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("set_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomInterval::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("interval_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class RoomSetInterval(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "set_id", index = true) val setId: Long,
    @ColumnInfo(name = "interval_id", index = true) val intervalId: Long
)

@Entity
class RoomInterval(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("duration")
    val duration: Int,
    @ColumnInfo("color")
    val color: Long,
    @ColumnInfo("skip_on_last_set")
    val skipOnLastSet: Boolean,
    @ColumnInfo("count_up")
    val countUp: Boolean,
    @ColumnInfo("manual_next")
    val manualNext: Boolean,
    @ColumnInfo("beep_id")
    val beepId: Int,
    @ColumnInfo("vibration_id")
    val vibrationId: Int,
    @ColumnInfo("final_count_down_duration")
    val finalCountDownDuration: Int,
    @ColumnInfo("final_count_down_beep_id")
    val finalCountDownBeepId: Int,
    @ColumnInfo("final_count_down_vibration_id")
    val finalCountDownVibrationId: Int,
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomTimer::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("timer_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class RoomTimerStats(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "timer_id", index = true) val timerId: Long,
    @ColumnInfo(name = "started_count", index = true) val startedCount: Long = 0,
    @ColumnInfo(name = "completed_count", index = true) val completedCount: Long = 0,
)

class RealmTimerRepository(private val appDatabase: AppDatabase) : ITimerRepository {
    private val timerDao = appDatabase.timerDao()

    override fun getShallowTimers(): Flow<List<RoomTimer>> {
        return timerDao.getTimers()
    }

    override suspend fun getTimers(): List<Timer> {
        return timerDao.getTimers().first().map { getRestOfTimer(it) }.sortedBy { it.sortOrder }
    }

    override suspend fun insertTimer(timer: Timer) {
        appDatabase.timerDao().insertTimerTransaction(
            timer.toRoomTimer(),
            roomSetsAndIntervals(timer)
        )
    }

    override suspend fun updateTimerStats(timer: Timer, timerStats: TimerStats) {
        appDatabase.timerDao()
            .updateTimerStats(
                timer.id,
                timerStats.startedCount,
                timerStats.completedCount
            )
    }

    override suspend fun updateTimer(timer: Timer) {
        deleteTimer(timer.id)
        insertTimer(timer)
    }

    override suspend fun deleteTimer(timerId: Long) {
        timerDao.deleteTimerTransaction(timerId)
    }

    private fun roomSetsAndIntervals(timer: Timer): MutableMap<RoomSet, List<RoomInterval>> {
        val inputMap = mutableMapOf<RoomSet, List<RoomInterval>>()
        timer.sets.forEach { timerSet ->
            val roomSet = timerSet.toRoomSet()
            val roomIntervals = timerSet.intervals.map { it.toRoomInterval() }
            inputMap[roomSet] = roomIntervals
        }
        return inputMap
    }

    override suspend fun duplicate(timerId: Long) {
        val timer = getTimer(timerId)
        val toInsert = timer.copy(
            id = 0,
            name = timer.name + " (copy)",
            sets = timer.sets.map { timerSet ->
                timerSet.copy(
                    id = 0,
                    intervals = timerSet.intervals.map { timerInterval ->
                        timerInterval.copy(id = 0)
                    }.toPersistentList()
                )
            }.toPersistentList()
        )
        insertTimer(toInsert)
    }

    override suspend fun getTimer(timerId: Long): Timer {
        return getRestOfTimer(timerDao.getTimer(timerId).first())
    }

    override suspend fun swapTimers(
        fromId: Long,
        fromSortOrder: Long,
        toId: Long,
        toSortOrder: Long
    ) {
        println("swap timers $fromId $fromSortOrder $toId $toSortOrder")
        timerDao.updateSortOrder(
            fromId,
            fromSortOrder,
            toId,
            toSortOrder
        )
    }

    private suspend fun getRestOfTimer(roomTimer: RoomTimer): Timer {
        val sets = timerDao.getSets(
            timerDao.getTimerSet(roomTimer.id).first()
                .map { roomTimerSet -> roomTimerSet.setId }
        ).firstOrNull() ?: emptyList()
        val timerStats = timerDao.getTimerStats(roomTimer.id)
        return Timer(
            id = roomTimer.id,
            sortOrder = roomTimer.sortOrder,
            name = roomTimer.name,
            sets = sets.map { roomSet ->
                val intervals = timerDao.getInterval(
                    timerDao.getSetInterval(roomSet.id).first().map { intervalSet ->
                        intervalSet.intervalId
                    }).first()
                TimerSet(
                    id = roomSet.id,
                    repetitions = roomSet.repetitions,
                    intervals = intervals.map { roomInterval ->
                        TimerInterval(
                            id = roomInterval.id,
                            name = roomInterval.name,
                            duration = roomInterval.duration,
                            color = Color(color = roomInterval.color),
                            skipOnLastSet = roomInterval.skipOnLastSet,
                            countUp = roomInterval.countUp,
                            manualNext = roomInterval.manualNext,
                            beep = Beep.entries[roomInterval.beepId],
                            vibration = Vibration.entries[roomInterval.vibrationId],
                            finalCountDown = FinalCountDown(
                                roomInterval.finalCountDownDuration,
                                Beep.entries[roomInterval.finalCountDownBeepId],
                                Vibration.entries[roomInterval.finalCountDownVibrationId]
                            ),
                        )
                    }.toPersistentList()
                )
            }.toPersistentList(),
            finishColor = Color(color = roomTimer.finishColor),
            finishBeep = Beep.entries[roomTimer.finishBeepId],
            finishVibration = Vibration.entries[roomTimer.finishVibration],
            TimerStats(
                startedCount = timerStats.first().startedCount,
                completedCount = timerStats.first().completedCount
            )
        )
    }
}

private fun Timer.toRoomTimer(): RoomTimer {
    return RoomTimer(
        id = this.id,
        name = this.name,
        finishColor = this.finishColor.toArgb().toLong(),
        finishBeepId = this.finishBeep.ordinal,
        finishVibration = this.finishVibration.ordinal,
        sortOrder = this.sortOrder,
        duration = this.length().toLong()
    )
}

private fun TimerSet.toRoomSet(): RoomSet {
    return RoomSet(
        id = this.id,
        repetitions = this.repetitions
    )
}

private fun TimerInterval.toRoomInterval(): RoomInterval {
    return RoomInterval(
        id = this.id,
        name = this.name,
        duration = this.duration,
        color = this.color.toArgb().toLong(),
        skipOnLastSet = this.skipOnLastSet,
        countUp = this.countUp,
        manualNext = this.manualNext,
        beepId = this.beep.ordinal,
        vibrationId = this.vibration.ordinal,
        finalCountDownDuration = this.finalCountDown.duration,
        finalCountDownBeepId = this.beep.ordinal,
        finalCountDownVibrationId = this.vibration.ordinal,
    )
}
