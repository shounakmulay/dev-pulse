package dev.shounakmulay.devpulse.core.designsystem.icon

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val EmptyContentIcon: ImageVector
    get() {
        val current = _emptyListIconDesign
        if (current != null) return current

        return ImageVector.Builder(
            name = "EmptyContentIcon",
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
                fillAlpha = 1.0f,
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
            ) {
                moveTo(x = 30.0f, y = 210.0f)
                arcToRelative(a = 170.0f, b = 170.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 340.0f, dy1 = 0.0f)
                arcToRelative(a = 170.0f, b = 170.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = -340.0f, dy1 = 0.0f)
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0xFF3F4A5E),
                    1f to Color(0xFF2A3444),
                    start = Offset(x = 110f, y = 90f),
                    end = Offset(x = 110f, y = 300f),
                ),
                fillAlpha = 0.5f,
                strokeAlpha = 0.5f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 1.0f,
            ) {
                moveTo(x = 118.0f, y = 112.0f)
                arcToRelative(a = 14.0f, b = 14.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 14.0f, dy1 = -14.0f)
                horizontalLineToRelative(dx = 152.0f)
                arcToRelative(a = 14.0f, b = 14.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 14.0f, dy1 = 14.0f)
                verticalLineToRelative(dy = 182.0f)
                arcToRelative(a = 14.0f, b = 14.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = -14.0f, dy1 = 14.0f)
                horizontalLineToRelative(dx = -152.0f)
                arcToRelative(a = 14.0f, b = 14.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = -14.0f, dy1 = -14.0f)
                close()
            }
            group(
                clipPathData = PathData {
                    // M 110 104
                    moveTo(x = 110.0f, y = 104.0f)
                    // a 14 14 0 0 1 14 -14
                    arcToRelative(
                        a = 14.0f,
                        b = 14.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 14.0f,
                        dy1 = -14.0f,
                    )
                    // h 152
                    horizontalLineToRelative(dx = 152.0f)
                    // a 14 14 0 0 1 14 14
                    arcToRelative(
                        a = 14.0f,
                        b = 14.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 14.0f,
                        dy1 = 14.0f,
                    )
                    // v 182
                    verticalLineToRelative(dy = 182.0f)
                    // a 14 14 0 0 1 -14 14
                    arcToRelative(
                        a = 14.0f,
                        b = 14.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -14.0f,
                        dy1 = 14.0f,
                    )
                    // h -152
                    horizontalLineToRelative(dx = -152.0f)
                    // a 14 14 0 0 1 -14 -14z
                    arcToRelative(
                        a = 14.0f,
                        b = 14.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -14.0f,
                        dy1 = -14.0f,
                    )
                    close()
                },
            ) {
                path(
                    fill = Brush.linearGradient(
                        0f to Color(0xFF3F4A5E),
                        1f to Color(0xFF2A3444),
                        start = Offset(x = 110f, y = 90f),
                        end = Offset(x = 110f, y = 300f),
                    ),
                    fillAlpha = 1.0f,
                    strokeAlpha = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineWidth = 1.0f,
                ) {
                    moveTo(x = 110.0f, y = 90.0f)
                    horizontalLineToRelative(dx = 180.0f)
                    verticalLineToRelative(dy = 210.0f)
                    horizontalLineToRelative(dx = -180.0f)
                    close()
                }
                path(
                    fill = Brush.linearGradient(
                        0f to Color(0xFF0F172A),
                        1f to Color(0xFF1E293B),
                        start = Offset(x = 110f, y = 90f),
                        end = Offset(x = 290f, y = 90f),
                    ),
                    fillAlpha = 1.0f,
                    strokeAlpha = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineWidth = 1.0f,
                ) {
                    moveTo(x = 110.0f, y = 90.0f)
                    horizontalLineToRelative(dx = 180.0f)
                    verticalLineToRelative(dy = 28.0f)
                    horizontalLineToRelative(dx = -180.0f)
                    close()
                }
            }
            path(
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF1E293B)),
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 1.0f,
            ) {
                moveTo(x = 110.0f, y = 104.0f)
                arcToRelative(a = 14.0f, b = 14.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 14.0f, dy1 = -14.0f)
                horizontalLineToRelative(dx = 152.0f)
                arcToRelative(a = 14.0f, b = 14.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 14.0f, dy1 = 14.0f)
                verticalLineToRelative(dy = 182.0f)
                arcToRelative(a = 14.0f, b = 14.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = -14.0f, dy1 = 14.0f)
                horizontalLineToRelative(dx = -152.0f)
                arcToRelative(a = 14.0f, b = 14.0f, theta = 0.0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = -14.0f, dy1 = -14.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF64748B)),
                fillAlpha = 1.0f,
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 1.0f,
            ) {
                moveTo(x = 130.0f, y = 104.0f)
                moveToRelative(dx = -4.0f, dy = 0.0f)
                arcToRelative(a = 4.0f, b = 4.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 8.0f, dy1 = 0.0f)
                arcToRelative(a = 4.0f, b = 4.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = -8.0f, dy1 = 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF4B5768)),
                fillAlpha = 1.0f,
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 1.0f,
            ) {
                moveTo(x = 148.0f, y = 104.0f)
                moveToRelative(dx = -4.0f, dy = 0.0f)
                arcToRelative(a = 4.0f, b = 4.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 8.0f, dy1 = 0.0f)
                arcToRelative(a = 4.0f, b = 4.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = -8.0f, dy1 = 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF4B5768)),
                fillAlpha = 1.0f,
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 1.0f,
            ) {
                moveTo(x = 166.0f, y = 104.0f)
                moveToRelative(dx = -4.0f, dy = 0.0f)
                arcToRelative(a = 4.0f, b = 4.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 8.0f, dy1 = 0.0f)
                arcToRelative(a = 4.0f, b = 4.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = -8.0f, dy1 = 0.0f)
                close()
            }
            path(
                fill = Brush.radialGradient(
                    0f to Color(0xFFF8FAFC),
                    1f to Color(0xFFE2E8F0),
                    center = Offset(x = 212f, y = 227f),
                    radius = 55f,
                ),
                fillAlpha = 1.0f,
                stroke = Brush.linearGradient(
                    0f to Color(0xFFFB923C),
                    0.6f to Color(0xFFF97316),
                    1f to Color(0xFFEA580C),
                    start = Offset(x = 157f, y = 172f),
                    end = Offset(x = 267f, y = 282f),
                ),
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 12.0f,
            ) {
                moveTo(x = 212.0f, y = 227.0f)
                moveToRelative(dx = -55.0f, dy = 0.0f)
                arcToRelative(a = 55.0f, b = 55.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 110.0f, dy1 = 0.0f)
                arcToRelative(a = 55.0f, b = 55.0f, theta = 0.0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = -110.0f, dy1 = 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF94A3B8)),
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 6.0f,
            ) {
                moveTo(x = 182.0f, y = 210.0f)
                lineTo(x = 242.0f, y = 210.0f)
            }
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF94A3B8)),
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 6.0f,
            ) {
                moveTo(x = 182.0f, y = 230.0f)
                lineTo(x = 232.0f, y = 230.0f)
            }
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF94A3B8)),
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 6.0f,
            ) {
                moveTo(x = 182.0f, y = 250.0f)
                lineTo(x = 222.0f, y = 250.0f)
            }
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = Brush.linearGradient(
                    0f to Color(0xFFFB923C),
                    0.6f to Color(0xFFF97316),
                    1f to Color(0xFFEA580C),
                    start = Offset(x = 172f, y = 192f),
                    end = Offset(x = 252f, y = 262f),
                ),
                strokeAlpha = 1.0f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineWidth = 10.0f,
            ) {
                moveTo(x = 172.0f, y = 192.0f)
                lineTo(x = 252.0f, y = 262.0f)
            }
        }.build().also { _emptyListIconDesign = it }
    }

@Suppress("ObjectPropertyName")
private var _emptyListIconDesign: ImageVector? = null
