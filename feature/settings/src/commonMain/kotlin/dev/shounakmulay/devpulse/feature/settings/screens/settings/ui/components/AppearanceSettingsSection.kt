package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonGroup
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.list.post.FeedPostListItem
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import devpulse.core.resources.generated.resources.appearance
import devpulse.core.resources.generated.resources.article_reader
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AppearanceSettingsSection(
    selectedVariant: FeedsPostListItemVariant,
    onVariantSelected: (FeedsPostListItemVariant) -> Unit,
    onArticleSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.sm)
    ) {
        SettingsSectionHeading(stringResource(stringRes.appearance))
        SettingsSubPageLink(
            headlineText = stringResource(stringRes.article_reader),
            onClick = onArticleSettingsClick
        )
        var bookmarked by remember { mutableStateOf(false) }
        DPButtonGroup(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LocalDPSpacing.current.md)
                .horizontalScroll(rememberScrollState()),
            overflowIndicator = {},
        ) {
            FeedsPostListItemVariant.entries.forEach { variant ->
                val isSelected = selectedVariant == variant

                toggleableItem(
                    checked = isSelected,
                    label = variant.name,
                    onCheckedChange = { checked ->
                        if (checked) {
                            onVariantSelected(variant)
                        }
                    }
                )
            }
        }
        FeedPostListItem(
            modifier = Modifier.padding(horizontal = LocalDPSpacing.current.md)
                .animateContentSize(),
            variant = selectedVariant,
            showImage = true,
            title = TextResource.fromText("As We May Think"),
            description = TextResource.fromText(
                "Vannevar Bush imagines the memex, a device for storing and linking books, records, " +
                        "and communications to help people explore knowledge and extend their memory."
            ),
            context = null,
            imageUrl = "https://cdn.theatlantic.com/thumbor/bZX1oW71zQgJwzuSKMaGQSW7_w8=/1x416:2999x2102/976x549/media/img/2018/03/AP_413517775098/original.jpg",
            websiteImageUrl = "https://cdn.theatlantic.com/_next/static/images/favicon-3888b0e329526a975703e3059a02b92d.ico",
            feedInitials = "TA",
            feedTitle = "The Atlantic",
            publishedText = "July 1945",
            createdAt = "July 1945",
            bookmarked = bookmarked,
            onBookmarkChanged = { bookmarked = it },
            onPostClick = {}
        )
    }
}