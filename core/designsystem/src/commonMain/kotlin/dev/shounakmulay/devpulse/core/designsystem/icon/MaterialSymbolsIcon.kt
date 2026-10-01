package dev.shounakmulay.devpulse.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val MobileIcon: ImageVector
    get() {
        val current = _materialSymbolsIcon
        if (current != null) return current

        return ImageVector.Builder(
            name = "com.example.theme.AppTheme.MaterialSymbolsIcon",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE3E3E3)),
            ) {
                moveTo(x = 280.0f, y = 920.0f)
                quadToRelative(dx1 = -33.0f, dy1 = 0.0f, dx2 = -56.5f, dy2 = -23.5f)
                reflectiveQuadTo(x1 = 200.0f, y1 = 840.0f)
                lineToRelative(dx = 0.0f, dy = -720.0f)
                quadToRelative(dx1 = 0.0f, dy1 = -33.0f, dx2 = 23.5f, dy2 = -56.5f)
                reflectiveQuadTo(x1 = 280.0f, y1 = 40.0f)
                lineToRelative(dx = 400.0f, dy = 0.0f)
                quadToRelative(dx1 = 33.0f, dy1 = 0.0f, dx2 = 56.5f, dy2 = 23.5f)
                reflectiveQuadTo(x1 = 760.0f, y1 = 120.0f)
                lineToRelative(dx = 0.0f, dy = 124.0f)
                quadToRelative(dx1 = 18.0f, dy1 = 7.0f, dx2 = 29.0f, dy2 = 22.0f)
                reflectiveQuadToRelative(dx1 = 11.0f, dy1 = 34.0f)
                lineToRelative(dx = 0.0f, dy = 80.0f)
                quadToRelative(dx1 = 0.0f, dy1 = 19.0f, dx2 = -11.0f, dy2 = 34.0f)
                reflectiveQuadToRelative(dx1 = -29.0f, dy1 = 22.0f)
                lineToRelative(dx = 0.0f, dy = 404.0f)
                quadToRelative(dx1 = 0.0f, dy1 = 33.0f, dx2 = -23.5f, dy2 = 56.5f)
                reflectiveQuadTo(x1 = 680.0f, y1 = 920.0f)
                close()
                moveToRelative(dx = 0.0f, dy = -80.0f)
                lineToRelative(dx = 400.0f, dy = 0.0f)
                lineToRelative(dx = 0.0f, dy = -720.0f)
                lineTo(x = 280.0f, y = 120.0f)
                close()
                moveToRelative(dx = 0.0f, dy = 0.0f)
                lineToRelative(dx = 0.0f, dy = -720.0f)
                close()
                moveToRelative(dx = 228.5f, dy = -611.5f)
                quadTo(x1 = 520.0f, y1 = 217.0f, x2 = 520.0f, y2 = 200.0f)
                reflectiveQuadToRelative(dx1 = -11.5f, dy1 = -28.5f)
                reflectiveQuadTo(x1 = 480.0f, y1 = 160.0f)
                reflectiveQuadToRelative(dx1 = -28.5f, dy1 = 11.5f)
                reflectiveQuadTo(x1 = 440.0f, y1 = 200.0f)
                reflectiveQuadToRelative(dx1 = 11.5f, dy1 = 28.5f)
                reflectiveQuadTo(x1 = 480.0f, y1 = 240.0f)
                reflectiveQuadToRelative(dx1 = 28.5f, dy1 = -11.5f)
            }
        }.build().also { _materialSymbolsIcon = it }
    }

@Suppress("ObjectPropertyName")
private var _materialSymbolsIcon: ImageVector? = null
