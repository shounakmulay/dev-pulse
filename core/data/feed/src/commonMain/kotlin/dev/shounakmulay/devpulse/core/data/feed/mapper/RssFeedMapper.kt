package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.model.feed.embedded.LocalRssFeedImage
import dev.shounakmulay.devpulse.core.data.db.model.feed.embedded.LocalRssFeedYoutubeChannel
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssFeedSearchResult
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedImage
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedSearchResult
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedYoutubeChannel
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedImage
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedMetadata
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedYoutubeChannel
import org.koin.core.annotation.Factory

@Factory
class RssFeedMapper(
    private val identityGenerator: IdentityGenerator,
    private val dateTimeProvider: DateTimeProvider,
    private val uuidMapper: UuidMapper
) {
    fun toRssFeedSearchResult(from: LocalRssFeedSearchResult): RssFeedSearchResult {
        return RssFeedSearchResult(
            id = uuidMapper.toUuid(from.id),
            title = from.title,
            name = from.name,
            sourceUrl = from.sourceUrl,
            link = from.link,
            image = from.image?.toRssFeedImage(),
            pinned = from.pinned,
            highlightedTitle = from.highlightedTitle,
            highlightedName = from.highlightedName,
            highlightedDescription = from.highlightedDescription,
        )
    }

    fun toRssIdentity(from: LocalRssFeedIdentitySlice): RssFeedIdentity {
        return RssFeedIdentity(
            id = uuidMapper.toUuid(from.id),
            title = from.title,
            name = from.name,
            pinned = from.pinned,
            sourceUrl = from.sourceUrl,
            link = from.link,
            createdAt = from.createdAt,
            updatedAt = from.updatedAt,
            lastOpenedAt = from.lastOpenedAt,
        )
    }

    fun toLocalRssFeed(
        from: ParsedFeedMetadata,
        existingIdentity: LocalRssFeedIdentitySlice?,
        queueEntry: RssFeedQueueEntry
    ): LocalRssFeed {
        val now = dateTimeProvider.nowEpochMilliseconds()
        return LocalRssFeed(
            id = existingIdentity?.id
                ?: uuidMapper.fromUuid(identityGenerator.generateSortableId()),
            name = queueEntry.name.orEmpty(),
            sourceUrl = queueEntry.url,
            title = (existingIdentity?.title ?: from.title).orEmpty(),
            link = from.link,
            description = from.description,
            image = from.image?.let { toLocalRssFeedImage(it) },
            lastBuildDate = from.lastBuildDate,
            updatePeriod = from.updatePeriod,
            youtubeChannel = from.youtubeChannel?.let { toLocalRssFeedYoutubeChannel(it) },
            createdAt = existingIdentity?.createdAt ?: now,
            updatedAt = now,
            pinned = existingIdentity?.pinned ?: false,
            lastOpenedAt = existingIdentity?.lastOpenedAt,
        )
    }

    fun toLocalRssFeed(
        from: ParsedFeedMetadata,
        existingIdentity: RssFeedIdentity?,
        queueEntry: RssFeedQueueEntry
    ): LocalRssFeed {
        val now = dateTimeProvider.nowEpochMilliseconds()
        val existingUUID = existingIdentity?.id?.let {
            uuidMapper.fromUuid(it)
        }
        return LocalRssFeed(
            id = existingUUID
                ?: uuidMapper.fromUuid(identityGenerator.generateSortableId()),
            name = queueEntry.name.orEmpty(),
            sourceUrl = queueEntry.url,
            title = (existingIdentity?.title ?: from.title).orEmpty(),
            link = from.link,
            description = from.description,
            image = from.image?.let { toLocalRssFeedImage(it) },
            lastBuildDate = from.lastBuildDate,
            updatePeriod = from.updatePeriod,
            youtubeChannel = from.youtubeChannel?.let { toLocalRssFeedYoutubeChannel(it) },
            createdAt = existingIdentity?.createdAt ?: now,
            updatedAt = now,
            pinned = existingIdentity?.pinned ?: false,
            lastOpenedAt = existingIdentity?.lastOpenedAt,
        )
    }

    fun toLocalRssFeed(from: RssFeed): LocalRssFeed {
        return LocalRssFeed(
            id = uuidMapper.fromUuid(from.id),
            name = from.name.orEmpty(),
            sourceUrl = from.sourceUrl,
            title = from.title.orEmpty(),
            link = from.link,
            description = from.description,
            image = from.image?.let { toLocalRssFeedImage(it) },
            lastBuildDate = from.lastBuildDate,
            updatePeriod = from.updatePeriod,
            youtubeChannel = from.youtubeChannel?.let { toLocalRssFeedYoutubeChannel(it) },
            createdAt = from.createdAt,
            updatedAt = from.updatedAt,
            pinned = from.pinned,
            lastOpenedAt = from.lastOpenedAt,
        )
    }

    fun toRssFeed(
        from: ParsedFeedMetadata,
        existingIdentity: RssFeedIdentity?,
        queueEntry: RssFeedQueueEntry
    ): RssFeed {
        val now = dateTimeProvider.nowEpochMilliseconds()
        val existingUUID = existingIdentity?.id
        return RssFeed(
            id = existingUUID
                ?: identityGenerator.generateSortableId(),
            name = queueEntry.name.orEmpty(),
            sourceUrl = queueEntry.url,
            title = (existingIdentity?.title ?: from.title).orEmpty(),
            link = from.link,
            description = from.description,
            image = from.image?.let { toRssFeedImage(it) },
            lastBuildDate = from.lastBuildDate,
            updatePeriod = from.updatePeriod,
            youtubeChannel = from.youtubeChannel?.let { toRssFeedYoutubeChannel(it) },
            createdAt = existingIdentity?.createdAt ?: now,
            updatedAt = now,
            pinned = existingIdentity?.pinned ?: false,
            lastOpenedAt = existingIdentity?.lastOpenedAt,
        )
    }

    private fun toLocalRssFeedYoutubeChannel(from: ParsedFeedYoutubeChannel) =
        LocalRssFeedYoutubeChannel(
            channelId = from.channelId,
        )

    private fun toRssFeedYoutubeChannel(from: ParsedFeedYoutubeChannel) =
        RssFeedYoutubeChannel(
            channelId = from.channelId,
        )

    private fun toLocalRssFeedYoutubeChannel(from: RssFeedYoutubeChannel) =
        LocalRssFeedYoutubeChannel(
            channelId = from.channelId,
        )


    private fun toLocalRssFeedImage(from: ParsedFeedImage) =
        LocalRssFeedImage(
            title = from.title,
            url = from.url,
            link = from.link,
            description = from.description,
        )

    private fun toRssFeedImage(from: ParsedFeedImage) = RssFeedImage(
        title = from.title,
        url = from.url,
        link = from.link,
        description = from.description,
    )

    private fun toLocalRssFeedImage(from: RssFeedImage) =
        LocalRssFeedImage(
            title = from.title,
            url = from.url,
            link = from.link,
            description = from.description,
        )


    fun toRssFeed(from: LocalRssFeed) = RssFeed(
        id = uuidMapper.toUuid(from.id),
        sourceUrl = from.sourceUrl,
        name = from.name,
        title = from.title,
        link = from.link,
        description = from.description,
        image = from.image?.toRssFeedImage(),
        lastBuildDate = from.lastBuildDate,
        updatePeriod = from.updatePeriod,
        youtubeChannel = from.youtubeChannel?.toRssFeedYoutubeChannel(),
        createdAt = from.createdAt,
        updatedAt = from.updatedAt,
        pinned = from.pinned,
        lastOpenedAt = from.lastOpenedAt,
    )

    private fun LocalRssFeedImage.toRssFeedImage() = RssFeedImage(
        title = title,
        url = url,
        link = link,
        description = description
    )

    private fun LocalRssFeedYoutubeChannel.toRssFeedYoutubeChannel() =
        RssFeedYoutubeChannel(
            channelId = channelId
        )
}
