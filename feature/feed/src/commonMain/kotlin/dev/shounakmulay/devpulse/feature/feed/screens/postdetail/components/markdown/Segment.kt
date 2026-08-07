package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import org.intellij.markdown.ast.ASTNode

internal sealed class Segment {
    data class TextRun(val nodes: List<ASTNode>) : Segment()
    data class ImageNode(val node: ASTNode) : Segment()
}