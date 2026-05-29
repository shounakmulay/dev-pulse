package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedImage
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedYoutubeChannel
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedImage
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedMetadata
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedYoutubeChannel
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedImage
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedYoutubeChannel
import org.koin.core.annotation.Factory

@Factory
class RssFeedMapper(
    private val identityGenerator: IdentityGenerator,
    private val dateTimeProvider: DateTimeProvider
) {
    fun toRssIdentity(from: LocalRssFeedIdentitySlice): RssFeedIdentity {
        return RssFeedIdentity(
            id = from.id,
            title = from.title,
            name = from.name,
            pinned = from.pinned,
            sourceUrl = from.sourceUrl,
            link = from.link,
            createdAt = from.createdAt,
            updatedAt = from.updatedAt,
        )
    }
    fun toLocalRssFeed(
        from: ParsedFeedMetadata,
        existingIdentity: LocalRssFeedIdentitySlice?,
        queueEntry: RssFeedQueueEntry
    ): LocalRssFeed {
        val now = dateTimeProvider.nowEpochMilliseconds()
        return LocalRssFeed(
            id = existingIdentity?.id ?: identityGenerator.generateSortableId(),
            name = queueEntry.name,
            sourceUrl = queueEntry.url,
            title = existingIdentity?.title ?: from.title,
            link = from.link,
            description = from.description,
            image = from.image?.let { toLocalRssFeedImage(it) },
            lastBuildDate = from.lastBuildDate,
            updatePeriod = from.updatePeriod,
            youtubeChannel = from.youtubeChannel?.let { toLocalRssFeedYoutubeChannel(it) },
            createdAt = existingIdentity?.createdAt ?: now,
            updatedAt = now,
            pinned = existingIdentity?.pinned ?: false,
        )
    }
    private fun toLocalRssFeedYoutubeChannel(from: ParsedFeedYoutubeChannel) = LocalRssFeedYoutubeChannel(
        channelId = from.channelId,
    )
    private fun toLocalRssFeedImage(from: ParsedFeedImage) = LocalRssFeedImage(
        title = from.title,
        url = from.url,
        link = from.link,
        description = from.description,
    )
    fun toRssFeed(from: LocalRssFeed) = RssFeed(
        id = from.id,
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
    )
    private fun LocalRssFeedImage.toRssFeedImage() = RssFeedImage(
        title = title,
        url = url,
        link = link,
        description = description
    )
    private fun LocalRssFeedYoutubeChannel.toRssFeedYoutubeChannel() = RssFeedYoutubeChannel(
        channelId = channelId
    )
}
