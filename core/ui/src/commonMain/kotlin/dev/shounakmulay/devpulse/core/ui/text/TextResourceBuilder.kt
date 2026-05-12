package dev.shounakmulay.devpulse.core.ui.text

import androidx.compose.ui.text.TextLinkStyles
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource

class TextResourceBuilder(private val separator: String = "") {
    private val parts = mutableListOf<TextResource>()

    fun append(resource: TextResource) {
        parts.add(resource)
    }

    fun append(text: String) {
        if (text.isNotEmpty()) {
            parts.add(SimpleTextResource(text))
        }
    }

    fun append(res: StringResource) {
        parts.add(StringResTextResource(res))
    }

    fun append(res: StringResource, vararg args: String) {
        parts.add(StringResWithArgsTextResource(res, args.toList()))
    }

    fun appendPlural(res: PluralStringResource, quantity: Int) {
        parts.add(PluralResTextResource(res, quantity))
    }

    fun appendBold(resource: TextResource) {
        parts.add(StyledTextResource(resource, Style.BOLD))
    }

    fun appendBold(text: String) {
        if (text.isNotEmpty()) {
            parts.add(StyledTextResource(SimpleTextResource(text), Style.BOLD))
        }
    }

    fun appendItalic(resource: TextResource) {
        parts.add(StyledTextResource(resource, Style.ITALIC))
    }

    fun appendItalic(text: String) {
        if (text.isNotEmpty()) {
            parts.add(StyledTextResource(SimpleTextResource(text), Style.ITALIC))
        }
    }

    fun appendUrl(resource: TextResource, url: String, styles: TextLinkStyles? = null) {
        parts.add(UrlTextResource(resource, url))
    }

    fun appendUrl(text: String, url: String, styles: TextLinkStyles? = null) {
        if (text.isNotEmpty()) {
            parts.add(UrlTextResource(SimpleTextResource(text), url))
        }
    }

    fun appendClickable(
        resource: TextResource,
        tag: String = "",
        styles: TextLinkStyles? = null,
    ) {
        parts.add(ClickableTextResource(resource, tag))
    }

    fun appendClickable(
        text: String,
        tag: String = "",
        styles: TextLinkStyles? = null,
    ) {
        if (text.isNotEmpty()) {
            parts.add(ClickableTextResource(SimpleTextResource(text), tag))
        }
    }

    internal fun build(): TextResource = when {
        parts.isEmpty() -> TextResource.Empty
        parts.size == 1 && separator.isEmpty() -> parts.first()
        else -> JoinedTextResource(parts.toList(), separator)
    }
}
