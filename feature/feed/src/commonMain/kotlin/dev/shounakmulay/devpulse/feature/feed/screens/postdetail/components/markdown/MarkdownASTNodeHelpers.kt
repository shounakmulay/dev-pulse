package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import com.mikepenz.markdown.model.ReferenceLinkHandler
import com.mikepenz.markdown.utils.getUnescapedTextInNode
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType

internal fun ASTNode.resolveImageAlt(content: String): String? {
    findChildOfTypeRecursive(MarkdownElementTypes.LINK_TEXT)?.let {
        val text = it.getUnescapedTextInNode(content).trim('[', ']').trim()
        if (text.isNotEmpty()) return text
    }
    findChildOfTypeRecursive(MarkdownElementTypes.LINK_LABEL)?.let {
        val text = it.getUnescapedTextInNode(content).trim('[', ']').trim()
        if (text.isNotEmpty()) return text
    }
    return null
}

internal fun ASTNode.resolveImageLink(
    content: String,
    referenceLinkHandler: ReferenceLinkHandler?,
): String? {
    findChildOfTypeRecursive(MarkdownElementTypes.LINK_DESTINATION)?.let {
        return it.getUnescapedTextInNode(content)
    }
    val refNode = findChildOfTypeRecursive(MarkdownElementTypes.FULL_REFERENCE_LINK)
        ?: findChildOfTypeRecursive(MarkdownElementTypes.SHORT_REFERENCE_LINK)
        ?: return null
    val label = refNode.findChildOfType(MarkdownElementTypes.LINK_LABEL)
        ?.getUnescapedTextInNode(content)
        ?: return null
    return referenceLinkHandler?.find(label)?.takeIf { it.isNotEmpty() }
}

internal fun ASTNode.findChildOfTypeRecursive(type: IElementType): ASTNode? {
    children.forEach {
        if (it.type == type) {
            return it
        } else {
            val found = it.findChildOfTypeRecursive(type)
            if (found != null) {
                return found
            }
        }
    }
    return null
}