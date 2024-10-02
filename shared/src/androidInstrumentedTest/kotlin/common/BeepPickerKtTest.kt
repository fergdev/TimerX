package common

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.timerx.sound.Beep
import com.timerx.ui.common.BeepPicker
import de.mannodermaus.junit5.compose.createComposeExtension
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class BeepPickerKtTest {

    @Suppress("JUnitMalformedDeclaration")
    @JvmField
    @RegisterExtension
    @OptIn(ExperimentalTestApi::class)
    val extension = createComposeExtension()

    @Test
    fun `show picker - displays all alerts`() = extension.use {
        setContent {
            BeepPicker { }
        }
        Beep.entries.forEach {
            onNodeWithText(it.displayName).assertIsDisplayed()
        }
    }

    @Test
    fun `on selected - invokes callback`() = extension.use {
        var invoked = false
        setContent {
            BeepPicker { invoked = true }
        }
        onNodeWithText(Beep.entries[0].displayName).performClick()
        assertThat(invoked, `is`(true))
    }
}
