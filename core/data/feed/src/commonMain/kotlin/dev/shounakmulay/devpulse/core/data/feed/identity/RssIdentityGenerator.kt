package dev.shounakmulay.devpulse.core.data.feed.identity

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import okio.ByteString.Companion.encodeUtf8
import org.koin.core.annotation.Factory
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Factory(binds = [IdentityGenerator::class])
class RssIdentityGenerator : IdentityGenerator {
    @OptIn(ExperimentalUuidApi::class)
    override fun generateSortableId(): UUID {
        return UUID(Uuid.generateV7().toString())
    }

    override fun generateFingerprint(vararg strings: String): String {
        require(strings.isNotEmpty()) { "Fingerprint cannot be generated without any source values." }
        val stringToHash = strings.joinToString(separator = "-")
        val encoded = stringToHash.encodeUtf8()
        return encoded.sha256().toString()
    }
}