package dev.shounakmulay.devpulse.core.data.feed.mapper

import com.prof18.rssparser.model.RawEnclosure
import com.prof18.rssparser.model.RawMediaContent
import com.prof18.rssparser.model.RssItem
import com.prof18.rssparser.model.YoutubeItemData
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemMediaContent
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemRawEnclosure
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemYoutubeData
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentity
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import org.koin.core.annotation.Factory
import kotlin.time.Clock

@Factory
class RssItemMapper(
    private val idGenerator: IdentityGenerator
) {

    fun toLocalRssContentFeedPost(
        item: RssItem,
        feedId: String,
        existingIdentity: LocalRssContentFeedPostIdentity?
    ): LocalRssContentFeedPost {
        val now = Clock.System.now().epochSeconds
        return LocalRssContentFeedPost(
            id = existingIdentity?.id ?: idGenerator.generateSortableId(),
            feedId = feedId,
            fingerprint = existingIdentity?.fingerprint ?: idGenerator.generateFingerprint(),
            guid = item.guid,
            title = item.title,
            author = item.author,
            link = item.link,
            pubDate = item.pubDate,
            description = item.description,
            content = item.content,
            image = item.image,
            audio = item.author,
            video = item.video,
            sourceName = item.sourceName,
            sourceUrl = item.sourceUrl,
            categories = item.categories.joinToString(),
            commentsUrl = item.commentsUrl,
            youtubeData = item.youtubeItemData?.let { mapYoutubeData(it) },
            rawEnclosure = item.rawEnclosure?.let { mapRawEnclosure(it) },
            rawMedia = item.rawMediaContent?.let { mapRawMediaContent(it) },
            createdAt = existingIdentity?.createdAt ?: now,
            updatedAt = now
        )
    }

    private fun mapRawMediaContent(from: RawMediaContent): LocalRssFeedItemMediaContent {
        return LocalRssFeedItemMediaContent(
            url = from.url,
            type = from.type,
            medium = from.medium
        )
    }

    private fun mapRawEnclosure(from: RawEnclosure): LocalRssFeedItemRawEnclosure {
        return LocalRssFeedItemRawEnclosure(
            url = from.url,
            length = from.length,
            type = from.type
        )
    }

    private fun mapYoutubeData(from: YoutubeItemData): LocalRssFeedItemYoutubeData {
        return LocalRssFeedItemYoutubeData(
            videoId = from.videoId,
            title = from.title,
            videoUrl = from.videoUrl,
            thumbnailUrl = from.thumbnailUrl,
            description = from.description,
            viewsCount = from.viewsCount,
            likesCount = from.likesCount
        )
    }
}
