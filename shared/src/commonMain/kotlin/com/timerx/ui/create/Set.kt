@file:OptIn(ExperimentalResourceApi::class)

package com.timerx.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timerx.CustomIcons
import com.timerx.domain.TimerSet
import com.timerx.domain.length
import com.timerx.domain.timeFormatted
import com.timerx.ui.common.NumberIncrement
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.copy
import timerx.shared.generated.resources.delete
import timerx.shared.generated.resources.down
import timerx.shared.generated.resources.sets
import timerx.shared.generated.resources.up

@Composable
internal fun Set(
    timerSet: TimerSet,
    interactions: CreateViewModel.Interactions
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SetTopControls(interactions, timerSet)

            timerSet.intervals.forEach { interval ->
                Interval(
                    interval = interval,
                    canSkipOnLastSet = timerSet.repetitions > 1,
                    interactions
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(text = timerSet.length().timeFormatted())

            SetBottomControls(interactions, timerSet)
        }
    }
}

@Composable
private fun SetBottomControls(
    interactions: CreateViewModel.Interactions,
    timerSet: TimerSet,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FilledTonalIconButton(onClick = { interactions.set.duplicate(timerSet) }) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = CustomIcons.contentCopy(),
                contentDescription = stringResource(Res.string.copy)
            )
        }
        FilledTonalIconButton(onClick = { interactions.set.update.newInterval(timerSet) }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.add)
            )
        }
        FilledTonalIconButton(onClick = { interactions.set.delete(timerSet) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(Res.string.delete)
            )
        }
    }
}

@Composable
private fun SetTopControls(
    interactions: CreateViewModel.Interactions,
    timerSet: TimerSet,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        OutlinedIconButton(onClick = { interactions.set.moveDown(timerSet) }) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = stringResource(Res.string.down)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(Res.string.sets))
            NumberIncrement(
                value = timerSet.repetitions,
                onChange = {
                    interactions.set.update.updateRepetitions(
                        timerSet,
                        it
                    )
                })
        }
        OutlinedIconButton(onClick = { interactions.set.moveUp(timerSet) }) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = stringResource(Res.string.up)
            )
        }
    }
}
