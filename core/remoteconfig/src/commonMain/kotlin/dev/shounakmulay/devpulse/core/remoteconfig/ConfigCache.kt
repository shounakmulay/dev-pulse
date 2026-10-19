package dev.shounakmulay.devpulse.core.remoteconfig

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class CachedConfigEntry(
    val value: ConfigValue,
    val revision: String,
    val fetchedAtMillis: Long,
    val expiresAtMillis: Long,
)

@Serializable
internal data class CachedConfig(
    val formatVersion: Int = 1,
    val schemaVersion: Int,
    val context: ConfigContext,
    val active: Map<String, CachedConfigEntry>,
    val previous: Map<String, CachedConfigEntry>,
    val pending: Map<String, CachedConfigEntry>,
)

internal object ConfigCacheCodec {
    const val MAX_BYTES = 262_144
    private val json = Json

    fun encode(cache: CachedConfig): String = json.encodeToString(cache).also {
        require(it.encodeToByteArray().size <= MAX_BYTES) { "Config cache exceeds size limit" }
    }

    fun decode(value: String): CachedConfig {
        require(value.length <= MAX_BYTES && value.encodeToByteArray().size <= MAX_BYTES)
        return json.decodeFromString<CachedConfig>(value)
    }
}
