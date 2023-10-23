package com.timerx.android.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.timerx.android.TimerXTheme
import com.timerx.android.main.Screens.CREATE
import com.timerx.android.main.Screens.MAIN
import com.timerx.android.main.Screens.RUN
import com.timerx.android.create.CreateScreen
import com.timerx.android.main.Screens.RUN_TEMPLATE
import com.timerx.android.main.Screens.RUN_TIMER_ID
import com.timerx.android.run.RunScreen
import com.timerx.database.TimerDatabase
import com.timerx.domain.Timer
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

object Screens {
    const val MAIN = "main"
    const val CREATE = "create"

    const val RUN = "run"
    const val RUN_TEMPLATE = "run/{timerId}"
    const val RUN_TIMER_ID = "timerId"
}

class MainViewModel(private val timerRepository: TimerDatabase) : ViewModel() {

    data class State(val timers: PersistentList<Timer> = persistentListOf())

    private val _stateFlow = MutableStateFlow(State())
    val state: StateFlow<State> = _stateFlow

    init {
        refreshData()
    }

    fun refreshData() {
        _stateFlow.update {
            State(timerRepository.getTimers().toPersistentList())
        }
    }

    fun deleteTimer(timer: Timer) {
        timerRepository.deleteTimer(timer)
        refreshData()
    }
}

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
                        color = MaterialTheme.colorScheme.background
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
                            composable(CREATE) {
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
        TopAppBar(title = { Text(text = "TimerX") })
    }, floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate(CREATE) }) {
            Text(text = "Add")
        }
    }) {
        Box(modifier = Modifier.padding(it)) {
            if (state.timers.isEmpty()) {
                Text(text = "No timers yet, try adding one :)")
            } else {
                LazyColumn {
                    items(state.timers.size) { index ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            val timer = state.timers[index]
                            ListItem(modifier = Modifier.clickable { navController.navigate("$RUN/${timer.id}") },
                                headlineContent = { Text(text = timer.name) },
                                trailingContent = {
                                    IconButton(onClick = {
                                        mainViewModel.deleteTimer(timer)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null
                                        )
                                    }
                                })
                        }
                    }
                }
            }
        }
    }
}