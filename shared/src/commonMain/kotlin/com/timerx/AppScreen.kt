package com.timerx

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timerx.repository.TimerRepository

val timerRepository = TimerRepository.TimerRepositoryImpl()
val timers = timerRepository.timers()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "TimerX") })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {}) {
            Text(text = "Add")
        }
    }) {
        Box(modifier = androidx.compose.ui.Modifier.padding(it).clickable {  }) {
            if (timers.isEmpty()) {
                Text(text = "No timers yet, try adding one :)")
            } else {
                LazyColumn {
                    items(timers.size) { index ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            val timer = timers[index]
                            ListItem(modifier = Modifier.clickable {},
                                headlineContent = { Text(text = timer.name) },
                                trailingContent = {
                                    IconButton(onClick = {
//                                        mainViewModel.deleteTimer(timer)
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