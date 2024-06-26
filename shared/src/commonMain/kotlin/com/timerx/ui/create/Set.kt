package com.timerx.ui.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.timerx.domain.TimerSet
import com.timerx.domain.length
import com.timerx.domain.timeFormatted
import com.timerx.ui.CustomIcons
import com.timerx.ui.common.AnimatedNumber
import com.timerx.ui.common.NumberIncrement
import com.timerx.ui.common.RevealDirection
import com.timerx.ui.common.RevealSwipe
import com.timerx.ui.common.rememberRevealState
import kotlinx.coroutines.delay
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
    val revealState = rememberRevealState(
        maxRevealDp = 100.dp,
        directions = setOf(
            RevealDirection.EndToStart
        )
    )
    RevealSwipe(
        state = revealState,
        backgroundCardEndColor = MaterialTheme.colorScheme.surface,
        card = { shape, content ->
            Card(
                modifier = Modifier.matchParentSize(),
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = Color.Transparent
                ),
                shape = shape,
                content = content
            )
        },
        hiddenContentEnd = {
            Row {
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
            }
        }
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
                        var visible by remember { mutableStateOf(true) }
                        LaunchedEffect(visible) {
                            if (!visible) {
                                delay(400)
                                interactions.interval.delete(timerInterval)
                            }
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = expandIn(animationSpec = tween(400)),
                            exit = shrinkOut(animationSpec = tween((400)))
                        ) {
                            Interval(
                                interval = timerInterval,
                                canSkipOnLastSet = timerSet.repetitions > 1,
                                interactions = interactions,
                                scope = this@ReorderableColumn
                            ) { visible = false }
                        }
                    }
                }

                Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
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
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = stringResource(Res.string.sets),
            style = MaterialTheme.typography.titleMedium
        )
        NumberIncrement(
            modifier = Modifier.align(Alignment.Center),
            value = timerSet.repetitions,
            textStyle = MaterialTheme.typography.titleLarge,
            negativeButtonEnabled = timerSet.repetitions > 1,
            onChange = {
                interactions.set.update.updateRepetitions(
                    timerSet,
                    it
                )
            })
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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