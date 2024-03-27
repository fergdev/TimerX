package com.timerx.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object CustomIcons {

    val defaultIconSize: Dp = 24.dp

    @Composable
    fun checkIndeterminateSmall(): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "check_indeterminate_small",
                defaultWidth = 40.0.dp,
                defaultHeight = 40.0.dp,
                viewportWidth = 40.0f,
                viewportHeight = 40.0f
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    stroke = null,
                    strokeAlpha = 1f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(13.042f, 21.292f)
                    quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
                    reflectiveQuadTo(11.75f, 20f)
                    quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                    quadToRelative(0.375f, -0.395f, 0.917f, -0.395f)
                    horizontalLineToRelative(13.916f)
                    quadToRelative(0.542f, 0f, 0.938f, 0.395f)
                    quadToRelative(0.396f, 0.396f, 0.396f, 0.938f)
                    quadToRelative(0f, 0.542f, -0.396f, 0.917f)
                    reflectiveQuadToRelative(-0.938f, 0.375f)
                    close()
                }
            }.build()
        }
    }

    @Composable
    fun contentCopy(): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "content_copy",
                defaultWidth = 40.0.dp,
                defaultHeight = 40.0.dp,
                viewportWidth = 40.0f,
                viewportHeight = 40.0f
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    stroke = null,
                    strokeAlpha = 1f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(13.292f, 30.958f)
                    quadToRelative(-1.084f, 0f, -1.875f, -0.77f)
                    quadToRelative(-0.792f, -0.771f, -0.792f, -1.855f)
                    verticalLineToRelative(-22f)
                    quadToRelative(0f, -1.083f, 0.792f, -1.854f)
                    quadToRelative(0.791f, -0.771f, 1.875f, -0.771f)
                    horizontalLineToRelative(17.083f)
                    quadToRelative(1.083f, 0f, 1.854f, 0.771f)
                    quadTo(33f, 5.25f, 33f, 6.333f)
                    verticalLineToRelative(22f)
                    quadToRelative(0f, 1.084f, -0.771f, 1.855f)
                    quadToRelative(-0.771f, 0.77f, -1.854f, 0.77f)
                    close()
                    moveToRelative(0f, -2.625f)
                    horizontalLineToRelative(17.083f)
                    verticalLineToRelative(-22f)
                    horizontalLineTo(13.292f)
                    verticalLineToRelative(22f)
                    close()
                    moveTo(8f, 36.25f)
                    quadToRelative(-1.083f, 0f, -1.854f, -0.771f)
                    quadToRelative(-0.771f, -0.771f, -0.771f, -1.854f)
                    verticalLineTo(10.792f)
                    quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                    quadToRelative(0.375f, -0.396f, 0.917f, -0.396f)
                    quadToRelative(0.583f, 0f, 0.958f, 0.396f)
                    reflectiveQuadToRelative(0.375f, 0.938f)
                    verticalLineToRelative(22.833f)
                    horizontalLineToRelative(17.625f)
                    quadToRelative(0.5f, 0f, 0.896f, 0.375f)
                    reflectiveQuadToRelative(0.396f, 0.917f)
                    quadToRelative(0f, 0.583f, -0.396f, 0.958f)
                    reflectiveQuadToRelative(-0.896f, 0.375f)
                    close()
                    moveToRelative(5.292f, -29.917f)
                    verticalLineToRelative(22f)
                    verticalLineToRelative(-22f)
                    close()
                }
            }.build()
        }
    }

    @Composable
    fun pause(): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "pause",
                defaultWidth = 40.0.dp,
                defaultHeight = 40.0.dp,
                viewportWidth = 40.0f,
                viewportHeight = 40.0f
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    stroke = null,
                    strokeAlpha = 1f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(23.792f, 29.458f)
                    quadToRelative(-0.375f, 0f, -0.667f, -0.291f)
                    quadToRelative(-0.292f, -0.292f, -0.292f, -0.667f)
                    verticalLineToRelative(-17f)
                    quadToRelative(0f, -0.375f, 0.292f, -0.667f)
                    quadToRelative(0.292f, -0.291f, 0.667f, -0.291f)
                    horizontalLineTo(28.5f)
                    quadToRelative(0.375f, 0f, 0.667f, 0.291f)
                    quadToRelative(0.291f, 0.292f, 0.291f, 0.667f)
                    verticalLineToRelative(17f)
                    quadToRelative(0f, 0.375f, -0.291f, 0.667f)
                    quadToRelative(-0.292f, 0.291f, -0.667f, 0.291f)
                    close()
                    moveToRelative(-12.292f, 0f)
                    quadToRelative(-0.375f, 0f, -0.667f, -0.291f)
                    quadToRelative(-0.291f, -0.292f, -0.291f, -0.667f)
                    verticalLineToRelative(-17f)
                    quadToRelative(0f, -0.375f, 0.291f, -0.667f)
                    quadToRelative(0.292f, -0.291f, 0.667f, -0.291f)
                    horizontalLineToRelative(4.75f)
                    quadToRelative(0.333f, 0f, 0.646f, 0.291f)
                    quadToRelative(0.312f, 0.292f, 0.312f, 0.667f)
                    verticalLineToRelative(17f)
                    quadToRelative(0f, 0.375f, -0.312f, 0.667f)
                    quadToRelative(-0.313f, 0.291f, -0.646f, 0.291f)
                    close()
                }
            }.build()
        }
    }
}
