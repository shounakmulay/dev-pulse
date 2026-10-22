package dev.shounakmulay.devpulse.core.domain.feed.queue.hooks

import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPost
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostMediaContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostRawEnclosure
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostYoutubeData
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostWithExistingIdentity
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContentFeedPostSanitizationHookTest {

    private val hook = ContentFeedPostSanitizationHook()

    @Test
    fun `Given rich text fields When hook processes post Then summary is sanitized and original content is retained`() = runBlocking {
        val post = createPost(
            title = "  Plain   title  ",
            description = """
                <div>Hello <strong>world</strong></div>
                <script>alert('x')</script>
                <style>.a { color: red; }</style>
                <p>Next&nbsp;line</p>
            """.trimIndent(),
            content = "<article>First<br/>Second</article>"
        )

        val result = hook.process(actionType = RssFeedQueueActionType.IMPORT, post = RssFeedPostWithExistingIdentity(post = post, identity = null)).post

        assertEquals("Plain title", result.title)
        assertEquals("Hello world Next line", result.description)
        assertEquals("<article>First<br/>Second</article>", result.content)
    }

    @Test
    fun `Given encoded entities When hook processes post Then common HTML entities are decoded`() = runBlocking {
        val post = createPost(
            title = "Tom &amp; Jerry",
            description = "&lt;b&gt;Quoted &quot;text&quot; &#39;here&#39;&lt;/b&gt;",
            categories = "Kotlin &amp; Compose"
        )

        val result = hook.process(actionType = RssFeedQueueActionType.IMPORT, post = RssFeedPostWithExistingIdentity(post = post, identity = null)).post

        assertEquals("Tom & Jerry", result.title)
        assertEquals("Quoted \"text\" 'here'", result.description)
        assertEquals(listOf("Kotlin & Compose"), result.categories)
    }

    @Test
    fun `Given blank text values When hook processes post Then title and author are empty and summary is null`() =
        runBlocking {
            val post = createPost(
                title = "   ",
                author = "\n\t",
                description = "<p> </p>",
                categories = "   "
            )

            val result = hook.process(actionType = RssFeedQueueActionType.IMPORT, post = RssFeedPostWithExistingIdentity(post = post, identity = null)).post

            assertEquals("", result.title)
            assertEquals("", result.author)
            assertNull(result.description)
            assertEquals(listOf(""), result.categories)
        }

    @Test
    fun `Given URL fields When hook processes post Then valid URLs remain and unsafe URLs are nulled`() = runBlocking {
        val post = createPost(
            guid = " opaque-guid ",
            link = "http://example.com/article",
            image = "//cdn.example.com/image.png",
            audio = "javascript:alert(1)",
            video = "data:text/html,bad",
            sourceUrl = "file:///tmp/feed.xml",
            commentsUrl = "https://"
        )

        val result = hook.process(actionType = RssFeedQueueActionType.IMPORT, post = RssFeedPostWithExistingIdentity(post = post, identity = null)).post

        assertEquals("opaque-guid", result.guid)
        assertEquals("http://example.com/article", result.link)
        assertEquals("https://cdn.example.com/image.png", result.image)
        assertNull(result.audio)
        assertNull(result.video)
        assertEquals("", result.sourceUrl)
        assertNull(result.commentsUrl)
    }

    @Test
    fun `Given nested feed models When hook processes post Then nested text and URL fields are sanitized`() = runBlocking {
        val post = createPost(
            youtubeData = RssFeedPostYoutubeData(
                videoId = "video-1",
                title = "<b>Video&nbsp;title</b>",
                videoUrl = " https://youtube.example.com/watch?v=1 ",
                thumbnailUrl = "content://thumbnail",
                description = "<p>Video <em>description</em></p>",
                viewsCount = 10,
                likesCount = 2
            ),
            rawEnclosure = RssFeedPostRawEnclosure(
                url = "javascript:bad()",
                length = 123L,
                type = " Audio/MPEG "
            ),
            rawMedia = RssFeedPostMediaContent(
                url = "//media.example.com/video.mp4",
                type = " Video/MP4 ",
                medium = " Video "
            )
        )

        val result = hook.process(actionType = RssFeedQueueActionType.IMPORT, post = RssFeedPostWithExistingIdentity(post = post, identity = null)).post

        assertEquals("video-1", result.youtubeItemData?.videoId)
        assertEquals("Video title", result.youtubeItemData?.title)
        assertEquals("https://youtube.example.com/watch?v=1", result.youtubeItemData?.videoUrl)
        assertNull(result.youtubeItemData?.thumbnailUrl)
        assertEquals("Video description", result.youtubeItemData?.description)
        assertEquals(10, result.youtubeItemData?.viewsCount)
        assertEquals(2, result.youtubeItemData?.likesCount)
        assertNull(result.rawEnclosure?.url)
        assertEquals(123L, result.rawEnclosure?.length)
        assertEquals("audio/mpeg", result.rawEnclosure?.type)
        assertEquals("//media.example.com/video.mp4", result.rawMediaContent?.url)
        assertEquals(" Video/MP4 ", result.rawMediaContent?.type)
        assertEquals(" Video ", result.rawMediaContent?.medium)
    }

    @Test
    fun `Given identity and system fields When hook processes post Then those fields are unchanged`() = runBlocking {
        val post = createPost(
            id = "post-2",
            feedId = "feed-2",
            fingerprint = "fingerprint-2",
            pubDate = "Tue, 19 May 2026 10:00:00 GMT",
            createdAt = 111L,
            updatedAt = 222L,
            rawEnclosure = RssFeedPostRawEnclosure(
                url = "https://example.com/audio.mp3",
                length = 999L,
                type = "audio/mpeg"
            ),
            youtubeData = RssFeedPostYoutubeData(
                videoId = "video-2",
                title = "Video",
                videoUrl = "https://youtube.example.com/watch?v=2",
                thumbnailUrl = "https://img.example.com/2.jpg",
                description = "Description",
                viewsCount = 50,
                likesCount = 5
            )
        )

        val result = hook.process(actionType = RssFeedQueueActionType.IMPORT, post = RssFeedPostWithExistingIdentity(post = post, identity = null)).post

        assertEquals(UUID("post-2"), result.id)
        assertEquals(UUID("feed-2"), result.feedId)
        assertEquals("fingerprint-2", result.fingerprint)
        assertEquals("Tue, 19 May 2026 10:00:00 GMT", result.pubDate)
        assertEquals(111L, result.createdAtMillis)
        assertEquals(222L, result.updatedAtMillis)
        assertEquals(999L, result.rawEnclosure?.length)
        assertEquals(50, result.youtubeItemData?.viewsCount)
        assertEquals(5, result.youtubeItemData?.likesCount)
    }

    private fun createPost(
        id: String = "post-1",
        feedId: String = "feed-1",
        fingerprint: String = "fingerprint-1",
        guid: String? = "https://example.com/guid",
        title: String? = "Title",
        author: String? = "Author",
        link: String? = "https://example.com/post",
        pubDate: String? = "Tue, 19 May 2026 10:00:00 GMT",
        publishedAtEpochMillis: Long? = 1779184800000L,
        description: String? = "Description",
        content: String? = "Content",
        image: String? = "https://example.com/image.png",
        audio: String? = "https://example.com/audio.mp3",
        video: String? = "https://example.com/video.mp4",
        sourceName: String? = "Source",
        sourceUrl: String? = "https://example.com/feed.xml",
        categories: String = "Kotlin",
        commentsUrl: String? = "https://example.com/comments",
        youtubeData: RssFeedPostYoutubeData? = null,
        rawEnclosure: RssFeedPostRawEnclosure? = null,
        rawMedia: RssFeedPostMediaContent? = null,
        createdAt: Long = 1000L,
        updatedAt: Long = 2000L
    ): RssFeedPost {
        return RssFeedPost(
            id = UUID(id),
            feedId = UUID(feedId),
            fingerprint = fingerprint,
            guid = guid,
            title = title,
            author = author,
            link = link,
            pubDate = pubDate,
            publishedAtMillis = publishedAtEpochMillis ?: Long.MIN_VALUE,
            description = description,
            content = content,
            image = image,
            audio = audio,
            video = video,
            sourceName = sourceName,
            sourceUrl = sourceUrl,
            categories = listOf(categories),
            commentsUrl = commentsUrl,
            youtubeItemData = youtubeData,
            rawEnclosure = rawEnclosure,
            rawMediaContent = rawMedia,
            createdAtMillis = createdAt,
            updatedAtMillis = updatedAt
        )
    }
}
