package dev.shounakmulay.devpulse.core.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

suspend fun TextResource.resolve(): String = when (this) {
    is SimpleTextResource -> text
    is StringResTextResource -> getString(toStringResource())
    is StringResWithArgsTextResource -> getString(toStringResource(), *args.toTypedArray())
    is PluralResTextResource -> getPluralString(toPluralStringResource(), quantity, quantity)
    is JoinedTextResource -> parts.map { it.resolve() }.filter { it.isNotEmpty() }
        .joinToString(separator)

    is StyledTextResource -> resource.resolve()
    is UrlTextResource -> resource.resolve()
    is ClickableTextResource -> resource.resolve()
    is AnnotatedStringTextResource -> text
}

suspend fun TextResource.resolveAnnotated(onLinkClick: ((String) -> Unit)? = null): AnnotatedString =
    when (this) {
        is SimpleTextResource -> AnnotatedString(text)
        is StringResTextResource -> AnnotatedString(getString(toStringResource()))
        is StringResWithArgsTextResource -> AnnotatedString(
            getString(
                toStringResource(),
                *args.toTypedArray()
            )
        )

        is PluralResTextResource -> AnnotatedString(
            getPluralString(
                toPluralStringResource(),
                quantity,
                quantity
            )
        )

        is JoinedTextResource -> {
            val resolvedParts =
                parts.map { it.resolveAnnotated(onLinkClick) }.filter { it.isNotEmpty() }
            val sep = AnnotatedString(separator)
            buildAnnotatedString {
                resolvedParts.forEachIndexed { index, part ->
                    append(part)
                    if (index < resolvedParts.size - 1) append(sep)
                }
            }
        }

        is StyledTextResource -> {
            val inner = resource.resolveAnnotated(onLinkClick)
            buildAnnotatedString { withStyle(style.toSpanStyle()) { append(inner) } }
        }

        is UrlTextResource -> {
            val inner = resource.resolveAnnotated(onLinkClick)
            buildAnnotatedString { withLink(LinkAnnotation.Url(url)) { append(inner) } }
        }

        is ClickableTextResource -> {
            val inner = resource.resolveAnnotated(onLinkClick)
            buildAnnotatedString {
                withLink(LinkAnnotation.Clickable(tag) { onLinkClick?.invoke(tag) }) { append(inner) }
            }
        }

        is AnnotatedStringTextResource -> AnnotatedString(
            text = text,
            spanStyles = spans.map { it.toSpanStyleRange() },
        )
    }

// Composable layer — resolve inside composables (non-suspending)

@Composable
fun TextResource.asString(): String = when (this) {
    is SimpleTextResource -> text
    is StringResTextResource -> stringResource(toStringResource())
    is StringResWithArgsTextResource -> stringResource(toStringResource(), *args.toTypedArray())
    is PluralResTextResource -> pluralStringResource(toPluralStringResource(), quantity)
    is JoinedTextResource -> parts.map { it.asString() }.filter { it.isNotEmpty() }
        .joinToString(separator)

    is StyledTextResource -> resource.asString()
    is UrlTextResource -> resource.asString()
    is ClickableTextResource -> resource.asString()
    is AnnotatedStringTextResource -> text
}

@Composable
fun TextResource.asAnnotatedString(
    onLinkClick: ((String) -> Unit)? = null,
    color: Color? = null
): AnnotatedString =
    when (this) {
        is SimpleTextResource -> AnnotatedString(text)
        is StringResTextResource -> AnnotatedString(stringResource(toStringResource()))
        is StringResWithArgsTextResource -> AnnotatedString(
            stringResource(
                toStringResource(),
                *args.toTypedArray()
            )
        )

        is PluralResTextResource -> AnnotatedString(
            pluralStringResource(
                toPluralStringResource(),
                quantity
            )
        )

        is JoinedTextResource -> {
            val resolvedParts =
                parts.map { it.asAnnotatedString(onLinkClick) }.filter { it.isNotEmpty() }
            val sep = AnnotatedString(separator)
            buildAnnotatedString {
                resolvedParts.forEachIndexed { index, part ->
                    append(part)
                    if (index < resolvedParts.size - 1) append(sep)
                }
            }
        }

        is StyledTextResource -> {
            val inner = resource.asAnnotatedString(onLinkClick)
            buildAnnotatedString { withStyle(style.toSpanStyle()) { append(inner) } }
        }

        is UrlTextResource -> {
            val inner = resource.asAnnotatedString(onLinkClick)
            buildAnnotatedString { withLink(LinkAnnotation.Url(url)) { append(inner) } }
        }

        is ClickableTextResource -> {
            val inner = resource.asAnnotatedString(onLinkClick)
            buildAnnotatedString {
                withLink(LinkAnnotation.Clickable(tag) { onLinkClick?.invoke(tag) }) { append(inner) }
            }
        }

        is AnnotatedStringTextResource -> remember(text, spans, color) {
            AnnotatedString(
                text = text,
                spanStyles = spans.map { it.toSpanStyleRange(color = color) },
            )
        }
    }

