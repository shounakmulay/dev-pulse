package dev.shounakmulay.core.ui.text
//
//import android.content.res.Resources
//import android.graphics.Typeface
//import android.os.Parcelable
//import android.text.Spannable
//import android.text.SpannableString
//import android.text.SpannableStringBuilder
//import android.text.style.StyleSpan
//import androidx.annotation.PluralsRes
//import androidx.annotation.StringRes
//import kotlinx.parcelize.Parcelize
//import kotlinx.serialization.Serializable
//
///**
// * TextResource is an abstraction layer for Strings that supports plain text, string resources,
// * plurals, and styled text.
// * More info: https://hannesdorfmann.com/abstraction-text-resource/
// */
//@Serializable
//sealed class TextResource : Parcelable {
//    companion object {
//        /** Creates a TextResource from a plain string. */
//        fun fromText(text: String): TextResource = SimpleTextResource(text)
//
//        /** Creates a TextResource from a string resource ID. */
//        fun fromStringId(@StringRes id: Int): TextResource = IdTextResource(id)
//
//        /** Creates a TextResource from a string resource ID with format arguments. */
//        fun fromStringIdWithArg(@StringRes id: Int, vararg arg: Any): TextResource =
//            IdWithArgTextResource(id, arg.map { it.toString() }.toTypedArray())
//
//        /** Creates a TextResource from a plurals resource ID. */
//        fun fromPlural(@PluralsRes id: Int, pluralValue: Int): TextResource = PluralTextResource(id, pluralValue)
//
//        /** Joins multiple TextResources into one, filtering out any empty ones. */
//        fun fromJoined(vararg resources: TextResource, separator: String = " "): TextResource =
//            JoinedTextResource(resources.toList(), separator)
//
//        /** Builds a TextResource using a DSL-style builder. */
//        fun buildTextResource(separator: String = "", builderAction: TextResourceBuilder.() -> Unit): TextResource {
//            val builder = TextResourceBuilder(separator)
//            builder.builderAction()
//            return builder.build()
//        }
//
//        /** Makes a TextResource bold. */
//        fun bold(resource: TextResource): TextResource = StyledTextResource(resource, Style.BOLD)
//
//        /** Makes a TextResource italic. */
//        fun italic(resource: TextResource): TextResource = StyledTextResource(resource, Style.ITALIC)
//
//        /** Represents an empty TextResource. */
//        val Empty = fromText("")
//
//        val Space = fromText(" ")
//    }
//
//    override fun toString(): String = throw IllegalStateException(
//        "Did you use toString() on a TextResource? Use asString(resources) or asCharSequence(resources) instead.",
//    )
//}
//
//@Parcelize
//@Serializable
//data class SimpleTextResource(val text: String) : TextResource()
//
//@Parcelize
//@Serializable
//data class IdTextResource(@StringRes val id: Int) : TextResource()
//
//@Parcelize
//@Serializable
//data class IdWithArgTextResource(@StringRes val id: Int, val arg: Array<String>) : TextResource() {
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//        other as IdWithArgTextResource
//        if (id != other.id) return false
//        if (!arg.contentEquals(other.arg)) return false
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = id
//        result = 31 * result + arg.contentHashCode()
//        return result
//    }
//}
//
//@Parcelize
//@Serializable
//data class PluralTextResource(@PluralsRes val pluralId: Int, val quantity: Int) : TextResource()
//
//@Parcelize
//@Serializable
//data class JoinedTextResource(val parts: List<TextResource>, val separator: String) : TextResource()
//
//@Parcelize
//@Serializable
//data class StyledTextResource(val resource: TextResource, val style: Style) : TextResource()
//
//@Serializable
//enum class Style {
//    BOLD,
//    ITALIC,
//}
//
///**
// * Builder for constructing TextResources in a DSL style.
// *
// * Example usage:
// * ```
// * val text = TextResource.buildTextResource(separator = " ") {
// *     append("Hello")
// *     append(R.string.world)
// *     append("!")
// * }
// * ```
// */
//class TextResourceBuilder(private val separator: String = "") {
//    private val parts = mutableListOf<TextResource>()
//
//    /** Appends a TextResource to the builder. */
//    fun append(resource: TextResource) {
//        parts.add(resource)
//    }
//
//    /** Appends a plain string as a TextResource. */
//    fun append(text: String) {
//        if (text.isNotEmpty()) {
//            parts.add(SimpleTextResource(text))
//        }
//    }
//
//    /** Appends a string resource as a TextResource. */
//    fun append(@StringRes stringId: Int) {
//        parts.add(IdTextResource(stringId))
//    }
//
//    /** Appends a string resource with format arguments as a TextResource. */
//    fun append(@StringRes stringId: Int, vararg args: Any) {
//        parts.add(IdWithArgTextResource(stringId, args.map { it.toString() }.toTypedArray()))
//    }
//
//    /** Appends a plural resource as a TextResource. */
//    fun appendPlural(@PluralsRes pluralId: Int, quantity: Int) {
//        parts.add(PluralTextResource(pluralId, quantity))
//    }
//
//    /** Appends a bold TextResource. */
//    fun appendBold(resource: TextResource) {
//        parts.add(StyledTextResource(resource, Style.BOLD))
//    }
//
//    /** Appends a bold string as a TextResource. */
//    fun appendBold(text: String) {
//        if (text.isNotEmpty()) {
//            parts.add(StyledTextResource(SimpleTextResource(text), Style.BOLD))
//        }
//    }
//
//    /** Appends an italic TextResource. */
//    fun appendItalic(resource: TextResource) {
//        parts.add(StyledTextResource(resource, Style.ITALIC))
//    }
//
//    /** Appends an italic string as a TextResource. */
//    fun appendItalic(text: String) {
//        if (text.isNotEmpty()) {
//            parts.add(StyledTextResource(SimpleTextResource(text), Style.ITALIC))
//        }
//    }
//
//    /** Builds the final TextResource from all appended parts. */
//    internal fun build(): TextResource = when {
//        parts.isEmpty() -> TextResource.Empty
//        parts.size == 1 && separator.isEmpty() -> parts.first()
//        else -> JoinedTextResource(parts.toList(), separator)
//    }
//}
//
///**
// * Converts a TextResource to a CharSequence, preserving any styling.
// * This is the primary function to resolve a TextResource to a displayable format.
// */
//fun TextResource.asCharSequence(resources: Resources): CharSequence = when (this) {
//    is SimpleTextResource -> this.text
//    is IdTextResource -> resources.getString(this.id)
//    is PluralTextResource -> resources.getQuantityString(this.pluralId, this.quantity, this.quantity)
//    is IdWithArgTextResource -> resources.getString(id, *arg)
//    is JoinedTextResource -> {
//        val builder = SpannableStringBuilder()
//        val partsToJoin = this.parts
//            .map { it.asCharSequence(resources) } // Recursively convert parts to CharSequence
//            .filter { it.isNotEmpty() } // Condition: filter out empty parts
//
//        partsToJoin.forEachIndexed { index, part ->
//            builder.append(part)
//            if (index < partsToJoin.size - 1) {
//                builder.append(this.separator)
//            }
//        }
//        builder
//    }
//
//    is StyledTextResource -> {
//        val content = this.resource.asCharSequence(resources)
//        val styleSpan = when (this.style) {
//            Style.BOLD -> StyleSpan(Typeface.BOLD)
//            Style.ITALIC -> StyleSpan(Typeface.ITALIC)
//        }
//        SpannableString(content).apply {
//            setSpan(styleSpan, 0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        }
//    }
//}
//
///**
// * Converts a TextResource to a plain String, stripping any styling.
// */
//fun TextResource.asString(resources: Resources): String = asCharSequence(resources).toString()
//
///**
// * Returns the plain string content if the resource is a SimpleTextResource, otherwise null.
// */
//fun TextResource.asSimpleTextString(): String? = when (this) {
//    is SimpleTextResource -> this.text
//    else -> null
//}
//
///** Returns the TextResource or TextResource.Empty if it's null. */
//fun TextResource?.orEmpty() = this ?: TextResource.Empty
//
///** Checks if the TextResource is null or represents an empty string. */
//fun TextResource?.isNullOrEmpty(): Boolean {
//    if (this == null) return true
//    return when (this) {
//        is SimpleTextResource -> this.text.isEmpty()
//        is JoinedTextResource -> this.parts.all { it.isNullOrEmpty() }
//        else -> this == TextResource.Empty
//    }
//}
//
//fun TextResource?.isNotNullOrEmpty() = !this.isNullOrEmpty()
//
///**
// * Operator function to concatenate two TextResources using the + operator.
// * Creates a JoinedTextResource with no separator, flattening any existing JoinedTextResources.
// */
//operator fun TextResource.plus(other: TextResource): TextResource {
//    val leftParts = when (this) {
//        is JoinedTextResource ->
//            if (this.separator.isEmpty()) this.parts else listOf(this)
//
//        else -> listOf(this)
//    }
//
//    val rightParts = when (other) {
//        is JoinedTextResource ->
//            if (other.separator.isEmpty()) other.parts else listOf(other)
//
//        else -> listOf(other)
//    }
//
//    return JoinedTextResource(leftParts + rightParts, separator = "")
//}
//
///**
// * Replaces all occurrences of [oldValue] with [newValue] in this TextResource.
// * Preserves the TextResource tree structure and styling.
// *
// * @param oldValue the string to be replaced
// * @param newValue the string to replace with
// * @return a new TextResource with replacements applied
// */
//fun TextResource.replace(oldValue: String, newValue: String): TextResource {
//    if (oldValue.isEmpty()) return this
//
//    return when (this) {
//        is SimpleTextResource -> {
//            val replacedText = this.text.replace(oldValue, newValue)
//            SimpleTextResource(replacedText)
//        }
//        is IdTextResource -> this // Cannot modify resource IDs
//        is IdWithArgTextResource -> this // Cannot modify resource IDs
//        is PluralTextResource -> this // Cannot modify resource IDs
//        is JoinedTextResource -> {
//            val replacedParts = this.parts.map { it.replace(oldValue, newValue) }
//            JoinedTextResource(replacedParts, this.separator)
//        }
//        is StyledTextResource -> {
//            val replacedResource = this.resource.replace(oldValue, newValue)
//            StyledTextResource(replacedResource, this.style)
//        }
//    }
//}
//
///**
// * Replaces all occurrences of [oldValue] with [newValue] TextResource in this TextResource.
// * Preserves the TextResource tree structure. If [newValue] is a StyledTextResource,
// * its styling takes precedence; otherwise existing styling is preserved.
// *
// * @param oldValue the string to be replaced
// * @param newValue the TextResource to replace with
// * @return a new TextResource with replacements applied
// */
//fun TextResource.replace(oldValue: String, newValue: TextResource): TextResource {
//    if (oldValue.isEmpty()) return this
//
//    return when (this) {
//        is SimpleTextResource -> {
//            if (!this.text.contains(oldValue)) {
//                this
//            } else {
//                // Split the text by oldValue and reconstruct with newValue
//                val parts = this.text.split(oldValue)
//                if (parts.size == 1) {
//                    this
//                } else {
//                    val resultParts = mutableListOf<TextResource>()
//                    parts.forEachIndexed { index, part ->
//                        if (part.isNotEmpty()) {
//                            resultParts.add(SimpleTextResource(part))
//                        }
//                        if (index < parts.size - 1) {
//                            resultParts.add(newValue)
//                        }
//                    }
//                    when {
//                        resultParts.isEmpty() -> TextResource.Empty
//                        resultParts.size == 1 -> resultParts.first()
//                        else -> JoinedTextResource(resultParts, separator = "")
//                    }
//                }
//            }
//        }
//        is IdTextResource -> this // Cannot modify resource IDs
//        is IdWithArgTextResource -> this // Cannot modify resource IDs
//        is PluralTextResource -> this // Cannot modify resource IDs
//        is JoinedTextResource -> {
//            val replacedParts = this.parts.map { it.replace(oldValue, newValue) }
//            JoinedTextResource(replacedParts, this.separator)
//        }
//        is StyledTextResource -> {
//            val replacedResource = this.resource.replace(oldValue, newValue)
//            StyledTextResource(replacedResource, this.style)
//        }
//    }
//}
