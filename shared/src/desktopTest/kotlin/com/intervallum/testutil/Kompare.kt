package com.intervallum.testutil

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.fergdev.kompare.KReader
import com.fergdev.kompare.kompare
import intervallum.shared.generated.resources.Res
import io.kotest.core.test.TestScope

object RunContentTestReader : KReader {
    override suspend fun readBytes(path: String) = Res.readBytes(path)
}

context (ComposeUiTest, TestScope)
@OptIn(ExperimentalTestApi::class)
fun kompare() {
    kompare(RunContentTestReader)
}
