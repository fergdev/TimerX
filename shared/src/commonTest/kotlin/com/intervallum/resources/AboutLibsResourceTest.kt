package com.intervallum.resources

import com.mikepenz.aboutlibraries.Libs
import com.intervallum.testutil.NotAndroidCondition
import com.intervallum.ui.settings.about.aboutlibs.FILES_ABOUT_LIBRARIES_JSON
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import intervallum.shared.generated.resources.Res

@Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")
@EnabledIf(NotAndroidCondition::class)
class AboutLibsResourceTest : FreeSpec({
    "about libs can be parsed" {
        val data = Res.readBytes(FILES_ABOUT_LIBRARIES_JSON).decodeToString()
        val libs = Libs.Builder()
            .withJson(data)
            .build()
        libs.licenses.size shouldBeGreaterThan 1
        libs.libraries.size shouldBeGreaterThan 1
    }
})
