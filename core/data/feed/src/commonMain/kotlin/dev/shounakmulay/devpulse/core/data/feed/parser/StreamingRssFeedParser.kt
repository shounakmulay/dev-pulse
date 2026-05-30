package dev.shounakmulay.devpulse.core.data.feed.parser

import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeed
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.koin.core.annotation.Factory

@Factory(binds = [FeedParser::class])
internal class StreamingRssFeedParser(
    private val httpClient: HttpClient,
    private val xmlParser: KtXmlFeedParser
) : FeedParser {
    override suspend fun parseFeed(url: String): ParsedFeed {
        val chars = Channel<Char>(capacity = ChannelCapacity)
        val producer = CoroutineScope(currentCoroutineContext()).launch(Dispatchers.Default) {
            runCatching {
                httpClient.prepareGet(url).execute { response ->
                    response.bodyAsChannel().streamUtf8To(chars)
                }
            }.onFailure { throwable ->
                chars.close(throwable)
            }.onSuccess {
                chars.close()
            }
        }
        val iterator = ChannelCharIterator(chars)
        return try {
            val parsedFeed = xmlParser.parse(sourceUrl = url, chars = iterator)
            iterator.failure?.let { throwable -> throw throwable }
            parsedFeed.copy(
                items = parsedFeed.items.onCompletion {
                    producer.cancelAndJoin()
                }
            )
        } catch (throwable: Throwable) {
            producer.cancelAndJoin()
            throw throwable
        }
    }

    override fun parseText(sourceUrl: String?, xml: String): ParsedFeed {
        return xmlParser.parseText(sourceUrl = sourceUrl, xml = xml)
    }

    private suspend fun ByteReadChannel.streamUtf8To(chars: Channel<Char>) {
        val decoder = Utf8CharDecoder(chars)
        val buffer = ByteArray(BufferSize)
        var closed = false
        while (!closed) {
            val count = readAvailable(dst = buffer, offset = 0, length = buffer.size)
            when {
                count > 0 -> decoder.accept(buffer = buffer, count = count)
                count == 0 -> yield()
                else -> closed = true
            }
        }
        decoder.finish()
    }

    private class ChannelCharIterator(
        private val chars: Channel<Char>
    ) : CharIterator() {
        private var nextValue: Char? = null
        private var completed = false
        var failure: Throwable? = null
            private set

        override fun hasNext(): Boolean {
            if (nextValue != null) return true
            if (completed) return false
            val result = runBlocking { chars.receiveCatching() }
            result.exceptionOrNull()?.let { throwable ->
                failure = throwable
                throw IllegalStateException(throwable)
            }
            val value = result.getOrNull()
            if (value == null) {
                completed = true
                return false
            }
            nextValue = value
            return true
        }

        override fun nextChar(): Char {
            if (!hasNext()) throw NoSuchElementException()
            val value = checkNotNull(nextValue)
            nextValue = null
            return value
        }
    }

    private class Utf8CharDecoder(
        private val chars: Channel<Char>
    ) {
        private var codePoint = 0
        private var remaining = 0

        suspend fun accept(buffer: ByteArray, count: Int) {
            for (index in 0 until count) {
                acceptByte(buffer[index].toInt() and 0xFF)
            }
        }

        suspend fun finish() {
            if (remaining > 0) {
                chars.send(ReplacementCharacter)
                remaining = 0
                codePoint = 0
            }
        }

        private suspend fun acceptByte(byte: Int) {
            if (remaining == 0) {
                acceptLeadingByte(byte)
                return
            }
            if ((byte and ContinuationMask) == ContinuationPrefix) {
                codePoint = (codePoint shl 6) or (byte and PayloadMask)
                remaining -= 1
                if (remaining == 0) sendCodePoint()
            } else {
                chars.send(ReplacementCharacter)
                codePoint = 0
                remaining = 0
                acceptLeadingByte(byte)
            }
        }

        private suspend fun acceptLeadingByte(byte: Int) {
            when {
                byte < TwoBytePrefix -> chars.send(byte.toChar())
                byte in TwoBytePrefix until ThreeBytePrefix -> {
                    codePoint = byte and TwoBytePayloadMask
                    remaining = 1
                }
                byte in ThreeBytePrefix until FourBytePrefix -> {
                    codePoint = byte and ThreeBytePayloadMask
                    remaining = 2
                }
                byte in FourBytePrefix until InvalidFourBytePrefix -> {
                    codePoint = byte and FourBytePayloadMask
                    remaining = 3
                }
                else -> chars.send(ReplacementCharacter)
            }
        }

        private suspend fun sendCodePoint() {
            when {
                codePoint <= MaxBasicMultilingualPlane -> chars.send(codePoint.toChar())
                codePoint <= MaxUnicodeCodePoint -> {
                    val normalized = codePoint - SupplementaryPlaneOffset
                    chars.send((HighSurrogateStart + (normalized shr 10)).toChar())
                    chars.send((LowSurrogateStart + (normalized and LowSurrogateMask)).toChar())
                }
                else -> chars.send(ReplacementCharacter)
            }
            codePoint = 0
        }
    }

    private companion object {
        const val BufferSize = 8_192
        const val ChannelCapacity = 8_192
        const val ContinuationMask = 0xC0
        const val ContinuationPrefix = 0x80
        const val PayloadMask = 0x3F
        const val TwoBytePrefix = 0xC0
        const val ThreeBytePrefix = 0xE0
        const val FourBytePrefix = 0xF0
        const val InvalidFourBytePrefix = 0xF5
        const val TwoBytePayloadMask = 0x1F
        const val ThreeBytePayloadMask = 0x0F
        const val FourBytePayloadMask = 0x07
        const val MaxBasicMultilingualPlane = 0xFFFF
        const val MaxUnicodeCodePoint = 0x10FFFF
        const val SupplementaryPlaneOffset = 0x10000
        const val HighSurrogateStart = 0xD800
        const val LowSurrogateStart = 0xDC00
        const val LowSurrogateMask = 0x3FF
        const val ReplacementCharacter = '\uFFFD'
    }
}
