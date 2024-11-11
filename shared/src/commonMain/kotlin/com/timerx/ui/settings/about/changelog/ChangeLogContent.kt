package com.timerx.ui.settings.about.changelog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.timerx.ui.common.branded
import com.timerx.ui.common.withForEach
import com.timerx.ui.logging.LogScreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.change_log

internal const val FILES_CHANGE_LOG_JSON = "files/change_log.json"

private const val LOG_SCREEN_TAG = "Settings:About:ChangeLog"

@Composable
internal fun ChangeLogContent() {
    LogScreen(LOG_SCREEN_TAG)
    val logs = rememberChangeLog { Res.readBytes(FILES_CHANGE_LOG_JSON).decodeToString() }
    val changeLog = logs.value ?: persistentListOf()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(8.dp),
    ) {
        item {
            Text(
                text = stringResource(Res.string.change_log).branded(),
                style = MaterialTheme.typography.displayMedium
            )
        }
        items(changeLog) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                with(it) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = CenterVertically) {
                        Text(
                            text = "v$version".branded(),
                            style = MaterialTheme.typography.displaySmall
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = info,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    data?.withForEach {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = makeBulletedList(items),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Serializable
internal data class ChangeLogItem(
    val version: String,
    val date: String,
    val info: String,
    val data: List<ChangeDataItem>? = null
)

@Serializable
internal data class ChangeDataItem(val title: String, val items: List<String>)

@Composable
private fun rememberChangeLog(
    block: suspend () -> String,
): State<List<ChangeLogItem>?> =
    produceState<List<ChangeLogItem>?>(initialValue = null) {
        value = withContext(Dispatchers.Default) {
            Json.decodeFromString<List<ChangeLogItem>>(block())
        }
    }

@Composable
fun makeBulletedList(items: List<String>): AnnotatedString {
    val bulletString = "\u2022\t\t"
    val textStyle = LocalTextStyle.current
    val textMeasurer = rememberTextMeasurer()
    val bulletStringWidth = remember(textStyle, textMeasurer) {
        textMeasurer.measure(text = bulletString, style = textStyle).size.width
    }
    val restLine = with(LocalDensity.current) { bulletStringWidth.toSp() }
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = restLine))

    return buildAnnotatedString {
        items.forEach { text ->
            withStyle(style = paragraphStyle) {
                append(bulletString)
                append(text)
            }
        }
    }
}
