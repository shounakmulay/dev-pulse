package dev.shounakmulay.devpulse.core.ui.text

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource

@Immutable
@Serializable
sealed class TextResource {
    companion object {
        fun fromText(text: String): TextResource = SimpleTextResource(text)

        fun fromStringRes(res: StringResource): TextResource = StringResTextResource(res)

        fun fromStringResWithArgs(res: StringResource, vararg args: String): TextResource =
            StringResWithArgsTextResource(res, args.toList())

        fun fromPluralRes(res: PluralStringResource, quantity: Int): TextResource =
            PluralResTextResource(res, quantity)

        fun fromJoined(vararg resources: TextResource, separator: String = " "): TextResource =
            JoinedTextResource(resources.toList(), separator)

        fun buildTextResource(
            separator: String = "",
            builderAction: TextResourceBuilder.() -> Unit
        ): TextResource {
            val builder = TextResourceBuilder(separator)
            builder.builderAction()
            return builder.build()
        }

        fun bold(resource: TextResource): TextResource = StyledTextResource(resource, Style.BOLD)

        fun italic(resource: TextResource): TextResource =
            StyledTextResource(resource, Style.ITALIC)

        fun url(
            resource: TextResource,
            url: String,
            styles: TextLinkStyles? = null
        ): TextResource = UrlTextResource(resource, url)

        fun clickable(
            resource: TextResource,
            tag: String = "",
            styles: TextLinkStyles? = null,
            onClick: ((String) -> Unit)? = null
        ): TextResource = ClickableTextResource(resource, tag)

        fun fromAnnotatedString(annotatedString: AnnotatedString): TextResource =
            AnnotatedStringTextResource(
                text = annotatedString.text,
                spans = annotatedString.spanStyles.map { it.toTextResourceSpan() },
            )

        val Empty = fromText("")

        val Space = fromText(" ")
    }

    override fun toString(): String = throw IllegalStateException(
        "Did you use toString() on a TextResource? Use asString() or asAnnotatedString() instead.",
    )
}

@Serializable
data class SimpleTextResource(val text: String) : TextResource()

@Serializable
data class StringResTextResource(val key: String) : TextResource() {
    constructor(res: StringResource) : this(res.key)
}

@Serializable
data class StringResWithArgsTextResource(val key: String, val args: List<String>) : TextResource() {
    constructor(res: StringResource, args: List<String>) : this(res.key, args)
}

@Serializable
data class PluralResTextResource(val key: String, val quantity: Int) : TextResource() {
    constructor(res: PluralStringResource, quantity: Int) : this(res.key, quantity)
}

@Serializable
data class JoinedTextResource(val parts: List<TextResource>, val separator: String) : TextResource()

@Serializable
data class StyledTextResource(val resource: TextResource, val style: Style) : TextResource()

@Serializable
data class UrlTextResource(val resource: TextResource, val url: String) : TextResource()

@Serializable
data class ClickableTextResource(val resource: TextResource, val tag: String) : TextResource()

/**
 * Serializable stand-in for an [AnnotatedString]. [spans] carry only the [SpanStyle] fields
 * that round-trip cleanly across platforms and survive kotlinx.serialization.
 */
@Serializable
data class AnnotatedStringTextResource(
    val text: String,
    val spans: List<TextResourceSpan> = emptyList(),
) : TextResource()

@Serializable
data class TextResourceSpan(
    val start: Int,
    val end: Int,
    val colorArgb: Int? = null,
    val backgroundArgb: Int? = null,
    val fontWeight: Int? = null,       // FontWeight.weight, 1..1000
    val italic: Boolean? = null,
    val underline: Boolean? = null,
    val strikethrough: Boolean? = null,
    val fontSizeSp: Float? = null,
)

private fun AnnotatedString.Range<SpanStyle>.toTextResourceSpan(): TextResourceSpan {
    val style = item
    val decoration = style.textDecoration
    return TextResourceSpan(
        start = start,
        end = end,
        colorArgb = style.color.takeIf { it.isSpecified }?.toArgb(),
        backgroundArgb = style.background.takeIf { it.isSpecified }?.toArgb(),
        fontWeight = style.fontWeight?.weight,
        italic = style.fontStyle?.let { it == FontStyle.Italic },
        underline = decoration?.let { TextDecoration.Underline in it },
        strikethrough = decoration?.let { TextDecoration.LineThrough in it },
        fontSizeSp = style.fontSize.takeIf { it != TextUnit.Unspecified }?.value,
    )
}

internal fun TextResourceSpan.toSpanStyleRange(
    color: Color? = null
): AnnotatedString.Range<SpanStyle> =
    AnnotatedString.Range(
        item = SpanStyle(
            color = color ?: colorArgb?.let { Color(it) } ?: Color.Unspecified,
            background = backgroundArgb?.let { Color(it) } ?: Color.Unspecified,
            fontWeight = fontWeight?.let { androidx.compose.ui.text.font.FontWeight(it) },
            fontStyle = italic?.let { if (it) FontStyle.Italic else FontStyle.Normal },
            textDecoration = buildTextDecoration(),
            fontSize = fontSizeSp?.sp ?: TextUnit.Unspecified,
        ),
        start = start,
        end = end,
    )

private fun TextResourceSpan.buildTextDecoration(): TextDecoration? {
    if (underline == null && strikethrough == null) return null
    val decorations = buildList {
        if (underline == true) add(TextDecoration.Underline)
        if (strikethrough == true) add(TextDecoration.LineThrough)
    }
    return if (decorations.isEmpty()) TextDecoration.None else TextDecoration.combine(decorations)
}