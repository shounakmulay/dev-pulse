package dev.shounakmulay.devpulse.core.data.db.converter

import okio.Buffer
import okio.GzipSink
import okio.GzipSource
import okio.buffer
import okio.use
import org.koin.core.annotation.Single

@Single
class GzipCompressor {
    
    /**
     * Converts a raw String (HTML/Markdown/JSON) into a Gzipped ByteArray.
     */
    fun compress(text: String): ByteArray {
        val buffer = Buffer()
        GzipSink(buffer).buffer().use { sink ->
            sink.writeUtf8(text)
        }
        return buffer.readByteArray()
    }

    /**
     * Decompresses a Gzipped ByteArray back into its original raw String.
     */
    fun decompress(bytes: ByteArray): String {
        val buffer = Buffer().write(bytes)
        return GzipSource(buffer).buffer().use { source ->
            source.readUtf8()
        }
    }
}