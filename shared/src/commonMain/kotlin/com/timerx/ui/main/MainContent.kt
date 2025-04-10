package com.timerx.ui.main

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.timerx.domain.SortTimersBy
import com.timerx.domain.imageVector
import com.timerx.domain.next
import com.timerx.domain.timeFormatted
import com.timerx.ui.ads.GoogleAd
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.DefaultLoading
import com.timerx.ui.common.RevealDirection
import com.timerx.ui.common.RevealSwipe
import com.timerx.ui.common.TCard
import com.timerx.ui.common.TIcon
import com.timerx.ui.common.TScaffold
import com.timerx.ui.common.TTopBar
import com.timerx.ui.common.branded
import com.timerx.ui.common.contrastSystemBarColor
import com.timerx.ui.common.doubleBranded
import com.timerx.ui.common.rememberRevealState
import com.timerx.ui.common.resetFast
import com.timerx.ui.logging.LogScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pro.respawn.flowmvi.api.IntentReceiver
import pro.respawn.flowmvi.compose.dsl.DefaultLifecycle
import pro.respawn.flowmvi.compose.dsl.subscribe
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.app_name
import timerx.shared.generated.resources.copy
import timerx.shared.generated.resources.delete
import timerx.shared.generated.resources.edit
import timerx.shared.generated.resources.enable
import timerx.shared.generated.resources.enable_notifications
import timerx.shared.generated.resources.enable_notifications_message
import timerx.shared.generated.resources.ignore
import timerx.shared.generated.resources.no_timers
import timerx.shared.generated.resources.settings
import timerx.shared.generated.resources.sort_order

