package dev.shounakmulay.devpulse.core.data.feed

import okio.ByteString.Companion.encodeUtf8
import org.koin.core.annotation.Factory
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Factory(binds = [IdentityGenerator::class])
class RssIdentityGenerator : IdentityGenerator {
    @OptIn(ExperimentalUuidApi::class)
    override fun generateSortableId(): String {
        return Uuid.generateV7().toString()
    }

    override fun generateHash(vararg strings: String): String {
        val stringToHash = strings.joinToString(separator = "-")
        val encoded = stringToHash.encodeUtf8()
        return encoded.sha256().toString()
    }

}