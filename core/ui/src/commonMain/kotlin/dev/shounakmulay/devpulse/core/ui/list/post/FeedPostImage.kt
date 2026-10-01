package dev.shounakmulay.devpulse.core.ui.list.post

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.image.DPImage
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asString
import devpulse.core.resources.generated.resources.feed_article_image_content_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun FeedPostImage(
    imageUrl: String?,
    title: TextResource,
    modifier: Modifier,
) {
    if (imageUrl.isNullOrBlank()) return
    DPImage(
        url = imageUrl,
        contentDescription = stringResource(
            stringRes.feed_article_image_content_description,
            title.asString()
        ),
        modifier = modifier,
        contentScale = ContentScale.Crop,
    ) {
//        DPImage(
//            url = article.feed.websiteImageUrl.orEmpty(),
//            contentDescription = stringResource(
//                stringRes.feed_article_image_content_description,
//                article.title
//            ),
//            modifier = it,
//            contentScale = ContentScale.Crop,
//        )
    }
}