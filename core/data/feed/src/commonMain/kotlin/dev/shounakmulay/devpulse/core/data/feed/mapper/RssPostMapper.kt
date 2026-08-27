package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.embedded.LocalRssFeedItemMediaContent
import dev.shounakmulay.devpulse.core.data.db.model.feed.embedded.LocalRssFeedItemRawEnclosure
import dev.shounakmulay.devpulse.core.data.db.model.feed.embedded.LocalRssFeedItemYoutubeData
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostCategory
import dev.shounakmulay.devpulse.core.data.feed.hook.model.LocalPostWithIdentity
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPost
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostIdentity
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostMediaContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostRawEnclosure
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostWithExistingIdentity
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostYoutubeData
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostCategory
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedItem
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedItemMediaContent
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedItemRawEnclosure
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedItemYoutubeData
import org.koin.core.annotation.Factory

@Factory
class RssPostMapper(
    private val idGenerator: IdentityGenerator,
    private val dateTimeProvider: DateTimeProvider,
    private val uuidMapper: UuidMapper
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
        feedId: LocalUUID,
        fingerprint: String,
        existingIdentity: LocalRssContentFeedPostIdentitySlice?
    ): LocalRssContentFeedPost {
        val now = dateTimeProvider.nowEpochMilliseconds()
        val publishedAt = item.pubDate?.let {
            dateTimeProvider.parse(it)?.toEpochMilliseconds()
        } ?: Long.MIN_VALUE
        return LocalRssContentFeedPost(
            id = existingIdentity?.id ?: uuidMapper.fromUuid(idGenerator.generateSortableId()),
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

    fun toRssPostCategories(
        postId: String,
        categories: List<String>
    ): List<RssPostCategory> {
        return categories
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
            .distinct()
            .map {
                RssPostCategory(
                    postId = UUID(postId),
                    category = it
                )
            }
    }

    fun toLocalRssPostCategory(from: RssPostCategory) = LocalRssPostCategory(
        postId = uuidMapper.fromUuid(from.postId),
        category = from.category
    )

    fun toRssPostCategory(from: LocalRssPostCategory) = RssPostCategory(
        postId = uuidMapper.toUuid(from.postId),
        category = from.category
    )


    fun toRssFeedPost(from: LocalRssContentFeedPost): RssFeedPost {
        return RssFeedPost(
            id = uuidMapper.toUuid(from.id),
            feedId = uuidMapper.toUuid(from.feedId),
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
            createdAtMillis = from.createdAt,
            updatedAtMillis = from.updatedAt,
            pubDate = from.pubDate
        )
    }

    fun toLocalRssContentFeedPost(from: RssFeedPost): LocalRssContentFeedPost {
        return LocalRssContentFeedPost(
            id = uuidMapper.fromUuid(from.id),
            feedId = uuidMapper.fromUuid(from.feedId),
            fingerprint = from.fingerprint,
            guid = from.guid,
            title = from.title.orEmpty(),
            author = from.author.orEmpty(),
            link = from.link,
            description = from.description,
            content = from.content,
            image = from.image,
            audio = from.audio,
            video = from.video,
            sourceName = from.sourceName.orEmpty(),
            sourceUrl = from.sourceUrl.orEmpty(),
            categories = from.categories.joinToString(","),
            commentsUrl = from.commentsUrl,
            bookmarked = from.bookmarked,
            pubDate = from.pubDate,
            publishedAtEpochMillis = from.publishedAtMillis,
            youtubeData = from.youtubeItemData?.let { mapFromYoutubeData(it) },
            rawEnclosure = from.rawEnclosure?.let { mapFromRawEnclosure(it) },
            rawMedia = from.rawMediaContent?.let { mapFromMediaContent(it) },
            createdAt = from.createdAtMillis,
            updatedAt = from.updatedAtMillis,
        )
    }

    fun toRssFeedPostIdentity(from: LocalRssContentFeedPostIdentitySlice): RssFeedPostIdentity {
        return RssFeedPostIdentity(
            id = uuidMapper.toUuid(from.id),
            fingerprint = from.fingerprint,
            bookmarked = from.bookmarked,
            createdAt = from.createdAt,
            updatedAt = from.updatedAt
        )
    }

    fun toLocalRssContentFeedPostIdentitySlice(from: RssFeedPostIdentity): LocalRssContentFeedPostIdentitySlice {
        return LocalRssContentFeedPostIdentitySlice(
            id = uuidMapper.fromUuid(from.id),
            fingerprint = from.fingerprint,
            bookmarked = from.bookmarked,
            createdAt = from.createdAt,
            updatedAt = from.updatedAt
        )
    }

    fun toRssFeedPostWithIdentity(from: LocalPostWithIdentity): RssFeedPostWithExistingIdentity {
        return RssFeedPostWithExistingIdentity(
            post = toRssFeedPost(from.post),
            identity = from.identity?.let { toRssFeedPostIdentity(it) }
        )
    }

    private fun mapRawMediaContent(from: ParsedFeedItemMediaContent): LocalRssFeedItemMediaContent {
        return LocalRssFeedItemMediaContent(
            url = from.url,
            type = from.type,
            medium = from.medium
        )
    }

    private fun mapFromMediaContent(from: RssFeedPostMediaContent): LocalRssFeedItemMediaContent {
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

    private fun mapFromRawEnclosure(from: RssFeedPostRawEnclosure): LocalRssFeedItemRawEnclosure {
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

    private fun mapFromYoutubeData(from: RssFeedPostYoutubeData): LocalRssFeedItemYoutubeData {
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
