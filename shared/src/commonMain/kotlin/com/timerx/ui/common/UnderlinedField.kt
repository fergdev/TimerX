package com.timerx.ui.common

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
fun UnderlinedField(
    modifier: Modifier = Modifier,
    value: String,
    maxLines: Int = 1,
    singleLine: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    onValueChange: (String) -> Unit,
    cursorBrush: SolidColor
) {
    Column(modifier = modifier) {
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
        HorizontalDivider(color = color)
    }
}

