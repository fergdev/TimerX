package com.intervallum.ui.settings.about.aboutlibs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.intervallum.ui.common.TCard
import com.intervallum.ui.logging.LocalIntervallumAnalytics
import com.intervallum.ui.logging.LogScreen
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.m3.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.util.author
import intervallum.shared.generated.resources.Res
import kotlinx.collections.immutable.persistentListOf

internal const val FILES_ABOUT_LIBRARIES_JSON = "files/aboutlibraries.json"

internal interface LibsLoader {
    suspend fun load(): String
}

internal object FileLibsLoader : LibsLoader {
    override suspend fun load() =
        Res.readBytes(FILES_ABOUT_LIBRARIES_JSON).decodeToString()
}

private const val SCREEN_NAME_LOGGING = "Settings:About:AboutLibs"

@Composable
internal fun AboutLibsContent(libsLoader: LibsLoader = FileLibsLoader) {
    LogScreen(SCREEN_NAME_LOGGING)
    val libs = rememberLibraries { libsLoader.load() }
    val libraries = libs.value?.libraries ?: persistentListOf()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(items = libraries) { Library(library = it) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Library(library: Library) {
    val intervallumAnalytics = LocalIntervallumAnalytics.current
    val uriHandler = LocalUriHandler.current
    TCard(
        modifier = Modifier
            .testTag(library.uniqueId)
            .padding(top = 8.dp, bottom = 8.dp)
            .widthIn(max = 600.dp)
            .clickable {
                library.licenses.firstOrNull()?.url?.also {
                    try {
                        uriHandler.openUri(it)
                    } catch (t: IllegalArgumentException) {
                        intervallumAnalytics.logException(t)
                    }
                }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = library.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val version = library.artifactVersion
            if (version != null) {
                Text(
                    text = version,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        val author = library.author
        if (author.isNotBlank()) {
            Text(
                text = author,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (library.licenses.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                library.licenses.forEach {
                    Badge {
                        Text(
                            modifier = Modifier.padding(),
                            text = it.name
                        )
                    }
                }
            }
        }
    }
}
