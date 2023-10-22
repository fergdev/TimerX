package com.timerx.android.create

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.timerx.android.main.Screens.MAIN

@Composable
fun CreateScreen(navController: NavHostController) {
    Column {
        Text(text = "Create Screen")
        Button(onClick = { navController.navigate(MAIN) }) {
            Text(text = "Home")
        }
    }
}
