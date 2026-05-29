package dev.shounakmulay.devpulse.core.data.feed.parser

import dev.shounakmulay.devpulse.core.data.feed.parser.opml.OpmlParser
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RssFeedParserTest {

    @Test
    fun `Given RSS with optional malformed values When parsed Then usable fields are extracted`() = runTest {
        val parser = KtXmlFeedParser()

        val result = parser.parseText(
            sourceUrl = "https://example.com/feed.xml",
            xml = rssFixture
        )
        val items = result.items.toList()

        assertEquals("Example Feed", result.metadata.title)
        assertEquals("https://example.com", result.metadata.link)
        assertEquals("Feed description", result.metadata.description)
        assertEquals("https://example.com/feed.png", result.metadata.image?.url)
        assertEquals("daily", result.metadata.updatePeriod)
        assertEquals("channel-1", result.metadata.youtubeChannel?.channelId)
        assertEquals(2, items.size)
        assertEquals("Post title", items[0].title)
        assertEquals("https://example.com/post", items[0].link)
        assertEquals("Summary", items[0].description)
        assertEquals("Full content", items[0].content)
        assertEquals("https://example.com/audio.mp3", items[0].audio)
        assertEquals("https://example.com/thumb.jpg", items[0].image)
        assertEquals("Kotlin", items[0].categories.first())
        assertEquals("video-1", items[0].youtubeItemData?.videoId)
        assertNull(items[1].title)
        assertEquals("https://example.com/minimal", items[1].link)
    }

    @Test
    fun `Given Atom feed When parsed Then entries are normalized`() = runTest {
        val parser = KtXmlFeedParser()

        val result = parser.parseText(
            sourceUrl = "https://example.com/atom.xml",
            xml = atomFixture
        )
        val item = result.items.toList().single()

        assertEquals("Atom Feed", result.metadata.title)
        assertEquals("https://example.com", result.metadata.link)
        assertEquals("Atom subtitle", result.metadata.description)
        assertEquals("atom-entry-1", item.guid)
        assertEquals("Atom title", item.title)
        assertEquals("https://example.com/atom-post", item.link)
        assertEquals("Author Name", item.author)
        assertEquals("Atom summary", item.description)
        assertEquals("Atom content", item.content)
    }

    @Test
    fun `Given OPML text When parsed Then nested feeds preserve category path`() {
        val result = OpmlParser().parseText(opmlFixture)

        assertEquals("Subscriptions", result.title)
        assertEquals(2, result.feeds.size)
        assertEquals("Dev", result.feeds[0].categoryPath.single())
        assertEquals("https://example.com/feed.xml", result.feeds[0].xmlUrl)
        assertEquals("Loose", result.feeds[1].title)
    }

    @Test
    fun `Given OPML file bytes When parsed Then feeds are extracted`() {
        val result = OpmlParser().parseFileBytes(fileName = "feeds.opml", bytes = opmlFixture.encodeToByteArray())

        assertNotNull(result.feeds.firstOrNull())
    }

    private val rssFixture = """
        <rss xmlns:content="http://purl.org/rss/1.0/modules/content/"
             xmlns:media="http://search.yahoo.com/mrss/"
             xmlns:yt="http://www.youtube.com/xml/schemas/2015">
            <channel>
                <title> Example Feed </title>
                <link>https://example.com</link>
                <description>Feed&nbsp;description</description>
                <image>
                    <url>https://example.com/feed.png</url>
                    <title>Feed image</title>
                    <link>https://example.com</link>
                    <description>Image description</description>
                </image>
                <lastBuildDate>Tue, 19 May 2026 10:00:00 GMT</lastBuildDate>
                <sy:updatePeriod xmlns:sy="http://purl.org/rss/1.0/modules/syndication/">daily</sy:updatePeriod>
                <yt:channelId>channel-1</yt:channelId>
                <item>
                    <guid>guid-1</guid>
                    <title>Post title</title>
                    <author>Author</author>
                    <link>https://example.com/post</link>
                    <pubDate>Tue, 19 May 2026 10:00:00 GMT</pubDate>
                    <description><![CDATA[<p>Summary</p><script>bad()</script>]]></description>
                    <content:encoded><![CDATA[<article>Full content</article>]]></content:encoded>
                    <category>Kotlin</category>
                    <comments>https://example.com/comments</comments>
                    <source url="https://example.com/feed.xml">Example Feed</source>
                    <enclosure url="https://example.com/audio.mp3" length="42" type="audio/mpeg" />
                    <media:thumbnail url="https://example.com/thumb.jpg" />
                    <yt:videoId>video-1</yt:videoId>
                </item>
                <item>
                    <link>https://example.com/minimal</link>
                    <description></description>
                </item>
            </channel>
        </rss>
    """.trimIndent()

    private val atomFixture = """
        <feed xmlns="http://www.w3.org/2005/Atom">
            <title>Atom Feed</title>
            <subtitle>Atom subtitle</subtitle>
            <link rel="alternate" href="https://example.com" />
            <updated>2026-05-19T10:00:00Z</updated>
            <entry>
                <id>atom-entry-1</id>
                <title>Atom title</title>
                <author><name>Author Name</name></author>
                <link rel="alternate" href="https://example.com/atom-post" />
                <published>2026-05-19T10:00:00Z</published>
                <summary>Atom summary</summary>
                <content>Atom content</content>
                <category term="Kotlin" />
            </entry>
        </feed>
    """.trimIndent()

    private val opmlFixture = """
        <opml version="2.0">
            <head><title>Subscriptions</title></head>
            <body>
                <outline text="Dev">
                    <outline
                        text="Example"
                        title="Example Feed"
                        type="rss"
                        xmlUrl="https://example.com/feed.xml"
                        htmlUrl="https://example.com"
                    />
                </outline>
                <outline title="Loose" type="rss" xmlUrl="https://loose.example.com/rss" />
            </body>
        </opml>
    """.trimIndent()
}
