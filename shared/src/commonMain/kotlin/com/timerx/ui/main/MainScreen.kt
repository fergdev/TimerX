package com.timerx.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.timerx.ads.GoogleAd
import com.timerx.domain.timeFormatted
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.RevealDirection
import com.timerx.ui.common.RevealSwipe
import com.timerx.ui.common.contrastSystemBarColor
import com.timerx.ui.common.rememberRevealState
import com.timerx.ui.common.reset
import com.timerx.ui.navigation.Screen
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.app_name
import timerx.shared.generated.resources.completed_value
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
import timerx.shared.generated.resources.started_value

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
internal fun MainScreen(navigate: (Screen) -> Unit) {
    val viewModel: MainViewModel = koinViewModel(vmClass = MainViewModel::class)
    val state by viewModel.state.collectAsState()

    contrastSystemBarColor(MaterialTheme.colorScheme.surface)

    val appBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Box {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(Res.string.app_name)) },
                    scrollBehavior = appBarScrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = Color.Transparent,
                    ),
                    actions = { TopAppBarActions(viewModel.interactions, state, navigate) },
                )
            }, floatingActionButton = {
                FloatingActionButton(onClick = { navigate(Screen.CreateScreen()) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(Res.string.add)
                    )
                }
            }) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.loadingTimers) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (state.timers.isEmpty()) {
                    Text(
                        modifier = Modifier.padding(16.dp).align(Alignment.Center),
                        text = stringResource(Res.string.no_timers)
                    )
                } else {
                    val lazyListState = rememberLazyListState()
                    val reorderableLazyListState =
                        rememberReorderableLazyListState(lazyListState) { from, to ->
                            val fromItem = state.timers[from.index]
                            val toItem = state.timers[to.index]
                            if (fromItem is Timer && toItem is Timer) {
                                viewModel.interactions.swapTimers(
                                    fromItem, toItem
                                )
                            }
                        }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(appBarScrollBehavior.nestedScrollConnection),
                        state = lazyListState,
                    ) {
                        item {
                            Spacer(Modifier.height(paddingValues.calculateTopPadding()))
                        }
                        items(items = state.timers, key = { it.id }) { timer ->
                            ReorderableItem(state = reorderableLazyListState, key = timer.id) {
                                when (timer) {
                                    is Timer -> {
                                        Timer(
                                            timer = timer,
                                            interactions = viewModel.interactions,
                                            navigateRunScreen = { navigate(Screen.RunScreen(timer.id)) },
                                            navigateEditScreen = {
                                                navigate(
                                                    Screen.CreateScreen(
                                                        timer.id
                                                    )
                                                )
                                            },
                                            reorderableScope = this,
                                            isReorderable = state.sortTimersBy == SortTimersBy.SORT_ORDER
                                        )
                                    }

                                    is Ad -> {
                                        GoogleAd()
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(paddingValues.calculateBottomPadding()))
                        }
                    }
                }
            }

            NotificationPermissions(
                state, viewModel.interactions
            )
        }
    }
}

@Composable
private fun TopAppBarActions(
    interactions: MainViewModel.Interactions,
    state: MainViewModel.State,
    navigate: (Screen) -> Unit
) {
    IconButton(
        onClick = {
            interactions.updateSortTimersBy(state.sortTimersBy.next())
        }) {
        Icon(
            imageVector = state.sortTimersBy.imageVector(),
            contentDescription = stringResource(Res.string.sort_order)
        )
    }
    IconButton(onClick = { navigate(Screen.SettingsScreen) }) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = stringResource(Res.string.settings)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NotificationPermissions(
    state: MainViewModel.State, interactions: MainViewModel.Interactions
) {
    if (state.showNotificationsPermissionRequest) {
        ModalBottomSheet(onDismissRequest = {
            interactions.hidePermissionsDialog()
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
                Button(modifier = Modifier.width(150.dp),
                    onClick = { interactions.requestNotificationsPermission() }) {
                    Text(text = stringResource(Res.string.enable))
                }
                Spacer(Modifier.height(8.dp))
                TextButton(modifier = Modifier.width(150.dp),
                    onClick = { interactions.ignoreNotificationsPermission() }) {
                    Text(text = stringResource(Res.string.ignore))
                }
            }
        }
    }
}

@Composable
private fun Timer(
    timer: Timer,
    interactions: MainViewModel.Interactions,
    navigateRunScreen: (Long) -> Unit,
    navigateEditScreen: (Long) -> Unit,
    reorderableScope: ReorderableCollectionItemScope,
    isReorderable: Boolean
) {
    val revealState = rememberRevealState(
        200.dp, directions = setOf(RevealDirection.EndToStart)
    )
    val coroutineScope = rememberCoroutineScope()
    val hideReveal = {
        coroutineScope.launch {
            revealState.reset()
        }
    }
    RevealSwipe(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        state = revealState,
        shape = RoundedCornerShape(16.dp),
        backgroundCardStartColor = MaterialTheme.colorScheme.surface,
        backgroundCardEndColor = MaterialTheme.colorScheme.surfaceVariant,
        card = { shape, content ->
            OutlinedCard(
                modifier = Modifier.matchParentSize(),
                shape = shape,
                colors = CardDefaults.cardColors(),
                content = content
            )
        },
        hiddenContentEnd = {
            Row {
                IconButton(onClick = {
                    interactions.duplicateTimer(timer)
                    hideReveal()
                }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = CustomIcons.contentCopy,
                        contentDescription = stringResource(Res.string.copy)
                    )
                }
                IconButton(onClick = { navigateEditScreen(timer.id) }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(Res.string.edit)
                    )
                }
                IconButton(onClick = { interactions.deleteTimer(timer) }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.delete)
                    )
                }
            }
        },
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().clickable { navigateRunScreen(timer.id) }
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.weight(1F)) {
                    Text(
                        text = timer.name,
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text(
                        text = timer.duration.toInt().timeFormatted(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(text = stringResource(Res.string.started_value, timer.startedCount))
                    Text(
                        text = stringResource(
                            Res.string.completed_value,
                            timer.completedCount
                        )
                    )
                    Text(text = timer.lastRunFormatted)
                }
                if (isReorderable) {
                    Icon(
                        modifier = with(reorderableScope) {
                            Modifier.size(24.dp).draggableHandle()
                        },
                        imageVector = CustomIcons.dragHandle,
                        contentDescription = stringResource(Res.string.copy)
                    )
                }
            }
        }
    }
}