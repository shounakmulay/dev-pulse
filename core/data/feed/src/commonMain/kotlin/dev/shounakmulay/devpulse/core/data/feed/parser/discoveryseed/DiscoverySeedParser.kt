package dev.shounakmulay.devpulse.core.data.feed.parser.discoveryseed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.db.converter.GzipCompressor
import dev.shounakmulay.devpulse.core.data.feed.model.discoveryseed.DiscoverySeedData
import devpulse.core.resources.generated.resources.Res
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory

@Factory
class DiscoverySeedParser(
    private val gzipCompressor: GzipCompressor,
    private val dispatcherProvider: DispatcherProvider
) {
    private companion object {
        const val SEED_SCHEMA_VERSION = 1
        const val MASTER_SCHEMA_VERSION = 1
        const val COLLECTION_SCHEMA_VERSION = 1
    }

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun parse(seedFilePath: String): DiscoverySeedData =
        withContext(dispatcherProvider.ioDispatcher) {
            val seedJson = gzipCompressor.decompress(Res.readBytes(seedFilePath))
            val seed = json.decodeFromString(
                deserializer = DiscoverySeedData.serializer(),
                string = seedJson
            )
            require(seed.schemaVersion == SEED_SCHEMA_VERSION) {
                "Unsupported discovery seed schema version: ${seed.schemaVersion}"
            }
            require(seed.master.schemaVersion == MASTER_SCHEMA_VERSION) {
                "Unsupported discovery master schema version: ${seed.master.schemaVersion}"
            }
            seed.collections.forEach { collection ->
                require(collection.document.schemaVersion == COLLECTION_SCHEMA_VERSION) {
                    "Unsupported discovery collection schema version: ${collection.document.schemaVersion} at ${collection.path}"
                }
            }
            seed
        }
}
