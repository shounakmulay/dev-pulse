package dev.shounakmulay.core.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import dev.shounakmulay.core.designsystem.theme.DPTheme

enum class DPTextSize { Large, Medium, Small }

@Composable
fun DPDisplayText(
    text: String,
    modifier: Modifier = Modifier,
    size: DPTextSize = DPTextSize.Large,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val colours = DPTheme.colours
    val typography = DPTheme.typography
    val style = when (size) {
        DPTextSize.Large -> typography.displayLarge
        DPTextSize.Medium -> typography.displayMedium
        DPTextSize.Small -> typography.displaySmall
    }
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = if (color == Color.Unspecified) colours.textPrimary else color,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun DPHeadline(
    text: String,
    modifier: Modifier = Modifier,
    size: DPTextSize = DPTextSize.Large,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val colours = DPTheme.colours
    val typography = DPTheme.typography
    val style = when (size) {
        DPTextSize.Large -> typography.headlineLarge
        DPTextSize.Medium -> typography.headlineMedium
        DPTextSize.Small -> typography.headlineSmall
    }
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = if (color == Color.Unspecified) colours.textPrimary else color,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun DPTitle(
    text: String,
    modifier: Modifier = Modifier,
    size: DPTextSize = DPTextSize.Large,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
) {
    val colours = DPTheme.colours
    val typography = DPTheme.typography
    val style = when (size) {
        DPTextSize.Large -> typography.titleLarge
        DPTextSize.Medium -> typography.titleMedium
        DPTextSize.Small -> typography.titleSmall
    }
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = if (color == Color.Unspecified) colours.textPrimary else color,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun DPBody(
    text: String,
    modifier: Modifier = Modifier,
    size: DPTextSize = DPTextSize.Medium,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val colours = DPTheme.colours
    val typography = DPTheme.typography
    val style = when (size) {
        DPTextSize.Large -> typography.bodyLarge
        DPTextSize.Medium -> typography.bodyMedium
        DPTextSize.Small -> typography.bodySmall
    }
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = if (color == Color.Unspecified) colours.textSecondary else color,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun DPLabel(
    text: String,
    modifier: Modifier = Modifier,
    size: DPTextSize = DPTextSize.Medium,
    style: TextStyle? = null,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
) {
    val colours = DPTheme.colours
    val typography = DPTheme.typography
    val resolvedStyle = style ?: when (size) {
        DPTextSize.Large -> typography.labelLarge
        DPTextSize.Medium -> typography.labelMedium
        DPTextSize.Small -> typography.labelSmall
    }
    Text(
        text = text,
        modifier = modifier,
        style = resolvedStyle,
        color = if (color == Color.Unspecified) colours.textSecondary else color,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun DPMono(
    text: String,
    modifier: Modifier = Modifier,
    size: DPTextSize = DPTextSize.Medium,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
) {
    val colours = DPTheme.colours
    val typography = DPTheme.typography
    val style = when (size) {
        DPTextSize.Large, DPTextSize.Medium -> typography.codeMedium
        DPTextSize.Small -> typography.codeSmall
    }
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = if (color == Color.Unspecified) colours.textTertiary else color,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun DPSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    val colours = DPTheme.colours
    val typography = DPTheme.typography
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = typography.eyebrow,
        color = if (color == Color.Unspecified) colours.textTertiary else color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
