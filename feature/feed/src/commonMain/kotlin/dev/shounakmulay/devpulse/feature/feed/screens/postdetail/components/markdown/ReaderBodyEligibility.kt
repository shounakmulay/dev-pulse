package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal fun hasReadableBodyText(node: ASTNode, content: String): Boolean {
    if (node.type == MarkdownElementTypes.IMAGE || node.type == MarkdownElementTypes.HTML_BLOCK || node.type == MarkdownTokenTypes.HTML_TAG) return false
    if (node.type == MarkdownTokenTypes.TEXT || node.type == MarkdownTokenTypes.CODE_LINE || node.type == MarkdownTokenTypes.CODE_FENCE_CONTENT) {
        return content.substring(node.startOffset, node.endOffset).any { !it.isWhitespace() }
    }
    return node.children.any { hasReadableBodyText(it, content) }
}

internal fun isReaderBodyExposed(renderedContent: String, selectedContent: String, hasText: Boolean, isResumed: Boolean, isVisible: Boolean, isEligible: Boolean = true): Boolean =
    isEligible && renderedContent == selectedContent && hasText && isResumed && isVisible

internal fun containsBodyImage(node: ASTNode): Boolean = node.type == MarkdownElementTypes.IMAGE || node.children.any(::containsBodyImage)
