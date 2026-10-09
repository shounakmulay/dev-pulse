package dev.shounakmulay.devpulse.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val CollapseContentIcon: ImageVector
    get() {
        val current = _materialSymbolsCollapseIcon
        if (current != null) return current

        return ImageVector.Builder(
            name = "com.example.theme.AppTheme.MaterialSymbolsCollapseIcon",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE3E3E3)),
            ) {
                moveTo(x = 440.0f, y = 520.0f)
                lineToRelative(dx = 0.0f, dy = 240.0f)
                lineToRelative(dx = -80.0f, dy = 0.0f)
                lineToRelative(dx = 0.0f, dy = -160.0f)
                lineTo(x = 200.0f, y = 600.0f)
                lineToRelative(dx = 0.0f, dy = -80.0f)
                close()
                moveToRelative(dx = 160.0f, dy = -320.0f)
                lineToRelative(dx = 0.0f, dy = 160.0f)
                lineToRelative(dx = 160.0f, dy = 0.0f)
                lineToRelative(dx = 0.0f, dy = 80.0f)
                lineTo(x = 520.0f, y = 440.0f)
                lineToRelative(dx = 0.0f, dy = -240.0f)
                close()
            }
        }.build().also { _materialSymbolsCollapseIcon = it }
    }

@Suppress("ObjectPropertyName")
private var _materialSymbolsCollapseIcon: ImageVector? = null
