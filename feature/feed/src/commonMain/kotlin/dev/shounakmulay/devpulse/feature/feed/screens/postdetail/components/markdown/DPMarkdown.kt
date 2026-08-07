package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.mikepenz.markdown.compose.MarkdownElement
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.highlightedCodeBlock
import com.mikepenz.markdown.compose.elements.highlightedCodeFence
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.State
import com.mikepenz.markdown.model.markdownPadding
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.designsystem.theme.monoFontFamily
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DPMarkdown(markdown: String) {
    val scopedViewModelOwner = rememberViewModelStoreOwner()
    CompositionLocalProvider(LocalViewModelStoreOwner provides scopedViewModelOwner) {
        val viewModel = koinViewModel<MarkdownViewModel> { parametersOf(markdown) }
        val markdownState by viewModel.markdownState.collectAsStateWithLifecycle()

        DPMarkdownContent(markdownState)
    }
}

@Composable
fun DPMarkdownContent(state: State) {
    val components = remember {
        markdownComponents(
            codeBlock = highlightedCodeBlock,
            codeFence = highlightedCodeFence,
            paragraph = {
                SplitMarkdownParagraph(
                    content = it.content,
                    node = it.node,
                )
            },
            image = {
                MDImage(it.content, it.node)
            }
        )
    }
    Markdown(
        modifier = Modifier.fillMaxSize(),
        state = state,
        imageTransformer = CoilMarkdownTransformer,
        padding = markdownPadding(block = LocalDPSpacing.current.md),
        typography = markdownTypography(
            h1 = MaterialTheme.typography.displaySmall,
            h2 = MaterialTheme.typography.headlineLarge,
            h3 = MaterialTheme.typography.headlineMedium,
            h4 = MaterialTheme.typography.headlineSmall,
            h5 = MaterialTheme.typography.titleLarge,
            h6 = MaterialTheme.typography.titleMedium,
            textLink =
                TextLinkStyles(
                    MaterialTheme.typography.bodyLarge
                        .copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                        .toSpanStyle()
                ),
            code = MaterialTheme.typography.bodyMedium.copy(fontFamily = monoFontFamily()),
            inlineCode = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = monoFontFamily(),
                fontSize = TextUnit.Unspecified
            ),
        ),
        components = components,
        success = { state, components, modifier ->
            val nodes = remember(state.node) { state.node.children }
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
            ) {
                items(
                    items = nodes,
                    key = { node -> node.startOffset },
                    contentType = { node -> node.type }
                ) { node ->
                    Box(modifier = Modifier.padding(horizontal = LocalDPSpacing.current.lg)) {
                        MarkdownElement(
                            node = node,
                            components = components,
                            content = state.content,
                            includeSpacer = true,
                        )
                    }
                }
            }
        }
    )
}


