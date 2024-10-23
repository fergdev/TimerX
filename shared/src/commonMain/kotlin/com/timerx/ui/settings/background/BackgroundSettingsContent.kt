package com.timerx.ui.settings.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timerx.settings.Pattern
import com.timerx.settings.backgroundAlphaRange
import com.timerx.ui.common.DefaultLoading
import com.timerx.ui.common.TCard
import com.timerx.ui.common.TScaffold
import com.timerx.ui.settings.background.BackgroundSettingsIntent.UpdateAlpha
import com.timerx.ui.settings.background.BackgroundSettingsIntent.UpdatePattern
import com.timerx.ui.settings.background.BackgroundSettingsState.LoadedState
import com.timerx.ui.settings.background.BackgroundSettingsState.Loading
import com.timerx.util.capitalize
import org.jetbrains.compose.resources.stringResource
import pro.respawn.flowmvi.api.IntentReceiver
import pro.respawn.flowmvi.compose.dsl.DefaultLifecycle
import pro.respawn.flowmvi.compose.dsl.subscribe
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.alpha
import timerx.shared.generated.resources.pattern
import timerx.shared.generated.resources.theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundSettingsContent(
    component: BackgroundSettingsComponent
) = with(component) {
    TScaffold(
        title = stringResource(Res.string.theme),
        onBack = ::onBackClicked
    ) { scaffoldPadding ->
        val systemBarPadding = WindowInsets.systemBars.asPaddingValues()
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    top = scaffoldPadding.calculateTopPadding().plus(8.dp),
                    bottom = scaffoldPadding.calculateBottomPadding() + systemBarPadding.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
                .widthIn(max = 600.dp)
                .align(Alignment.TopCenter),
        ) {
            val state by subscribe(DefaultLifecycle)
            when (state) {
                Loading -> DefaultLoading()
                is LoadedState -> LoadedContent(state as LoadedState)
            }
        }
    }
}

@Composable
private fun IntentReceiver<BackgroundSettingsIntent>.LoadedContent(state: LoadedState) =
    with(state) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AlphaCard(alpha)
            PatternCard(pattern)
        }
    }

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntentReceiver<UpdatePattern>.PatternCard(selectedPattern: Pattern) {
    TCard {
        Text(text = stringResource(Res.string.pattern))

        FlowRow(
            modifier = Modifier.fillMaxWidth().wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Pattern.entries.forEach {
                FilterChip(
                    label = { Text(text = it.name.capitalize()) },
                    selected = it == selectedPattern,
                    onClick = { intent(UpdatePattern(it)) },
                )
            }
        }
    }
}

@Composable
private fun IntentReceiver<UpdateAlpha>.AlphaCard(volume: Float) {
    TCard {
        Text(text = stringResource(Res.string.alpha))
        Slider(
            value = volume,
            valueRange = backgroundAlphaRange,
            onValueChange = { intent(UpdateAlpha(it)) }
        )
    }
}