private const val LOG_SCREEN_TAG = "Main"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainContent(mainComponent: MainComponent) {
    with(mainComponent) {
        LogScreen(LOG_SCREEN_TAG)
        var timerUpdatedId: Long? = null
        val state by subscribe(DefaultLifecycle) {
            when (it) {
                is MainAction.TimerUpdated -> timerUpdatedId = it.timerId
            }
        }
        contrastSystemBarColor(MaterialTheme.colorScheme.surface)

        val appBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        TScaffold(
            topBar = {
                TTopBar(
                    title = stringResource(Res.string.app_name).doubleBranded(),
                    scrollBehavior = appBarScrollBehavior,
                    actions = { TopAppBarActions(state.sortTimersBy, mainComponent) },
                )
            },
            fab = {
                FloatingActionButton(
                    modifier = Modifier.padding(
                        end = WindowInsets.navigationBars.asPaddingValues()
                            .calculateRightPadding(LayoutDirection.Ltr),
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    ),
                    onClick = { mainComponent.onCreate(null) }
                ) {
                    TIcon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(Res.string.add)
                    )
                }
            },
        ) { padding ->
            when (state) {
                is MainState.Loading -> DefaultLoading()
                is MainState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.padding(16.dp).align(Alignment.Center),
                            text = stringResource(Res.string.no_timers),
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                }

                is MainState.Content -> {
                    with(state as MainState.Content) {
                        Content(
                            modifier = Modifier.widthIn(max = 600.dp).align(Alignment.TopCenter),
                            state = this,
                            mainComponent = mainComponent,
                            appBarScrollBehavior = appBarScrollBehavior,
                            padding = padding,
                            timerUpdatedId = timerUpdatedId,
                        )
                        if (this.showNotificationsPermissionRequest) {
                            NotificationPermissions()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IntentReceiver<MainIntent>.Content(
    state: MainState.Content,
    mainComponent: MainComponent,
    appBarScrollBehavior: TopAppBarScrollBehavior,
    padding: PaddingValues,
    timerUpdatedId: Long?,
    modifier: Modifier = Modifier,
) {
    val systemBarPadding = WindowInsets.systemBars.asPaddingValues()
    val displayCutoutPadding = WindowInsets.displayCutout.asPaddingValues()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(timerUpdatedId) {
        val index = state.timers.indexOfFirst { it.id == timerUpdatedId }
        if (index >= 0) {
            lazyListState.animateScrollToItem(index = index)
        }
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(appBarScrollBehavior.nestedScrollConnection),
        state = lazyListState
    ) {
        item {
            Spacer(Modifier.height(padding.calculateTopPadding()))
        }
        items(items = state.timers, key = { it.id }) { timer ->
            val layoutDirection = LocalLayoutDirection.current
            Box(
                modifier = Modifier.padding(
                    start = systemBarPadding
                        .calculateStartPadding(layoutDirection)
                        .coerceAtLeast(
                            displayCutoutPadding.calculateStartPadding(
                                layoutDirection = LayoutDirection.Ltr
                            )
                        )
                        .coerceAtLeast(16.dp),
                    end = systemBarPadding
                        .calculateEndPadding(layoutDirection)
                        .coerceAtLeast(
                            displayCutoutPadding.calculateEndPadding(
                                layoutDirection = LayoutDirection.Ltr
                            )
                        )
                        .coerceAtLeast(16.dp),
                )
            ) {
                when (timer) {
                    is MainTimer -> {
                        TimerCard(
                            mainTimer = timer,
                            onNavigateRunScreen = mainComponent.onRun,
                            onNavigateEditScreen = mainComponent.onCreate,
                            highlight = timer.id == timerUpdatedId
                        )
                    }

                    is Ad -> {
                        Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            GoogleAd()
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(systemBarPadding.calculateBottomPadding()))
        }
    }
}

@Composable
private fun IntentReceiver<MainIntent>.TopAppBarActions(
    sortTimersBy: SortTimersBy,
    mainComponent: MainComponent,
) {
    IconButton(
        onClick = {
            intent(MainIntent.UpdateSortTimersBy(sortTimersBy.next()))
        }
    ) {
        Icon(
            imageVector = sortTimersBy.imageVector(),
            contentDescription = stringResource(Res.string.sort_order)
        )
    }
    IconButton(onClick = mainComponent.onSettings) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = stringResource(Res.string.settings)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun IntentReceiver<MainIntent>.NotificationPermissions() {
    ModalBottomSheet(onDismissRequest = {
        intent(MainIntent.HidePermissionsDialog)
    }) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.enable_notifications),
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.enable_notifications_message),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Button(
                modifier = Modifier.width(150.dp),
                onClick = {
                    intent(MainIntent.RequestNotificationsPermission)
                }
            ) {
                Text(text = stringResource(Res.string.enable))
            }
            Spacer(Modifier.height(8.dp))
            TextButton(
                modifier = Modifier.width(150.dp),
                onClick = { intent(MainIntent.IgnoreNotificationsPermission) }
            ) {
                Text(text = stringResource(Res.string.ignore))
            }
        }
    }
}

@Composable
private fun IntentReceiver<MainIntent>.TimerCard(
    mainTimer: MainTimer,
    onNavigateRunScreen: (Long) -> Unit,
    onNavigateEditScreen: (Long) -> Unit,
    highlight: Boolean,
) {
    val revealState = rememberRevealState(
        200.dp, directions = setOf(RevealDirection.EndToStart)
    )
    val coroutineScope = rememberCoroutineScope()
    val hideReveal = {
        coroutineScope.launch {
            revealState.resetFast()
        }
    }
    RevealSwipe(
        modifier = Modifier.padding(vertical = 8.dp),
        state = revealState,
        shape = MaterialTheme.shapes.large,
        card = { content ->
            TCard(
                modifier = Modifier.matchParentSize(),
                internalPadding = 0.dp,
                content = content
            )
        },
        hiddenContentEnd = {
            Row {
                IconButton(onClick = {
                    intent(MainIntent.DuplicateTimer(mainTimer))
                    hideReveal()
                }) {
                    TIcon(
                        imageVector = CustomIcons.contentCopy,
                        contentDescription = stringResource(Res.string.copy),
                    )
                }
                IconButton(onClick = { onNavigateEditScreen(mainTimer.id) }) {
                    TIcon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(Res.string.edit),
                    )
                }
                IconButton(onClick = {
                    intent(MainIntent.DeleteTimer(mainTimer))
                }) {
                    TIcon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.delete),
                    )
                }
            }
        },
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val borderColor by if (highlight) infiniteTransition.animateColor(
            initialValue = MaterialTheme.colorScheme.primary,
            targetValue = MaterialTheme.colorScheme.surface,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        ) else {
            mutableStateOf(Color.Transparent)
        }
        Box(
            modifier = Modifier
                .border(2.dp, borderColor, shape = MaterialTheme.shapes.medium)
                .fillMaxWidth()
                .clickable { onNavigateRunScreen(mainTimer.id) },
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = mainTimer.name.branded(),
                    style = MaterialTheme.typography.displaySmall,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = mainTimer.duration.timeFormatted(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = mainTimer.lastRunFormatted,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
