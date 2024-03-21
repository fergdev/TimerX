package com.timerx.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.timerx.domain.Timer
import com.timerx.domain.formatted
import com.timerx.domain.length
import moe.tlaster.precompose.koin.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
    navigateSettingsScreen: () -> Unit,
    navigateAddScreen: () -> Unit,
    navigateEditScreen: (String) -> Unit,
    navigateRunScreen: (String) -> Unit
) {
    val viewModel: MainViewModel = koinViewModel(vmClass = MainViewModel::class)
    // TODO REMOVE this hack. This was put in here to refresh the data after adding a timer in the create screen
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "TimerX") },
                actions = {
                    IconButton(onClick = navigateSettingsScreen) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateAddScreen) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }) {

        Box(modifier = Modifier.padding(it)) {
            if (state.timers.isEmpty()) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "No timers"
                )
            } else {
                LazyColumn {
                    items(state.timers) { timer ->
                        Timer(
                            timer = timer,
                            duplicateTimer = viewModel::duplicateTimer,
                            deleteTimer = viewModel::deleteTimer,
                            navigateRunScreen = navigateRunScreen,
                            navigateEditScreen = navigateEditScreen,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Timer(
    timer: Timer,
    duplicateTimer: (Timer) -> Unit,
    deleteTimer: (Timer) -> Unit,
    navigateRunScreen: (String) -> Unit,
    navigateEditScreen: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ListItem(
            modifier = Modifier.clickable { navigateRunScreen(timer.id) },
            headlineContent = { Text(text = timer.name) },
            supportingContent = { Text(text = timer.length().formatted()) },
            trailingContent = {
                Row {
                    IconButton(onClick = { duplicateTimer(timer) }) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { navigateEditScreen(timer.id) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { deleteTimer(timer) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                }
            }
        )
    }
}