fun TextResource.asSimpleTextString(): String? = when (this) {
    is SimpleTextResource -> this.text
    else -> null
}

fun TextResource?.orEmpty() = this ?: TextResource.Empty

fun TextResource?.isNullOrEmpty(): Boolean {
    if (this == null) return true
    return when (this) {
        is SimpleTextResource -> this.text.isEmpty()
        is JoinedTextResource -> this.parts.all { it.isNullOrEmpty() }
        is AnnotatedStringTextResource -> this.text.isEmpty()
        else -> this == TextResource.Empty
    }
}

fun TextResource?.isNotNullOrEmpty() = !this.isNullOrEmpty()

operator fun TextResource.plus(other: TextResource): TextResource {
    val leftParts = when (this) {
        is JoinedTextResource ->
            if (this.separator.isEmpty()) this.parts else listOf(this)

        else -> listOf(this)
    }

    val rightParts = when (other) {
        is JoinedTextResource ->
            if (other.separator.isEmpty()) other.parts else listOf(other)

        else -> listOf(other)
    }

    return JoinedTextResource(leftParts + rightParts, separator = "")
}

fun TextResource.replace(oldValue: String, newValue: String): TextResource {
    if (oldValue.isEmpty()) return this

    return when (this) {
        is SimpleTextResource -> SimpleTextResource(this.text.replace(oldValue, newValue))
        is StringResTextResource -> this
        is StringResWithArgsTextResource -> this
        is PluralResTextResource -> this
        is JoinedTextResource -> JoinedTextResource(this.parts.map {
            it.replace(
                oldValue,
                newValue
            )
        }, this.separator)

        is StyledTextResource -> StyledTextResource(
            this.resource.replace(oldValue, newValue),
            this.style
        )

        is UrlTextResource -> UrlTextResource(this.resource.replace(oldValue, newValue), this.url)
        is ClickableTextResource -> ClickableTextResource(
            this.resource.replace(oldValue, newValue),
            this.tag
        )

        is AnnotatedStringTextResource -> {
            if (!this.text.contains(oldValue)) {
                this
            } else {
                val builder = StringBuilder()
                val resultSpans = mutableListOf<TextResourceSpan>()
                var cursor = 0
                while (true) {
                    val idx = this.text.indexOf(oldValue, cursor)
                    if (idx == -1) {
                        appendAnnotatedSegment(
                            builder,
                            resultSpans,
                            this.text,
                            this.spans,
                            cursor,
                            this.text.length
                        )
                        break
                    }
                    appendAnnotatedSegment(builder, resultSpans, this.text, this.spans, cursor, idx)
                    builder.append(newValue) // replacement text carries no span styling
                    cursor = idx + oldValue.length
                }
                AnnotatedStringTextResource(builder.toString(), resultSpans)
            }
        }
    }
}

