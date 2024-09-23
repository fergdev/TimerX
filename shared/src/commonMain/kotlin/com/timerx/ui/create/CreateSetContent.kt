package com.timerx.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timerx.domain.TimerSet
import com.timerx.domain.length
import com.timerx.domain.timeFormatted
import com.timerx.ui.common.AnimatedNumber
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.NumberIncrement
import com.timerx.ui.common.RevealDirection
import com.timerx.ui.common.RevealSwipe
import com.timerx.ui.common.rememberRevealState
import com.timerx.ui.create.CreateScreenIntent.DeleteSet
import com.timerx.ui.create.CreateScreenIntent.DuplicateSet
import com.timerx.ui.create.CreateScreenIntent.UpdateSetRepetitions
import org.jetbrains.compose.resources.stringResource
import pro.respawn.flowmvi.api.IntentReceiver
import sh.calvin.reorderable.ReorderableColumn
import sh.calvin.reorderable.ReorderableScope
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.copy
import timerx.shared.generated.resources.delete
import timerx.shared.generated.resources.down
import timerx.shared.generated.resources.sets

@Composable
internal fun IntentReceiver<CreateScreenIntent>.CreateSetContent(
    timerSet: TimerSet,
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
        shape = RoundedCornerShape(12.dp),
        card = { shape, content ->
            ElevatedCard(
                modifier = Modifier.matchParentSize(),
                shape = shape,
                content = content
            )
        },
        hiddenContentEnd = {
            Row {
                FilledTonalIconButton(onClick = { intent(DuplicateSet(timerSet)) }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = CustomIcons.contentCopy,
                        contentDescription = stringResource(Res.string.copy)
                    )
                }
                FilledTonalIconButton(onClick = { intent(DeleteSet(timerSet)) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.delete)
                    )
                }
            }
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SetTopControls(timerSet, reorderableScope)
            ReorderableColumn(
                list = timerSet.intervals,
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                onSettle = { from, to ->
                    intent(CreateScreenIntent.MoveInterval(timerSet, from, to))
                },
            ) { _, timerInterval, _ ->
                key(timerInterval.id) {
                    CreateIntervalContent(
                        interval = timerInterval,
                        canSkipOnLastSet = timerSet.repetitions > 1,
                        scope = this@ReorderableColumn
                    )
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
                    onClick = { intent(CreateScreenIntent.NewInterval(timerSet)) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(Res.string.add)
                    )
                }
            }
        }
    }
}

@Composable
private fun IntentReceiver<CreateScreenIntent>.SetTopControls(
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
            value = timerSet.repetitions.toLong(),
            textStyle = MaterialTheme.typography.titleLarge,
            negativeButtonEnabled = timerSet.repetitions > 1,
            onChange = {
                intent(UpdateSetRepetitions(timerSet, it.toInt()))
            }
        )
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = CustomIcons.dragHandle,
                contentDescription = stringResource(Res.string.down),
                modifier = with(reorderableScope) {
                    Modifier.size(CustomIcons.defaultIconSize)
                        .draggableHandle()
                }
            )
        }
    }
}
