package dev.shounakmulay.devpulse.core.data.feed.parser

import com.prof18.rssparser.model.RawEnclosure
import com.prof18.rssparser.model.RawMediaContent
import com.prof18.rssparser.model.RssChannel
import com.prof18.rssparser.model.RssImage
import com.prof18.rssparser.model.RssItem
import com.prof18.rssparser.model.YoutubeChannelData
import com.prof18.rssparser.model.YoutubeItemData
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.Prof18ParsedFeedMapper
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class Prof18ParsedFeedMapperTest {
    private val mapper = Prof18ParsedFeedMapper()

    @Test
    fun `Given Prof18 RSS channel When mapped to parsed feed Then feed fields are preserved`() = runTest {
        val result = mapper.toParsedFeed(createProf18Channel())

        assertEquals("Title", result.metadata.title)
        assertEquals("https://example.com", result.metadata.link)
        assertEquals("Description", result.metadata.description)
        assertEquals("https://example.com/feed.png", result.metadata.image?.url)
        assertEquals("Tue, 19 May 2026 10:00:00 GMT", result.metadata.lastBuildDate)
        assertEquals("hourly", result.metadata.updatePeriod)
        assertEquals("channel-1", result.metadata.youtubeChannel?.channelId)
        assertEquals(emptyList(), result.issues)
    }

    @Test
    fun `Given Prof18 RSS item When mapped to parsed item Then post fields and ordinal are preserved`() = runTest {
        val item = mapper.toParsedFeed(createProf18Channel()).items.toList().single()

        assertEquals("guid-1", item.guid)
        assertEquals("Title", item.title)
        assertEquals("https://example.com/post", item.link)
        assertEquals("Tue, 19 May 2026 10:00:00 GMT", item.pubDate)
        assertEquals("https://example.com/image.jpg", item.image)
        assertEquals(listOf("Kotlin"), item.categories)
        assertEquals("video-1", item.youtubeItemData?.videoId)
        assertEquals("https://example.com/audio.mp3", item.rawEnclosure?.url)
        assertEquals("https://example.com/media.mp4", item.rawMediaContent?.url)
    }

    private fun createProf18Channel(): RssChannel {
        return RssChannel(
            title = "Title",
            link = "https://example.com",
            description = "Description",
            image = RssImage(
                title = "Image",
                url = "https://example.com/feed.png",
                link = "https://example.com",
                description = "Image description"
            ),
            lastBuildDate = "Tue, 19 May 2026 10:00:00 GMT",
            updatePeriod = "hourly",
            items = listOf(createProf18Item()),
            itunesChannelData = null,
            youtubeChannelData = YoutubeChannelData(channelId = "channel-1")
        )
    }

    private fun createProf18Item(): RssItem {
        return RssItem(
            guid = "guid-1",
            title = "Title",
            author = "Author",
            link = "https://example.com/post",
            pubDate = "Tue, 19 May 2026 10:00:00 GMT",
            description = "Description",
            content = "Content",
            image = "https://example.com/image.jpg",
            audio = "https://example.com/audio.mp3",
            video = "https://example.com/video.mp4",
            sourceName = "Source",
            sourceUrl = "https://example.com/feed.xml",
            categories = listOf("Kotlin"),
            itunesItemData = null,
            commentsUrl = "https://example.com/comments",
            youtubeItemData = YoutubeItemData(
                videoId = "video-1",
                title = "Video",
                videoUrl = "https://example.com/watch",
                thumbnailUrl = "https://example.com/thumb.jpg",
                description = "Video description",
                viewsCount = 10,
                likesCount = 5
            ),
            rawEnclosure = RawEnclosure(
                url = "https://example.com/audio.mp3",
                length = 42L,
                type = "audio/mpeg"
            ),
            rawMediaContent = RawMediaContent(
                url = "https://example.com/media.mp4",
                type = "video/mp4",
                medium = "video"
            )
        )
    }
}
