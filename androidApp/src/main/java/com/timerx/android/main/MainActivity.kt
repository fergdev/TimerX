package com.timerx.android.main

import android.os.Bundle
<<<<<<< HEAD
import com.timerx.TimerXTheme
import com.timerx.sharedModule
import com.timerx.ui.App
import moe.tlaster.precompose.lifecycle.PreComposeActivity
import moe.tlaster.precompose.lifecycle.setContent
import org.koin.compose.KoinApplication
=======
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.timerx.TimerXTheme
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
>>>>>>> 38d1bf9 (Initial kmp commit.)

class MainActivity : PreComposeActivity() {

<<<<<<< HEAD
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KoinApplication(
                application = {
                    modules(sharedModule())
=======
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
private fun App() {
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
>>>>>>> 38d1bf9 (Initial kmp commit.)
                }
            ) {
                TimerXTheme {
                    App()
                }
            }
        }
    }
}