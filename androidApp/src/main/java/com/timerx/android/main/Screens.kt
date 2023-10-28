package com.timerx.android.main

import androidx.navigation.NavController
import com.timerx.android.main.Screens.CREATE
import com.timerx.android.main.Screens.RUN
import com.timerx.android.main.Screens.SETTINGS

object Screens {
    const val MAIN = "main"
    const val SETTINGS = "settings"

    const val CREATE = "create"
    const val CREATE_TEMPLATE = "create/{timerId}"

    const val RUN = "run"
    const val RUN_TEMPLATE = "run/{timerId}"

    const val TIMER_ID = "timerId"
}

fun NavController.runScreen(timerId: Long) {
    this.navigate("$RUN/$timerId")
}

fun NavController.addScreen() {
    this.navigate("$CREATE/-1")
}

fun NavController.editScreen(id: Long) {
    this.navigate("$CREATE/$id")
}

fun NavController.settingsScreen() {
    this.navigate(SETTINGS)
}
