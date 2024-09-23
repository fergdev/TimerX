package com.timerx.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.timerx.ui.common.RScaffoldDefaults.ContentFadeDistance
import androidx.compose.material3.FabPosition.Companion as Material3FabPosition

object RScaffoldDefaults {

    // we use this to circumvent scaffold applying padding in places where we provide our own
    val partialWindowInsets
        @Composable get() = ScaffoldDefaults.contentWindowInsets.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Top
        )

    val FabPosition = Material3FabPosition.End
    val contentColor @Composable get() = MaterialTheme.colorScheme.onBackground

    // we define background color on the app-level to not increase overdraw
    val containerColor = Color.Transparent

    val ContentFadeDistance = 4.dp
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TScaffold(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBack: (() -> Unit)? = null,
    topBarScrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    snackbarHostState: SnackbarHostState? = null,
    containerColor: Color = RScaffoldDefaults.containerColor,
    contentColor: Color = RScaffoldDefaults.contentColor,
    fabPosition: FabPosition = RScaffoldDefaults.FabPosition,
    contentWindowInsets: WindowInsets? = RScaffoldDefaults.partialWindowInsets,
    fadeContent: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    content: @Composable BoxScope.(PaddingValues) -> Unit,
) = TScaffold(
    modifier = modifier,
    topBar = bar@{
        if (title == null && onBack == null) return@bar
        TTopBar(
            actions = actions,
            onNavigationIconClick = onBack,
            title = title?.branded(),
            scrollBehavior = topBarScrollBehavior,
        )
    },
    nestedScrollConnection = topBarScrollBehavior?.nestedScrollConnection,
    snackbarHostState = snackbarHostState,
    bottomBar = bottomBar,
    containerColor = containerColor,
    contentColor = contentColor,
    fab = fab,
    fadeContent = fadeContent,
    contentWindowInsets = contentWindowInsets,
    fabPosition = fabPosition,
    content = content,
)

@Composable
fun TScaffold(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    containerColor: Color = RScaffoldDefaults.containerColor,
    contentColor: Color = RScaffoldDefaults.contentColor,
    fabPosition: FabPosition = RScaffoldDefaults.FabPosition,
    contentWindowInsets: WindowInsets? = RScaffoldDefaults.partialWindowInsets,
    nestedScrollConnection: NestedScrollConnection? = null,
    fadeContent: Boolean = true,
    bottomBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    topBar: @Composable () -> Unit,
    content: @Composable BoxScope.(PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = topBar,
        containerColor = containerColor,
        contentColor = contentColor,
        floatingActionButton = fab,
        floatingActionButtonPosition = fabPosition,
        snackbarHost = { snackbarHostState?.let { SnackbarHost(hostState = it) } },
        modifier = modifier
            .thenIf(nestedScrollConnection != null) { nestedScroll(nestedScrollConnection!!) },
        bottomBar = bottomBar,
        contentWindowInsets = contentWindowInsets ?: WindowInsets(0.dp),
    ) { padding ->
        val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
        val cutoutPadding = WindowInsets.displayCutout.asPaddingValues()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = systemBarsPadding.calculateStartPadding(LocalLayoutDirection.current)
                        .coerceAtLeast(cutoutPadding.calculateStartPadding(LocalLayoutDirection.current))
                        .coerceAtLeast(16.dp),
                    end = systemBarsPadding.calculateEndPadding(LocalLayoutDirection.current)
                        .coerceAtLeast(cutoutPadding.calculateEndPadding(LocalLayoutDirection.current))
                        .coerceAtLeast(16.dp)
                )
                .thenIf(fadeContent) { fadingEdge(FadingEdge.Top, ContentFadeDistance) }
        ) {
//                Spacer(Modifier.height(padding.calculateTopPadding()))
                content(padding)
//                Spacer(Modifier.height(padding.calculateBottomPadding()))
        }
    }
}
