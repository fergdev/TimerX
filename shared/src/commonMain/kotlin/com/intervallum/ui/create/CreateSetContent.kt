package com.intervallum.ui.create

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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.intervallum.domain.timeFormatted
import com.intervallum.ui.common.AnimatedNumber
import com.intervallum.ui.common.CustomIcons
import com.intervallum.ui.common.NumberIncrement
import com.intervallum.ui.common.RevealDirection
import com.intervallum.ui.common.RevealSwipe
import com.intervallum.ui.common.TCard
import com.intervallum.ui.common.TIcon
import com.intervallum.ui.common.rememberRevealState
import com.intervallum.ui.common.reset
import com.intervallum.ui.create.CreateScreenIntent.DeleteSet
import com.intervallum.ui.create.CreateScreenIntent.DuplicateSet
import com.intervallum.ui.create.CreateScreenIntent.UpdateSetRepetitions
import intervallum.shared.generated.resources.Res
import intervallum.shared.generated.resources.add
import intervallum.shared.generated.resources.copy
import intervallum.shared.generated.resources.delete
import intervallum.shared.generated.resources.down
import intervallum.shared.generated.resources.sets
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pro.respawn.flowmvi.api.IntentReceiver
import sh.calvin.reorderable.ReorderableColumn
import sh.calvin.reorderable.ReorderableScope

@Composable
internal fun IntentReceiver<CreateScreenIntent>.CreateSetContent(
    timerSet: CreateTimerSet,
    canVibrate: Boolean,
    reorderableScope: ReorderableScope
) {
    val revealState = rememberRevealState(
        maxRevealDp = 100.dp,
        directions = setOf(RevealDirection.EndToStart)
    )
    val coroutineScope = rememberCoroutineScope()
    val hideReveal = {
        coroutineScope.launch {
            revealState.reset()
        }
    }
    RevealSwipe(
        state = revealState,
        shape = MaterialTheme.shapes.medium,
        card = { content ->
            TCard(
                internalPadding = 0.dp,
                modifier = Modifier.matchParentSize(),
                content = content
            )
        },
        hiddenContentEnd = {
            Row {
                FilledTonalIconButton(onClick = {
                    hideReveal()
                    intent(DuplicateSet(timerSet))
                }) {
                    TIcon(
                        modifier = Modifier.size(24.dp),
                        imageVector = CustomIcons.contentCopy,
                        contentDescription = stringResource(Res.string.copy)
                    )
                }
                FilledTonalIconButton(onClick = {
                    hideReveal()
                    intent(DeleteSet(timerSet))
                }) {
                    TIcon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.delete)
                    )
                }
            }
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SetTopControls(timerSet, reorderableScope)
            val hapticFeedback = LocalHapticFeedback.current
            ReorderableColumn(
                list = timerSet.intervals,
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                onSettle = { from, to ->
                    intent(CreateScreenIntent.MoveInterval(timerSet, from, to))
                },
                onMove = {
                    hapticFeedback.performHapticFeedback(LongPress)
                }
            ) { _, timerInterval, _ ->
                key(timerInterval.id) {
                    CreateIntervalContent(
                        interval = timerInterval,
                        canSkipOnLastSet = timerSet.repetitions > 1,
                        canVibrate = canVibrate,
                        scope = this@ReorderableColumn
                    )
                }
            }

            Box(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp).fillMaxWidth()) {
                AnimatedNumber(
                    modifier = Modifier.align(Alignment.Center),
                    value = timerSet.length().timeFormatted(),
                    textStyle = MaterialTheme.typography.titleLarge
                )
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
    timerSet: CreateTimerSet,
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
            TIcon(
                imageVector = CustomIcons.dragHandle,
                contentDescription = stringResource(Res.string.down),
                modifier = with(reorderableScope) {
                    Modifier.draggableHandle()
                }
            )
        }
    }
}
