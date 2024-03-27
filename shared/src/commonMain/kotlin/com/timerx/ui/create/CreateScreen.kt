@file:OptIn(ExperimentalResourceApi::class)

package com.timerx.ui.create

import ColorPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.timerx.domain.length
import com.timerx.domain.timeFormatted
import com.timerx.ui.common.AnimatedNumber
import moe.tlaster.precompose.koin.koinViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.back
import timerx.shared.generated.resources.create
import timerx.shared.generated.resources.create_timer
import timerx.shared.generated.resources.finish_color
import timerx.shared.generated.resources.timer_name
import timerx.shared.generated.resources.total_value

private const val TWO_HUNDRED_SEVENTY_DEG = 270f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateScreen(
    timerId: String,
    navigateUp: () -> Unit
) {
    val viewModel: CreateViewModel =
        koinViewModel(vmClass = CreateViewModel::class) { parametersOf(timerId) }

    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.create_timer)) },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.rotate(TWO_HUNDRED_SEVENTY_DEG),
                        onClick = { navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.interactions.save()
                navigateUp()
            }) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(Res.string.create)
                )
            }
        }

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            CreateContent(state, viewModel)
        }
    }
}

@Composable
private fun CreateContent(
    state: CreateViewModel.State,
    viewModel: CreateViewModel,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = state.timerName,
                label = { Text(text = stringResource(Res.string.timer_name)) },
                onValueChange = { viewModel.interactions.updateTimerName(it) })
        }
        items(state.sets) {
            Set(
                timerSet = it,
                viewModel.interactions
            )
        }
        item {
            Box(
                modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                FilledIconButton(onClick = { viewModel.interactions.addSet() }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(Res.string.add)
                    )
                }
            }
        }
        item {
            Box(
                modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                AnimatedNumber(state.sets.length()) { it.timeFormatted() }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            FinishColorPicker(state.finishColor, viewModel.interactions.updateFinishColor)
        }
    }
}

@Composable
private fun FinishColorPicker(
    finishColor: Color,
    updateFinishColor: (Color) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(Res.string.finish_color))
        Spacer(modifier = Modifier.width(16.dp))
        var colorPickerVisible by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier.size(48.dp).background(finishColor)
                .clickable {
                    colorPickerVisible = true
                })

        if (colorPickerVisible) {
            ColorPicker {
                if (it != null) {
                    updateFinishColor(it)
                }
                colorPickerVisible = false
            }
        }
    }
}
