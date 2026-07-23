package dev.shounakmulay.devpulse.core.data.feed.parser

import dev.shounakmulay.devpulse.core.data.feed.parser.common.ArticleHtmlParser
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.AtomFeedParser
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.KtXmlRssFeedParser
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.RdfFeedParser
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.RssFeedParser
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.opml.OpmlParser
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RssFeedParserTest {

    private val articleHtmlParser = ArticleHtmlParser()

    @Test
    fun `Given RSS with optional malformed values When parsed Then usable fields are extracted`() = runTest {
        val parser = ktXmlParser()

        val result = parser.parse(rssFixture.iterator())
        val items = result.items.toList()

        assertEquals("Example Feed", result.metadata.title)
        assertEquals("https://example.com", result.metadata.link)
        assertEquals("Feed description", result.metadata.description)
        assertEquals("https://example.com/feed.png", result.metadata.image?.url)
        assertEquals(2, items.size)
        assertEquals("Post title", items[0].title)
        assertEquals("https://example.com/post", items[0].link)
        assertEquals("Summary", items[0].description)
        assertEquals("Full content", items[0].content)
        assertEquals("https://example.com/audio.mp3", items[0].audio)
        assertEquals("https://example.com/image.jpg", items[0].image)
        assertEquals("Kotlin", items[0].categories.first())
        assertNull(items[1].title)
        assertEquals("https://example.com/minimal", items[1].link)
    }

    @Test
    fun `Given RSS feed When parsed Then item XML is consumed by item flow`() = runTest {
        val parser = ktXmlParser()
        val chars = CountingCharIterator(rssFixture)

        val result = parser.parse(chars)
        val consumedBeforeItems = chars.consumed

        assertEquals("Example Feed", result.metadata.title)
        assertTrue(consumedBeforeItems < rssFixture.length)

        val firstItem = result.items.toList().first()

        assertEquals("Post title", firstItem.title)
        assertTrue(chars.consumed > consumedBeforeItems)
    }

    @Test
    fun `Given RSS with content encoded HTML When parsed Then hero image is extracted from content`() = runTest {
        val parser = ktXmlParser()

        val result = parser.parse(rssHtmlFixture.iterator())
        val items = result.items.toList()

        assertEquals(1, items.size)
        assertEquals("HTML Post", items[0].title)
        assertEquals("https://example.com/html-post", items[0].link)
        assertEquals("https://example.com/hero.jpg", items[0].image)
    }

    @Test
    fun `Given RSS with content encoded HTML and no other image When parsed Then hero image extracted from description HTML`() = runTest {
        val parser = ktXmlParser()

        val result = parser.parse(rssDescriptionHtmlFixture.iterator())
        val items = result.items.toList()

        assertEquals(1, items.size)
        assertEquals("Desc HTML Post", items[0].title)
        assertEquals("https://example.com/desc-image.png", items[0].image)
    }

    @Test
    fun `Given Atom feed When parsed Then entries are normalized`() = runTest {
        val parser = ktXmlParser()

        val result = parser.parse(atomFixture.iterator())
        val item = result.items.toList().single()

        assertEquals("Atom Feed", result.metadata.title)
        assertEquals("https://example.com", result.metadata.link)
        assertEquals("Atom subtitle", result.metadata.description)
        assertEquals("2026-05-19T10:00:00Z", result.metadata.lastBuildDate)
        assertEquals("https://example.com/icon.png", result.metadata.image?.url)
        assertEquals("atom-entry-1", item.guid)
        assertEquals("Atom title", item.title)
        assertEquals("https://example.com/atom-post", item.link)
        assertEquals("2026-05-18T10:00:00Z", item.pubDate)
        assertEquals("Author Name", item.author)
        assertEquals("Atom summary", item.description)
        assertEquals("Atom content", item.content)
        assertEquals("Kotlin", item.categories.single())
        assertEquals("https://example.com/audio.mp3", item.audio)
    }

    @Test
    fun `Given Atom feed When parsed Then item XML is consumed by item flow`() = runTest {
        val parser = ktXmlParser()
        val chars = CountingCharIterator(atomFixture)

        val result = parser.parse(chars)
        val consumedBeforeItems = chars.consumed

        assertEquals("Atom Feed", result.metadata.title)
        assertTrue(consumedBeforeItems < atomFixture.length)

        val item = result.items.toList().single()

        assertEquals("Atom title", item.title)
        assertTrue(chars.consumed > consumedBeforeItems)
    }

    @Test
    fun `Given Atom feed with HTML content When parsed Then hero image is extracted from content`() = runTest {
        val parser = ktXmlParser()

        val result = parser.parse(atomHtmlFixture.iterator())
        val item = result.items.toList().single()

        assertEquals("Atom HTML Entry", item.title)
        assertEquals("https://example.com/atom-hero.jpg", item.image)
    }

    @Test
    fun `Given RDF feed When parsed Then channel and items are normalized`() = runTest {
        val parser = ktXmlParser()

        val result = parser.parse(rdfFixture.iterator())
        val item = result.items.toList().single()

        assertEquals("RDF Feed", result.metadata.title)
        assertEquals("https://example.com/rdf", result.metadata.link)
        assertEquals("RDF description", result.metadata.description)
        assertEquals("https://example.com/rdf.png", result.metadata.image?.url)
        assertEquals("RDF title", item.title)
        assertEquals("https://example.com/rdf-post", item.link)
        assertEquals("RDF summary", item.description)
        assertEquals("RDF content", item.content)
        assertEquals("2026-05-19T10:00:00Z", item.pubDate)
    }

    @Test
    fun `Given RDF feed When parsed Then item XML is consumed by item flow`() = runTest {
        val parser = ktXmlParser()
        val chars = CountingCharIterator(rdfFixture)

        val result = parser.parse(chars)
        val consumedBeforeItems = chars.consumed

        assertEquals("RDF Feed", result.metadata.title)
        assertTrue(consumedBeforeItems < rdfFixture.length)

        val item = result.items.toList().single()

        assertEquals("RDF title", item.title)
        assertTrue(chars.consumed > consumedBeforeItems)
    }

    @Test
    fun `Given RDF feed with HTML content When parsed Then hero image is extracted from content`() = runTest {
        val parser = ktXmlParser()

        val result = parser.parse(rdfHtmlFixture.iterator())
        val item = result.items.toList().single()

        assertEquals("RDF HTML Item", item.title)
        assertEquals("https://example.com/rdf-hero.jpg", item.image)
    }

    @Test
    fun `Given unsupported XML root When parsed Then exception is thrown`() = runTest {
        val parser = ktXmlParser()

        assertFailsWith<IllegalArgumentException> {
            parser.parse("<opml />".iterator())
        }
    }

    @Test
    fun `Given OPML text When parsed Then feed URLs are preserved`() {
        val result = assertNotNull(opmlParser().parse(opmlFixture.iterator()))

        assertEquals("Subscriptions", result.title)
        assertEquals(2, result.feeds.size)
        assertEquals("https://example.com/feed.xml", result.feeds[0].xmlUrl)
        assertEquals("Loose", result.feeds[1].title)
    }

    @Test
    fun `Given OPML with URL attribute case variants When parsed Then feeds preserve URLs`() {
        val result = assertNotNull(opmlParser().parse(opmlAttributeCaseFixture.iterator()))

        assertEquals(2, result.feeds.size)
        assertEquals("https://example.com/lower.xml", result.feeds[0].xmlUrl)
        assertEquals("https://example.com/lower", result.feeds[0].htmlUrl)
        assertEquals("https://example.com/camel.xml", result.feeds[1].xmlUrl)
        assertEquals("https://example.com/camel", result.feeds[1].htmlUrl)
    }

    @Test
    fun `Given Plenary OPML with category outline When parsed Then nested feed URLs are preserved`() {
        val result = assertNotNull(opmlParser().parse(plenaryCategoryFixture.iterator()))

        assertEquals("Android Development", result.title)
        assertEquals(2, result.feeds.size)
        assertEquals("https://androidweekly.net/issues?format=rss&category=android", result.feeds.first().xmlUrl)
    }

    @Test
    fun `Given unsupported XML root When parsed Then null is returned`() {
        val result = opmlParser().parse("<rss><channel /></rss>".iterator())

        assertNull(result)
    }

    private class CountingCharIterator(
        private val value: String
    ) : CharIterator() {
        var consumed = 0
            private set

        override fun hasNext(): Boolean {
            return consumed < value.length
        }

        override fun nextChar(): Char {
            if (!hasNext()) throw NoSuchElementException()
            val char = value[consumed]
            consumed += 1
            return char
        }
    }

    private fun opmlParser(): OpmlParser {
        return OpmlParser()
    }

    private fun ktXmlParser(): KtXmlRssFeedParser {
        return KtXmlRssFeedParser(
            rssFeedParser = RssFeedParser(articleHtmlParser),
            rdfFeedParser = RdfFeedParser(articleHtmlParser),
            atomFeedParser = AtomFeedParser(articleHtmlParser)
        )
    }

    private val rssFixture = """
        <rss xmlns:content="http://purl.org/rss/1.0/modules/content/"
             xmlns:media="http://search.yahoo.com/mrss/"
             xmlns:yt="http://www.youtube.com/xml/schemas/2015">
            <channel>
                <title>Example Feed</title>
                <link>https://example.com</link>
                <description>Feed description</description>
                <image>
                    <url>https://example.com/feed.png</url>
                    <title>Feed image</title>
                    <link>https://example.com</link>
                    <description>Image description</description>
                </image>
                <lastBuildDate>Tue, 19 May 2026 10:00:00 GMT</lastBuildDate>
                <item>
                    <guid>guid-1</guid>
                    <title>Post title</title>
                    <author>Author</author>
                    <link>https://example.com/post</link>
                    <pubDate>Tue, 19 May 2026 10:00:00 GMT</pubDate>
                    <description>Summary</description>
                    <content:encoded>Full content</content:encoded>
                    <category>Kotlin</category>
                    <enclosure url="https://example.com/audio.mp3" length="42" type="audio/mpeg" />
                    <media:content url="https://example.com/image.jpg" medium="image" />
                </item>
                <item>
                    <link>https://example.com/minimal</link>
                    <description></description>
                </item>
            </channel>
        </rss>
    """.trimIndent()

    private val rssHtmlFixture = """
        <rss xmlns:content="http://purl.org/rss/1.0/modules/content/">
            <channel>
                <title>HTML Feed</title>
                <link>https://example.com</link>
                <description>Feed with HTML content</description>
                <item>
                    <guid>html-guid-1</guid>
                    <title>HTML Post</title>
                    <link>https://example.com/html-post</link>
                    <description>Plain text summary</description>
                    <content:encoded><![CDATA[<p>Some intro text</p><img src="https://example.com/hero.jpg" alt="Hero" /><p>More text</p>]]></content:encoded>
                </item>
            </channel>
        </rss>
    """.trimIndent()

    private val rssDescriptionHtmlFixture = """
        <rss xmlns:content="http://purl.org/rss/1.0/modules/content/">
            <channel>
                <title>Desc HTML Feed</title>
                <link>https://example.com</link>
                <description>Feed with HTML description</description>
                <item>
                    <guid>desc-html-guid-1</guid>
                    <title>Desc HTML Post</title>
                    <link>https://example.com/desc-html-post</link>
                    <description><![CDATA[<img src="https://example.com/desc-image.png" alt="Desc" />Some text]]></description>
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
            <icon>https://example.com/icon.png</icon>
            <entry>
                <id>atom-entry-1</id>
                <title>Atom title</title>
                <author><name>Author Name</name></author>
                <link rel="alternate" href="https://example.com/atom-post" />
                <link rel="enclosure" href="https://example.com/audio.mp3" type="audio/mpeg" />
                <published>2026-05-18T10:00:00Z</published>
                <updated>2026-05-19T10:00:00Z</updated>
                <summary>Atom summary</summary>
                <content>Atom content</content>
                <category term="Kotlin" />
            </entry>
        </feed>
    """.trimIndent()

    private val atomHtmlFixture = """
        <feed xmlns="http://www.w3.org/2005/Atom">
            <title>Atom HTML Feed</title>
            <link rel="alternate" href="https://example.com" />
            <updated>2026-05-19T10:00:00Z</updated>
            <entry>
                <id>atom-html-1</id>
                <title>Atom HTML Entry</title>
                <link rel="alternate" href="https://example.com/atom-html-post" />
                <published>2026-05-18T10:00:00Z</published>
                <summary>Plain summary</summary>
                <content type="html"><![CDATA[<p>Intro</p><img src="https://example.com/atom-hero.jpg" /><p>Body</p>]]></content>
            </entry>
        </feed>
    """.trimIndent()

    private val rdfFixture = """
        <rdf:RDF
            xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            xmlns:content="http://purl.org/rss/1.0/modules/content/"
            xmlns:dc="http://purl.org/dc/elements/1.1/">
            <channel rdf:about="https://example.com/rdf">
                <title>RDF Feed</title>
                <link>https://example.com/rdf</link>
                <description>RDF description</description>
                <image rdf:resource="https://example.com/rdf.png" />
            </channel>
            <item rdf:about="https://example.com/rdf-post">
                <title>RDF title</title>
                <link>https://example.com/rdf-post</link>
                <description>RDF summary</description>
                <content:encoded>RDF content</content:encoded>
                <dc:date>2026-05-19T10:00:00Z</dc:date>
            </item>
        </rdf:RDF>
    """.trimIndent()

    private val rdfHtmlFixture = """
        <rdf:RDF
            xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            xmlns:content="http://purl.org/rss/1.0/modules/content/"
            xmlns:dc="http://purl.org/dc/elements/1.1/">
            <channel rdf:about="https://example.com/rdf-html">
                <title>RDF HTML Feed</title>
                <link>https://example.com/rdf-html</link>
                <description>RDF HTML description</description>
            </channel>
            <item rdf:about="https://example.com/rdf-html-post">
                <title>RDF HTML Item</title>
                <link>https://example.com/rdf-html-post</link>
                <description>Plain summary</description>
                <content:encoded><![CDATA[<p>Intro</p><img src="https://example.com/rdf-hero.jpg" /><p>Body</p>]]></content:encoded>
                <dc:date>2026-05-19T10:00:00Z</dc:date>
            </item>
        </rdf:RDF>
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

    private val plenaryCategoryFixture = """
        <?xml version="1.0" encoding="utf-8"?>
        <opml version="2.0">
            <head><title>Android Development</title></head>
            <body>
                <outline text="Android Development">
                    <outline
                        text="Android Weekly"
                        title="Android Weekly"
                        type="rss"
                        xmlUrl="https://androidweekly.net/issues?format=rss&amp;category=android"
                        htmlUrl="https://androidweekly.net"
                    />
                    <outline
                        text="Kotlin Blog"
                        title="Kotlin Blog"
                        type="rss"
                        xmlUrl="https://blog.jetbrains.com/kotlin/feed/"
                    />
                </outline>
            </body>
        </opml>
    """.trimIndent()

    private val opmlAttributeCaseFixture = """
        <opml version="2.0">
            <body>
                <outline text="Lower" xmlurl="https://example.com/lower.xml" htmlurl="https://example.com/lower" />
                <outline text="Camel" xmlUrl="https://example.com/camel.xml" htmlUrl="https://example.com/camel" />
            </body>
        </opml>
    """.trimIndent()
}