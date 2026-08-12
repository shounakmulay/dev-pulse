package dev.shounakmulay.devpulse.core.designsystem.icon

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val ImageLoadingError: ImageVector
    get() {
        val current = _imageLoadingError
        if (current != null) return current

        return ImageVector.Builder(
            name = "com.example.theme.AppTheme.ImageLoadingError",
            defaultWidth = 242.0.dp,
            defaultHeight = 242.0.dp,
            viewportWidth = 242.0f,
            viewportHeight = 242.0f,
        ).apply {
            path(
                fill = Brush.radialGradient(
                    0f to Color(0x1AD9843B),
                    0.55f to Color(0x1A1E293B),
                    1f to Color(0x001E293B),
                    center = Offset(x = 120f, y = 120f),
                    radius = 110f,
                ),
                fillAlpha = 1.0f,
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 1.0f,
            ) {
                moveTo(x = 120.0f, y = 120.0f)
                moveToRelative(dx = -110.0f, dy = 0.0f)
                arcToRelative(a = 110.0f, b = 110.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 220.0f, dy1 = 0.0f)
                arcToRelative(a = 110.0f, b = 110.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = -220.0f, dy1 = 0.0f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0xFF3B4658),
                    1f to Color(0xFF262F3D),
                    start = Offset(x = 20f, y = 20f),
                    end = Offset(x = 20f, y = 220f),
                ),
                fillAlpha = 0.35f,
                strokeAlpha = 0.35f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 1.0f,
            ) {
                moveTo(x = 26.0f, y = 44.0f)
                arcToRelative(a = 18.0f, b = 18.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 18.0f, dy1 = -18.0f)
                horizontalLineToRelative(dx = 164.0f)
                arcToRelative(a = 18.0f, b = 18.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 18.0f, dy1 = 18.0f)
                verticalLineToRelative(dy = 164.0f)
                arcToRelative(a = 18.0f, b = 18.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = -18.0f, dy1 = 18.0f)
                horizontalLineToRelative(dx = -164.0f)
                arcToRelative(a = 18.0f, b = 18.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = -18.0f, dy1 = -18.0f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0xFF3B4658),
                    1f to Color(0xFF262F3D),
                    start = Offset(x = 20f, y = 20f),
                    end = Offset(x = 20f, y = 220f),
                ),
                fillAlpha = 1.0f,
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 1.0f,
            ) {
                moveTo(x = 20.0f, y = 38.0f)
                arcToRelative(a = 18.0f, b = 18.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 18.0f, dy1 = -18.0f)
                horizontalLineToRelative(dx = 164.0f)
                arcToRelative(a = 18.0f, b = 18.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 18.0f, dy1 = 18.0f)
                verticalLineToRelative(dy = 164.0f)
                arcToRelative(a = 18.0f, b = 18.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = -18.0f, dy1 = 18.0f)
                horizontalLineToRelative(dx = -164.0f)
                arcToRelative(a = 18.0f, b = 18.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = -18.0f, dy1 = -18.0f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0xFF4A5871),
                    1f to Color(0xFF3B4658),
                    start = Offset(x = 20f, y = 20f),
                    end = Offset(x = 220f, y = 20f),
                ),
                fillAlpha = 0.5f,
                strokeAlpha = 0.5f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
            ) {
                moveTo(x = 38.0f, y = 20.0f)
                horizontalLineToRelative(dx = 164.0f)
                arcToRelative(a = 18.0f, b = 18.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 18.0f, dy1 = 18.0f)
                verticalLineToRelative(dy = 4.0f)
                horizontalLineTo(x = 20.0f)
                verticalLineToRelative(dy = -4.0f)
                arcToRelative(a = 18.0f, b = 18.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 18.0f, dy1 = -18.0f)
            }
            path(
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF1B222D)),
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 1.5f,
            ) {
                moveTo(x = 20.75f, y = 38.05f)
                arcToRelative(a = 17.3f, b = 17.3f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 17.3f, dy1 = -17.3f)
                horizontalLineToRelative(dx = 164.4f)
                arcToRelative(a = 17.3f, b = 17.3f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 17.3f, dy1 = 17.3f)
                verticalLineToRelative(dy = 164.4f)
                arcToRelative(a = 17.3f, b = 17.3f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = -17.3f, dy1 = 17.3f)
                horizontalLineToRelative(dx = -164.4f)
                arcToRelative(a = 17.3f, b = 17.3f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = -17.3f, dy1 = -17.3f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0xFF6B7A93),
                    1f to Color(0xFF4B5768),
                    start = Offset(x = 53f, y = 56f),
                    end = Offset(x = 76f, y = 78f),
                ),
                fillAlpha = 1.0f,
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 1.0f,
            ) {
                moveTo(x = 64.4f, y = 66.7f)
                moveToRelative(dx = -11.0f, dy = 0.0f)
                arcToRelative(a = 11.0f, b = 11.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 22.0f, dy1 = 0.0f)
                arcToRelative(a = 11.0f, b = 11.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = -22.0f, dy1 = 0.0f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0xFF4B5768),
                    1f to Color(0xFF39445A),
                    start = Offset(x = 38f, y = 124f),
                    end = Offset(x = 202f, y = 189f),
                ),
                fillAlpha = 1.0f,
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
            ) {
                moveTo(x = 37.8f, y = 188.9f)
                lineToRelative(dx = 51.1f, dy = -62.2f)
                lineToRelative(dx = 35.5f, dy = 37.7f)
                lineToRelative(dx = 31.2f, dy = -40.0f)
                lineToRelative(dx = 46.6f, dy = 64.5f)
                close()
            }
            path(
                fill = Brush.radialGradient(
                    0f to Color(0xFFF8FAFC),
                    1f to Color(0xFFE2E8F0),
                    center = Offset(x = 200f, y = 200f),
                    radius = 32f,
                ),
                fillAlpha = 1.0f,
                stroke = Brush.linearGradient(
                    0f to Color(0xFFFB923C),
                    0.6f to Color(0xFFF97316),
                    1f to Color(0xFFEA580C),
                    start = Offset(x = 168f, y = 168f),
                    end = Offset(x = 232f, y = 232f),
                ),
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 10.0f,
            ) {
                moveTo(x = 200.0f, y = 200.0f)
                moveToRelative(dx = -32.0f, dy = 0.0f)
                arcToRelative(a = 32.0f, b = 32.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 64.0f, dy1 = 0.0f)
                arcToRelative(a = 32.0f, b = 32.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = -64.0f, dy1 = 0.0f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0xFFFB923C),
                    0.6f to Color(0xFFF97316),
                    1f to Color(0xFFEA580C),
                    start = Offset(x = 168f, y = 168f),
                    end = Offset(x = 232f, y = 232f),
                ),
                fillAlpha = 1.0f,
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 1.0f,
            ) {
                moveTo(x = 196.5f, y = 185.5f)
                arcToRelative(a = 3.5f, b = 3.5f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 3.5f, dy1 = -3.5f)
                horizontalLineToRelative(dx = 0.0f)
                arcToRelative(a = 3.5f, b = 3.5f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 3.5f, dy1 = 3.5f)
                verticalLineToRelative(dy = 15.0f)
                arcToRelative(a = 3.5f, b = 3.5f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = -3.5f, dy1 = 3.5f)
                horizontalLineToRelative(dx = 0.0f)
                arcToRelative(a = 3.5f, b = 3.5f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = -3.5f, dy1 = -3.5f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0xFFFB923C),
                    0.6f to Color(0xFFF97316),
                    1f to Color(0xFFEA580C),
                    start = Offset(x = 168f, y = 168f),
                    end = Offset(x = 232f, y = 232f),
                ),
                fillAlpha = 1.0f,
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 1.0f,
            ) {
                moveTo(x = 200.0f, y = 213.0f)
                moveToRelative(dx = -4.2f, dy = 0.0f)
                arcToRelative(a = 4.2f, b = 4.2f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 8.4f, dy1 = 0.0f)
                arcToRelative(a = 4.2f, b = 4.2f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = -8.4f, dy1 = 0.0f)
                close()
            }
        }.build().also { _imageLoadingError = it }
    }

@Suppress("ObjectPropertyName")
private var _imageLoadingError: ImageVector? = null
