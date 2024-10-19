package com.timerx.ui.settings.about.main

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.TMenuItem
import com.timerx.ui.common.TScaffold
import com.timerx.ui.common.rainbow
import com.timerx.ui.settings.about.aboutlibs.AboutLibsContent
import com.timerx.ui.theme.Size
import org.jetbrains.compose.resources.stringResource
import pro.respawn.flowmvi.compose.dsl.DefaultLifecycle
import pro.respawn.flowmvi.compose.dsl.subscribe
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.about_libs
import timerx.shared.generated.resources.about_libs_subtitle
import timerx.shared.generated.resources.privacy_policy
import timerx.shared.generated.resources.privacy_policy_message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutMainContent(component: AboutMainComponent) {
    with(component) {
        val state by subscribe(DefaultLifecycle)
        TScaffold(
            title = state.versionName,
            onBack = component::back
        ) { padding ->

            Column(
                modifier = Modifier
                    .widthIn(max = Size.maxWidth)
                    .align(Alignment.TopCenter)
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding().plus(16.dp),
                        bottom = padding.calculateBottomPadding().plus(16.dp),
                        start = 16.dp,
                        end = 16.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                val infiniteTransition = rememberInfiniteTransition(label = "InfiniteTransition")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0.8f,
                    animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
                    label = "FloatAnimation"
                )
                Image(
                    modifier = Modifier.fillMaxWidth(0.5f).scale(scale),
                    contentScale = ContentScale.FillWidth,
                    imageVector = CustomIcons.avTimer,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                )

                TMenuItem(
                    title = stringResource(Res.string.about_libs),
                    color = rainbow[1],
                    icon = Icons.Filled.Info,
                    subtitle = stringResource(Res.string.about_libs_subtitle),
                    onClick = { component.onLibs() }
                )
                val uriHandler = LocalUriHandler.current
                TMenuItem(
                    title = stringResource(Res.string.privacy_policy),
                    color = rainbow[5],
                    icon = Icons.Filled.Lock,
                    subtitle = stringResource(Res.string.privacy_policy_message),
                    onClick = { uriHandler.openUri(state.privacyPolicyUri) }
                )
            }

            val aboutLibsSlot by component.aboutLibsSlot.subscribeAsState()
            aboutLibsSlot.child?.instance?.also {
                ModalBottomSheet(onDismissRequest = ::dismissLibs) {
                    AboutLibsContent()
                }
            }
        }
    }
}