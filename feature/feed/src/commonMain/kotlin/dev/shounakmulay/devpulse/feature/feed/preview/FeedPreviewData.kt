package dev.shounakmulay.devpulse.feature.feed.preview

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeedImportState
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost

internal object FeedPreviewData {
    val feed = UIFeed(
        id = UUID("00000000-0000-0000-0000-000000000001"),
        imageUrl = null,
        title = "Kotlin Blog",
        initials = "KB",
        pinned = true,
        sourceUrl = "https://blog.jetbrains.com/kotlin/feed/",
        websiteImageUrl = null,
    )

    val post = UIFeedPost(
        id = UUID("00000000-0000-0000-0000-000000000002"),
        title = "Building better apps with Kotlin Multiplatform",
        sourceName = feed.title,
        sourceUrl = feed.sourceUrl,
        articleUrl = "https://blog.jetbrains.com/kotlin/",
        publishedText = "2 hours ago",
        imageUrl = null,
        summary = "Explore shared business logic and adaptive interfaces across Android, iOS, and desktop.",
        bookmarked = true,
        createdAt = "Today",
        feed = feed,
    )

    val discoveryFeed = UIDiscoveryFeed(
        id = feed.id,
        title = feed.title,
        description = TextResource.fromText("News, tutorials, and release updates from the Kotlin team."),
        feedUrl = feed.sourceUrl,
        websiteUrl = "https://blog.jetbrains.com/kotlin/",
        imageUrl = null,
        initials = feed.initials,
        importState = UIDiscoveryFeedImportState.NotAdded,
    )
    val rssFeed = RssFeed(
        id = feed.id,
        pinned = feed.pinned,
        name = feed.title,
        sourceUrl = feed.sourceUrl,
        title = feed.title,
        link = "https://blog.jetbrains.com/kotlin/",
        description = "News, tutorials, and release updates from the Kotlin team.",
        image = null,
        lastBuildDate = null,
        updatePeriod = null,
        youtubeChannel = null,
        createdAt = 0L,
        updatedAt = 0L,
        lastOpenedAt = null,
    )
}
