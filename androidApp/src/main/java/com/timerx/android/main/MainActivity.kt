package com.timerx.android.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.timerx.android.R
import com.timerx.android.TimerXTheme
import com.timerx.android.add.CreateScreen
import com.timerx.android.main.Screens.ADD
import com.timerx.android.main.Screens.MAIN
import com.timerx.android.main.Screens.RUN_TEMPLATE
import com.timerx.android.main.Screens.RUN_TIMER_ID
import com.timerx.android.run.RunScreen
import com.timerx.domain.formatted
import com.timerx.domain.length
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerXTheme {
                KoinAndroidContext {
                    val navController = rememberNavController()
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = colorScheme.background
                    ) {
                        NavHost(navController = navController, startDestination = MAIN) {
                            composable(MAIN) {
                                MainScreen(navController = navController)
                            }
                            composable(
                                RUN_TEMPLATE, arguments = listOf(navArgument(RUN_TIMER_ID) {
                                    type = NavType.LongType
                                })
                            ) {
                                RunScreen(navController = navController)
                            }
                            composable(ADD) {
                                CreateScreen(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel = koinViewModel()
) {
    // HACK TODO REMOVE this. This was put in here to refresh the data after adding a timer in the create screen
    LaunchedEffect(Unit) {
        mainViewModel.refreshData()
    }

    val state by mainViewModel.state.collectAsState()
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = stringResource(id = R.string.appName)) })
    }, floatingActionButton = {
        FloatingActionButton(onClick = { navController.addScreen() }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add)
            )
        }
    }) {
        Box(modifier = Modifier.padding(it)) {
            if (state.timers.isEmpty()) {
                Text(text = stringResource(id = R.string.noTimers))
            } else {
                LazyColumn {
                    items(state.timers.size) { index ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            val timer = state.timers[index]
                            ListItem(modifier = Modifier.clickable { navController.runScreen(timer.id) },
                                headlineContent = { Text(text = timer.name) },
                                supportingContent = { Text(text = timer.length().formatted()) },
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = {
                                            navController.editScreen(timer.id)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null
                                            )
                                        }
                                        IconButton(onClick = {
                                            mainViewModel.deleteTimer(timer)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                })
                        }
                    }
                }
            }
        }
    }
}