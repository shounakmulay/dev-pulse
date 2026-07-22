package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemMediaContent
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemRawEnclosure
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemYoutubeData
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssPostCategory
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItem
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItemMediaContent
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItemRawEnclosure
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItemYoutubeData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedPost
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedPostMediaContent
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedPostRawEnclosure
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedPostYoutubeData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssPostWithFeedIdentity
import org.koin.core.annotation.Factory

@Factory
class RssPostMapper(
    private val idGenerator: IdentityGenerator,
    private val dateTimeProvider: DateTimeProvider
) {
    fun toRssPostWithFeedIdentity(
        post: RssFeedPost,
        identity: RssFeedIdentity
    ): RssPostWithFeedIdentity {
        return RssPostWithFeedIdentity(
            post = post,
            feedIdentity = identity
        )
    }

    fun toLocalRssContentFeedPost(
        item: ParsedFeedItem,
        feedId: String,
        fingerprint: String,
        existingIdentity: LocalRssContentFeedPostIdentitySlice?
    ): LocalRssContentFeedPost {
        val now = dateTimeProvider.nowEpochMilliseconds()
        val publishedAt = item.pubDate?.let {
            dateTimeProvider.parse(it)?.toEpochMilliseconds()
        } ?: Long.MIN_VALUE
        return LocalRssContentFeedPost(
            id = existingIdentity?.id ?: idGenerator.generateSortableId(),
            feedId = feedId,
            fingerprint = fingerprint,
            guid = item.guid,
            title = item.title.orEmpty(),
            author = item.author.orEmpty(),
            link = item.link,
            pubDate = item.pubDate,
            publishedAtEpochMillis = publishedAt,
            description = item.description,
            content = item.content,
            image = item.image,
            audio = item.audio,
            video = item.video,
            sourceName = item.sourceName.orEmpty(),
            sourceUrl = item.sourceUrl.orEmpty(),
            categories = item.categories.joinToString(),
            commentsUrl = item.commentsUrl,
            bookmarked = existingIdentity?.bookmarked ?: false,
            youtubeData = item.youtubeItemData?.let { mapYoutubeData(it) },
            rawEnclosure = item.rawEnclosure?.let { mapRawEnclosure(it) },
            rawMedia = item.rawMediaContent?.let { mapRawMediaContent(it) },
            createdAt = existingIdentity?.createdAt ?: now,
            updatedAt = now
        )
    }

    fun toLocalRssPostCategories(
        postId: String,
        categories: List<String>
    ): List<LocalRssPostCategory> {
        return categories
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
            .distinct()
            .map {
                LocalRssPostCategory(
                    postId = postId,
                    category = it
                )
            }
    }

    fun toRssFeedPost(from: LocalRssContentFeedPost): RssFeedPost {
        return RssFeedPost(
            id = from.id,
            feedId = from.feedId,
            fingerprint = from.fingerprint,
            guid = from.guid,
            title = from.title,
            author = from.author,
            link = from.link,
            publishedAtMillis = from.publishedAtEpochMillis,
            description = from.description,
            content = from.content,
            image = from.image,
            audio = from.audio,
            video = from.video,
            sourceName = from.sourceName,
            sourceUrl = from.sourceUrl,
            categories = from.categories.split(",").map { it.trim() }.filter { it.isNotBlank() },
            commentsUrl = from.commentsUrl,
            bookmarked = from.bookmarked,
            youtubeItemData = from.youtubeData?.toRssFeedItemYoutubeData(),
            rawEnclosure = from.rawEnclosure?.toRssFeedItemRawEnclosure(),
            rawMediaContent = from.rawMedia?.toRssFeedItemMediaContent(),
            createdAtMillis = from.createdAt
        )
    }
    private fun mapRawMediaContent(from: ParsedFeedItemMediaContent): LocalRssFeedItemMediaContent {
        return LocalRssFeedItemMediaContent(
            url = from.url,
            type = from.type,
            medium = from.medium
        )
    }

    private fun mapRawEnclosure(from: ParsedFeedItemRawEnclosure): LocalRssFeedItemRawEnclosure {
        return LocalRssFeedItemRawEnclosure(
            url = from.url,
            length = from.length,
            type = from.type
        )
    }

    private fun mapYoutubeData(from: ParsedFeedItemYoutubeData): LocalRssFeedItemYoutubeData {
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

    private fun LocalRssFeedItemYoutubeData.toRssFeedItemYoutubeData() = RssFeedPostYoutubeData(
        videoId = videoId,
        title = title,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl,
        description = description,
        viewsCount = viewsCount,
        likesCount = likesCount,
    )
    private fun LocalRssFeedItemRawEnclosure.toRssFeedItemRawEnclosure() = RssFeedPostRawEnclosure(
        url = url,
        length = length,
        type = type,
    )
    private fun LocalRssFeedItemMediaContent.toRssFeedItemMediaContent() = RssFeedPostMediaContent(
        url = url,
        type = type,
        medium = medium,
    )
}
