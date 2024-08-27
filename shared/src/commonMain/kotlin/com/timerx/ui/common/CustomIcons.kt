@file:Suppress("ObjectPropertyName")

package com.timerx.ui.common

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

    private var _checkIntermediateSmall: ImageVector? = null
    val checkIntermediateSmall: ImageVector
        get() {
            if (_checkIntermediateSmall != null) {
                return _checkIntermediateSmall!!
            }

            _checkIntermediateSmall = ImageVector.Builder(
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
            return _checkIntermediateSmall!!
        }

    private var _contentCopy: ImageVector? = null
    val contentCopy: ImageVector
        get() {
            if (_contentCopy != null) {
                return _contentCopy!!
            }
            _contentCopy = ImageVector.Builder(
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
            return _contentCopy!!
        }

    private var _pause: ImageVector? = null
    val pause: ImageVector
        get() {
            if (_pause != null) {
                return _pause!!
            }
            _pause = ImageVector.Builder(
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
            return _pause!!
        }

    private var _vibration: ImageVector? = null
    val vibration: ImageVector
        get() {
            if (_vibration != null) {
                return _vibration!!
            }
            _vibration = ImageVector.Builder(
                name = "vibration",
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
                    moveTo(1.667f, 24.75f)
                    quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
                    reflectiveQuadToRelative(-0.375f, -0.958f)
                    verticalLineToRelative(-6.875f)
                    quadToRelative(0f, -0.542f, 0.375f, -0.917f)
                    reflectiveQuadToRelative(0.958f, -0.375f)
                    quadToRelative(0.542f, 0f, 0.917f, 0.375f)
                    reflectiveQuadToRelative(0.375f, 0.917f)
                    verticalLineToRelative(6.875f)
                    quadToRelative(0f, 0.583f, -0.396f, 0.958f)
                    reflectiveQuadToRelative(-0.937f, 0.375f)
                    close()
                    moveToRelative(4.916f, 3.542f)
                    quadToRelative(-0.541f, 0f, -0.916f, -0.396f)
                    reflectiveQuadToRelative(-0.375f, -0.938f)
                    verticalLineTo(13.042f)
                    quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                    quadToRelative(0.375f, -0.396f, 0.958f, -0.396f)
                    quadToRelative(0.542f, 0f, 0.917f, 0.396f)
                    reflectiveQuadToRelative(0.375f, 0.938f)
                    verticalLineToRelative(13.916f)
                    quadToRelative(0f, 0.542f, -0.396f, 0.938f)
                    quadToRelative(-0.396f, 0.396f, -0.938f, 0.396f)
                    close()
                    moveToRelative(31.75f, -3.542f)
                    quadToRelative(-0.583f, 0f, -0.958f, -0.375f)
                    reflectiveQuadTo(37f, 23.417f)
                    verticalLineToRelative(-6.875f)
                    quadToRelative(0f, -0.542f, 0.396f, -0.917f)
                    reflectiveQuadToRelative(0.937f, -0.375f)
                    quadToRelative(0.542f, 0f, 0.917f, 0.375f)
                    reflectiveQuadToRelative(0.375f, 0.917f)
                    verticalLineToRelative(6.875f)
                    quadToRelative(0f, 0.583f, -0.375f, 0.958f)
                    reflectiveQuadToRelative(-0.917f, 0.375f)
                    close()
                    moveToRelative(-4.916f, 3.542f)
                    quadToRelative(-0.584f, 0f, -0.959f, -0.396f)
                    reflectiveQuadToRelative(-0.375f, -0.938f)
                    verticalLineTo(13.042f)
                    quadToRelative(0f, -0.542f, 0.396f, -0.938f)
                    quadToRelative(0.396f, -0.396f, 0.938f, -0.396f)
                    quadToRelative(0.541f, 0f, 0.916f, 0.396f)
                    reflectiveQuadToRelative(0.375f, 0.938f)
                    verticalLineToRelative(13.916f)
                    quadToRelative(0f, 0.542f, -0.375f, 0.938f)
                    quadToRelative(-0.375f, 0.396f, -0.916f, 0.396f)
                    close()
                    moveTo(12.333f, 34.75f)
                    quadToRelative(-0.875f, 0f, -1.5f, -0.625f)
                    reflectiveQuadToRelative(-0.625f, -1.5f)
                    verticalLineTo(7.375f)
                    quadToRelative(0f, -0.917f, 0.625f, -1.521f)
                    quadToRelative(0.625f, -0.604f, 1.5f, -0.604f)
                    horizontalLineToRelative(15.334f)
                    quadToRelative(0.875f, 0f, 1.5f, 0.604f)
                    reflectiveQuadToRelative(0.625f, 1.521f)
                    verticalLineToRelative(25.25f)
                    quadToRelative(0f, 0.875f, -0.625f, 1.5f)
                    reflectiveQuadToRelative(-1.5f, 0.625f)
                    close()
                    moveToRelative(0.5f, -2.625f)
                    horizontalLineToRelative(14.334f)
                    verticalLineTo(7.875f)
                    horizontalLineTo(12.833f)
                    close()
                    moveToRelative(0f, -24.25f)
                    verticalLineToRelative(24.25f)
                    close()
                }
            }.build()
            return _vibration!!
        }

    private var _colorFill: ImageVector? = null
    val colorFill: ImageVector
        get() {
            if (_colorFill != null) {
                return _colorFill!!
            }
            _colorFill = ImageVector.Builder(
                name = "format_color_fill",
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
                    moveTo(17.167f, 27.667f)
                    quadToRelative(-0.584f, 0f, -1.188f, -0.25f)
                    reflectiveQuadToRelative(-1.062f, -0.709f)
                    lineTo(7.5f, 19.292f)
                    quadToRelative(-0.5f, -0.5f, -0.729f, -1.084f)
                    quadToRelative(-0.229f, -0.583f, -0.229f, -1.25f)
                    quadToRelative(0f, -0.625f, 0.229f, -1.229f)
                    quadToRelative(0.229f, -0.604f, 0.729f, -1.062f)
                    lineToRelative(7.75f, -7.792f)
                    lineToRelative(-3.667f, -3.667f)
                    quadToRelative(-0.375f, -0.375f, -0.375f, -0.916f)
                    quadToRelative(0f, -0.542f, 0.417f, -0.959f)
                    quadToRelative(0.375f, -0.375f, 0.917f, -0.375f)
                    quadToRelative(0.541f, 0f, 0.958f, 0.375f)
                    lineToRelative(13.333f, 13.334f)
                    quadToRelative(0.459f, 0.458f, 0.709f, 1.062f)
                    quadToRelative(0.25f, 0.604f, 0.25f, 1.229f)
                    quadToRelative(0f, 0.667f, -0.25f, 1.25f)
                    quadToRelative(-0.25f, 0.584f, -0.709f, 1.084f)
                    lineToRelative(-7.458f, 7.416f)
                    quadToRelative(-0.458f, 0.459f, -1.042f, 0.709f)
                    quadToRelative(-0.583f, 0.25f, -1.166f, 0.25f)
                    close()
                    moveToRelative(0f, -18.917f)
                    lineTo(9f, 16.875f)
                    horizontalLineToRelative(0.021f)
                    horizontalLineTo(9f)
                    horizontalLineToRelative(16.25f)
                    close()
                    moveToRelative(14.125f, 19.583f)
                    quadToRelative(-1.292f, 0f, -2.167f, -0.895f)
                    quadToRelative(-0.875f, -0.896f, -0.875f, -2.146f)
                    quadToRelative(0f, -0.709f, 0.396f, -1.584f)
                    quadToRelative(0.396f, -0.875f, 0.979f, -1.75f)
                    quadToRelative(0.333f, -0.5f, 0.771f, -1.083f)
                    quadToRelative(0.437f, -0.583f, 0.896f, -1.083f)
                    quadToRelative(0.458f, 0.5f, 0.896f, 1.083f)
                    quadToRelative(0.437f, 0.583f, 0.77f, 1.083f)
                    quadToRelative(0.584f, 0.875f, 0.959f, 1.75f)
                    reflectiveQuadToRelative(0.375f, 1.584f)
                    quadToRelative(0f, 1.25f, -0.875f, 2.146f)
                    quadToRelative(-0.875f, 0.895f, -2.125f, 0.895f)
                    close()
                    moveTo(6.167f, 39.875f)
                    quadToRelative(-1.084f, 0f, -1.875f, -0.792f)
                    quadToRelative(-0.792f, -0.791f, -0.792f, -1.875f)
                    quadToRelative(0f, -1.125f, 0.792f, -1.896f)
                    quadToRelative(0.791f, -0.77f, 1.875f, -0.77f)
                    horizontalLineToRelative(27.708f)
                    quadToRelative(1.083f, 0f, 1.854f, 0.791f)
                    quadToRelative(0.771f, 0.792f, 0.771f, 1.875f)
                    quadToRelative(0f, 1.084f, -0.771f, 1.875f)
                    quadToRelative(-0.771f, 0.792f, -1.854f, 0.792f)
                    close()
                }
            }.build()

            return _colorFill!!
        }

    private var _dragHandle: ImageVector? = null
    val dragHandle: ImageVector
        get() {
            if (_dragHandle != null) {
                return _dragHandle!!
            }
            _dragHandle = ImageVector.Builder(
                name = "drag_handle",
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
                    moveTo(8.167f, 18.667f)
                    quadToRelative(-0.542f, 0f, -0.938f, -0.396f)
                    quadToRelative(-0.396f, -0.396f, -0.396f, -0.938f)
                    quadToRelative(0f, -0.541f, 0.396f, -0.916f)
                    reflectiveQuadToRelative(0.938f, -0.375f)
                    horizontalLineToRelative(23.666f)
                    quadToRelative(0.542f, 0f, 0.917f, 0.375f)
                    reflectiveQuadToRelative(0.375f, 0.916f)
                    quadToRelative(0f, 0.584f, -0.375f, 0.959f)
                    reflectiveQuadToRelative(-0.917f, 0.375f)
                    close()
                    moveToRelative(0f, 5.291f)
                    quadToRelative(-0.542f, 0f, -0.938f, -0.396f)
                    quadToRelative(-0.396f, -0.395f, -0.396f, -0.937f)
                    reflectiveQuadToRelative(0.396f, -0.937f)
                    quadToRelative(0.396f, -0.396f, 0.938f, -0.396f)
                    horizontalLineToRelative(23.666f)
                    quadToRelative(0.542f, 0f, 0.917f, 0.396f)
                    quadToRelative(0.375f, 0.395f, 0.375f, 0.937f)
                    reflectiveQuadToRelative(-0.375f, 0.937f)
                    quadToRelative(-0.375f, 0.396f, -0.917f, 0.396f)
                    close()
                }
            }.build()
            return _dragHandle!!
        }

    private var _skipNext: ImageVector? = null
    val skipNext: ImageVector
        get() {
            if (_skipNext != null) {
                return _skipNext!!
            }
            _skipNext = ImageVector.Builder(
                name = "skip_next",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(660f, 720f)
                    verticalLineToRelative(-480f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(480f)
                    close()
                    moveToRelative(-440f, 0f)
                    verticalLineToRelative(-480f)
                    lineToRelative(360f, 240f)
                    close()
                    moveToRelative(80f, -150f)
                    lineToRelative(136f, -90f)
                    lineToRelative(-136f, -90f)
                    close()
                }
            }.build()
            return _skipNext!!
        }

    private var _skipPrevious: ImageVector? = null
    val skipPrevious: ImageVector
        get() {
            if (_skipPrevious != null) {
                return _skipPrevious!!
            }
            _skipPrevious = ImageVector.Builder(
                name = "skip_previous",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(220f, 720f)
                    verticalLineToRelative(-480f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(480f)
                    close()
                    moveToRelative(520f, 0f)
                    lineTo(380f, 480f)
                    lineToRelative(360f, -240f)
                    close()
                    moveToRelative(-80f, -150f)
                    verticalLineToRelative(-180f)
                    lineToRelative(-136f, 90f)
                    close()
                }
            }.build()
            return _skipPrevious!!
        }
}
