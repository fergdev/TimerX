package com.timerx.android.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.timerx.android.main.Screens.CREATE_TEMPLATE
import com.timerx.android.main.Screens.MAIN
import com.timerx.android.main.Screens.RUN_TEMPLATE
import com.timerx.android.main.Screens.SETTINGS
import com.timerx.android.main.Screens.TIMER_ID
import com.timerx.android.run.RunScreen
import com.timerx.android.settings.SettingsScreen
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

private const val ENTER_DURATION = 400
private const val EXIT_DURATION = 200

class MainActivity : ComponentActivity() {

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerXTheme {
                KoinAndroidContext {
                    val navController = rememberNavController()
                    val navigateUp: () -> Unit = { navController.navigateUp() }

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = colorScheme.background
                    ) {
                        NavHost(navController = navController, startDestination = MAIN) {
                            composable(route = MAIN, enterTransition = {
                                fadeIn(animationSpec = tween(ENTER_DURATION))
                            }, exitTransition = {
                                fadeOut(animationSpec = tween(EXIT_DURATION))
                            }) {
                                MainScreen(
                                    navigateSettingsScreen = navController::settingsScreen,
                                    navigateAddScreen = navController::addScreen,
                                    navigateEditScreen = navController::editScreen,
                                    navigateRunScreen = navController::runScreen
                                )
                            }
                            composable(route = RUN_TEMPLATE,
                                arguments = listOf(navArgument(TIMER_ID) {
                                    type = NavType.LongType
                                }),
                                enterTransition = {
                                    slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Left,
                                        animationSpec = tween(ENTER_DURATION)
                                    )
                                },
                                exitTransition = {
                                    slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Right,
                                        animationSpec = tween(EXIT_DURATION)
                                    )
                                }) {
                                RunScreen(navigateUp)
                            }
                            composable(CREATE_TEMPLATE, arguments = listOf(navArgument(TIMER_ID) {
                                type = NavType.LongType
                                defaultValue = -1L
                            }),
                                enterTransition = {
                                    slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Up,
                                        animationSpec = tween(ENTER_DURATION)
                                    )
                                }, exitTransition = {
                                    slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Down,
                                        animationSpec = tween(EXIT_DURATION)
                                    )
                                }) {
                                CreateScreen(navigateUp = navigateUp)
                            }
                            composable(SETTINGS,
                                enterTransition = {
                                    slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Down,
                                        animationSpec = tween(ENTER_DURATION)
                                    )
                                }, exitTransition = {
                                    slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Up,
                                        animationSpec = tween(EXIT_DURATION)
                                    )
                                }) {
                                SettingsScreen(navigateUp = navigateUp)
                            }
                        }
                    }
                }
            }
        }
    }
}

