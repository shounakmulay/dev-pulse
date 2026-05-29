package dev.shounakmulay.devpulse.core.data.feed.mapper

import com.prof18.rssparser.model.RawEnclosure
import com.prof18.rssparser.model.RawMediaContent
import com.prof18.rssparser.model.RssItem
import com.prof18.rssparser.model.YoutubeItemData
import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemMediaContent
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemRawEnclosure
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemYoutubeData
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedPost
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedPostMediaContent
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedPostRawEnclosure
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedPostYoutubeData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssPostWithFeedIdentity
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
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
        item: RssItem,
        feedId: String,
        fingerprint: String,
        existingIdentity: LocalRssContentFeedPostIdentitySlice?
    ): LocalRssContentFeedPost {
        val now = dateTimeProvider.nowEpochMilliseconds()
        return LocalRssContentFeedPost(
            id = existingIdentity?.id ?: idGenerator.generateSortableId(),
            feedId = feedId,
            fingerprint = fingerprint,
            guid = item.guid,
            title = item.title,
            author = item.author,
            link = item.link,
            pubDate = item.pubDate,
            publishedAtEpochMillis = item.pubDate?.toRssEpochMilliseconds(),
            description = item.description,
            content = item.content,
            image = item.image,
            audio = item.audio,
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

    fun toRssFeedPost(from: LocalRssContentFeedPost): RssFeedPost {
        return RssFeedPost(
            id = from.id,
            feedId = from.feedId,
            fingerprint = from.fingerprint,
            guid = from.guid,
            title = from.title,
            author = from.author,
            link = from.link,
            pubDate = from.pubDate,
            description = from.description,
            content = from.content,
            image = from.image,
            audio = from.audio,
            video = from.video,
            sourceName = from.sourceName,
            sourceUrl = from.sourceUrl,
            categories = from.categories.split(",").map { it.trim() }.filter { it.isNotBlank() },
            commentsUrl = from.commentsUrl,
            youtubeItemData = from.youtubeData?.toRssFeedItemYoutubeData(),
            rawEnclosure = from.rawEnclosure?.toRssFeedItemRawEnclosure(),
            rawMediaContent = from.rawMedia?.toRssFeedItemMediaContent(),
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

    private fun String.toRssEpochMilliseconds(): Long? {
        val value = trim()
            .removePrefix("Published:")
            .trim()
        val withoutDay = value.substringAfter(", ", value)
        val parts = withoutDay.split(Regex("\\s+"))
        if (parts.size < 4) return null

        val day = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toMonthNumber() ?: return null
        val year = parts[2].toIntOrNull() ?: return null
        val timeParts = parts[3].split(":")
        if (timeParts.size < 2) return null

        val hour = timeParts[0].toIntOrNull() ?: return null
        val minute = timeParts[1].toIntOrNull() ?: return null
        val second = timeParts.getOrNull(2)?.toIntOrNull() ?: 0
        val offsetMinutes = parts.getOrNull(4)?.toOffsetMinutes() ?: 0
        val localDateTime = runCatching {
            LocalDateTime(
                year = year,
                monthNumber = month,
                dayOfMonth = day,
                hour = hour,
                minute = minute,
                second = second
            )
        }.getOrNull() ?: return null

        return localDateTime
            .toInstant(TimeZone.UTC)
            .toEpochMilliseconds() - (offsetMinutes.toLong() * 60_000)
    }

    private fun String.toMonthNumber(): Int? {
        return when (lowercase().take(3)) {
            "jan" -> 1
            "feb" -> 2
            "mar" -> 3
            "apr" -> 4
            "may" -> 5
            "jun" -> 6
            "jul" -> 7
            "aug" -> 8
            "sep" -> 9
            "oct" -> 10
            "nov" -> 11
            "dec" -> 12
            else -> null
        }
    }

    private fun String.toOffsetMinutes(): Int? {
        val upper = uppercase()
        if (upper == "GMT" || upper == "UTC" || upper == "Z") return 0
        val normalized = replace(":", "")
        if (normalized.length != 5) return null
        val sign = when (normalized.first()) {
            '+' -> 1
            '-' -> -1
            else -> return null
        }
        val hours = normalized.substring(1, 3).toIntOrNull() ?: return null
        val minutes = normalized.substring(3, 5).toIntOrNull() ?: return null
        return sign * ((hours * 60) + minutes)
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
