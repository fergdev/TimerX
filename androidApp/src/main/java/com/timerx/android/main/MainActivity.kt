package com.timerx.android.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.timerx.android.MyApplicationTheme
import com.timerx.android.main.Screens.CREATE
import com.timerx.android.main.Screens.MAIN
import com.timerx.android.main.Screens.RUN
import com.timerx.android.create.CreateScreen
import com.timerx.android.main.Screens.RUN_TEMPLATE
import com.timerx.android.main.Screens.RUN_TIMER_ID
import com.timerx.android.run.RunScreen
import com.timerx.domain.Timer
import com.timerx.repository.TimerRepository
import kotlinx.collections.immutable.PersistentList
import org.koin.androidx.compose.koinViewModel

object Screens {
    const val MAIN = "main"
    const val CREATE = "create"

    const val RUN = "run"
    const val RUN_TEMPLATE = "run/{timerId}"
    const val RUN_TIMER_ID = "timerId"
}

class MainViewModel(private val timerRepository: TimerRepository) : ViewModel() {
    fun timers(): PersistentList<Timer> {
        return timerRepository.timers()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("Main Activity OnCreate")
        setContent {
            MyApplicationTheme {

                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = MAIN) {
                        composable(MAIN) {
                            MainScreen(navController = navController, koinViewModel())
                        }
                        composable(
                            RUN_TEMPLATE, arguments = listOf(
                                navArgument(RUN_TIMER_ID) {
                                    type = NavType.IntType
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    navController: NavHostController, mainViewModel: MainViewModel
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "TimerX") })
    }, floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate(CREATE) }) {
            Text(text = "Add")
        }
    }) {
        LazyColumn(modifier = Modifier.padding(it)) {
            val timers = mainViewModel.timers()
            items(timers.size) { index ->
                val timer = timers[index]
                ListItem(modifier = Modifier.clickable { navController.navigate("$RUN/${timer.id}") },
                    headlineContent = { Text(text = timer.name) }
                )
            }
        }
    }
}