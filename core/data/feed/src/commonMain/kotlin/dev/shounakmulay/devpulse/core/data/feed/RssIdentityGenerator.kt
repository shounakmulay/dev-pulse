package dev.shounakmulay.devpulse.core.data.feed

import okio.ByteString.Companion.encodeUtf8
import org.koin.core.annotation.Factory
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Factory(binds = [IdentityGenerator::class])
class RssIdentityGenerator : IdentityGenerator {
    @OptIn(ExperimentalUuidApi::class)
    override fun generateSortableId(): String {
        return Uuid.Companion.generateV7().toString()
    }

    override fun generateHash(string: String): String {
        val encoded = string.encodeUtf8()
        return encoded.sha256().toString()
    }

}