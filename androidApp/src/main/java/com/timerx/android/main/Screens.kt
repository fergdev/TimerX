package com.timerx.android.main

import androidx.navigation.NavController
import com.timerx.android.main.Screens.ADD
import com.timerx.android.main.Screens.RUN
import com.timerx.android.main.Screens.SETTINGS

object Screens {
    const val MAIN = "main"
    const val ADD = "add"
    const val SETTINGS = "settings"

    const val RUN = "run"
    const val RUN_TEMPLATE = "run/{timerId}"
    const val RUN_TIMER_ID = "timerId"
}

fun NavController.runScreen(timerId: Long) {
    this.navigate("$RUN/$timerId")
}

fun NavController.addScreen() {
    this.navigate(ADD)
}

fun NavController.editScreen(id: Long) {
    this.navigate(ADD)
}

fun NavController.settingsScreen() {
    this.navigate(SETTINGS)
}