fun TextResource.replace(oldValue: String, newValue: TextResource): TextResource {
    if (oldValue.isEmpty()) return this

    return when (this) {
        is SimpleTextResource -> {
            if (!this.text.contains(oldValue)) {
                this
            } else {
                val parts = this.text.split(oldValue)
                if (parts.size == 1) {
                    this
                } else {
                    val resultParts = mutableListOf<TextResource>()
                    parts.forEachIndexed { index, part ->
                        if (part.isNotEmpty()) {
                            resultParts.add(SimpleTextResource(part))
                        }
                        if (index < parts.size - 1) {
                            resultParts.add(newValue)
                        }
                    }
                    when {
                        resultParts.isEmpty() -> TextResource.Empty
                        resultParts.size == 1 -> resultParts.first()
                        else -> JoinedTextResource(resultParts, separator = "")
                    }
                }
            }
        }

        is StringResTextResource -> this
        is StringResWithArgsTextResource -> this
        is PluralResTextResource -> this
        is JoinedTextResource -> JoinedTextResource(this.parts.map {
            it.replace(
                oldValue,
                newValue
            )
        }, this.separator)

        is StyledTextResource -> StyledTextResource(
            this.resource.replace(oldValue, newValue),
            this.style
        )

        is UrlTextResource -> UrlTextResource(this.resource.replace(oldValue, newValue), this.url)
        is ClickableTextResource -> ClickableTextResource(
            this.resource.replace(oldValue, newValue),
            this.tag
        )

        is AnnotatedStringTextResource -> {
            if (!this.text.contains(oldValue)) {
                this
            } else {
                val resultParts = mutableListOf<TextResource>()
                var cursor = 0
                while (true) {
                    val idx = this.text.indexOf(oldValue, cursor)
                    if (idx == -1) {
                        if (cursor < this.text.length) {
                            resultParts += sliceAnnotatedResource(
                                this.text,
                                this.spans,
                                cursor,
                                this.text.length
                            )
                        }
                        break
                    }
                    if (idx > cursor) {
                        resultParts += sliceAnnotatedResource(this.text, this.spans, cursor, idx)
                    }
                    resultParts += newValue
                    cursor = idx + oldValue.length
                }
                when {
                    resultParts.isEmpty() -> TextResource.Empty
                    resultParts.size == 1 -> resultParts.first()
                    else -> JoinedTextResource(resultParts, separator = "")
                }
            }
        }
    }
}

/**
 * Appends `sourceText[rangeStart, rangeEnd)` to [builder] and re-bases any [sourceSpans] that
 * fall (fully or partially) within that range onto the builder's current length, clipping spans
 * at the range boundary. Used by [replace] (String, String) to keep span offsets correct while
 * merging kept/removed regions back into a single [AnnotatedStringTextResource].
 */
private fun appendAnnotatedSegment(
    builder: StringBuilder,
    resultSpans: MutableList<TextResourceSpan>,
    sourceText: String,
    sourceSpans: List<TextResourceSpan>,
    rangeStart: Int,
    rangeEnd: Int,
) {
    if (rangeStart >= rangeEnd) return
    val outputOffset = builder.length
    builder.append(sourceText, rangeStart, rangeEnd)
    sourceSpans.forEach { span ->
        val clippedStart = span.start.coerceAtLeast(rangeStart)
        val clippedEnd = span.end.coerceAtMost(rangeEnd)
        if (clippedStart < clippedEnd) {
            resultSpans += span.copy(
                start = outputOffset + (clippedStart - rangeStart),
                end = outputOffset + (clippedEnd - rangeStart),
            )
        }
    }
}

/**
 * Slices `text[rangeStart, rangeEnd)` into a standalone [TextResource], carrying over only the
 * [spans] that fall within the slice (re-based to slice-local offsets, clipped at boundaries).
 * Used by [replace] (String, TextResource) when a kept segment needs to become its own part in
 * the resulting [JoinedTextResource].
 */
private fun sliceAnnotatedResource(
    text: String,
    spans: List<TextResourceSpan>,
    rangeStart: Int,
    rangeEnd: Int,
): TextResource {
    val slice = text.substring(rangeStart, rangeEnd)
    if (slice.isEmpty()) return TextResource.Empty
    val slicedSpans = spans.mapNotNull { span ->
        val clippedStart = span.start.coerceAtLeast(rangeStart)
        val clippedEnd = span.end.coerceAtMost(rangeEnd)
        if (clippedStart >= clippedEnd) null
        else span.copy(start = clippedStart - rangeStart, end = clippedEnd - rangeStart)
    }
    return if (slicedSpans.isEmpty()) SimpleTextResource(slice)
    else AnnotatedStringTextResource(slice, slicedSpans)
}