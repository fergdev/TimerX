package com.timerx.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.back

@Composable
fun SettingsScaffold(
    title: String,
    onBackClicked: () -> Unit,
     content: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { SettingsAppBar(title, onBackClicked) },
        content = { scaffoldPadding ->
            val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
            val cutoutPadding = WindowInsets.displayCutout.asPaddingValues()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        bottom = 16.dp,
                        start = systemBarsPadding.calculateStartPadding(LocalLayoutDirection.current)
                            .coerceAtLeast(cutoutPadding.calculateStartPadding(LocalLayoutDirection.current))
                            .coerceAtLeast(16.dp),
                        end = systemBarsPadding.calculateEndPadding(LocalLayoutDirection.current)
                            .coerceAtLeast(cutoutPadding.calculateEndPadding(LocalLayoutDirection.current))
                            .coerceAtLeast(16.dp)
                    )
                    .verticalScroll(rememberScrollState()),
            ) {
                Column {
                    Spacer(Modifier.height(scaffoldPadding.calculateTopPadding()))
                    content()
                    Spacer(Modifier.height(scaffoldPadding.calculateBottomPadding()))
                }
            }
        }
    )
}

private const val NINETY_DEGREES = 90f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppBar(name: String, onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = name) },
        navigationIcon = {
            IconButton(
                modifier = Modifier.rotate(NINETY_DEGREES),
                onClick = { onNavigateBack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.back)
                )
            }
        }
    )
}
