@file:OptIn(ExperimentalTime::class)

package com.intervallum.database

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
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.intervallum.domain.FinalCountDown
import com.intervallum.domain.NO_SORT_ORDER
import com.intervallum.domain.ShallowTimer
import com.intervallum.domain.Timer
import com.intervallum.domain.TimerInterval
import com.intervallum.domain.TimerSet
import com.intervallum.domain.length
import com.intervallum.sound.Beep
import com.intervallum.vibration.Vibration
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Suppress("TooManyFunctions", "ComplexInterface")
@Dao
interface RoomTimerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: RoomTimer): Long

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
        roomTimerSets: List<Pair<RoomSet, List<RoomInterval>>>,
    ): Long {
        val insertSortOrder = if (roomTimer.sortOrder == NO_SORT_ORDER) {
            getMaxSortOrder().first() + 1L
        } else {
            roomTimer.sortOrder
        }
        val timerPrimaryKey = insert(roomTimer.copy(sortOrder = insertSortOrder))
        roomTimerSets.forEach { entry ->
            val setPrimaryKey = insert(entry.first)
            insert(
                RoomTimerSet(
                    id = 0,
                    timerId = timerPrimaryKey,
                    setId = setPrimaryKey
                )
            )
            entry.second.forEach { roomInterval ->
                val intervalPrimaryKey = insert(roomInterval)
                insert(RoomSetInterval(0, setPrimaryKey, intervalPrimaryKey))
            }
        }
        return timerPrimaryKey
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

    @Transaction
    suspend fun incrementStartedCount(timerId: Long) {
        val timer = getTimer(timerId).first()
        requireNotNull(timer) {
            "Attempting to increment start count for null timer `$timerId`"
        }
        setStartedCount(timerId, timer.startedCount + 1, Clock.System.now())
    }

    @Query("UPDATE RoomTimer SET started_count = :startedCount, last_run = :lastRun WHERE id = :timerId")
    suspend fun setStartedCount(timerId: Long, startedCount: Long, lastRun: Instant)

    @Transaction
    suspend fun incrementCompletedCount(timerId: Long) {
        val timer = getTimer(timerId).first()
        requireNotNull(timer) {
            "Attempting to increment completed count for null timer `$timerId`"
        }
        setCompletedCount(timerId, timer.completedCount + 1)
    }

    @Query("UPDATE RoomTimer SET completed_count = :completedCount WHERE id = :timerId")
    suspend fun setCompletedCount(timerId: Long, completedCount: Long)

    @Query(value = "DELETE FROM RoomTimer WHERE id IS (:id)")
    suspend fun deleteTimer(id: Long)

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
    }

    @Transaction
    suspend fun updateTimer(
        roomTimer: RoomTimer,
        roomTimerSets: List<Pair<RoomSet, List<RoomInterval>>>
    ) {
        deleteTimerTransaction(roomTimer.id)
        insertTimerTransaction(roomTimer, roomTimerSets)
    }

    @Query("SELECT * FROM RoomTimer ORDER BY sort_order DESC")
    fun getTimers(): Flow<List<RoomTimer>>

    @Query("SELECT * FROM RoomTimer WHERE id IS (:timerId)")
    fun getTimer(timerId: Long): Flow<RoomTimer?>

    @Query("SELECT * FROM RoomTimerSet WHERE timer_id IS (:timerId)")
    fun getTimerSet(timerId: Long): Flow<List<RoomTimerSet>>

    @Query("SELECT * FROM RoomSet WHERE id IN (:ids)")
    fun getSets(ids: List<Long>): Flow<List<RoomSet>>

    @Query("SELECT * FROM RoomSetInterval WHERE set_id IS (:setId)")
    fun getSetInterval(setId: Long): Flow<List<RoomSetInterval>>

    @Query("SELECT * FROM RoomInterval WHERE id IN (:ids)")
    fun getInterval(ids: List<Long>): Flow<List<RoomInterval>>

    @Query("SELECT * FROM RoomTimer WHERE id IS (:timerId)")
    fun doesTimerExist(timerId: Long): Flow<RoomTimer?>
}

@Database(
    entities = [
        RoomTimer::class,
        RoomTimerSet::class,
        RoomSet::class,
        RoomSetInterval::class,
        RoomInterval::class,
    ],
    version = 1
)
@TypeConverters(DateTimeConverter::class)
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
    fun clearAllTables() {
    }
}

internal const val DB_FILE_NAME = "intervallum.db"

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
    val duration: Long,
    @ColumnInfo(name = "started_count")
    val startedCount: Long = 0,
    @ColumnInfo(name = "completed_count")
    val completedCount: Long = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "last_run")
    val lastRun: Instant? = null
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
data class RoomTimerSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "timer_id", index = true) val timerId: Long,
    @ColumnInfo(name = "set_id", index = true) val setId: Long
)

@Entity
data class RoomSet(
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
data class RoomSetInterval(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "set_id", index = true) val setId: Long,
    @ColumnInfo(name = "interval_id", index = true) val intervalId: Long
)

