package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.image.DPImage
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeedPost
import devpulse.core.resources.generated.resources.feed_article_image_content_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun ArticleImage(
    article: UIFeedPost,
    modifier: Modifier,
) {
    DPImage(
        url = article.imageUrl.orEmpty(),
        contentDescription = stringResource(
            stringRes.feed_article_image_content_description,
            article.title
        ),
        modifier = modifier,
        contentScale = ContentScale.Crop,
    ) {
        DPImage(
            url = article.feed.websiteImageUrl.orEmpty(),
            contentDescription = stringResource(
                stringRes.feed_article_image_content_description,
                article.title
            ),
            modifier = it,
            contentScale = ContentScale.Crop,
        )
    }
}