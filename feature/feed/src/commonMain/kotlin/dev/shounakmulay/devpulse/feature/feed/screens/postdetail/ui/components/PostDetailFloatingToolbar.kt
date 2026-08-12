package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroupScope
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonGroup
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconToggleButton
import dev.shounakmulay.devpulse.core.designsystem.components.dpIconToggleButtonShapes
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.PostDetailScreenSection

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
internal fun BoxScope.PostDetailFloatingToolbar(
    visible: Boolean,
    selectedSection: PostDetailScreenSection,
    onSectionSelected: (PostDetailScreenSection) -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn() + scaleIn(),
        exit = scaleOut() + fadeOut() + slideOutVertically { it },
        modifier = Modifier.zIndex(10f).align(Alignment.BottomCenter).padding(4.dp)
            .padding(top = 16.dp),
    ) {
        HorizontalFloatingToolbar(
            selectedSection = selectedSection,
            onSectionSelected = onSectionSelected
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun HorizontalFloatingToolbar(
    selectedSection: PostDetailScreenSection,
    onSectionSelected: (PostDetailScreenSection) -> Unit
) {
    HorizontalFloatingToolbar(
        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
            toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                alpha = 0.85f
            ),
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        expanded = true,
        content = {
            val interactionSources = remember {
                List(PostDetailScreenSection.entries.size) { MutableInteractionSource() }
            }
            DPButtonGroup(
                overflowIndicator = {
                    DPIconButton(
                        icon = DPIcons.MoreOptionsVert,
                        variant = DPIconButtonVariant.Tertiary,
                        contentDescription = "",
                        size = DPSize.Small
                    ) {
                        it.show()
                    }
                }) {
                toggleButton(
                    selectedSection = selectedSection,
                    onSectionSelected = onSectionSelected,
                    interactionSources = interactionSources
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun ButtonGroupScope.toggleButton(
    selectedSection: PostDetailScreenSection,
    onSectionSelected: (PostDetailScreenSection) -> Unit,
    interactionSources: List<MutableInteractionSource>
) {
    PostDetailScreenSection.entries.forEachIndexed { index, section ->
        customItem(
            buttonGroupContent = {
                toggleButtonGroupContent(
                    section = section,
                    selected = section == selectedSection,
                    onSectionSelected = onSectionSelected,
                    index = index,
                    interactionSources = interactionSources
                )
            },
            menuContent = {
                ToggleButtonMenuContent(section, interactionSources, index)
            },
        )
    }
}

@Composable
private fun ToggleButtonMenuContent(
    section: PostDetailScreenSection,
    interactionSources: List<MutableInteractionSource>,
    index: Int
) {
    val icon = remember(section) {
        when (section) {
            PostDetailScreenSection.RSS -> DPIcons.RssFeed
            PostDetailScreenSection.EXTRACTED -> DPIcons.Article
        }
    }
    DropdownMenuItem(
        leadingIcon = { Icon(icon, contentDescription = null) },
        text = { Text(section.name) },
        onClick = {},
        interactionSource = interactionSources[index],
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ButtonGroupScope.toggleButtonGroupContent(
    section: PostDetailScreenSection,
    selected: Boolean,
    onSectionSelected: (PostDetailScreenSection) -> Unit,
    index: Int,
    interactionSources: List<MutableInteractionSource>
) {
    val icon = remember(section) {
        when (section) {
            PostDetailScreenSection.RSS -> DPIcons.RssFeed
            PostDetailScreenSection.EXTRACTED -> DPIcons.Article
        }
    }
    val scope = rememberCoroutineScope()
    DPIconToggleButton(
        icon = icon,
        size = DPSize.Small,
        variant = DPIconButtonVariant.Secondary,
        contentDescription = section.name,
        checked = selected,
        onCheckedChange = {
            onSectionSelected(section)
        },
        shapes = dpIconToggleButtonShapes(
            index = index,
            lastIndex = PostDetailScreenSection.entries.lastIndex
        ),
        interactionSource = interactionSources[index],
        modifier = Modifier.animateWidth(interactionSource = interactionSources[index]),
    )
}