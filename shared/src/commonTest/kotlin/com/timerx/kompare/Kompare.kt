package com.timerx.kompare

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.SemanticsConfiguration
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.toSize
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.runBlocking
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timerx.shared.generated.resources.Res

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.serializeDomToNode() = onRoot().fetchSemanticsNode().serializeDomNode()

fun SemanticsNode.serializeDomNode(): KNode {
    val childrenNode = children.map { it.serializeDomNode() }
    return KNode(
        this.id,
        this.unclippedGlobalBounds,
        this.config.toPropertyMap(),
        childrenNode
    )
}

fun SemanticsConfiguration.toPropertyMap() =
    mutableMapOf<String, String>().apply {
        this@toPropertyMap.forEach {
            this@apply.put(it.key.toString(), it.value.toString())
        }
    }

@Suppress("ExpressionBodySyntax")
val SemanticsNode.unclippedGlobalBounds: Rect
    get() {
        return createRect(positionInWindow, size.toSize())
    }

@Serializable
data class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float)

fun createRect(offset: Offset, size: Size) =
    Rect(
        offset.x,
        offset.y,
        offset.x + size.width,
        offset.y + size.height
    )

context (ComposeUiTest, TestScope)
@OptIn(ExperimentalTestApi::class)
fun kompare() {
    val actualKTree = serializeDomToNode()
    val path = "files/kompare/" + getFullTestName() + ".json"
    val expected = try {
        runBlocking { Res.readBytes(path).decodeToString() }
    } catch (t: Throwable) {
        println("Unable to load path '$path'")
        println("Possible missing test data???")
        println("Add following to complete test")
        println("Path '$path'")
        println("Data '${Json.encodeToString(actualKTree)}'")
        throw t
    }
    require(expected.isNotBlank()) {
        "Kompare: empty file at path '$path'" +
            "Actual tree '${Json.encodeToString(actualKTree)}'"
    }
    try {
        val expectedKTree = Json.decodeFromString<KNode>(expected)
        actualKTree shouldBe expectedKTree
    } catch (assertionFailedError: Throwable) {
        println("expected $expected")
        println("actual ${Json.encodeToString(actualKTree)}")

        throw assertionFailedError
    }
}

fun TestScope.getFullTestName(): String {
    val specName = this.testCase.spec::class.simpleName ?: "unknown"
    val list = mutableListOf<String>()
    var testCase: TestCase? = this.testCase
    while (testCase != null) {
        list.add(testCase.name.originalName)
        testCase = testCase.parent
    }
    list.add(specName)
    list.reverse()
    return list.joinToString("-")
}