@Entity
data class RoomInterval(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("duration")
    val duration: Long,
    @ColumnInfo("color")
    val color: Long,
    @ColumnInfo("skip_on_last_set")
    val skipOnLastSet: Boolean,
    @ColumnInfo("count_up")
    val countUp: Boolean,
    @ColumnInfo("manual_next")
    val manualNext: Boolean,
    @ColumnInfo("text_to_speech")
    val textToSpeech: Boolean,
    @ColumnInfo("beep_id")
    val beepId: Int,
    @ColumnInfo("vibration_id")
    val vibrationId: Int,
    @ColumnInfo("final_count_down_duration")
    val finalCountDownDuration: Long,
    @ColumnInfo("final_count_down_beep_id")
    val finalCountDownBeepId: Int,
    @ColumnInfo("final_count_down_vibration_id")
    val finalCountDownVibrationId: Int,
)

internal class DateTimeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?) = value?.let {
        Instant.fromEpochMilliseconds(it)
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?) = date?.toEpochMilliseconds()
}

class TimerRepository(private val appDatabase: AppDatabase) : ITimerRepository {
    private val timerDao = appDatabase.timerDao()

    override fun getShallowTimers() = timerDao.getTimers().map {
        it.map { roomTimer ->
            ShallowTimer(
                id = roomTimer.id,
                sortOrder = roomTimer.sortOrder,
                name = roomTimer.name,
                duration = roomTimer.duration,
                startedCount = roomTimer.startedCount,
                completedCount = roomTimer.completedCount,
                createdAt = roomTimer.createdAt,
                lastRun = roomTimer.lastRun
            )
        }
    }

    override suspend fun insertTimer(timer: Timer): Long =
        appDatabase.timerDao().insertTimerTransaction(
            timer.toRoomTimer(),
            roomSetsAndIntervals(timer)
        )

    override suspend fun incrementStartedCount(timerId: Long) {
        appDatabase.timerDao().incrementStartedCount(timerId)
    }

    override suspend fun incrementCompletedCount(timerId: Long) {
        appDatabase.timerDao().incrementCompletedCount(timerId)
    }

    override suspend fun updateTimer(timer: Timer) {
        deleteTimer(timer.id)
        insertTimer(timer)
    }

    override suspend fun deleteTimer(timerId: Long) {
        timerDao.deleteTimerTransaction(timerId)
    }

    private fun roomSetsAndIntervals(timer: Timer): List<Pair<RoomSet, List<RoomInterval>>> =
        timer.sets.map { timerSet ->
            Pair(timerSet.toRoomSet(), timerSet.intervals.map { it.toRoomInterval() })
        }

    override suspend fun duplicate(timerId: Long): Long {
        val toCopy = getTimer(timerId).first()
        requireNotNull(toCopy) { "Attempting to copy null timer $timerId" }
        val toInsert = toCopy.copy(
            id = 0,
            name = toCopy.name + " (copy)",
            sets = toCopy.sets.map { timerSet ->
                timerSet.copy(
                    id = 0,
                    intervals = timerSet.intervals.map { timerInterval ->
                        timerInterval.copy(id = 0)
                    }.toPersistentList()
                )
            }.toPersistentList(),
            sortOrder = NO_SORT_ORDER
        )
        return insertTimer(toInsert)
    }

    override suspend fun getTimer(timerId: Long) =
        timerDao.getTimer(timerId).map { it?.let { getRestOfTimer(it) } }

    override suspend fun swapTimers(
        fromId: Long,
        fromSortOrder: Long,
        toId: Long,
        toSortOrder: Long
    ) {
        timerDao.updateSortOrder(
            fromId,
            fromSortOrder,
            toId,
            toSortOrder
        )
    }

    override suspend fun doesTimerExist(timerId: Long) =
        timerDao.doesTimerExist(timerId).map { it != null }

    private suspend fun getRestOfTimer(roomTimer: RoomTimer): Timer {
        val sets = timerDao.getSets(
            timerDao.getTimerSet(roomTimer.id).first()
                .map { roomTimerSet -> roomTimerSet.setId }
        ).firstOrNull() ?: emptyList()
        return Timer(
            id = roomTimer.id,
            sortOrder = roomTimer.sortOrder,
            name = roomTimer.name,
            sets = sets.map { roomSet ->
                val intervals = timerDao.getInterval(
                    timerDao.getSetInterval(roomSet.id).first().map { intervalSet ->
                        intervalSet.intervalId
                    }
                ).first()
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
                            textToSpeech = roomInterval.textToSpeech,
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
            startedCount = roomTimer.startedCount,
            completedCount = roomTimer.completedCount,
            createdAt = roomTimer.createdAt,
            lastRun = roomTimer.lastRun
        )
    }
}

private fun Timer.toRoomTimer() =
    RoomTimer(
        id = this.id,
        name = this.name,
        finishColor = this.finishColor.toArgb().toLong(),
        finishBeepId = this.finishBeep.ordinal,
        finishVibration = this.finishVibration.ordinal,
        sortOrder = this.sortOrder,
        duration = this.length(),
        createdAt = this.createdAt,
        lastRun = this.lastRun
    )

private fun TimerSet.toRoomSet() =
    RoomSet(
        id = this.id,
        repetitions = this.repetitions
    )

private fun TimerInterval.toRoomInterval() =
    RoomInterval(
        id = this.id,
        name = this.name,
        duration = this.duration,
        color = this.color.toArgb().toLong(),
        skipOnLastSet = this.skipOnLastSet,
        countUp = this.countUp,
        manualNext = this.manualNext,
        textToSpeech = this.textToSpeech,
        beepId = this.beep.ordinal,
        vibrationId = this.vibration.ordinal,
        finalCountDownDuration = this.finalCountDown.duration,
        finalCountDownBeepId = this.beep.ordinal,
        finalCountDownVibrationId = this.vibration.ordinal,
    )
