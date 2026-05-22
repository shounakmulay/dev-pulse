package dev.shounakmulay.devpulse.core.data.feed.hook

import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemMediaContent
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemRawEnclosure
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemYoutubeData
import dev.shounakmulay.devpulse.core.data.feed.hook.model.PostWithIdentity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContentFeedPostSanitizationHookTest {

    private val hook = ContentFeedPostSanitizationHook()

    @Test
    fun `Given rich text fields When hook processes post Then tags scripts styles and whitespace are removed`() = runTest {
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

        val result = hook.process(PostWithIdentity(post = post, identity = null)).post

        assertEquals("Plain title", result.title)
        assertEquals("Hello world Next line", result.description)
        assertEquals("First Second", result.content)
    }

    @Test
    fun `Given encoded entities When hook processes post Then common HTML entities are decoded`() = runTest {
        val post = createPost(
            title = "Tom &amp; Jerry",
            description = "&lt;b&gt;Quoted &quot;text&quot; &#39;here&#39;&lt;/b&gt;",
            categories = "Kotlin &amp; Compose"
        )

        val result = hook.process(PostWithIdentity(post = post, identity = null)).post

        assertEquals("Tom & Jerry", result.title)
        assertEquals("Quoted \"text\" 'here'", result.description)
        assertEquals("Kotlin & Compose", result.categories)
    }

    @Test
    fun `Given blank text values When hook processes post Then nullable strings are null and categories is empty`() =
        runTest {
            val post = createPost(
                title = "   ",
                author = "\n\t",
                description = "<p> </p>",
                categories = "   "
            )

            val result = hook.process(PostWithIdentity(post = post, identity = null)).post

            assertNull(result.title)
            assertNull(result.author)
            assertNull(result.description)
            assertEquals("", result.categories)
        }

    @Test
    fun `Given URL fields When hook processes post Then valid URLs remain and unsafe URLs are nulled`() = runTest {
        val post = createPost(
            guid = " opaque-guid ",
            link = "http://example.com/article",
            image = "//cdn.example.com/image.png",
            audio = "javascript:alert(1)",
            video = "data:text/html,bad",
            sourceUrl = "file:///tmp/feed.xml",
            commentsUrl = "https://"
        )

        val result = hook.process(PostWithIdentity(post = post, identity = null)).post

        assertEquals("opaque-guid", result.guid)
        assertEquals("http://example.com/article", result.link)
        assertEquals("https://cdn.example.com/image.png", result.image)
        assertNull(result.audio)
        assertNull(result.video)
        assertNull(result.sourceUrl)
        assertNull(result.commentsUrl)
    }

    @Test
    fun `Given nested feed models When hook processes post Then nested text and URL fields are sanitized`() = runTest {
        val post = createPost(
            youtubeData = LocalRssFeedItemYoutubeData(
                videoId = "video-1",
                title = "<b>Video&nbsp;title</b>",
                videoUrl = " https://youtube.example.com/watch?v=1 ",
                thumbnailUrl = "content://thumbnail",
                description = "<p>Video <em>description</em></p>",
                viewsCount = 10,
                likesCount = 2
            ),
            rawEnclosure = LocalRssFeedItemRawEnclosure(
                url = "javascript:bad()",
                length = 123L,
                type = " Audio/MPEG "
            ),
            rawMedia = LocalRssFeedItemMediaContent(
                url = "//media.example.com/video.mp4",
                type = " Video/MP4 ",
                medium = " Video "
            )
        )

        val result = hook.process(PostWithIdentity(post = post, identity = null)).post

        assertEquals("video-1", result.youtubeData?.videoId)
        assertEquals("Video title", result.youtubeData?.title)
        assertEquals("https://youtube.example.com/watch?v=1", result.youtubeData?.videoUrl)
        assertNull(result.youtubeData?.thumbnailUrl)
        assertEquals("Video description", result.youtubeData?.description)
        assertEquals(10, result.youtubeData?.viewsCount)
        assertEquals(2, result.youtubeData?.likesCount)
        assertNull(result.rawEnclosure?.url)
        assertEquals(123L, result.rawEnclosure?.length)
        assertEquals("audio/mpeg", result.rawEnclosure?.type)
        assertEquals("https://media.example.com/video.mp4", result.rawMedia?.url)
        assertEquals("video/mp4", result.rawMedia?.type)
        assertEquals("video", result.rawMedia?.medium)
    }

    @Test
    fun `Given identity and system fields When hook processes post Then those fields are unchanged`() = runTest {
        val post = createPost(
            id = "post-2",
            feedId = "feed-2",
            fingerprint = "fingerprint-2",
            pubDate = "Tue, 19 May 2026 10:00:00 GMT",
            createdAt = 111L,
            updatedAt = 222L,
            rawEnclosure = LocalRssFeedItemRawEnclosure(
                url = "https://example.com/audio.mp3",
                length = 999L,
                type = "audio/mpeg"
            ),
            youtubeData = LocalRssFeedItemYoutubeData(
                videoId = "video-2",
                title = "Video",
                videoUrl = "https://youtube.example.com/watch?v=2",
                thumbnailUrl = "https://img.example.com/2.jpg",
                description = "Description",
                viewsCount = 50,
                likesCount = 5
            )
        )

        val result = hook.process(PostWithIdentity(post = post, identity = null)).post

        assertEquals("post-2", result.id)
        assertEquals("feed-2", result.feedId)
        assertEquals("fingerprint-2", result.fingerprint)
        assertEquals("Tue, 19 May 2026 10:00:00 GMT", result.pubDate)
        assertEquals(111L, result.createdAt)
        assertEquals(222L, result.updatedAt)
        assertEquals(999L, result.rawEnclosure?.length)
        assertEquals(50, result.youtubeData?.viewsCount)
        assertEquals(5, result.youtubeData?.likesCount)
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
        description: String? = "Description",
        content: String? = "Content",
        image: String? = "https://example.com/image.png",
        audio: String? = "https://example.com/audio.mp3",
        video: String? = "https://example.com/video.mp4",
        sourceName: String? = "Source",
        sourceUrl: String? = "https://example.com/feed.xml",
        categories: String = "Kotlin",
        commentsUrl: String? = "https://example.com/comments",
        youtubeData: LocalRssFeedItemYoutubeData? = null,
        rawEnclosure: LocalRssFeedItemRawEnclosure? = null,
        rawMedia: LocalRssFeedItemMediaContent? = null,
        createdAt: Long = 1000L,
        updatedAt: Long = 2000L
    ): LocalRssContentFeedPost {
        return LocalRssContentFeedPost(
            id = id,
            feedId = feedId,
            fingerprint = fingerprint,
            guid = guid,
            title = title,
            author = author,
            link = link,
            pubDate = pubDate,
            description = description,
            content = content,
            image = image,
            audio = audio,
            video = video,
            sourceName = sourceName,
            sourceUrl = sourceUrl,
            categories = categories,
            commentsUrl = commentsUrl,
            youtubeData = youtubeData,
            rawEnclosure = rawEnclosure,
            rawMedia = rawMedia,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
