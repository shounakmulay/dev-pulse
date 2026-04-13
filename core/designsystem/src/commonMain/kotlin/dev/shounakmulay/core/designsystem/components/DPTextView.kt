package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.ui.unit.sp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.monoFontFamily

enum class DPTextSize { Large, Medium, Small }

enum class DPTextViewVariant {
    HeadingLarge,
    HeadingMedium,
    HeadingSmall,
    TitleLarge,
    TitleMedium,
    TitleSmall,
    BodyLarge,
    BodyMedium,
    BodySmall,
    LabelLarge,
    LabelMedium,
    LabelSmall,
}

@Composable
fun DPTextViewVariant.textStyle(): TextStyle = when (this) {
    DPTextViewVariant.HeadingLarge -> MaterialTheme.typography.headlineLarge
    DPTextViewVariant.HeadingMedium -> MaterialTheme.typography.headlineMedium
    DPTextViewVariant.HeadingSmall -> MaterialTheme.typography.headlineSmall
    DPTextViewVariant.TitleLarge -> MaterialTheme.typography.titleLarge
    DPTextViewVariant.TitleMedium -> MaterialTheme.typography.titleMedium
    DPTextViewVariant.TitleSmall -> MaterialTheme.typography.titleSmall
    DPTextViewVariant.BodyLarge -> MaterialTheme.typography.bodyLarge
    DPTextViewVariant.BodyMedium -> MaterialTheme.typography.bodyMedium
    DPTextViewVariant.BodySmall -> MaterialTheme.typography.bodySmall
    DPTextViewVariant.LabelLarge -> MaterialTheme.typography.labelLarge
    DPTextViewVariant.LabelMedium -> MaterialTheme.typography.labelMedium
    DPTextViewVariant.LabelSmall -> MaterialTheme.typography.labelSmall
}

@Composable
fun DPTextView(
    text: String,
    variant: DPTextViewVariant,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
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

    Text(
        text = text,
        modifier = modifier,
        color = color,
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

@Composable
fun DPHeading(
    text: String,
    modifier: Modifier = Modifier,
    size: DPTextSize = DPTextSize.Large,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val variant = when (size) {
        DPTextSize.Large -> DPTextViewVariant.HeadingLarge
        DPTextSize.Medium -> DPTextViewVariant.HeadingMedium
        DPTextSize.Small -> DPTextViewVariant.HeadingSmall
    }
    DPTextView(
        text = text,
        variant = variant,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
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
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val variant = when (size) {
        DPTextSize.Large -> DPTextViewVariant.TitleLarge
        DPTextSize.Medium -> DPTextViewVariant.TitleMedium
        DPTextSize.Small -> DPTextViewVariant.TitleSmall
    }
    DPTextView(
        text = text,
        variant = variant,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
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
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val variant = when (size) {
        DPTextSize.Large -> DPTextViewVariant.BodyLarge
        DPTextSize.Medium -> DPTextViewVariant.BodyMedium
        DPTextSize.Small -> DPTextViewVariant.BodySmall
    }
    DPTextView(
        text = text,
        variant = variant,
        modifier = modifier,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun DPLabel(
    text: String,
    modifier: Modifier = Modifier,
    size: DPTextSize = DPTextSize.Medium,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val variant = when (size) {
        DPTextSize.Large -> DPTextViewVariant.LabelLarge
        DPTextSize.Medium -> DPTextViewVariant.LabelMedium
        DPTextSize.Small -> DPTextViewVariant.LabelSmall
    }
    DPTextView(
        text = text,
        variant = variant,
        modifier = modifier,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign,
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
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val monoFamily = monoFontFamily()
    val variant = when (size) {
        DPTextSize.Large -> DPTextViewVariant.LabelLarge
        DPTextSize.Medium -> DPTextViewVariant.LabelMedium
        DPTextSize.Small -> DPTextViewVariant.LabelSmall
    }
    DPTextView(
        text = text,
        variant = variant,
        modifier = modifier,
        color = color,
        fontFamily = monoFamily,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@DPComponentPreview
@Composable
private fun DPHeadingPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DPHeading(text = "Heading Large", size = DPTextSize.Large)
            DPHeading(text = "Heading Medium", size = DPTextSize.Medium)
            DPHeading(text = "Heading Small", size = DPTextSize.Small)
        }
    }
}

@DPComponentPreview
@Composable
private fun DPTitlePreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DPTitle(text = "Title Large", size = DPTextSize.Large)
            DPTitle(text = "Title Medium", size = DPTextSize.Medium)
            DPTitle(text = "Title Small", size = DPTextSize.Small)
        }
    }
}

@DPComponentPreview
@Composable
private fun DPBodyPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DPBody(text = "Body Large", size = DPTextSize.Large)
            DPBody(text = "Body Medium", size = DPTextSize.Medium)
            DPBody(text = "Body Small", size = DPTextSize.Small)
        }
    }
}

@DPComponentPreview
@Composable
private fun DPLabelPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DPLabel(text = "Label Large", size = DPTextSize.Large)
            DPLabel(text = "Label Medium", size = DPTextSize.Medium)
            DPLabel(text = "Label Small", size = DPTextSize.Small)
        }
    }
}

@DPComponentPreview
@Composable
private fun DPMonoPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DPMono(text = "Mono Large", size = DPTextSize.Large)
            DPMono(text = "Mono Medium", size = DPTextSize.Medium)
            DPMono(text = "Mono Small", size = DPTextSize.Small)
        }
    }
}
