package com.timerx.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timerx.domain.Timer
import com.timerx.domain.length
import com.timerx.domain.timeFormatted
import com.timerx.ui.CustomIcons
import com.timerx.ui.SetStatusBarColor
import moe.tlaster.precompose.koin.koinViewModel
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.app_name
import timerx.shared.generated.resources.copy
import timerx.shared.generated.resources.delete
import timerx.shared.generated.resources.edit
import timerx.shared.generated.resources.no_timers
import timerx.shared.generated.resources.settings

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
internal fun MainScreen(
    navigateSettingsScreen: () -> Unit,
    navigateAddScreen: () -> Unit,
    navigateEditScreen: (String) -> Unit,
    navigateRunScreen: (String) -> Unit
) {
    val viewModel: MainViewModel = koinViewModel(vmClass = MainViewModel::class)

    LaunchedEffect(Unit) {
        viewModel.interactions.refreshData()
    }

    SetStatusBarColor(MaterialTheme.colorScheme.surface)

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.app_name)) },
                actions = {
                    IconButton(onClick = navigateSettingsScreen) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(Res.string.settings)
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateAddScreen) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.add)
                )
            }
        }) { paddingValues ->

        Box(modifier = Modifier.padding(paddingValues)) {
            if (state.timers.isEmpty()) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(Res.string.no_timers)
                )
            } else {
                val lazyListState = rememberLazyListState()
                val reorderableLazyListState =
                    rememberReorderableLazyListState(lazyListState) { from, to ->
                        viewModel.interactions.swapTimers(from.index, to.index)
                    }

                LazyColumn(modifier = Modifier.fillMaxSize(), state = lazyListState) {
                    items(state.timers, key = { it.id }) { timer ->
                        ReorderableItem(reorderableLazyListState, key = timer.id) {
                            Timer(
                                modifier = Modifier.longPressDraggableHandle(),
                                timer = timer,
                                interactions = viewModel.interactions,
                                navigateRunScreen = navigateRunScreen,
                                navigateEditScreen = navigateEditScreen,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Timer(
    modifier: Modifier,
    timer: Timer,
    interactions: MainViewModel.Interactions,
    navigateRunScreen: (String) -> Unit,
    navigateEditScreen: (String) -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ListItem(
            modifier = Modifier.clickable { navigateRunScreen(timer.id) },
            headlineContent = {
                Text(
                    text = timer.name,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            supportingContent = { Text(text = timer.length().timeFormatted()) },
            trailingContent = {
                Row {
                    IconButton(
                        onClick = { interactions.duplicateTimer(timer) }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = CustomIcons.contentCopy(),
                            contentDescription = stringResource(Res.string.copy)
                        )
                    }
                    IconButton(
                        onClick = { navigateEditScreen(timer.id) }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(Res.string.edit)
                        )
                    }
                    IconButton(
                        onClick = { interactions.deleteTimer(timer) }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.delete)
                        )
                    }
                }
            }
        )
    }
}
