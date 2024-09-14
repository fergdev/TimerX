package com.timerx.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.GlanceStateDefinition
import com.timerx.database.ITimerRepository
import com.timerx.domain.SortTimersBy
import com.timerx.settings.ITimerXSettings
import com.timerx.time.toAgo
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.mp.KoinPlatform
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class TimerXWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TimerXWidget()
    private val coroutineScope = MainScope()
    private val timerRepository = KoinPlatform.getKoin().get<ITimerRepository>()
    private val timerXSettings = KoinPlatform.getKoin().get<ITimerXSettings>()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        observeData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        observeData(context)
    }

    private fun observeData(context: Context) {
        coroutineScope.launch {
            GlanceAppWidgetManager(context).getGlanceIds(TimerXWidget::class.java)
                .forEach {
                    updateAppWidgetState(context, TimerXWidgetStateDefinition, it) {
                        TimerWidgetInfo.Loading
                    }
                }
            timerRepository.getShallowTimers()
                .combine(timerXSettings.settings) { timers, settings ->
                    val sortedTimers = settings.sortTimersBy.sort(timers)
                    TimerWidgetInfo.Available(
                        sortedTimers.map { timer ->
                            TimerData(
                                id = timer.id,
                                name = timer.name,
                                length = timer.duration,
                                lastRun = timer.lastRun?.toAgo() ?: "Never"
                            )
                        },
                        settings.sortTimersBy
                    )
                }
                .collect { availableWidgetInfo ->
                    GlanceAppWidgetManager(context).getGlanceIds(TimerXWidget::class.java)
                        .forEach {
                            updateAppWidgetState(context, TimerXWidgetStateDefinition, it) {
                                availableWidgetInfo
                            }
                            glanceAppWidget.update(context, it)
                        }
                }
        }
    }
}

@Serializable
sealed interface TimerWidgetInfo {
    @Serializable
    data object Loading : TimerWidgetInfo

    @Serializable
    data class Available(
        val timers: List<TimerData>,
        val sortTimersBy: SortTimersBy
    ) : TimerWidgetInfo

    @Serializable
    data class Unavailable(val message: String) : TimerWidgetInfo
}

@Serializable
data class TimerData(
    val id: Long,
    val name: String,
    val length: Long,
    val lastRun: String
)

object TimerXWidgetStateDefinition : GlanceStateDefinition<TimerWidgetInfo> {
    private const val FILE_NAME = "timerx_widget_store"

    private val Context.dataStore by dataStore(FILE_NAME, TimerInfoSerializer)

    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<TimerWidgetInfo> = context.dataStore

    override fun getLocation(context: Context, fileKey: String) =
        File(context.applicationContext.filesDir, "datastore/$FILE_NAME")

    object TimerInfoSerializer : Serializer<TimerWidgetInfo> {
        override val defaultValue = TimerWidgetInfo.Unavailable("No timers")

        override suspend fun readFrom(input: InputStream): TimerWidgetInfo = try {
            Json.decodeFromString(
                TimerWidgetInfo.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException(
                message = "Could not read timer data: ${exception.message}",
                cause = exception
            )
        }

        override suspend fun writeTo(t: TimerWidgetInfo, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(TimerWidgetInfo.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}
