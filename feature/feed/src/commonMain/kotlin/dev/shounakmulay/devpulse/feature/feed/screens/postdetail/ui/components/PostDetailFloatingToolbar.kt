package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonGroup
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconToggleButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTooltip
import dev.shounakmulay.devpulse.core.designsystem.components.dpIconToggleButtonShapes
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.PostDetailScreenSection
import devpulse.core.resources.generated.resources.post_detail_article
import devpulse.core.resources.generated.resources.post_detail_rss
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
internal fun BoxScope.PostDetailFloatingToolbar(
    visible: Boolean,
    selectedSection: PostDetailScreenSection,
    onSectionSelected: (PostDetailScreenSection) -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it / 2 } + fadeIn(),
        exit = slideOutVertically { it / 2 } + fadeOut(),
        modifier = Modifier
            .zIndex(10f)
            .align(Alignment.BottomCenter)
            .padding(horizontal = DPTheme.spacing.lg, vertical = DPTheme.spacing.sm),
    ) {
        HorizontalFloatingToolbar(
            expanded = true,
            colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
            contentPadding = PaddingValues(DPTheme.spacing.xs),
        ) {
            val interactionSources = remember {
                List(PostDetailScreenSection.entries.size) { MutableInteractionSource() }
            }
            DPButtonGroup(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                overflowIndicator = {},
            ) {
                PostDetailScreenSection.entries.forEachIndexed { index, section ->
                    customItem(
                        buttonGroupContent = {
                            val description = stringResource(
                                when (section) {
                                    PostDetailScreenSection.RSS -> stringRes.post_detail_rss
                                    PostDetailScreenSection.EXTRACTED -> stringRes.post_detail_article
                                },
                            )
                            DPTooltip(
                                text = description,
                                modifier = Modifier.animateWidth(
                                    interactionSource = interactionSources[index],
                                ),
                            ) {
                                DPIconToggleButton(
                                    icon = when (section) {
                                        PostDetailScreenSection.RSS -> DPIcons.RssFeed
                                        PostDetailScreenSection.EXTRACTED -> DPIcons.Article
                                    },
                                    contentDescription = description,
                                    checked = selectedSection == section,
                                    onCheckedChange = { onSectionSelected(section) },
                                    shapes = dpIconToggleButtonShapes(
                                        index = index,
                                        lastIndex = PostDetailScreenSection.entries.lastIndex,
                                    ),
                                    interactionSource = interactionSources[index],
                                    modifier = Modifier.fillMaxWidth(),
                                    variant = DPIconButtonVariant.Secondary
                                )
                            }
                        },
                        menuContent = {},
                    )
                }
            }
        }
    }
}

@DPComponentPreview
@Composable
private fun PostDetailFloatingToolbarPreview() {
    Preview {
        Column(verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.lg)) {
            PostDetailScreenSection.entries.forEach { section ->
                var selectedSection by remember(section) { mutableStateOf(section) }
                Box(Modifier.size(width = 320.dp, height = 100.dp)) {
                    PostDetailFloatingToolbar(
                        visible = true,
                        selectedSection = selectedSection,
                        onSectionSelected = { selectedSection = it },
                    )
                }
            }
        }
    }
}
