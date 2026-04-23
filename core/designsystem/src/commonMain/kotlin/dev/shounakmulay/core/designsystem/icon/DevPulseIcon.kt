package dev.shounakmulay.core.designsystem.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.theme.AppTheme

internal val DevPulseIcon: ImageVector
    get() {
        val current = _devPulseIcon
        if (current != null) return current

        return ImageVector.Builder(
            name = "com.example.theme.AppTheme.VectorFromFigma",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE39F51)),
            ) {
                moveTo(x = 13.24f, y = 23.94f)
                arcTo(
                    horizontalEllipseRadius = 13.0f,
                    verticalEllipseRadius = 13.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 11.0f,
                    y1 = 23.96f
                )
                arcToRelative(
                    a = 11.0f,
                    b = 11.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.6f,
                    dy1 = -0.92f
                )
                lineToRelative(dx = -0.66f, dy = -0.3f)
                arcTo(
                    horizontalEllipseRadius = 13.0f,
                    verticalEllipseRadius = 13.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 3.15f,
                    y1 = 20.0f
                )
                arcToRelative(
                    a = 14.0f,
                    b = 14.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.67f,
                    dy1 = -2.33f
                )
                arcToRelative(
                    a = 14.0f,
                    b = 14.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.87f,
                    dy1 = -2.03f
                )
                arcToRelative(
                    a = 8.0f,
                    b = 8.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.37f,
                    dy1 = -1.47f
                )
                arcToRelative(
                    a = 8.0f,
                    b = 8.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.21f,
                    dy1 = -1.64f
                )
                arcTo(
                    horizontalEllipseRadius = 9.0f,
                    verticalEllipseRadius = 9.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 0.2f,
                    y1 = 9.9f
                )
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.41f,
                    dy1 = -1.57f
                )
                arcToRelative(
                    a = 13.0f,
                    b = 13.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.2f,
                    dy1 = -4.0f
                )
                arcToRelative(
                    a = 13.0f,
                    b = 13.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.93f,
                    dy1 = -3.14f
                )
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.3f,
                    dy1 = -1.07f
                )
                arcTo(
                    horizontalEllipseRadius = 8.0f,
                    verticalEllipseRadius = 8.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 11.95f,
                    y1 = 0.0f
                )
                arcToRelative(
                    a = 10.0f,
                    b = 10.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.73f,
                    dy1 = 0.26f
                )
                arcToRelative(
                    a = 15.0f,
                    b = 15.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.76f,
                    dy1 = 0.54f
                )
                lineTo(x = 17.0f, y = 1.06f)
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 4.49f,
                    dy1 = 3.6f
                )
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.6f,
                    dy1 = 2.75f
                )
                arcToRelative(
                    a = 10.24f,
                    b = 10.24f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.9f,
                    dy1 = 4.41f
                )
                arcToRelative(
                    a = 9.0f,
                    b = 9.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.29f,
                    dy1 = 2.88f
                )
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.25f,
                    dy1 = 4.62f
                )
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.9f,
                    dy1 = 2.7f
                )
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -5.32f,
                    dy1 = 1.92f
                )
                moveToRelative(dx = -3.08f, dy = -12.12f)
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.3f,
                    dy1 = 0.37f
                )
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.42f,
                    dy1 = -0.51f
                )
                lineToRelative(dx = 0.2f, dy = -0.28f)
                arcToRelative(
                    a = 93.0f,
                    b = 93.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 2.52f,
                    dy1 = -3.65f
                )
                arcToRelative(
                    a = 15.0f,
                    b = 15.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.49f,
                    dy1 = -2.81f
                )
                arcToRelative(
                    a = 6.0f,
                    b = 6.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.77f,
                    dy1 = -0.87f
                )
                arcToRelative(
                    a = 3.4f,
                    b = 3.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.35f,
                    dy1 = 0.31f
                )
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.4f,
                    dy1 = 0.16f
                )
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.58f,
                    dy1 = -0.67f
                )
                arcToRelative(
                    a = 11.3f,
                    b = 11.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -6.52f,
                    dy1 = -3.24f
                )
                arcTo(
                    horizontalEllipseRadius = 11.3f,
                    verticalEllipseRadius = 11.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 2.68f,
                    y1 = 5.46f
                )
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.56f,
                    dy1 = 0.88f
                )
                quadTo(x1 = 2.06f, y1 = 6.47f, x2 = 2.08f, y2 = 6.49f)
                curveToRelative(
                    dx1 = 0.03f,
                    dy1 = 0.0f,
                    dx2 = 0.2f,
                    dy2 = -0.1f,
                    dx3 = 0.38f,
                    dy3 = -0.2f
                )
                arcToRelative(
                    a = 2.4f,
                    b = 2.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.35f,
                    dy1 = -0.35f
                )
                arcTo(
                    horizontalEllipseRadius = 3.77f,
                    verticalEllipseRadius = 3.77f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 5.97f,
                    y1 = 6.7f
                )
                arcTo(
                    horizontalEllipseRadius = 11.0f,
                    verticalEllipseRadius = 11.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 8.05f,
                    y1 = 8.8f
                )
                arcToRelative(
                    a = 87.98f,
                    b = 87.98f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 2.1f,
                    dy1 = 3.0f
                )
                moveToRelative(dx = 1.1f, dy = 11.58f)
                lineToRelative(dx = 0.67f, dy = 0.02f)
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 5.51f,
                    dy1 = -1.4f
                )
                lineToRelative(dx = 0.35f, dy = -0.2f)
                arcToRelative(
                    a = 13.0f,
                    b = 13.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 2.84f,
                    dy1 = -2.37f
                )
                curveToRelative(
                    dx1 = 0.4f,
                    dy1 = -0.45f,
                    dx2 = 1.28f,
                    dy2 = -1.7f,
                    dx3 = 1.28f,
                    dy3 = -1.83f
                )
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.07f,
                    dx2 = -0.22f,
                    dy2 = -0.04f,
                    dx3 = -0.45f,
                    dy3 = 0.06f
                )
                arcToRelative(
                    a = 1.8f,
                    b = 1.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.32f,
                    dy1 = -0.05f
                )
                arcTo(
                    horizontalEllipseRadius = 6.0f,
                    verticalEllipseRadius = 6.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 19.05f,
                    y1 = 17.0f
                )
                curveToRelative(
                    dx1 = -1.26f,
                    dy1 = -1.02f,
                    dx2 = -1.83f,
                    dy2 = -1.23f,
                    dx3 = -2.63f,
                    dy3 = -0.97f
                )
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.47f,
                    dy1 = 0.88f
                )
                arcToRelative(
                    a = 26.0f,
                    b = 26.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.13f,
                    dy1 = 1.0f
                )
                arcToRelative(
                    a = 7.0f,
                    b = 7.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.6f,
                    dy1 = 1.1f
                )
                arcToRelative(
                    a = 2.5f,
                    b = 2.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.25f,
                    dy1 = 0.23f
                )
                curveToRelative(
                    dx1 = -1.17f,
                    dy1 = 0.0f,
                    dx2 = -2.36f,
                    dy2 = -0.67f,
                    dx3 = -3.78f,
                    dy3 = -2.12f
                )
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.5f,
                    dy1 = -0.48f
                )
                lineToRelative(dx = -0.23f, dy = 0.1f)
                arcToRelative(
                    a = 8.0f,
                    b = 8.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.21f,
                    dy1 = 0.47f
                )
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.7f,
                    dy1 = 0.04f
                )
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.91f,
                    dy1 = -1.03f
                )
                quadToRelative(dx1 = -0.51f, dy1 = -0.53f, dx2 = 0.12f, dy2 = 0.7f)
                arcToRelative(
                    a = 8.0f,
                    b = 8.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.77f,
                    dy1 = 1.34f
                )
                quadToRelative(dx1 = 0.02f, dy1 = 0.08f, dx2 = 0.07f, dy2 = 0.09f)
                quadToRelative(dx1 = 0.04f, dy1 = 0.0f, dx2 = 0.04f, dy2 = 0.05f)
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.07f,
                    dx2 = 0.4f,
                    dy2 = 0.58f,
                    dx3 = 0.85f,
                    dy3 = 1.12f
                )
                arcToRelative(
                    a = 13.0f,
                    b = 13.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 2.86f,
                    dy1 = 2.35f
                )
                arcToRelative(
                    a = 11.0f,
                    b = 11.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 4.92f,
                    dy1 = 1.53f
                )
                moveToRelative(dx = 1.63f, dy = -9.08f)
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.62f,
                    dy1 = 0.07f
                )
                curveToRelative(
                    dx1 = 1.01f,
                    dy1 = 0.0f,
                    dx2 = 1.83f,
                    dy2 = -0.5f,
                    dx3 = 3.27f,
                    dy3 = -2.04f
                )
                curveToRelative(
                    dx1 = 1.83f,
                    dy1 = -1.96f,
                    dx2 = 2.6f,
                    dy2 = -2.47f,
                    dx3 = 3.83f,
                    dy3 = -2.53f
                )
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.87f,
                    dy1 = 0.1f
                )
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.6f,
                    dy1 = 0.25f
                )
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.2f,
                    dy1 = 1.12f
                )
                curveToRelative(
                    dx1 = 0.08f,
                    dy1 = 0.0f,
                    dx2 = -0.1f,
                    dy2 = -1.25f,
                    dx3 = -0.26f,
                    dy3 = -1.9f
                )
                lineToRelative(dx = -0.13f, dy = -0.52f)
                lineToRelative(dx = -0.38f, dy = -1.0f)
                arcToRelative(
                    a = 7.0f,
                    b = 7.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.04f,
                    dy1 = -1.65f
                )
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.94f,
                    dy1 = -0.84f
                )
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.3f,
                    dy1 = -0.62f
                )
                curveToRelative(
                    dx1 = -1.42f,
                    dy1 = -0.3f,
                    dx2 = -2.7f,
                    dy2 = 0.38f,
                    dx3 = -4.18f,
                    dy3 = 2.22f
                )
                arcToRelative(
                    a = 27.0f,
                    b = 27.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.63f,
                    dy1 = 2.3f
                )
                arcToRelative(
                    a = 80.0f,
                    b = 80.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.74f,
                    dy1 = 2.5f
                )
                lineToRelative(dx = -0.53f, dy = 0.68f)
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.23f,
                    dy1 = 0.31f
                )
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.05f,
                    dx2 = 0.8f,
                    dy2 = 0.84f,
                    dx3 = 1.12f,
                    dy3 = 1.11f
                )
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.85f,
                    dy1 = 0.44f
                )
                moveToRelative(dx = -2.7f, dy = 4.1f)
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.87f,
                    dy1 = 0.1f
                )
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.86f,
                    dy1 = -0.16f
                )
                arcTo(
                    horizontalEllipseRadius = 7.0f,
                    verticalEllipseRadius = 7.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 13.78f,
                    y1 = 17.0f
                )
                curveToRelative(
                    dx1 = 1.57f,
                    dy1 = -1.47f,
                    dx2 = 2.63f,
                    dy2 = -1.95f,
                    dx3 = 3.74f,
                    dy3 = -1.69f
                )
                curveToRelative(
                    dx1 = 0.56f,
                    dy1 = 0.14f,
                    dx2 = 0.88f,
                    dy2 = 0.33f,
                    dx3 = 1.86f,
                    dy3 = 1.1f
                )
                arcToRelative(
                    a = 6.0f,
                    b = 6.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.81f,
                    dy1 = 0.54f
                )
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.66f,
                    dy1 = 0.16f
                )
                curveToRelative(
                    dx1 = 0.48f,
                    dy1 = 0.0f,
                    dx2 = 0.76f,
                    dy2 = -0.14f,
                    dx3 = 1.2f,
                    dy3 = -0.6f
                )
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.2f,
                    dy1 = -3.13f
                )
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.63f,
                    dx2 = -0.06f,
                    dy2 = -0.9f,
                    dx3 = -0.3f,
                    dy3 = -1.35f
                )
                arcToRelative(
                    a = 2.8f,
                    b = 2.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.75f,
                    dy1 = -1.52f
                )
                curveToRelative(
                    dx1 = -0.67f,
                    dy1 = -0.2f,
                    dx2 = -1.6f,
                    dy2 = 0.08f,
                    dx3 = -2.32f,
                    dy3 = 0.67f
                )
                arcToRelative(
                    a = 20.0f,
                    b = 20.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.62f,
                    dy1 = 1.6f
                )
                arcToRelative(
                    a = 8.0f,
                    b = 8.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.54f,
                    dy1 = 2.07f
                )
                arcToRelative(
                    a = 3.4f,
                    b = 3.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.41f,
                    dy1 = 0.25f
                )
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.4f,
                    dy1 = -0.48f
                )
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.97f,
                    dy1 = -0.8f
                )
                curveToRelative(
                    dx1 = -0.27f,
                    dy1 = -0.27f,
                    dx2 = -0.4f,
                    dy2 = -0.37f,
                    dx3 = -0.49f,
                    dy3 = -0.37f
                )
                reflectiveCurveToRelative(dx1 = -0.2f, dy1 = 0.1f, dx2 = -0.56f, dy2 = 0.5f)
                arcToRelative(
                    a = 21.0f,
                    b = 21.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.9f,
                    dy1 = 1.79f
                )
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.64f,
                    dy1 = 0.48f
                )
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.05f,
                    dx2 = 0.41f,
                    dy2 = 0.47f,
                    dx3 = 0.89f,
                    dy3 = 0.9f
                )
                arcToRelative(
                    a = 6.0f,
                    b = 6.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.96f,
                    dy1 = 1.3f
                )
                moveToRelative(dx = -3.92f, dy = -3.45f)
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.61f,
                    dy1 = 0.68f
                )
                curveToRelative(
                    dx1 = 0.24f,
                    dy1 = 0.0f,
                    dx2 = 2.32f,
                    dy2 = -1.88f,
                    dx3 = 2.9f,
                    dy3 = -2.61f
                )
                lineToRelative(dx = 0.19f, dy = -0.26f)
                lineToRelative(dx = -0.15f, dy = -0.22f)
                lineToRelative(dx = -0.3f, dy = -0.39f)
                lineToRelative(dx = -0.59f, dy = -0.83f)
                lineToRelative(dx = -1.05f, dy = -1.51f)
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -2.31f,
                    dy1 = -2.55f
                )
                curveTo(x1 = 4.43f, y1 = 6.45f, x2 = 3.26f, y2 = 6.4f, x3 = 2.4f, y3 = 7.16f)
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.31f,
                    dy1 = 2.2f
                )
                curveToRelative(
                    dx1 = -0.14f,
                    dy1 = 0.42f,
                    dx2 = -0.22f,
                    dy2 = 0.81f,
                    dx3 = -0.18f,
                    dy3 = 0.81f
                )
                lineToRelative(dx = 0.18f, dy = -0.08f)
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.52f,
                    dy1 = -0.08f
                )
                curveToRelative(
                    dx1 = 0.33f,
                    dy1 = 0.0f,
                    dx2 = 0.4f,
                    dy2 = 0.02f,
                    dx3 = 0.66f,
                    dy3 = 0.16f
                )
                curveToRelative(
                    dx1 = 0.57f,
                    dy1 = 0.3f,
                    dx2 = 1.14f,
                    dy2 = 0.91f,
                    dx3 = 2.16f,
                    dy3 = 2.33f
                )
                lineToRelative(dx = 1.0f, dy = 1.38f)
                close()
                moveToRelative(dx = -2.46f, dy = 1.6f)
                arcToRelative(
                    a = 3.6f,
                    b = 3.6f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 2.0f,
                    dy1 = -0.3f
                )
                curveToRelative(
                    dx1 = 0.43f,
                    dy1 = -0.2f,
                    dx2 = 0.44f,
                    dy2 = -0.22f,
                    dx3 = 0.23f,
                    dy3 = -0.45f
                )
                arcToRelative(
                    a = 23.0f,
                    b = 23.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.31f,
                    dy1 = -1.68f
                )
                lineToRelative(dx = -0.55f, dy = -0.76f)
                arcToRelative(
                    a = 18.0f,
                    b = 18.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.04f,
                    dy1 = -1.41f
                )
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.2f,
                    dy1 = -1.18f
                )
                curveToRelative(
                    dx1 = -0.72f,
                    dy1 = -0.34f,
                    dx2 = -1.28f,
                    dy2 = 0.4f,
                    dx3 = -1.2f,
                    dy3 = 1.62f
                )
                arcToRelative(
                    a = 4.7f,
                    b = 4.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.71f,
                    dy1 = 3.58f
                )
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.36f,
                    dy1 = 0.59f
                )
            }
        }.build().also { _devPulseIcon = it }
    }

@Preview
@Composable
private fun IconPreview() {
    AppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                imageVector = DevPulseIcon,
                contentDescription = null,
                modifier = Modifier
                    .width((24.0).dp)
                    .height((24.0).dp),
            )
        }
    }
}

@Suppress("ObjectPropertyName")
private var _devPulseIcon: ImageVector? = null
