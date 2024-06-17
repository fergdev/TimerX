package com.timerx.ui.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timerx.domain.TimerSet
import com.timerx.domain.length
import com.timerx.domain.timeFormatted
import com.timerx.ui.CustomIcons
import com.timerx.ui.common.AnimatedNumber
import com.timerx.ui.common.NumberIncrement
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableColumn
import sh.calvin.reorderable.ReorderableScope
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.copy
import timerx.shared.generated.resources.delete
import timerx.shared.generated.resources.down
import timerx.shared.generated.resources.sets

@Composable
internal fun Set(
    timerSet: TimerSet,
    interactions: CreateViewModel.Interactions,
    reorderableScope: ReorderableScope
) {
    Surface {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SetTopControls(interactions, timerSet, reorderableScope)
            ReorderableColumn(
                list = timerSet.intervals,
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                onSettle = { from, to ->
                    interactions.set.update.moveInterval(timerSet, from, to)
                },
            ) { _, timerInterval, _ ->

                key(timerInterval.id) {
                    Interval(
                        interval = timerInterval,
                        canSkipOnLastSet = timerSet.repetitions > 1,
                        interactions = interactions,
                        scope = this
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                AnimatedNumber(
                    modifier = Modifier.align(Alignment.Center),
                    value = timerSet.length(),
                    textStyle = MaterialTheme.typography.titleLarge
                ) { it.timeFormatted() }
                FilledTonalIconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = { interactions.set.update.newInterval(timerSet) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(Res.string.add)
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun SetTopControls(
    interactions: CreateViewModel.Interactions,
    timerSet: TimerSet,
    reorderableScope: ReorderableScope,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = stringResource(Res.string.sets)
        )
        NumberIncrement(
            modifier = Modifier.align(Alignment.Center),
            value = timerSet.repetitions,
            negativeButtonEnabled = timerSet.repetitions > 1,
            onChange = {
                interactions.set.update.updateRepetitions(
                    timerSet,
                    it
                )
            })
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(onClick = { interactions.set.duplicate(timerSet) }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = CustomIcons.contentCopy(),
                    contentDescription = stringResource(Res.string.copy)
                )
            }
            FilledTonalIconButton(onClick = { interactions.set.delete(timerSet) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.delete)
                )
            }
            Icon(
                imageVector = CustomIcons.dragHandle(),
                contentDescription = stringResource(Res.string.down),
                modifier = with(reorderableScope) {
                    Modifier.size(CustomIcons.defaultIconSize)
                        .draggableHandle()
                }
            )
        }
    }
}