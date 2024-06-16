package com.timerx.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction

@Composable
fun UnderlinedTextBox(
    modifier: Modifier = Modifier,
    value: String,
    maxLines: Int = 1,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    color: Color = Color.Unspecified,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    onValueChange: (String) -> Unit
) {
    Column {
        BasicTextField(
            modifier = modifier,
            value = value,
            maxLines = maxLines,
            textStyle = textStyle.merge(color = color),
            keyboardOptions = keyboardOptions,
            onValueChange = onValueChange
        )
        HorizontalDivider(color = color)
    }
}

