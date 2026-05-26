package dev.shounakmulay.devpulse.core.ui.text

import androidx.compose.runtime.Composable
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
    is JoinedTextResource -> parts.map { it.resolve() }.filter { it.isNotEmpty() }.joinToString(separator)
    is StyledTextResource -> resource.resolve()
    is UrlTextResource -> resource.resolve()
    is ClickableTextResource -> resource.resolve()
}

suspend fun TextResource.resolveAnnotated(onLinkClick: ((String) -> Unit)? = null): AnnotatedString =
    when (this) {
        is SimpleTextResource -> AnnotatedString(text)
        is StringResTextResource -> AnnotatedString(getString(toStringResource()))
        is StringResWithArgsTextResource -> AnnotatedString(getString(toStringResource(), *args.toTypedArray()))
        is PluralResTextResource -> AnnotatedString(getPluralString(toPluralStringResource(), quantity, quantity))
        is JoinedTextResource -> {
            val resolvedParts = parts.map { it.resolveAnnotated(onLinkClick) }.filter { it.isNotEmpty() }
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
    }

// Composable layer — resolve inside composables (non-suspending)

@Composable
fun TextResource.asString(): String = when (this) {
    is SimpleTextResource -> text
    is StringResTextResource -> stringResource(toStringResource())
    is StringResWithArgsTextResource -> stringResource(toStringResource(), *args.toTypedArray())
    is PluralResTextResource -> pluralStringResource(toPluralStringResource(), quantity)
    is JoinedTextResource -> parts.map { it.asString() }.filter { it.isNotEmpty() }.joinToString(separator)
    is StyledTextResource -> resource.asString()
    is UrlTextResource -> resource.asString()
    is ClickableTextResource -> resource.asString()
}

@Composable
fun TextResource.asAnnotatedString(onLinkClick: ((String) -> Unit)? = null): AnnotatedString =
    when (this) {
        is SimpleTextResource -> AnnotatedString(text)
        is StringResTextResource -> AnnotatedString(stringResource(toStringResource()))
        is StringResWithArgsTextResource -> AnnotatedString(stringResource(toStringResource(), *args.toTypedArray()))
        is PluralResTextResource -> AnnotatedString(pluralStringResource(toPluralStringResource(), quantity))
        is JoinedTextResource -> {
            val resolvedParts = parts.map { it.asAnnotatedString(onLinkClick) }.filter { it.isNotEmpty() }
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
        is JoinedTextResource -> JoinedTextResource(this.parts.map { it.replace(oldValue, newValue) }, this.separator)
        is StyledTextResource -> StyledTextResource(this.resource.replace(oldValue, newValue), this.style)
        is UrlTextResource -> UrlTextResource(this.resource.replace(oldValue, newValue), this.url)
        is ClickableTextResource -> ClickableTextResource(this.resource.replace(oldValue, newValue), this.tag)
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
        is JoinedTextResource -> JoinedTextResource(this.parts.map { it.replace(oldValue, newValue) }, this.separator)
        is StyledTextResource -> StyledTextResource(this.resource.replace(oldValue, newValue), this.style)
        is UrlTextResource -> UrlTextResource(this.resource.replace(oldValue, newValue), this.url)
        is ClickableTextResource -> ClickableTextResource(this.resource.replace(oldValue, newValue), this.tag)
    }
}
