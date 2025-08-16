package com.intervallum.resources

import com.intervallum.testutil.NotAndroidCondition
import com.intervallum.ui.settings.about.changelog.ChangeLogItem
import com.intervallum.ui.settings.about.changelog.FILES_CHANGE_LOG_JSON
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.serialization.json.Json
import intervallum.shared.generated.resources.Res

@Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")
@EnabledIf(NotAndroidCondition::class)
class ChangeLogResourceTest : FreeSpec({
    "current change log can be parsed" {
        val logs = Res.readBytes(FILES_CHANGE_LOG_JSON).decodeToString()
        val items = Json.decodeFromString<List<ChangeLogItem>>(logs)
        items.size shouldBeGreaterThan 3
    }
})
