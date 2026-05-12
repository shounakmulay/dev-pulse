package dev.shounakmulay.devpulse.core.ui.text

import androidx.compose.ui.text.TextLinkStyles
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource

@DslMarker
annotation class TextResourceDsl

@TextResourceDsl
class TextResourceScope internal constructor(private val separator: String = "") {
    private val parts = mutableListOf<TextResource>()

    operator fun String.unaryPlus() {
        if (isNotEmpty()) parts.add(SimpleTextResource(this))
    }

    operator fun TextResource.unaryPlus() {
        if (isNotNullOrEmpty()) add(this)
    }

    fun add(resource: TextResource) {
        parts.add(resource)
    }

    fun stringRes(resource: StringResource) {
        parts.add(StringResTextResource(resource))
    }

    fun stringRes(resource: StringResource, vararg args: String) {
        parts.add(StringResWithArgsTextResource(resource, args.toList()))
    }

    fun pluralStringRes(resource: PluralStringResource, quantity: Int) {
        parts.add(PluralResTextResource(resource, quantity))
    }

    fun bold(block: TextResourceScope.() -> Unit) {
        parts.add(StyledTextResource(TextResourceScope().apply(block).build(), Style.BOLD))
    }

    fun italic(block: TextResourceScope.() -> Unit) {
        parts.add(StyledTextResource(TextResourceScope().apply(block).build(), Style.ITALIC))
    }

    fun url(
        link: String,
        styles: TextLinkStyles? = null,
        block: TextResourceScope.() -> Unit
    ) {
        val inner = TextResourceScope().apply(block).build()
        parts.add(UrlTextResource(inner, link))
    }

    fun clickable(
        tag: String = "",
        styles: TextLinkStyles? = null,
        block: TextResourceScope.() -> Unit
    ) {
        val inner = TextResourceScope().apply(block).build()
        parts.add(ClickableTextResource(inner, tag))
    }

    internal fun build(): TextResource = when {
        parts.isEmpty() -> TextResource.Empty
        parts.size == 1 && separator.isEmpty() -> parts.first()
        else -> JoinedTextResource(parts.toList(), separator)
    }
}

fun textResource(separator: String = "", block: TextResourceScope.() -> Unit): TextResource =
    TextResourceScope(separator).apply(block).build()
