package com.timerx.android.main

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.timerx.android.R
import com.timerx.domain.Timer
import com.timerx.domain.formatted
import com.timerx.domain.length
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel = koinViewModel()
) {
    // TODO REMOVE this hack. This was put in here to refresh the data after adding a timer in the create screen
    LaunchedEffect(Unit) {
        mainViewModel.refreshData()
    }

    val state by mainViewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.appName)) },
                actions = {
                    IconButton(onClick = { navController.settingsScreen() }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.addScreen() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add)
                )
            }
        }) {
        Box(modifier = Modifier.padding(it)) {
            if (state.timers.isEmpty()) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(id = R.string.no_timers)
                )
            } else {
                LazyColumn {
                    items(state.timers) { timer ->
                        Timer(navController, timer, mainViewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun Timer(
    navController: NavHostController,
    timer: Timer,
    mainViewModel: MainViewModel
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ListItem(modifier = Modifier.clickable { navController.runScreen(timer.id) },
            headlineContent = { Text(text = timer.name) },
            supportingContent = { Text(text = timer.length().formatted()) },
            trailingContent = {
                Row {
                    IconButton(onClick = {
                        mainViewModel.duplicateTimer(timer)
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_copy),
                            contentDescription = stringResource(R.string.duplicate)
                        )
                    }
                    IconButton(onClick = {
                        navController.editScreen(timer.id)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit)
                        )
                    }
                    IconButton(onClick = {
                        mainViewModel.deleteTimer(timer)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete)
                        )
                    }
                }
            })
    }
}