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
import kotlinx.coroutines.MainScope
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
            timerRepository.getShallowTimers()
                .collect { timers ->
                    val glanceId =
                        GlanceAppWidgetManager(context).getGlanceIds(TimerXWidget::class.java)
                            .firstOrNull()
                    glanceId?.let {
                        updateAppWidgetState(context, TimerXWidgetStateDefinition, it) {
                            TimerInfo.Available(
                                timers.map { timer ->
                                    TimerData(
                                        id = timer.id,
                                        name = timer.name,
                                        length = timer.duration.toInt()
                                    )
                                }
                            )
                        }
                        glanceAppWidget.update(context, it)
                    }
                }
        }
    }
}

@Serializable
sealed interface TimerInfo {
    @Serializable
    object Loading : TimerInfo

    @Serializable
    data class Available(val timers: List<TimerData>) : TimerInfo

    @Serializable
    data class Unavailable(val message: String) : TimerInfo
}

@Serializable
data class TimerData(
    val id: Long,
    val name: String,
    val length: Int
)

object TimerXWidgetStateDefinition : GlanceStateDefinition<TimerInfo> {
    private const val FILE_NAME = "timerx_widget_store"

    private val Context.dataStore by dataStore(FILE_NAME, TimerInfoSerializer)

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<TimerInfo> {
        return context.dataStore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return File(context.applicationContext.filesDir, "datastore/$FILE_NAME")
    }

    object TimerInfoSerializer : Serializer<TimerInfo> {
        override val defaultValue = TimerInfo.Unavailable("No timers")

        override suspend fun readFrom(input: InputStream): TimerInfo = try {
            Json.decodeFromString(
                TimerInfo.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Could not read timer data: ${exception.message}")
        }

        override suspend fun writeTo(t: TimerInfo, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(TimerInfo.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}