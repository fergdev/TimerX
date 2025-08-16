package com.intervallum.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction

@Composable
fun UnderlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    singleLine: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    cursorBrush: SolidColor = SolidColor(MaterialTheme.colorScheme.onSecondary),
    placeholder: @Composable (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (placeholder != null && value.isEmpty()) {
                placeholder()
            }
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                maxLines = maxLines,
                singleLine = singleLine,
                textStyle = textStyle.merge(color = color),
                keyboardOptions = keyboardOptions,
                onValueChange = onValueChange,
                cursorBrush = cursorBrush
            )
        }
        HorizontalDivider(color = color)
    }
}
