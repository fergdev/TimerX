package com.timerx.ui.settings.about.main

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.PaddedElevatedCard
import com.timerx.ui.common.TMenuItem
import com.timerx.ui.common.TScaffold
import com.timerx.ui.common.appendNewline
import com.timerx.ui.common.branded
import com.timerx.ui.common.doubleBranded
import com.timerx.ui.common.rainbow
import com.timerx.ui.settings.about.aboutlibs.AboutLibsContent
import com.timerx.ui.settings.about.changelog.ChangeLogContent
import com.timerx.ui.settings.about.main.AboutIntent.UpdateCollectAnalytics
import com.timerx.ui.theme.Size
import org.jetbrains.compose.resources.stringResource
import pro.respawn.flowmvi.compose.dsl.DefaultLifecycle
import pro.respawn.flowmvi.compose.dsl.subscribe
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.about_libs
import timerx.shared.generated.resources.about_libs_subtitle
import timerx.shared.generated.resources.app_name
import timerx.shared.generated.resources.collect_analytics
import timerx.shared.generated.resources.collect_analytics_message
import timerx.shared.generated.resources.contact_support
import timerx.shared.generated.resources.privacy_policy
import timerx.shared.generated.resources.privacy_policy_message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutMainContent(component: AboutMainComponent) {
    with(component) {
        val state by subscribe(DefaultLifecycle)
        TScaffold(
            title = buildAnnotatedString {
                append(stringResource(Res.string.app_name).doubleBranded())
                append(" ")
                append("v".branded())
                append(state.versionName)
            },
            onBack = component::onBackClicked
        ) { padding ->

            Column(
                modifier = Modifier
                    .widthIn(max = Size.maxWidth)
                    .align(Alignment.TopCenter)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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

                PaddedElevatedCard { Text(text = DeveloperMessage()) }

                val uriHandler = LocalUriHandler.current
                TMenuItem(
                    title = stringResource(Res.string.privacy_policy),
                    color = rainbow[0],
                    icon = Icons.Filled.Lock,
                    subtitle = stringResource(Res.string.privacy_policy_message),
                    onClick = { uriHandler.openUri(state.privacyPolicyUri) }
                )

                TMenuItem(
                    title = stringResource(Res.string.about_libs),
                    color = rainbow[1],
                    icon = Icons.Filled.Info,
                    subtitle = stringResource(Res.string.about_libs_subtitle),
                    onClick = { component.onLibsClicked() }
                )

                TMenuItem(
                    title = "Change Log",
                    color = rainbow[2],
                    icon = Icons.Filled.DateRange,
                    onClick = { component.onChangeLog() }
                )

                TMenuItem(
                    title = stringResource(Res.string.contact_support),
                    color = rainbow[3],
                    icon = Icons.Filled.Email,
                    onClick = { intent(AboutIntent.ContactSupport) }
                )

                if (state.hasAnalytics) {
                    TMenuItem(
                        title = stringResource(Res.string.collect_analytics),
                        color = rainbow[4],
                        icon = Icons.Filled.Share,
                        subtitle = stringResource(Res.string.collect_analytics_message),
                        onClick = {
                            intent(UpdateCollectAnalytics(state.collectAnalytics.not()))
                        },
                        trailing = {
                            Switch(
                                checked = state.collectAnalytics,
                                onCheckedChange = {
                                    intent(UpdateCollectAnalytics(state.collectAnalytics.not()))
                                }
                            )
                        }
                    )
                }
                Spacer(
                    Modifier.height(
                        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    )
                )
            }

            val aboutLibsSlot by component.aboutLibsSlot.subscribeAsState()
            aboutLibsSlot.child?.instance?.also {
                ModalBottomSheet(onDismissRequest = ::onDismissLibs) {
                    AboutLibsContent()
                }
            }

            val changeLogSlot by component.changeLogSlot.subscribeAsState()
            changeLogSlot.child?.instance?.also {
                ModalBottomSheet(onDismissRequest = ::onDismissChangeLog) {
                    ChangeLogContent()
                }
            }
        }
    }
}

@Composable
@Suppress("MaxLineLength")
private fun DeveloperMessage() = buildAnnotatedString {
    append("Hey \uD83D\uDC4B".branded())
    appendNewline(2)
    append(stringResource(Res.string.app_name).doubleBranded())
    append(" was built to help you crush your fitness goals with a beautiful, fully customizable HIT timer \uD83D\uDCAA ")
    append("It’s designed to keep you focused and in control, whether you’re just starting out or pushing your limits \uD83C\uDFCB\uFE0F".branded())
    append("I hope you love using it as much as I loved creating it \uD83D\uDC97".branded())
    appendNewline(2)
    append("Cheers,".branded())
    appendNewline()
    append("Ferg.Dev \uD83D\uDCAA".branded())
}