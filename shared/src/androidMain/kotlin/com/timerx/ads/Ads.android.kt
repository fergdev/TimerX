package com.timerx.ads

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

private const val TAG = "ADView"

@Composable
actual fun GoogleAd() {
    val deviceWidth = LocalConfiguration.current.screenWidthDp

    AndroidView(
        modifier = Modifier
            .wrapContentSize()
            .background(Color.Transparent),
        factory = { context ->
            val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, deviceWidth)
            AdView(context).apply {
                setAdSize(adSize)
                adUnitId = "ca-app-pub-2499949091653906/6117628581"
                loadAd(AdRequest.Builder().build())
                adListener =
                    object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d(TAG, "Banner ad was loaded.")
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e(TAG, "Banner ad failed to load.")
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