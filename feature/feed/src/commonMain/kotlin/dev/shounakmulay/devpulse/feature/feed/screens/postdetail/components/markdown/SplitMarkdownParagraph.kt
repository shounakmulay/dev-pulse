package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.mikepenz.markdown.annotator.AnnotatorSettings
import com.mikepenz.markdown.annotator.annotatorSettings
import com.mikepenz.markdown.annotator.buildMarkdownAnnotatedString
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import com.mikepenz.markdown.compose.elements.MarkdownParagraph
import com.mikepenz.markdown.compose.elements.MarkdownText
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode

@Composable
fun SplitMarkdownParagraph(
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTypography.current.paragraph,
    annotatorSettings: AnnotatorSettings = annotatorSettings(),
    onImageClick: (link: String) -> Unit
) {
    // Group top-level children into runs: consecutive non-image nodes batched together,
    // image nodes isolated on their own. Preserves order.
    val segments = remember(node) {
        buildList {
            var currentRun = mutableListOf<ASTNode>()
            node.children.forEach { child ->
                if (child.type == MarkdownElementTypes.IMAGE) {
                    if (currentRun.isNotEmpty()) {
                        add(Segment.TextRun(currentRun.toList()))
                        currentRun = mutableListOf()
                    }
                    add(Segment.ImageNode(child))
                } else {
                    currentRun.add(child)
                }
            }
            if (currentRun.isNotEmpty()) add(Segment.TextRun(currentRun.toList()))
        }
    }

    // Single segment, no images: skip the split path entirely, behave exactly like default.
    if (segments.size == 1 && segments[0] is Segment.TextRun) {
        MarkdownParagraph(content, node, modifier, style, annotatorSettings)
        return
    }

    Column(modifier = modifier.fillMaxWidth()) {
        segments.forEach { segment ->
            when (segment) {
                is Segment.TextRun -> {
                    val styledText = buildAnnotatedString {
                        pushStyle(style.toSpanStyle())
                        buildMarkdownAnnotatedString(
                            content = content,
                            children = segment.nodes,
                            annotatorSettings = annotatorSettings,
                        )
                        pop()
                    }
                    // skip rendering an empty Text for whitespace-only runs between images
                    if (styledText.text.isNotBlank()) {
                        MarkdownText(
                            content = styledText,
                            node = node,
                            style = style,
                            sourceContent = content,
                        )
                    }
                }

                is Segment.ImageNode -> {
                    MDImage(
                        content = content,
                        node = segment.node,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onImageClick = onImageClick
                    )
                }
            }
        }
    }
}

