package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.DPIntent
import dev.shounakmulay.core.designsystem.theme.colors
import dev.shounakmulay.core.designsystem.theme.monoFontFamily

enum class DPTextViewVariant {
    DisplayLarge,
    DisplayMedium,
    DisplaySmall,
    DisplayLargeEmphasized,
    DisplayMediumEmphasized,
    DisplaySmallEmphasized,
    HeadingLarge,
    HeadingMedium,
    HeadingSmall,
    HeadingLargeEmphasized,
    HeadingMediumEmphasized,
    HeadingSmallEmphasized,
    TitleLarge,
    TitleMedium,
    TitleSmall,
    TitleLargeEmphasized,
    TitleMediumEmphasized,
    TitleSmallEmphasized,
    BodyLarge,
    BodyMedium,
    BodySmall,
    BodyLargeEmphasized,
    BodyMediumEmphasized,
    BodySmallEmphasized,
    LabelLarge,
    LabelMedium,
    LabelSmall,
    LabelLargeEmphasized,
    LabelMediumEmphasized,
    LabelSmallEmphasized,
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPTextViewVariant.textStyle(): TextStyle = when (this) {
    DPTextViewVariant.DisplayLarge -> MaterialTheme.typography.displayLarge
    DPTextViewVariant.DisplayMedium -> MaterialTheme.typography.displayMedium
    DPTextViewVariant.DisplaySmall -> MaterialTheme.typography.displaySmall
    DPTextViewVariant.DisplayLargeEmphasized -> MaterialTheme.typography.displayLargeEmphasized
    DPTextViewVariant.DisplayMediumEmphasized -> MaterialTheme.typography.displayMediumEmphasized
    DPTextViewVariant.DisplaySmallEmphasized -> MaterialTheme.typography.displaySmallEmphasized
    DPTextViewVariant.HeadingLarge -> MaterialTheme.typography.headlineLarge
    DPTextViewVariant.HeadingMedium -> MaterialTheme.typography.headlineMedium
    DPTextViewVariant.HeadingSmall -> MaterialTheme.typography.headlineSmall
    DPTextViewVariant.HeadingLargeEmphasized -> MaterialTheme.typography.headlineLargeEmphasized
    DPTextViewVariant.HeadingMediumEmphasized -> MaterialTheme.typography.headlineMediumEmphasized
    DPTextViewVariant.HeadingSmallEmphasized -> MaterialTheme.typography.headlineSmallEmphasized
    DPTextViewVariant.TitleLarge -> MaterialTheme.typography.titleLarge
    DPTextViewVariant.TitleMedium -> MaterialTheme.typography.titleMedium
    DPTextViewVariant.TitleSmall -> MaterialTheme.typography.titleSmall
    DPTextViewVariant.TitleLargeEmphasized -> MaterialTheme.typography.titleLargeEmphasized
    DPTextViewVariant.TitleMediumEmphasized -> MaterialTheme.typography.titleMediumEmphasized
    DPTextViewVariant.TitleSmallEmphasized -> MaterialTheme.typography.titleSmallEmphasized
    DPTextViewVariant.BodyLarge -> MaterialTheme.typography.bodyLarge
    DPTextViewVariant.BodyMedium -> MaterialTheme.typography.bodyMedium
    DPTextViewVariant.BodySmall -> MaterialTheme.typography.bodySmall
    DPTextViewVariant.BodyLargeEmphasized -> MaterialTheme.typography.bodyLargeEmphasized
    DPTextViewVariant.BodyMediumEmphasized -> MaterialTheme.typography.bodyMediumEmphasized
    DPTextViewVariant.BodySmallEmphasized -> MaterialTheme.typography.bodySmallEmphasized
    DPTextViewVariant.LabelLarge -> MaterialTheme.typography.labelLarge
    DPTextViewVariant.LabelMedium -> MaterialTheme.typography.labelMedium
    DPTextViewVariant.LabelSmall -> MaterialTheme.typography.labelSmall
    DPTextViewVariant.LabelLargeEmphasized -> MaterialTheme.typography.labelLargeEmphasized
    DPTextViewVariant.LabelMediumEmphasized -> MaterialTheme.typography.labelMediumEmphasized
    DPTextViewVariant.LabelSmallEmphasized -> MaterialTheme.typography.labelSmallEmphasized
}

@Composable
fun DPTextView(
    text: String,
    variant: DPTextViewVariant,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    intent: DPIntent? = null,
    autoSize: TextAutoSize? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle? = null,
) {
    val baseStyle = variant.textStyle()
    val mergedStyle = style?.let { baseStyle.merge(it) } ?: baseStyle
    val resolvedColor = when {
        color != Color.Unspecified -> color
        intent != null -> intent.colors().accent
        else -> Color.Unspecified
    }

    Text(
        text = text,
        modifier = modifier,
        color = resolvedColor,
        autoSize = autoSize,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = mergedStyle,
    )
}

@DPComponentPreview
@Composable
private fun DPTextViewAllVariantsPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DPTextViewVariant.entries.forEach { variant ->
                DPTextView(text = variant.name, variant = variant)
            }
        }
    }
}

@DPComponentPreview
@Composable
private fun DPTextViewMonoPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DPTextView(
                text = "Mono Large",
                variant = DPTextViewVariant.LabelLarge,
                fontFamily = monoFontFamily(),
            )
            DPTextView(
                text = "Mono Medium",
                variant = DPTextViewVariant.LabelMedium,
                fontFamily = monoFontFamily(),
            )
            DPTextView(
                text = "Mono Small",
                variant = DPTextViewVariant.LabelSmall,
                fontFamily = monoFontFamily(),
            )
        }
    }
}
