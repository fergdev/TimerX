package com.intervallum.widget

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
import com.intervallum.database.ITimerRepository
import com.intervallum.settings.IntervallumSettings
import com.intervallum.util.toAgo
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

class IntervallumWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = IntervallumWidget()
    private val coroutineScope = MainScope()
    private val timerRepository = KoinPlatform.getKoin().get<ITimerRepository>()
    private val intervallumSettings = KoinPlatform.getKoin().get<IntervallumSettings>()

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
            GlanceAppWidgetManager(context).getGlanceIds(IntervallumWidget::class.java)
                .forEach {
                    updateAppWidgetState(context, IntervallumWidgetStateDefinition, it) {
                        TimerWidgetInfo.Loading
                    }
                }
            timerRepository.getShallowTimers()
                .combine(intervallumSettings.sortTimersBy) { timers, sortTimersBy ->
                    val sortedTimers = sortTimersBy.sort(timers)
                    TimerWidgetInfo.Available(
                        sortedTimers.map { timer ->
                            TimerData(
                                id = timer.id,
                                name = timer.name,
                                length = timer.duration,
                                lastRun = timer.lastRun?.toAgo() ?: "Never"
                            )
                        }
                    )
                }
                .collect { availableWidgetInfo ->
                    GlanceAppWidgetManager(context).getGlanceIds(IntervallumWidget::class.java)
                        .forEach {
                            updateAppWidgetState(context, IntervallumWidgetStateDefinition, it) {
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
    data class Available(val timers: List<TimerData>) : TimerWidgetInfo

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

object IntervallumWidgetStateDefinition : GlanceStateDefinition<TimerWidgetInfo> {
    private const val FILE_NAME = "intervallum_widget_store"

    private val Context.dataStore by dataStore(FILE_NAME, TimerInfoSerializer)

    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<TimerWidgetInfo> = context.dataStore

    override fun getLocation(context: Context, fileKey: String) =
        File(context.applicationContext.filesDir, "datastore/$FILE_NAME")

    object TimerInfoSerializer : Serializer<TimerWidgetInfo> {
        private val json = Json {
            ignoreUnknownKeys = true
        }
        override val defaultValue = TimerWidgetInfo.Unavailable("No timers")

        override suspend fun readFrom(input: InputStream): TimerWidgetInfo = try {
            json.decodeFromString(
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
