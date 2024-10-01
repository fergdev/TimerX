package com.timerx.ui.settings.aboutlibs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.m3.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.util.author
import com.timerx.ui.common.PaddedElevatedCard
import com.timerx.ui.common.TScaffold
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.about_libs

private const val FILES_ABOUT_LIBRARIES_JSON = "files/aboutLibraries.json"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutLibsContent(component: AboutLibsComponent) {
    TScaffold(
        title = stringResource(Res.string.about_libs),
        onBack = component::onBack
    ) { scaffoldPadding ->
        val libs = rememberLibraries { Res.readBytes(FILES_ABOUT_LIBRARIES_JSON).decodeToString() }
        val libraries = libs.value?.libraries ?: persistentListOf()
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(scaffoldPadding.calculateTopPadding()))
            }
            LibraryDefaults
            items(items = libraries) { Library(library = it) }
            item {
                Spacer(modifier = Modifier.height(scaffoldPadding.calculateBottomPadding()))
            }
        }
    }
}

@Suppress("SwallowedException")
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun Library(
    library: Library,
    typography: Typography = MaterialTheme.typography,
) {
    val uriHandler = LocalUriHandler.current
    PaddedElevatedCard(modifier = Modifier.widthIn(max = 600.dp).clickable {
        val license = library.licenses.firstOrNull()
        if (!license?.url.isNullOrBlank()) {
            license?.url?.also {
                try {
                    uriHandler.openUri(it)
                } catch (t: IllegalArgumentException) {
                    println("Failed to open url: $it")
                }
            }
        }
    }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = library.name,
                modifier = Modifier.weight(1f),
                style = typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val version = library.artifactVersion
            if (version != null) {
                Text(
                    version,
                    style = typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        val author = library.author
        if (author.isNotBlank()) {
            Text(
                text = author,
                style = typography.bodyMedium,
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