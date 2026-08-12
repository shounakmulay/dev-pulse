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

internal val ImageLoadingIcon: ImageVector
    get() {
        val current = _imageLoadingIcon
        if (current != null) return current

        return ImageVector.Builder(
            name = "ImageLoadingIcon",
            defaultWidth = 64.dp,
            defaultHeight = 64.dp,
            viewportWidth = 240.0f,
            viewportHeight = 250.0f,
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
                arcToRelative(
                    a = 110.0f,
                    b = 110.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 220.0f,
                    dy1 = 0.0f
                )
                arcToRelative(
                    a = 110.0f,
                    b = 110.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -220.0f,
                    dy1 = 0.0f
                )
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0x803B4658),
                    1f to Color(0x80262F3D),
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
                arcToRelative(
                    a = 18.0f,
                    b = 18.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 18.0f,
                    dy1 = -18.0f
                )
                horizontalLineToRelative(dx = 164.0f)
                arcToRelative(
                    a = 18.0f,
                    b = 18.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 18.0f,
                    dy1 = 18.0f
                )
                verticalLineToRelative(dy = 164.0f)
                arcToRelative(
                    a = 18.0f,
                    b = 18.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -18.0f,
                    dy1 = 18.0f
                )
                horizontalLineToRelative(dx = -164.0f)
                arcToRelative(
                    a = 18.0f,
                    b = 18.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -18.0f,
                    dy1 = -18.0f
                )
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0x803B4658),
                    1f to Color(0x80262F3D),
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
                arcToRelative(
                    a = 18.0f,
                    b = 18.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 18.0f,
                    dy1 = -18.0f
                )
                horizontalLineToRelative(dx = 164.0f)
                arcToRelative(
                    a = 18.0f,
                    b = 18.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 18.0f,
                    dy1 = 18.0f
                )
                verticalLineToRelative(dy = 164.0f)
                arcToRelative(
                    a = 18.0f,
                    b = 18.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -18.0f,
                    dy1 = 18.0f
                )
                horizontalLineToRelative(dx = -164.0f)
                arcToRelative(
                    a = 18.0f,
                    b = 18.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -18.0f,
                    dy1 = -18.0f
                )
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0x804A5871),
                    1f to Color(0x803B4658),
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
                arcToRelative(
                    a = 18.0f,
                    b = 18.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 18.0f,
                    dy1 = 18.0f
                )
                verticalLineToRelative(dy = 4.0f)
                horizontalLineTo(x = 20.0f)
                verticalLineToRelative(dy = -4.0f)
                arcToRelative(
                    a = 18.0f,
                    b = 18.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 18.0f,
                    dy1 = -18.0f
                )
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
                arcToRelative(
                    a = 17.3f,
                    b = 17.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 17.3f,
                    dy1 = -17.3f
                )
                horizontalLineToRelative(dx = 164.4f)
                arcToRelative(
                    a = 17.3f,
                    b = 17.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 17.3f,
                    dy1 = 17.3f
                )
                verticalLineToRelative(dy = 164.4f)
                arcToRelative(
                    a = 17.3f,
                    b = 17.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -17.3f,
                    dy1 = 17.3f
                )
                horizontalLineToRelative(dx = -164.4f)
                arcToRelative(
                    a = 17.3f,
                    b = 17.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -17.3f,
                    dy1 = -17.3f
                )
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0x806B7A93),
                    1f to Color(0x804B5768),
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
                arcToRelative(
                    a = 11.0f,
                    b = 11.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 22.0f,
                    dy1 = 0.0f
                )
                arcToRelative(
                    a = 11.0f,
                    b = 11.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -22.0f,
                    dy1 = 0.0f
                )
                close()
            }
            path(
                fill = Brush.linearGradient(
                    0f to Color(0x804B5768),
                    1f to Color(0x8039445A),
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
            group(
                clipPathData = PathData {
                    // M 20 38
                    moveTo(x = 20.0f, y = 38.0f)
                    // a 18 18 0 0 1 18 -18
                    arcToRelative(
                        a = 18.0f,
                        b = 18.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 18.0f,
                        dy1 = -18.0f,
                    )
                    // h 164
                    horizontalLineToRelative(dx = 164.0f)
                    // a 18 18 0 0 1 18 18
                    arcToRelative(
                        a = 18.0f,
                        b = 18.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 18.0f,
                        dy1 = 18.0f,
                    )
                    // v 164
                    verticalLineToRelative(dy = 164.0f)
                    // a 18 18 0 0 1 -18 18
                    arcToRelative(
                        a = 18.0f,
                        b = 18.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -18.0f,
                        dy1 = 18.0f,
                    )
                    // h -164
                    horizontalLineToRelative(dx = -164.0f)
                    // a 18 18 0 0 1 -18 -18z
                    arcToRelative(
                        a = 18.0f,
                        b = 18.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -18.0f,
                        dy1 = -18.0f,
                    )
                    close()
                },
            ) {
                path(
                    fill = Brush.linearGradient(
                        0f to Color(0x00FFFFFF),
                        0.45f to Color(0x29F1D9BE),
                        0.5f to Color(0x38FBE6CE),
                        0.55f to Color(0x29F1D9BE),
                        1f to Color(0x00FFFFFF),
                        start = Offset(x = -217.98073f, y = 0f),
                        end = Offset(x = 0f, y = 144f),
                    ),
                    fillAlpha = 1.0f,
                    strokeAlpha = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineWidth = 1.0f,
                ) {
                    moveTo(x = -140.0f, y = 0.0f)
                    horizontalLineToRelative(dx = 140.0f)
                    verticalLineToRelative(dy = 240.0f)
                    horizontalLineToRelative(dx = -140.0f)
                    close()
                }
            }
        }.build().also { _imageLoadingIcon = it }
    }

@Suppress("ObjectPropertyName")
private var _imageLoadingIcon: ImageVector? = null
