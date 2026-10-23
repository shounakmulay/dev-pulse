package dev.shounakmulay.devpulse.core.data.feed.parser.discoveryseed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.db.converter.GzipCompressor
import dev.shounakmulay.devpulse.core.data.feed.model.discoveryseed.DiscoverySeedData
import dev.shounakmulay.devpulse.core.logging.log.DPLogger
import devpulse.core.resources.generated.resources.Res
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory

@Factory
class DiscoverySeedParser(
    private val gzipCompressor: GzipCompressor,
    private val dispatcherProvider: DispatcherProvider,
    logger: DPLogger,
) {
    private companion object {
        const val TAG = "DiscoverySeedParser"
        const val SEED_SCHEMA_VERSION = 1
        const val MASTER_SCHEMA_VERSION = 1
        const val COLLECTION_SCHEMA_VERSION = 1
    }

    private val log = logger.withTag(TAG)
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun parse(seedFilePath: String): DiscoverySeedData =
        withContext(dispatcherProvider.ioDispatcher) {
            log.d { "Reading discovery seed path=$seedFilePath" }
            val file = Res.readBytes(seedFilePath)
            log.d { "Read discovery seed compressedBytes=${file.size}" }
            val seedJson = gzipCompressor.decompress(file)
            log.d { "Decompressed discovery seed characters=${seedJson.length}" }
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
            log.d { "Validated discovery seed collections=${seed.collections.size} schemaVersion=${seed.schemaVersion}" }
            seed
        }
}
