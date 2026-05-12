@file:UseSerializers(StringResourceSerializer::class, PluralStringResourceSerializer::class)

package dev.shounakmulay.devpulse.core.ui.text

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextLinkStyles
import dev.shounakmulay.devpulse.core.ui.text.serialization.PluralStringResourceSerializer
import dev.shounakmulay.devpulse.core.ui.text.serialization.StringResourceSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
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
data class StringResTextResource(val res: StringResource) : TextResource()

@Serializable
data class StringResWithArgsTextResource(val res: StringResource, val args: List<String>) :
    TextResource()

@Serializable
data class PluralResTextResource(val res: PluralStringResource, val quantity: Int) : TextResource()

@Serializable
data class JoinedTextResource(val parts: List<TextResource>, val separator: String) : TextResource()

@Serializable
data class StyledTextResource(val resource: TextResource, val style: Style) : TextResource()

@Serializable
data class UrlTextResource(val resource: TextResource, val url: String) : TextResource()

@Serializable
data class ClickableTextResource(val resource: TextResource, val tag: String) : TextResource()
