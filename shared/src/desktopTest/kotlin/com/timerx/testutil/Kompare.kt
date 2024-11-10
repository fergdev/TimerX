package com.timerx.testutil

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.fergdev.kompare.KReader
import com.fergdev.kompare.kompare
import io.kotest.core.test.TestScope
import timerx.shared.generated.resources.Res

object RunContentTestReader : KReader {
    override suspend fun readBytes(path: String) = Res.readBytes(path)
}

context (ComposeUiTest, TestScope)
@OptIn(ExperimentalTestApi::class)
fun kompare() {
    kompare(RunContentTestReader)
}
