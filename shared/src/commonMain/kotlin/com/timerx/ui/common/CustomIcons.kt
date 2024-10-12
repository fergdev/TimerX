@file:Suppress("LargeClass")

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

    private var _Sort: ImageVector? = null
    val sortOrder: ImageVector
        get() {
            if (_Sort != null) {
                return _Sort!!
            }
            _Sort = ImageVector.Builder(
                name = "Sort",
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
                    moveTo(120f, 720f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(240f)
                    verticalLineToRelative(80f)
                    close()
                    moveToRelative(0f, -200f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(480f)
                    verticalLineToRelative(80f)
                    close()
                    moveToRelative(0f, -200f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(720f)
                    verticalLineToRelative(80f)
                    close()
                }
            }.build()
            return _Sort!!
        }

    private var _SortAlphaDown: ImageVector? = null
    val sortAlphaDown: ImageVector
        get() {
            if (_SortAlphaDown != null) {
                return _SortAlphaDown!!
            }
            _SortAlphaDown = ImageVector.Builder(
                name = "SortAlphaDown",
                defaultWidth = 16.dp,
                defaultHeight = 16.dp,
                viewportWidth = 16f,
                viewportHeight = 16f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.EvenOdd
                ) {
                    moveTo(10.082f, 5.629f)
                    lineTo(9.664f, 7f)
                    horizontalLineTo(8.598f)
                    lineToRelative(1.789f, -5.332f)
                    horizontalLineToRelative(1.234f)
                    lineTo(13.402f, 7f)
                    horizontalLineToRelative(-1.12f)
                    lineToRelative(-0.419f, -1.371f)
                    close()
                    moveToRelative(1.57f, -0.785f)
                    lineTo(11f, 2.687f)
                    horizontalLineToRelative(-0.047f)
                    lineToRelative(-0.652f, 2.157f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(12.96f, 14f)
                    horizontalLineTo(9.028f)
                    verticalLineToRelative(-0.691f)
                    lineToRelative(2.579f, -3.72f)
                    verticalLineToRelative(-0.054f)
                    horizontalLineTo(9.098f)
                    verticalLineToRelative(-0.867f)
                    horizontalLineToRelative(3.785f)
                    verticalLineToRelative(0.691f)
                    lineToRelative(-2.567f, 3.72f)
                    verticalLineToRelative(0.054f)
                    horizontalLineToRelative(2.645f)
                    close()
                    moveTo(4.5f, 2.5f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -1f,
                        0f
                    )
                    verticalLineToRelative(9.793f)
                    lineToRelative(-1.146f, -1.147f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -0.708f,
                        0.708f
                    )
                    lineToRelative(2f, 1.999f)
                    lineToRelative(0.007f, 0.007f)
                    arcToRelative(
                        0.497f,
                        0.497f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        0.7f,
                        -0.006f
                    )
                    lineToRelative(2f, -2f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -0.707f,
                        -0.708f
                    )
                    lineTo(4.5f, 12.293f)
                    close()
                }
            }.build()
            return _SortAlphaDown!!
        }

    private var _SortAlphaUpAlt: ImageVector? = null
    val sortAlphaUpAlt: ImageVector
        get() {
            if (_SortAlphaUpAlt != null) {
                return _SortAlphaUpAlt!!
            }
            _SortAlphaUpAlt = ImageVector.Builder(
                name = "SortAlphaUpAlt",
                defaultWidth = 16.dp,
                defaultHeight = 16.dp,
                viewportWidth = 16f,
                viewportHeight = 16f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(12.96f, 7f)
                    horizontalLineTo(9.028f)
                    verticalLineToRelative(-0.691f)
                    lineToRelative(2.579f, -3.72f)
                    verticalLineToRelative(-0.054f)
                    horizontalLineTo(9.098f)
                    verticalLineToRelative(-0.867f)
                    horizontalLineToRelative(3.785f)
                    verticalLineToRelative(0.691f)
                    lineToRelative(-2.567f, 3.72f)
                    verticalLineToRelative(0.054f)
                    horizontalLineToRelative(2.645f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.EvenOdd
                ) {
                    moveTo(10.082f, 12.629f)
                    lineTo(9.664f, 14f)
                    horizontalLineTo(8.598f)
                    lineToRelative(1.789f, -5.332f)
                    horizontalLineToRelative(1.234f)
                    lineTo(13.402f, 14f)
                    horizontalLineToRelative(-1.12f)
                    lineToRelative(-0.419f, -1.371f)
                    close()
                    moveToRelative(1.57f, -0.785f)
                    lineTo(11f, 9.688f)
                    horizontalLineToRelative(-0.047f)
                    lineToRelative(-0.652f, 2.156f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(4.5f, 13.5f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        -1f,
                        0f
                    )
                    verticalLineTo(3.707f)
                    lineTo(2.354f, 4.854f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        -0.708f,
                        -0.708f
                    )
                    lineToRelative(2f, -1.999f)
                    lineToRelative(0.007f, -0.007f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        0.7f,
                        0.006f
                    )
                    lineToRelative(2f, 2f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        -0.707f,
                        0.708f
                    )
                    lineTo(4.5f, 3.707f)
                    close()
                }
            }.build()
            return _SortAlphaUpAlt!!
        }

    private var _SortNumericDownAlt: ImageVector? = null
    val sortNumericDownAlt: ImageVector
        get() {
            if (_SortNumericDownAlt != null) {
                return _SortNumericDownAlt!!
            }
            _SortNumericDownAlt = ImageVector.Builder(
                name = "SortNumericDownAlt",
                defaultWidth = 16.dp,
                defaultHeight = 16.dp,
                viewportWidth = 16f,
                viewportHeight = 16f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.EvenOdd
                ) {
                    moveTo(11.36f, 7.098f)
                    curveToRelative(-1.1370f, 00f, -1.7080f, -0.6570f, -1.7620f, -1.2780f)
                    horizontalLineToRelative(1.004f)
                    curveToRelative(0.0580f, 0.2230f, 0.3430f, 0.450f, 0.7730f, 0.450f)
                    curveToRelative(0.8240f, 00f, 1.1640f, -0.8290f, 1.1330f, -1.8560f)
                    horizontalLineToRelative(-0.059f)
                    curveToRelative(-0.1480f, 0.390f, -0.570f, 0.7420f, -1.2610f, 0.7420f)
                    curveToRelative(-0.910f, 00f, -1.720f, -0.6130f, -1.720f, -1.7580f)
                    curveToRelative(00f, -1.1480f, 0.8480f, -1.8360f, 1.9730f, -1.8360f)
                    curveToRelative(1.090f, 00f, 2.0630f, 0.6370f, 2.0630f, 2.6880f)
                    curveToRelative(00f, 1.8670f, -0.7230f, 2.8480f, -2.1450f, 2.8480f)
                    close()
                    moveToRelative(0.062f, -2.735f)
                    curveToRelative(0.5040f, 00f, 0.9330f, -0.3360f, 0.9330f, -0.9720f)
                    curveToRelative(00f, -0.6330f, -0.3980f, -1.0080f, -0.940f, -1.0080f)
                    curveToRelative(-0.520f, 00f, -0.9270f, 0.3750f, -0.9270f, 10f)
                    curveToRelative(00f, 0.640f, 0.4180f, 0.980f, 0.9340f, 0.980f)
                }
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(12.438f, 8.668f)
                    verticalLineTo(14f)
                    horizontalLineTo(11.39f)
                    verticalLineTo(9.684f)
                    horizontalLineToRelative(-0.051f)
                    lineToRelative(-1.211f, 0.859f)
                    verticalLineToRelative(-0.969f)
                    lineToRelative(1.262f, -0.906f)
                    horizontalLineToRelative(1.046f)
                    close()
                    moveTo(4.5f, 2.5f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -1f,
                        0f
                    )
                    verticalLineToRelative(9.793f)
                    lineToRelative(-1.146f, -1.147f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -0.708f,
                        0.708f
                    )
                    lineToRelative(2f, 1.999f)
                    lineToRelative(0.007f, 0.007f)
                    arcToRelative(
                        0.497f,
                        0.497f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        0.7f,
                        -0.006f
                    )
                    lineToRelative(2f, -2f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -0.707f,
                        -0.708f
                    )
                    lineTo(4.5f, 12.293f)
                    close()
                }
            }.build()
            return _SortNumericDownAlt!!
        }

    private var _CalendarPlus: ImageVector? = null
    val calendarPlus: ImageVector
        get() {
            if (_CalendarPlus != null) {
                return _CalendarPlus!!
            }
            _CalendarPlus = ImageVector.Builder(
                name = "CalendarPlus",
                defaultWidth = 16.dp,
                defaultHeight = 16.dp,
                viewportWidth = 16f,
                viewportHeight = 16f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(8f, 7f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        0.5f,
                        0.5f
                    )
                    verticalLineTo(9f)
                    horizontalLineTo(10f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        0f,
                        1f
                    )
                    horizontalLineTo(8.5f)
                    verticalLineToRelative(1.5f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        -1f,
                        0f
                    )
                    verticalLineTo(10f)
                    horizontalLineTo(6f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        0f,
                        -1f
                    )
                    horizontalLineToRelative(1.5f)
                    verticalLineTo(7.5f)
                    arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8f, 7f)
                }
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(3.5f, 0f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        0.5f,
                        0.5f
                    )
                    verticalLineTo(1f)
                    horizontalLineToRelative(8f)
                    verticalLineTo(0.5f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        1f,
                        0f
                    )
                    verticalLineTo(1f)
                    horizontalLineToRelative(1f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 2f)
                    verticalLineToRelative(11f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 2f)
                    horizontalLineTo(2f)
                    arcToRelative(
                        2f,
                        2f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        -2f,
                        -2f
                    )
                    verticalLineTo(3f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, -2f)
                    horizontalLineToRelative(1f)
                    verticalLineTo(0.5f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        0.5f,
                        -0.5f
                    )
                    moveTo(1f, 4f)
                    verticalLineToRelative(10f)
                    arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, 1f)
                    horizontalLineToRelative(12f)
                    arcToRelative(
                        1f,
                        1f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        1f,
                        -1f
                    )
                    verticalLineTo(4f)
                    close()
                }
            }.build()
            return _CalendarPlus!!
        }

    private var _CalendarMinus: ImageVector? = null
    val calendarMinus: ImageVector
        get() {
            if (_CalendarMinus != null) {
                return _CalendarMinus!!
            }
            _CalendarMinus = ImageVector.Builder(
                name = "CalendarMinus",
                defaultWidth = 16.dp,
                defaultHeight = 16.dp,
                viewportWidth = 16f,
                viewportHeight = 16f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(5.5f, 9.5f)
                    arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6f, 9f)
                    horizontalLineToRelative(4f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        0f,
                        1f
                    )
                    horizontalLineTo(6f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        -0.5f,
                        -0.5f
                    )
                }
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(3.5f, 0f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        0.5f,
                        0.5f
                    )
                    verticalLineTo(1f)
                    horizontalLineToRelative(8f)
                    verticalLineTo(0.5f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        1f,
                        0f
                    )
                    verticalLineTo(1f)
                    horizontalLineToRelative(1f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 2f)
                    verticalLineToRelative(11f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 2f)
                    horizontalLineTo(2f)
                    arcToRelative(
                        2f,
                        2f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        -2f,
                        -2f
                    )
                    verticalLineTo(3f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, -2f)
                    horizontalLineToRelative(1f)
                    verticalLineTo(0.5f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        0.5f,
                        -0.5f
                    )
                    moveTo(1f, 4f)
                    verticalLineToRelative(10f)
                    arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, 1f)
                    horizontalLineToRelative(12f)
                    arcToRelative(
                        1f,
                        1f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        1f,
                        -1f
                    )
                    verticalLineTo(4f)
                    close()
                }
            }.build()
            return _CalendarMinus!!
        }

    private var _SortNumericUp: ImageVector? = null
    val sortNumericUp: ImageVector
        get() {
            if (_SortNumericUp != null) {
                return _SortNumericUp!!
            }
            _SortNumericUp = ImageVector.Builder(
                name = "SortNumericUp",
                defaultWidth = 16.dp,
                defaultHeight = 16.dp,
                viewportWidth = 16f,
                viewportHeight = 16f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(12.438f, 1.668f)
                    verticalLineTo(7f)
                    horizontalLineTo(11.39f)
                    verticalLineTo(2.684f)
                    horizontalLineToRelative(-0.051f)
                    lineToRelative(-1.211f, 0.859f)
                    verticalLineToRelative(-0.969f)
                    lineToRelative(1.262f, -0.906f)
                    horizontalLineToRelative(1.046f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.EvenOdd
                ) {
                    moveTo(11.36f, 14.098f)
                    curveToRelative(-1.1370f, 00f, -1.7080f, -0.6570f, -1.7620f, -1.2780f)
                    horizontalLineToRelative(1.004f)
                    curveToRelative(0.0580f, 0.2230f, 0.3430f, 0.450f, 0.7730f, 0.450f)
                    curveToRelative(0.8240f, 00f, 1.1640f, -0.8290f, 1.1330f, -1.8560f)
                    horizontalLineToRelative(-0.059f)
                    curveToRelative(-0.1480f, 0.390f, -0.570f, 0.7420f, -1.2610f, 0.7420f)
                    curveToRelative(-0.910f, 00f, -1.720f, -0.6130f, -1.720f, -1.7580f)
                    curveToRelative(00f, -1.1480f, 0.8480f, -1.8350f, 1.9730f, -1.8350f)
                    curveToRelative(1.090f, 00f, 2.0630f, 0.6360f, 2.0630f, 2.6870f)
                    curveToRelative(00f, 1.8670f, -0.7230f, 2.8480f, -2.1450f, 2.8480f)
                    close()
                    moveToRelative(0.062f, -2.735f)
                    curveToRelative(0.5040f, 00f, 0.9330f, -0.3360f, 0.9330f, -0.9720f)
                    curveToRelative(00f, -0.6330f, -0.3980f, -1.0080f, -0.940f, -1.0080f)
                    curveToRelative(-0.520f, 00f, -0.9270f, 0.3750f, -0.9270f, 10f)
                    curveToRelative(00f, 0.640f, 0.4180f, 0.980f, 0.9340f, 0.980f)
                }
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(4.5f, 13.5f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        -1f,
                        0f
                    )
                    verticalLineTo(3.707f)
                    lineTo(2.354f, 4.854f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        -0.708f,
                        -0.708f
                    )
                    lineToRelative(2f, -1.999f)
                    lineToRelative(0.007f, -0.007f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        0.7f,
                        0.006f
                    )
                    lineToRelative(2f, 2f)
                    arcToRelative(
                        0.5f,
                        0.5f,
                        0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        -0.707f,
                        0.708f
                    )
                    lineTo(4.5f, 3.707f)
                    close()
                }
            }.build()
            return _SortNumericUp!!
        }

    val avTimer: ImageVector
        get() {
            if (_avTimer != null) {
                return _avTimer!!
            }
            _avTimer = ImageVector.Builder(
                name = "Av_timer",
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
                    moveTo(480f, 840f)
                    quadToRelative(-74f, 0f, -139.5f, -28.5f)
                    reflectiveQuadTo(226f, 734f)
                    reflectiveQuadToRelative(-77.5f, -114.5f)
                    reflectiveQuadTo(120f, 480f)
                    quadToRelative(0f, -44f, 10f, -85.5f)
                    reflectiveQuadToRelative(29f, -78f)
                    reflectiveQuadToRelative(45.5f, -68f)
                    reflectiveQuadTo(264f, 192f)
                    lineToRelative(272f, 272f)
                    lineToRelative(-56f, 56f)
                    lineToRelative(-216f, -216f)
                    quadToRelative(-30f, 36f, -47f, 80.5f)
                    reflectiveQuadTo(200f, 480f)
                    quadToRelative(0f, 116f, 82f, 198f)
                    reflectiveQuadToRelative(198f, 82f)
                    reflectiveQuadToRelative(198f, -82f)
                    reflectiveQuadToRelative(82f, -198f)
                    quadToRelative(0f, -107f, -68.5f, -184.5f)
                    reflectiveQuadTo(520f, 204f)
                    verticalLineToRelative(76f)
                    horizontalLineToRelative(-80f)
                    verticalLineToRelative(-160f)
                    horizontalLineToRelative(40f)
                    quadToRelative(74f, 0f, 139.5f, 28.5f)
                    reflectiveQuadTo(734f, 226f)
                    reflectiveQuadToRelative(77.5f, 114.5f)
                    reflectiveQuadTo(840f, 480f)
                    reflectiveQuadToRelative(-28.5f, 139.5f)
                    reflectiveQuadTo(734f, 734f)
                    reflectiveQuadToRelative(-114.5f, 77.5f)
                    reflectiveQuadTo(480f, 840f)
                    moveTo(280f, 520f)
                    quadToRelative(-17f, 0f, -28.5f, -11.5f)
                    reflectiveQuadTo(240f, 480f)
                    reflectiveQuadToRelative(11.5f, -28.5f)
                    reflectiveQuadTo(280f, 440f)
                    reflectiveQuadToRelative(28.5f, 11.5f)
                    reflectiveQuadTo(320f, 480f)
                    reflectiveQuadToRelative(-11.5f, 28.5f)
                    reflectiveQuadTo(280f, 520f)
                    moveToRelative(200f, 200f)
                    quadToRelative(-17f, 0f, -28.5f, -11.5f)
                    reflectiveQuadTo(440f, 680f)
                    reflectiveQuadToRelative(11.5f, -28.5f)
                    reflectiveQuadTo(480f, 640f)
                    reflectiveQuadToRelative(28.5f, 11.5f)
                    reflectiveQuadTo(520f, 680f)
                    reflectiveQuadToRelative(-11.5f, 28.5f)
                    reflectiveQuadTo(480f, 720f)
                    moveToRelative(200f, -200f)
                    quadToRelative(-17f, 0f, -28.5f, -11.5f)
                    reflectiveQuadTo(640f, 480f)
                    reflectiveQuadToRelative(11.5f, -28.5f)
                    reflectiveQuadTo(680f, 440f)
                    reflectiveQuadToRelative(28.5f, 11.5f)
                    reflectiveQuadTo(720f, 480f)
                    reflectiveQuadToRelative(-11.5f, 28.5f)
                    reflectiveQuadTo(680f, 520f)
                }
            }.build()
            return _avTimer!!
        }

    private var _avTimer: ImageVector? = null

}
