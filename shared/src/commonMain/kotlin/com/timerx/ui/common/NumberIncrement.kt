@file:OptIn(ExperimentalResourceApi::class)

package com.timerx.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timerx.CustomIcons
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.minus

@Composable
fun NumberIncrement(
    value: Int,
    formatter: (Int) -> String = { "$it" },
    onChange: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(CustomIcons.defaultIconSize)
                .clickable {
                    onChange(value - 1)
                },
            imageVector = CustomIcons.checkIndeterminateSmall(),
            contentDescription = stringResource(Res.string.minus)
        )
        Text(text = formatter(value))
        Icon(
            modifier = Modifier.size(CustomIcons.defaultIconSize)
                .clickable {
                    onChange(value + 1)
                },
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(Res.string.minus)
        )
    }
}
