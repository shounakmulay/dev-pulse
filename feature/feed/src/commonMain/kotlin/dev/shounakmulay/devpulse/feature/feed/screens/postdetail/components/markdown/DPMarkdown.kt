package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.mikepenz.markdown.compose.MarkdownElement
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.State
import com.mikepenz.markdown.model.markdownPadding
import dev.shounakmulay.devpulse.core.designsystem.components.DPLoadingIndicator
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPDarkTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.designsystem.theme.monoFontFamily
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.content.ContentTextContainer
import dev.shounakmulay.devpulse.core.ui.content.EmptyContentPlaceholder
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes
import devpulse.core.resources.generated.resources.no_content
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.seconds

@Composable
fun DPMarkdown(
    markdown: String,
    header: LazyListScope.() -> Unit = {},
    footer: LazyListScope.() -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    onImageClick: (link: String) -> Unit
) {
    val scopedViewModelOwner = rememberViewModelStoreOwner()
    CompositionLocalProvider(LocalViewModelStoreOwner provides scopedViewModelOwner) {
        val viewModel = koinViewModel<MarkdownViewModel> { parametersOf("") }
        val markdownState by viewModel.markdownState.collectAsStateWithLifecycle()

        LaunchedEffect(markdown) {
            viewModel.setMarkdownContent(markdown)
        }

        when (markdownState) {
            is State.Error -> {
                DPMarkdownError(
                    listState = listState,
                    header = header,
                    footer = footer
                )
            }

            is State.Loading -> {
                DPMarkdownLoading(
                    listState = listState,
                    header = header,
                    footer = footer
                )
            }

            is State.Success -> {
                SelectionContainer {
                    DPMarkdownContent(
                        state = markdownState,
                        header = header,
                        footer = footer,
                        listState = listState,
                        onImageClick = onImageClick
                    )
                }
            }
        }
    }
}

@Composable
private fun DPMarkdownLoading(
    listState: LazyListState,
    header: LazyListScope.() -> Unit,
    footer: LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = listState
    ) {
        header()
        item {
            Box(
                modifier = Modifier.padding(horizontal = LocalDPSpacing.current.lg)
                    .fillMaxWidth().height(400.dp)
            ) {
                var loading by rememberSaveable {
                    mutableStateOf(true)
                }

                LaunchedEffect(loading) {
                    if (!loading) return@LaunchedEffect
                    delay(6.seconds)
                    loading = false
                }
                if (loading) {
                    DPLoadingIndicator(Modifier.align(Alignment.Center))
                } else {
                    EmptyContentPlaceholder(
                        icon = DPIcons.EmptyContent,
                        text = stringResource(stringRes.no_content)
                    )
                }
            }
        }
        footer()
    }
}

@Composable
private fun DPMarkdownError(
    listState: LazyListState,
    header: LazyListScope.() -> Unit,
    footer: LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = listState
    ) {
        header()
        item {
            Box(
                modifier = Modifier.padding(horizontal = LocalDPSpacing.current.lg)
                    .fillMaxWidth().height(400.dp)
            ) {
                EmptyContentPlaceholder(
                    icon = DPIcons.EmptyContent,
                    text = stringResource(stringRes.no_content)
                )
            }
        }
        footer()
    }
}

@Composable
fun DPMarkdownContent(
    state: State,
    header: LazyListScope.() -> Unit,
    footer: LazyListScope.() -> Unit,
    listState: LazyListState,
    onImageClick: (link: String) -> Unit
) {
    val components = remember {
        markdownComponents(
            codeBlock = {
                val darkTheme = LocalDPDarkTheme.current
                MarkdownHighlightedCodeBlock(
                    content = it.content,
                    node = it.node,
                    style = it.typography.code,
                    highlightsBuilder = remember(darkTheme) {
                        Highlights.Builder()
                            .theme(SyntaxThemes.default(darkMode = darkTheme))
                    },
                    showHeader = true,
                )
            },
            codeFence = {
                val darkTheme = LocalDPDarkTheme.current
                MarkdownHighlightedCodeFence(
                    content = it.content,
                    node = it.node,
                    style = it.typography.code,
                    highlightsBuilder = remember(darkTheme) {
                        Highlights.Builder()
                            .theme(SyntaxThemes.default(darkMode = darkTheme))
                    },
                    showHeader = true,
                )
            },
            paragraph = {
                SplitMarkdownParagraph(
                    content = it.content,
                    node = it.node,
                    onImageClick = onImageClick
                )
            },
            image = {
                MDImage(it.content, it.node, onImageClick = onImageClick)
            }
        )
    }
    ContentTextContainer {
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
                    state = listState
                ) {
                    header()
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
                    footer()
                }
            }
        )
    }
}


