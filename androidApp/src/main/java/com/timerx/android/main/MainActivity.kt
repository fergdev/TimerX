package com.timerx.android.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.timerx.android.TimerXTheme
import com.timerx.android.create.CreateScreen
import com.timerx.android.main.Screens.ADD
import com.timerx.android.main.Screens.MAIN
import com.timerx.android.main.Screens.RUN_TEMPLATE
import com.timerx.android.main.Screens.RUN_TIMER_ID
import com.timerx.android.main.Screens.SETTINGS
import com.timerx.android.run.RunScreen
import com.timerx.android.settings.SettingsScreen
import org.koin.androidx.compose.KoinAndroidContext
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
                        modifier = Modifier.fillMaxSize(), color = colorScheme.background
                    ) {
                        NavHost(navController = navController, startDestination = MAIN) {
                            composable(route = MAIN) {
                                MainScreen(navController = navController)
                            }
                            composable(route = RUN_TEMPLATE,
                                arguments = listOf(navArgument(RUN_TIMER_ID) {
                                    type = NavType.LongType
                                }),
                                enterTransition = {
                                    slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Left,
                                        animationSpec = tween(700)
                                    )
                                },
                                exitTransition = {
                                    slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Right,
                                        animationSpec = tween(700)
                                    )
                                }) {
                                RunScreen(navController = navController)
                            }
                            composable(ADD, enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Up,
                                    animationSpec = tween(700)
                                )
                            }, exitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Down,
                                    animationSpec = tween(700)
                                )
                            }) {
                                CreateScreen(navController = navController)
                            }
                            composable(SETTINGS, enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Down,
                                    animationSpec = tween(700)
                                )
                            }, exitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Up,
                                    animationSpec = tween(700)
                                )
                            }) {
                                SettingsScreen(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

