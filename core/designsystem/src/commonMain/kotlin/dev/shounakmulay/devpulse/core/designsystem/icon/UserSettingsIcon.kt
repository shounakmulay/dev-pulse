package dev.shounakmulay.devpulse.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val UserSettingsIcon: ImageVector
    get() {
        val current = _userSettingsIcon
        if (current != null) return current
        return ImageVector.Builder(
            name = "UserSettingsIcon",
            defaultWidth = 32.0.dp,
            defaultHeight = 32.0.dp,
            viewportWidth = 256.0f,
            viewportHeight = 256.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
            ) {
                moveTo(x = 144.0f, y = 157.68f)
                arcToRelative(a = 68.0f, b = 68.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = false, dx1 = -71.9f, dy1 = 0.0f)
                curveToRelative(dx1 = -20.65f, dy1 = 6.76f, dx2 = -39.23f, dy2 = 19.39f, dx3 = -54.17f, dy3 = 37.17f)
                arcToRelative(a = 8.0f, b = 8.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = false, dx1 = 12.24f, dy1 = 10.3f)
                curveTo(x1 = 50.25f, y1 = 181.19f, x2 = 77.91f, y2 = 168.0f, x3 = 108.0f, y3 = 168.0f)
                reflectiveCurveToRelative(dx1 = 57.75f, dy1 = 13.19f, dx2 = 77.87f, dy2 = 37.15f)
                arcToRelative(a = 8.0f, b = 8.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = 12.26f, dy1 = -10.3f)
                curveTo(x1 = 183.18f, y1 = 177.07f, x2 = 164.6f, y2 = 164.44f, x3 = 144.0f, y3 = 157.68f)
                close()
                moveTo(x = 56.0f, y = 100.0f)
                arcToRelative(a = 52.0f, b = 52.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 52.0f, dy1 = 52.0f)
                arcTo(horizontalEllipseRadius = 52.06f, verticalEllipseRadius = 52.06f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, x1 = 56.0f, y1 = 100.0f)
                close()
                moveToRelative(dx = 196.25f, dy = 43.07f)
                lineToRelative(dx = -4.66f, dy = -2.69f)
                arcToRelative(a = 23.6f, b = 23.6f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = 0.0f, dy1 = -8.76f)
                lineToRelative(dx = 4.66f, dy = -2.69f)
                arcToRelative(a = 8.0f, b = 8.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = false, dx1 = -8.0f, dy1 = -13.86f)
                lineToRelative(dx = -4.67f, dy = 2.7f)
                arcToRelative(a = 23.92f, b = 23.92f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = -7.58f, dy1 = -4.39f)
                verticalLineTo(y = 108.0f)
                arcToRelative(a = 8.0f, b = 8.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = -16.0f, dy1 = 0.0f)
                verticalLineToRelative(dy = 5.38f)
                arcToRelative(a = 23.92f, b = 23.92f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = -7.58f, dy1 = 4.39f)
                lineToRelative(dx = -4.67f, dy = -2.7f)
                arcToRelative(a = 8.0f, b = 8.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = false, dx1 = -8.0f, dy1 = 13.86f)
                lineToRelative(dx = 4.66f, dy = 2.69f)
                arcToRelative(a = 23.6f, b = 23.6f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = 0.0f, dy1 = 8.76f)
                lineToRelative(dx = -4.66f, dy = 2.69f)
                arcToRelative(a = 8.0f, b = 8.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = 8.0f, dy1 = 13.86f)
                lineToRelative(dx = 4.67f, dy = -2.7f)
                arcToRelative(a = 23.92f, b = 23.92f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = 7.58f, dy1 = 4.39f)
                verticalLineTo(y = 164.0f)
                arcToRelative(a = 8.0f, b = 8.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = 16.0f, dy1 = 0.0f)
                verticalLineToRelative(dy = -5.38f)
                arcToRelative(a = 23.92f, b = 23.92f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = 7.58f, dy1 = -4.39f)
                lineToRelative(dx = 4.67f, dy = 2.7f)
                arcToRelative(a = 7.92f, b = 7.92f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = 4.0f, dy1 = 1.07f)
                arcToRelative(a = 8.0f, b = 8.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = 4.0f, dy1 = -14.93f)
                close()
                moveTo(x = 216.0f, y = 136.0f)
                arcToRelative(a = 8.0f, b = 8.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 8.0f, dy1 = 8.0f)
                arcTo(horizontalEllipseRadius = 8.0f, verticalEllipseRadius = 8.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, x1 = 216.0f, y1 = 136.0f)
                close()
            }
        }.build().also { _userSettingsIcon = it }
    }

@Suppress("ObjectPropertyName")
private var _userSettingsIcon: ImageVector? = null
