package com.timerx.ads

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
    val deviceWidth = LocalConfiguration.current.screenWidthDp
    val activity = KoinPlatform.getKoin().get<ComponentActivity>()
    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, deviceWidth)

    AndroidView(
        modifier = Modifier
            .wrapContentSize()
            .height(adSize.height.dp)
            .background(Color.Transparent),
        factory = { context ->
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