package com.intervallum.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.sp
import com.intervallum.BuildFlags
import com.intervallum.util.Platform
import com.intervallum.util.platform
import intervallum.shared.generated.resources.Res
import intervallum.shared.generated.resources.comfortaa
import intervallum.shared.generated.resources.montserrat
import org.jetbrains.compose.resources.Font

val Comfortaa
    @Composable get() =
        // https://github.com/JetBrains/compose-multiplatform/issues/4635
        if (BuildFlags.platform == Platform.Web)
            FontFamily.Default
        else
            Font(Res.font.comfortaa).toFontFamily()

val Montserrat @Composable get() = Font(Res.font.montserrat).toFontFamily()

inline val FontFamily.Companion.Montserrat @Composable get() = com.intervallum.ui.theme.Montserrat
inline val FontFamily.Companion.Comfortaa @Composable get() = com.intervallum.ui.theme.Comfortaa

private val FontFeatures =
    "dlig, liga, kern, zero, locl, size".takeUnless { BuildFlags.platform == Platform.Web }

// region Typography
internal val AppTypography
    @Composable
    get() = run {
        val comfortaa = Comfortaa
        val montserrat = Montserrat
        remember {
            Typography(
                displayLarge = TextStyle(
                    fontFamily = montserrat,
                    fontWeight = FontWeight.W700,
                    fontSize = 48.sp,
                    lineHeight = 58.sp,
                    letterSpacing = 5.sp,
                    lineBreak = LineBreak.Heading,
                    hyphens = Hyphens.None,
                    fontFeatureSettings = FontFeatures,
                ),
                displayMedium = TextStyle(
                    fontFamily = montserrat,
                    fontWeight = FontWeight.W600,
                    fontSize = 40.sp,
                    lineHeight = 54.sp,
                    letterSpacing = 4.sp,
                    lineBreak = LineBreak.Heading,
                    hyphens = Hyphens.None,
                    fontFeatureSettings = FontFeatures,
                ),
                displaySmall = TextStyle(
                    fontFamily = montserrat,
                    fontWeight = FontWeight.W600,
                    fontSize = 32.sp,
                    lineHeight = 42.sp,
                    letterSpacing = 3.sp,
                    lineBreak = LineBreak.Heading,
                    hyphens = Hyphens.None,
                    fontFeatureSettings = FontFeatures,
                ),
                headlineLarge = TextStyle(
                    fontFamily = montserrat,
                    fontWeight = FontWeight.W500,
                    fontSize = 30.sp,
                    lineHeight = 38.sp,
                    letterSpacing = 2.sp,
                    lineBreak = LineBreak.Simple,
                    hyphens = Hyphens.None,
                    fontFeatureSettings = FontFeatures,
                ),
                headlineMedium = TextStyle(
                    fontFamily = comfortaa,
                    fontWeight = FontWeight.W500,
                    fontSize = 26.sp,
                    lineHeight = 34.sp,
                    letterSpacing = 1.5.sp,
                    lineBreak = LineBreak.Simple,
                    hyphens = Hyphens.None,
                    fontFeatureSettings = FontFeatures,
                ),
                headlineSmall = TextStyle(
                    fontFamily = comfortaa,
                    fontWeight = FontWeight.W500,
                    fontSize = 23.sp,
                    lineHeight = 32.sp,
                    letterSpacing = 1.sp,
                    lineBreak = LineBreak.Simple,
                    hyphens = Hyphens.None,
                    fontFeatureSettings = FontFeatures,
                ),
                titleLarge = TextStyle(
                    fontFamily = comfortaa,
                    fontWeight = FontWeight.W500,
                    fontSize = 22.sp,
                    lineHeight = 30.sp,
                    letterSpacing = 0.9.sp,
                    lineBreak = LineBreak.Simple,
                    hyphens = Hyphens.None,
                    fontFeatureSettings = FontFeatures,
                ),
                titleMedium = TextStyle(
                    fontFamily = comfortaa,
                    fontWeight = FontWeight.W500,
                    fontSize = 19.sp,
                    lineHeight = 28.sp,
                    letterSpacing = 0.8.sp,
                    lineBreak = LineBreak.Simple,
                    hyphens = Hyphens.None,
                    fontFeatureSettings = FontFeatures,
                ),
                titleSmall = TextStyle(
                    fontFamily = comfortaa,
                    fontWeight = FontWeight.W500,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.7.sp,
                    lineBreak = LineBreak.Simple,
                    hyphens = Hyphens.None,
                    fontFeatureSettings = FontFeatures,
                ),
                bodyLarge = TextStyle(
                    fontFamily = comfortaa,
                    fontWeight = FontWeight.W400,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.7.sp,
                    lineBreak = LineBreak.Paragraph,
                    hyphens = Hyphens.Auto,
                    fontFeatureSettings = FontFeatures,
                ),
                bodyMedium = TextStyle(
                    fontFamily = comfortaa,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    letterSpacing = 0.6.sp,
                    lineBreak = LineBreak.Paragraph,
                    hyphens = Hyphens.Auto,
                    fontFeatureSettings = FontFeatures,
                ),
                bodySmall = TextStyle(
                    fontFamily = comfortaa,
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    letterSpacing = 0.5.sp,
                    lineBreak = LineBreak.Paragraph,
                    hyphens = Hyphens.Auto,
                    fontFeatureSettings = FontFeatures,
                ),
                labelLarge = TextStyle(
                    fontFamily = comfortaa,
                    fontWeight = FontWeight.W300,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.5.sp,
                    lineBreak = LineBreak.Simple,
                    fontFeatureSettings = FontFeatures,
                ),
                labelMedium = TextStyle(
                    fontFamily = comfortaa,
                    fontWeight = FontWeight.W300,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    letterSpacing = 0.4.sp,
                    lineBreak = LineBreak.Simple,
                    fontFeatureSettings = FontFeatures,
                ),
                labelSmall = TextStyle(
                    fontFamily = comfortaa,
                    fontWeight = FontWeight.W300,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.4.sp,
                    lineBreak = LineBreak.Simple,
                    fontFeatureSettings = FontFeatures,
                ),
            )
        }
    }
