package com.timerx.ads

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import org.koin.mp.KoinPlatform

private const val TAG = "ADView"

@Composable
actual fun GoogleAd() {
    val deviceWidth = LocalConfiguration.current.screenWidthDp.dp
    val systemBarPadding = WindowInsets.systemBars.asPaddingValues()
    val cutoutPadding = WindowInsets.displayCutout.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current

    val paddingStart = systemBarPadding.calculateStartPadding(layoutDirection)
        .coerceAtLeast(cutoutPadding.calculateStartPadding(layoutDirection))
        .coerceAtLeast(16.dp)

    val paddingEnd = systemBarPadding.calculateEndPadding(layoutDirection)
        .coerceAtLeast(cutoutPadding.calculateEndPadding(layoutDirection))
        .coerceAtLeast(16.dp)

    val adWidth = deviceWidth - paddingStart - paddingEnd
    val context = KoinPlatform.getKoin().get<Context>()
    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
        context, adWidth.value.toInt()
    )

    AndroidView(
        modifier = Modifier
            .wrapContentSize()
            .height(adSize.height.dp),
        factory = { androidViewContext ->
            AdView(androidViewContext).apply {
                setAdSize(adSize)
                adUnitId = "ca-app-pub-2499949091653906/6117628581"
                loadAd(AdRequest.Builder()
                    .addKeyword("fitness")
                    .addKeyword("gym")
                    .addKeyword("health")
                    .addKeyword("workout")
                    .build())
                adListener =
                    object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d(TAG, "Banner ad was loaded.")
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e(
                                TAG,
                                "Banner ad failed to load. " +
                                        "code '${error.code} " +
                                        "message '${error.message}'" +
                                        "responseInfo '${error.responseInfo}'"
                            )
                        }

                        override fun onAdImpression() {
                            Log.d(TAG, "Banner ad had an impression.")
                        }

                        override fun onAdClicked() {
                            Log.d(TAG, "Banner ad was clicked.")
                        }
                    }
            }
        }
    )
}