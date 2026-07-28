package dev.shounakmulay.devpulse.core.designsystem.icon

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EmptyListIcon: ImageVector
    get() {
        val current = _emptyListIcon
        if (current != null) return current

        return ImageVector.Builder(
            name = "com.example.theme.AppTheme.MyIcon",
            defaultWidth = 400.0.dp,
            defaultHeight = 400.0.dp,
            viewportWidth = 400.0f,
            viewportHeight = 400.0f,
        ).apply {
            path(
                fill = Brush.radialGradient(
                    0f to Color(0x4DF97316),
                    0.45f to Color(0x331E293B),
                    1f to Color(0x001E293B),
                    center = Offset(x = 200f, y = 210f),
                    radius = 170f,
                ),
            ) {
                moveTo(x = 200.0f, y = 210.0f)
                moveToRelative(dx = -170.0f, dy = 0.0f)
                arcToRelative(
                    a = 170.0f,
                    b = 170.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 340.0f,
                    dy1 = 0.0f
                )
                arcToRelative(
                    a = 170.0f,
                    b = 170.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -340.0f,
                    dy1 = 0.0f
                )
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0xFF3F4A5E),
                    1f to Color(0xFF2A3444),
                    start = Offset(x = 94.78721f, y = 112.37367f),
                    end = Offset(x = 94.78721f, y = 337.62634f),
                ),
                fillAlpha = 0.5f,
                strokeAlpha = 0.5f,
            ) {
                moveTo(x = 116.47995f, y = 126.3045f)
                arcToRelative(
                    a = 16.0f,
                    b = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 17.830648f,
                    dy1 = -13.930829f
                )
                lineToRelative(dx = 136.97137f, dy = 16.81797f)
                arcToRelative(
                    a = 16.0f,
                    b = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 13.930829f,
                    dy1 = 17.830648f
                )
                lineToRelative(dx = -21.692743f, dy = 176.67322f)
                arcToRelative(
                    a = 16.0f,
                    b = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -17.830648f,
                    dy1 = 13.930829f
                )
                lineToRelative(dx = -136.97137f, dy = -16.81797f)
                arcToRelative(
                    a = 16.0f,
                    b = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -13.930829f,
                    dy1 = -17.830648f
                )
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0xFF3F4A5E),
                    1f to Color(0xFF2A3444),
                    start = Offset(x = 108.64995f, y = 105.45476f),
                    end = Offset(x = 108.64995f, y = 324.5696f),
                ),
            ) {
                moveTo(x = 108.64995f, y = 132.15828f)
                arcToRelative(
                    a = 16.0f,
                    b = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 14.844921f,
                    dy1 = -17.077127f
                )
                lineToRelative(dx = 137.66383f, dy = -9.626393f)
                arcToRelative(
                    a = 16.0f,
                    b = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 17.077127f,
                    dy1 = 14.844921f
                )
                lineToRelative(dx = 12.416653f, dy = 177.5664f)
                arcToRelative(
                    a = 16.0f,
                    b = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -14.844921f,
                    dy1 = 17.077127f
                )
                lineToRelative(dx = -137.66383f, dy = 9.626393f)
                arcToRelative(
                    a = 16.0f,
                    b = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -17.077127f,
                    dy1 = -14.844921f
                )
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0xFF0F172A),
                    1f to Color(0xFF1E293B),
                    start = Offset(x = 108.23141f, y = 105.45476f),
                    end = Offset(x = 278.23584f, y = 105.45476f),
                ),
            ) {
                moveTo(x = 108.64995f, y = 132.15828f)
                arcToRelative(
                    a = 16.0f,
                    b = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 14.844921f,
                    dy1 = -17.077127f
                )
                lineToRelative(dx = 137.66383f, dy = -9.626393f)
                arcToRelative(
                    a = 16.0f,
                    b = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 17.077127f,
                    dy1 = 14.844921f
                )
                lineToRelative(dx = -0.41853884f, dy = -5.9853845f)
                arcToRelative(
                    a = 16.0f,
                    b = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -14.844921f,
                    dy1 = 17.077127f
                )
                lineToRelative(dx = -137.66383f, dy = 9.626393f)
                arcToRelative(
                    a = 16.0f,
                    b = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -17.077127f,
                    dy1 = -14.844921f
                )
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0xFF0F172A),
                    1f to Color(0xFF1E293B),
                    start = Offset(x = 108.51044f, y = 118.30455f),
                    end = Offset(x = 278.9334f, y = 118.30455f),
                ),
            ) {
                moveTo(x = 108.51044f, y = 130.16315f)
                lineToRelative(dx = 169.58589f, dy = -11.858601f)
                lineToRelative(dx = 0.8370777f, dy = 11.970769f)
                lineToRelative(dx = -169.58589f, dy = 11.858601f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF64748B)),
            ) {
                moveTo(x = 133.96564f, y = 164.47105f)
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.7112303f,
                    dy1 = -4.269282f
                )
                lineToRelative(dx = 81.800255f, dy = -5.720031f)
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 4.269282f,
                    dy1 = 3.7112303f
                )
                lineToRelative(dx = 0.0f, dy = 0.0f)
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.7112303f,
                    dy1 = 4.269282f
                )
                lineToRelative(dx = -81.800255f, dy = 5.720031f)
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -4.269282f,
                    dy1 = -3.7112303f
                )
                close()
            }
            path(
                fill = SolidColor(Color(0xFF4B5768)),
            ) {
                moveTo(x = 135.57004f, y = 187.41504f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.7834227f,
                    dy1 = -3.2019615f
                )
                lineToRelative(dx = 117.712555f, dy = -8.231264f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.2019615f,
                    dy1 = 2.7834227f
                )
                lineToRelative(dx = 0.0f, dy = 0.0f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.7834227f,
                    dy1 = 3.2019615f
                )
                lineToRelative(dx = -117.712555f, dy = 8.231264f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.2019615f,
                    dy1 = -2.7834227f
                )
                close()
            }
            path(
                fill = SolidColor(Color(0xFF4B5768)),
            ) {
                moveTo(x = 136.82565f, y = 205.37119f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.7834227f,
                    dy1 = -3.2019615f
                )
                lineToRelative(dx = 117.712555f, dy = -8.231264f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.2019615f,
                    dy1 = 2.7834227f
                )
                lineToRelative(dx = 0.0f, dy = 0.0f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.7834227f,
                    dy1 = 3.2019615f
                )
                lineToRelative(dx = -117.712555f, dy = 8.231264f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.2019615f,
                    dy1 = -2.7834227f
                )
                close()
            }
            path(
                fill = SolidColor(Color(0xFF4B5768)),
            ) {
                moveTo(x = 138.08127f, y = 223.32733f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.7834227f,
                    dy1 = -3.2019615f
                )
                lineToRelative(dx = 88.7832f, dy = -6.2083263f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.2019615f,
                    dy1 = 2.7834227f
                )
                lineToRelative(dx = 0.0f, dy = 0.0f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.7834227f,
                    dy1 = 3.2019615f
                )
                lineToRelative(dx = -88.7832f, dy = 6.2083263f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.2019615f,
                    dy1 = -2.7834227f
                )
                close()
            }
            path(
                fill = SolidColor(Color(0xFF4B5768)),
            ) {
                moveTo(x = 140.03445f, y = 251.25912f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.7834227f,
                    dy1 = -3.2019615f
                )
                lineToRelative(dx = 117.712555f, dy = -8.231264f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.2019615f,
                    dy1 = 2.7834227f
                )
                lineToRelative(dx = 0.0f, dy = 0.0f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.7834227f,
                    dy1 = 3.2019615f
                )
                lineToRelative(dx = -117.712555f, dy = 8.231264f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.2019615f,
                    dy1 = -2.7834227f
                )
                close()
            }
            path(
                fill = SolidColor(Color(0xFF4B5768)),
            ) {
                moveTo(x = 141.29007f, y = 269.21527f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.7834227f,
                    dy1 = -3.2019615f
                )
                lineToRelative(dx = 117.712555f, dy = -8.231264f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.2019615f,
                    dy1 = 2.7834227f
                )
                lineToRelative(dx = 0.0f, dy = 0.0f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.7834227f,
                    dy1 = 3.2019615f
                )
                lineToRelative(dx = -117.712555f, dy = 8.231264f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.2019615f,
                    dy1 = -2.7834227f
                )
                close()
            }
            path(
                fill = SolidColor(Color(0xFF4B5768)),
            ) {
                moveTo(x = 142.54568f, y = 287.17145f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.7834227f,
                    dy1 = -3.2019615f
                )
                lineToRelative(dx = 68.83192f, dy = -4.8131967f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.2019615f,
                    dy1 = 2.7834227f
                )
                lineToRelative(dx = 0.0f, dy = 0.0f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.7834227f,
                    dy1 = 3.2019615f
                )
                lineToRelative(dx = -68.83192f, dy = 4.8131967f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.2019615f,
                    dy1 = -2.7834227f
                )
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)),
                stroke = Brush.linearGradient(
                    0f to Color(0xFFFB923C),
                    0.6f to Color(0xFFF97316),
                    1f to Color(0xFFEA580C),
                    start = Offset(x = 240f, y = 255f),
                    end = Offset(x = 290f, y = 305f),
                ),
                strokeLineCap = StrokeCap.Round,
                strokeLineWidth = 16.0f,
            ) {
                moveTo(x = 240.0f, y = 255.0f)
                lineTo(x = 290.0f, y = 305.0f)
            }
            path(
                fill = Brush.radialGradient(
                    0f to Color(0xFFF8FAFC),
                    1f to Color(0xFFE2E8F0),
                    center = Offset(x = 195.5f, y = 205f),
                    radius = 82.5f,
                ),
                stroke = Brush.linearGradient(
                    0f to Color(0xFFFB923C),
                    0.6f to Color(0xFFF97316),
                    1f to Color(0xFFEA580C),
                    start = Offset(x = 157f, y = 172f),
                    end = Offset(x = 267f, y = 282f),
                ),
                strokeLineWidth = 12.0f,
            ) {
                moveTo(x = 212.0f, y = 227.0f)
                moveToRelative(dx = -55.0f, dy = 0.0f)
                arcToRelative(
                    a = 55.0f,
                    b = 55.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 110.0f,
                    dy1 = 0.0f
                )
                arcToRelative(
                    a = 55.0f,
                    b = 55.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -110.0f,
                    dy1 = 0.0f
                )
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)),
                stroke = SolidColor(Color(0xFFEA580C)),
                strokeLineCap = StrokeCap.Round,
                strokeLineWidth = 8.0f,
            ) {
                moveTo(x = 194.0f, y = 209.0f)
                lineTo(x = 230.0f, y = 245.0f)
            }
            path(
                fill = SolidColor(Color(0xFF000000)),
                stroke = SolidColor(Color(0xFFEA580C)),
                strokeLineCap = StrokeCap.Round,
                strokeLineWidth = 8.0f,
            ) {
                moveTo(x = 230.0f, y = 209.0f)
                lineTo(x = 194.0f, y = 245.0f)
            }
        }.build().also { _emptyListIcon = it }
    }

@Suppress("ObjectPropertyName")
private var _emptyListIcon: ImageVector? = null

