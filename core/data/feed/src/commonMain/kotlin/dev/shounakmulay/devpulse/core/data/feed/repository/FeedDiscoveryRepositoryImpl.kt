package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDiscoveryCollectionDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDiscoveryCollectionToFeedMappingDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDiscoveryFeedMetadataDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDiscoveryMasterMetadataDao
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalFeedDiscoveryCollectionHashSlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalFeedDiscoveryCollectionPositionSlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryCollection
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryCollectionToFeedMapping
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryFeedMetadata
import dev.shounakmulay.devpulse.core.data.db.paging.FeedDiscoveryPagingSourceProvider
import dev.shounakmulay.devpulse.core.data.db.transaction.DevPulseDatabaseTransactionAccessor
import dev.shounakmulay.devpulse.core.data.feed.BuildKonfig
import dev.shounakmulay.devpulse.core.data.feed.mapper.FeedDiscoveryMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.FeedDiscoverySeedMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RemoteDiscoveryMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.UuidMapper
import dev.shounakmulay.devpulse.core.data.feed.model.discovery.RemoteDiscoveryCatalog
import dev.shounakmulay.devpulse.core.data.feed.model.discovery.RemoteDiscoveryCollection
import dev.shounakmulay.devpulse.core.data.feed.model.discovery.RemoteDiscoveryCollectionMetadata
import dev.shounakmulay.devpulse.core.data.feed.model.discoveryseed.DiscoverySeedData
import dev.shounakmulay.devpulse.core.data.feed.parser.discoveryseed.DiscoverySeedParser
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryCollection
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryCollectionShelf
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryQuery
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryFeed
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkResponse
import dev.shounakmulay.devpulse.core.network.HttpHeaders
import dev.shounakmulay.devpulse.core.network.HttpStatusCodes
import dev.shounakmulay.devpulse.core.network.body
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.koin.core.annotation.Factory

@Factory
class FeedDiscoveryRepositoryImpl(
    private val discoverySeedParser: DiscoverySeedParser,
    private val feedDiscoveryMasterMetadataDao: FeedDiscoveryMasterMetadataDao,
    private val feedDiscoveryCollectionDao: FeedDiscoveryCollectionDao,
    private val feedDiscoveryCollectionToFeedMappingDao: FeedDiscoveryCollectionToFeedMappingDao,
    private val feedDiscoveryFeedMetadataDao: FeedDiscoveryFeedMetadataDao,
    private val feedDiscoverySeedMapper: FeedDiscoverySeedMapper,
    private val remoteDiscoveryMapper: RemoteDiscoveryMapper,
    private val transactionAccessor: DevPulseDatabaseTransactionAccessor,
    private val devPulseNetworkClient: DevPulseNetworkClient,
    private val pagingSourceProvider: FeedDiscoveryPagingSourceProvider,
    private val feedDiscoveryMapper: FeedDiscoveryMapper,
    private val uuidMapper: UuidMapper,
    logger: DPLogger
) : FeedDiscoveryRepository {

    companion object {
        private const val TAG = "FeedDiscoveryRepositoryImpl"

        private val MASTER_URL = "${BuildKonfig.FEED_DISCOVERY_URL}/master.json"

        private fun getCollectionUrl(path: String) = "${BuildKonfig.FEED_DISCOVERY_URL}/$path"
    }

    private val log = logger.withTag(TAG)

    override fun observeCollectionShelves(previewCount: Int): Flow<List<FeedDiscoveryCollectionShelf>> {
        log.d { "Observing discovery shelves previewCount=$previewCount" }
        return feedDiscoveryCollectionDao.observeCollectionShelves(previewCount).map { rows ->
            log.d { "Discovery shelves updated collections=${rows.map { it.collection.id }.distinct().size} rows=${rows.size}" }
            rows.groupBy { it.collection }.map { (collection, entries) ->
                FeedDiscoveryCollectionShelf(
                    collection = feedDiscoveryMapper.toFeedDiscoveryCollection(collection),
                    feeds = entries.mapNotNull { it.feed }.map(feedDiscoveryMapper::toFeedDiscoveryFeed),
                )
            }
        }.onCompletion { cause ->
            if (cause != null && cause !is CancellationException) {
                log.e(cause) { "Discovery shelves observation failed" }
            }
        }
    }

    override fun observeCollection(collectionId: UUID): Flow<FeedDiscoveryCollection?> {
        log.d { "Observing discovery collectionId=$collectionId" }
        return feedDiscoveryCollectionDao.observeCollection(uuidMapper.fromUuid(collectionId)).map {
            log.d { "Discovery collectionId=$collectionId found=${it != null}" }
            it?.let(feedDiscoveryMapper::toFeedDiscoveryCollection)
        }.onCompletion { cause ->
            if (cause != null && cause !is CancellationException) {
                log.e(cause) { "Discovery observation failed collectionId=$collectionId" }
            }
        }
    }

    override fun observeFeed(feedId: UUID): Flow<FeedDiscoveryFeed?> {
        log.d { "Observing discovery feedId=$feedId" }
        return feedDiscoveryFeedMetadataDao.observeFeed(uuidMapper.fromUuid(feedId)).map {
            log.d { "Discovery feedId=$feedId found=${it != null} importStatus=${it?.importStatus} subscribed=${it?.subscribed}" }
            it?.let(feedDiscoveryMapper::toFeedDiscoveryFeed)
        }.onCompletion { cause ->
            if (cause != null && cause !is CancellationException) {
                log.e(cause) { "Discovery observation failed feedId=$feedId" }
            }
        }
    }

    override fun observeFeeds(
        query: FeedDiscoveryQuery,
        pagingConfig: PagingConfig,
    ): Flow<PagingData<FeedDiscoveryFeed>> {
        val localQuery = feedDiscoveryMapper.toLocalFeedDiscoveryQuery(query)
            ?: return flowOf(PagingData.empty())
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                log.d { "Creating discovery paging source query=$query" }
                pagingSourceProvider.getFeedDiscoveryPagingSource(localQuery)
            },
        ).flow.map { pagingData -> pagingData.map(feedDiscoveryMapper::toFeedDiscoveryFeed) }
    }

    override suspend fun isFeedDiscoveryInitialised(): Boolean {
        return feedDiscoveryMasterMetadataDao.hasMasterMetadata()
    }

    override suspend fun getDiscoverySeed(seedFilePath: String): DiscoverySeedData {
        return discoverySeedParser.parse(seedFilePath)
    }

    override suspend fun initialiseWithSeed(seed: DiscoverySeedData) {
        if (feedDiscoveryMasterMetadataDao.hasMasterMetadata()) {
            log.d { "Skipping discovery seed installation: catalog already initialised" }
            return
        }
        log.i { "Preparing discovery seed collections=${seed.master.collections.size}" }
        val masterMetadata = feedDiscoverySeedMapper.toLocalFeedDiscoveryMasterMetadata(seed.master)
        val documentsByPath = seed.collections.associate { it.path to it.document }
        val documents = seed.master.collections.map { documentsByPath.getValue(it.path) }
        val collections = seed.master.collections.map { metadata ->
            feedDiscoverySeedMapper.toLocalFeedDiscoveryCollection(
                from = metadata,
                document = documentsByPath.getValue(metadata.path),
                catalogId = masterMetadata.catalogId
            )
        }
        val feeds = documents.flatMap { it.feeds }
            .distinctBy { it.id }
            .map { feedDiscoverySeedMapper.toLocalFeedDiscoveryFeedMetadata(it) }
        val mappings = documents.flatMap {
            feedDiscoverySeedMapper.toLocalFeedDiscoveryCollectionToFeedMappings(it)
        }

        transactionAccessor.writeTransaction {
            if (feedDiscoveryMasterMetadataDao.hasMasterMetadata()) {
                log.d { "Skipping discovery seed transaction: catalog initialised concurrently" }
                return@writeTransaction
            }
            log.d { "Writing discovery seed collections=${collections.size} feeds=${feeds.size} mappings=${mappings.size}" }

            feedDiscoveryMasterMetadataDao.upsert(masterMetadata)
            feedDiscoveryCollectionDao.upsert(collections)
            feedDiscoveryFeedMetadataDao.upsert(feeds)
            feedDiscoveryCollectionToFeedMappingDao.upsert(mappings)
        }
        log.i { "Discovery seed transaction completed" }
    }

    override suspend fun refresh() {
        log.i { "Refreshing discovery catalog" }
        val etag = feedDiscoveryMasterMetadataDao.getEtag()
        log.d { "Fetching discovery master hasEtag=${etag != null}" }
        val masterResponse = fetchMasterJson(etag)
        val statusCode = masterResponse.status

        log.d { "Discovery master response status=$statusCode" }
        if (statusCode == HttpStatusCodes.NOT_MODIFIED) {
            log.i { "Discovery catalog unchanged" }
            return
        }

        val newEtag = requireNotNull(masterResponse.header(HttpHeaders.ETAG))

        val remoteDiscoveryCatalog = masterResponse.body<RemoteDiscoveryCatalog>()
        val collectionsToProcess = getCollectionsToProcessForRefresh(remoteDiscoveryCatalog)
        log.d {
            "Discovery refresh changes new=${collectionsToProcess.newCollections.size} " +
                "updated=${collectionsToProcess.collectionToFetch.size - collectionsToProcess.newCollections.size} " +
                "deleted=${collectionsToProcess.deletedCollections.size}"
        }
        val collectionsToSave = fetchCollections(collectionsToProcess)
        val localCollections = getLocalFeedDiscoveryCollectionForRefresh(
            collectionsToSave = collectionsToSave,
            collectionsToProcess = collectionsToProcess,
            remoteDiscoveryCatalog = remoteDiscoveryCatalog
        )
        val localFeeds = getLocalFeedMetadataForRefresh(collectionsToSave)
        val localMappings = getLocalCollectionToFeedMappingForRefresh(collectionsToSave)

        log.d { "Persisting discovery refresh collections=${localCollections.size} feeds=${localFeeds.size} mappings=${localMappings.size}" }
        persistDiscoveryRefreshData(
            remoteDiscoveryCatalog = remoteDiscoveryCatalog,
            newEtag = newEtag,
            collectionsToProcess = collectionsToProcess,
            localCollections = localCollections,
            localFeeds = localFeeds,
            collectionsToSave = collectionsToSave,
            localMappings = localMappings
        )
        log.i { "Discovery catalog refresh committed" }
    }

    private suspend fun persistDiscoveryRefreshData(
        remoteDiscoveryCatalog: RemoteDiscoveryCatalog,
        newEtag: String,
        collectionsToProcess: CollectionsToProcess,
        localCollections: List<LocalFeedDiscoveryCollection>,
        localFeeds: List<LocalFeedDiscoveryFeedMetadata>,
        collectionsToSave: List<RemoteDiscoveryCollection>,
        localMappings: List<LocalFeedDiscoveryCollectionToFeedMapping>
    ) {
        transactionAccessor.writeTransaction {
            feedDiscoveryMasterMetadataDao.upsert(
                remoteDiscoveryMapper.toLocalFeedDiscoveryMasterMetadata(
                    from = remoteDiscoveryCatalog,
                    etag = newEtag
                )
            )

            feedDiscoveryCollectionDao.delete(collectionsToProcess.deletedCollections.keys.toList())
            feedDiscoveryCollectionDao.updatePosition(collectionsToProcess.collectionPositionUpdates)
            feedDiscoveryCollectionDao.upsert(localCollections)

            feedDiscoveryFeedMetadataDao.upsert(localFeeds)

            feedDiscoveryCollectionToFeedMappingDao.delete(collectionsToSave.map { it.id })
            feedDiscoveryCollectionToFeedMappingDao.upsert(localMappings)
        }
    }

    private fun getLocalCollectionToFeedMappingForRefresh(collectionsToSave: List<RemoteDiscoveryCollection>): List<LocalFeedDiscoveryCollectionToFeedMapping> =
        collectionsToSave.flatMap { collection ->
            remoteDiscoveryMapper.toLocalFeedDiscoveryCollectionToFeedMappings(from = collection)
        }

    private fun getLocalFeedMetadataForRefresh(collectionsToSave: List<RemoteDiscoveryCollection>): List<LocalFeedDiscoveryFeedMetadata> =
        collectionsToSave.flatMap { it.feeds }.map {
            remoteDiscoveryMapper.toLocalFeedDiscoveryFeedMetadata(it)
        }

    private fun getLocalFeedDiscoveryCollectionForRefresh(
        collectionsToSave: List<RemoteDiscoveryCollection>,
        collectionsToProcess: CollectionsToProcess,
        remoteDiscoveryCatalog: RemoteDiscoveryCatalog
    ): List<LocalFeedDiscoveryCollection> {
        val collectionMetadata =
            collectionsToProcess.collectionToFetch.associateBy { LocalUUID(it.id) }
        return collectionsToSave.mapNotNull { collection ->
            val uuid = LocalUUID(collection.id)
            val collectionMetadata = collectionMetadata[uuid] ?: return@mapNotNull null
            remoteDiscoveryMapper.toLocalFeedDiscoveryCollection(
                from = collectionMetadata,
                document = collection,
                catalogId = LocalUUID(remoteDiscoveryCatalog.catalogId)
            )
        }
    }

    private suspend fun fetchCollections(
        collectionsToProcess: CollectionsToProcess,
    ): List<RemoteDiscoveryCollection> = coroutineScope {
        val fetchFeedSemaphore = Semaphore(10)
        collectionsToProcess.collectionToFetch.map {
            async {
                fetchFeedSemaphore.withPermit {
                    fetchDiscoveryFeed(it.path)
                }
            }
        }.awaitAll()
    }

    private suspend fun getCollectionsToProcessForRefresh(
        remoteDiscoveryCatalog: RemoteDiscoveryCatalog
    ): CollectionsToProcess {
        val existingCollectionHashes = feedDiscoveryCollectionDao.getHashes().associateBy { it.id }
        val newCollections = remoteDiscoveryCatalog.collections.associateBy { LocalUUID(it.id) }
        val collectionPositionUpdates = remoteDiscoveryCatalog.collections.map {
            LocalFeedDiscoveryCollectionPositionSlice(
                id = LocalUUID(it.id),
                position = it.position
            )
        }

        val deletedCollections = existingCollectionHashes.filter {
            it.key !in newCollections
        }
        val newCollectionsToAdd = newCollections.filter {
            it.key !in existingCollectionHashes
        }
        val updatedCollections = newCollections.filter { (id, metadata) ->
            val existing = existingCollectionHashes[id]
            existing != null && metadata.sha256 != existing.sha256Hash
        }
        val collectionToFetch = newCollectionsToAdd.values + updatedCollections.values
        return CollectionsToProcess(
            collectionToFetch = collectionToFetch,
            deletedCollections = deletedCollections,
            newCollections = newCollectionsToAdd,
            collectionPositionUpdates = collectionPositionUpdates
        )
    }

    private suspend fun fetchMasterJson(etag: String?): DevPulseNetworkResponse =
        devPulseNetworkClient.get(
            url = MASTER_URL,
            headers = buildMap {
                if (etag != null) {
                    put(HttpHeaders.IF_NONE_MATCH, etag)
                }
            }
        )

    private suspend fun fetchDiscoveryFeed(path: String): RemoteDiscoveryCollection {
        log.d { "Fetching discovery collection path=$path" }
        val collectionResponse = devPulseNetworkClient.get(
            url = getCollectionUrl(path)
        )
        log.d { "Discovery collection response path=$path status=${collectionResponse.status}" }
        return collectionResponse.body<RemoteDiscoveryCollection>().also {
            log.d { "Fetched discovery collection path=$path feeds=${it.feeds.size}" }
        }
    }

    private data class CollectionsToProcess(
        val collectionToFetch: List<RemoteDiscoveryCollectionMetadata>,
        val deletedCollections: Map<LocalUUID, LocalFeedDiscoveryCollectionHashSlice>,
        val newCollections: Map<LocalUUID, RemoteDiscoveryCollectionMetadata>,
        val collectionPositionUpdates: List<LocalFeedDiscoveryCollectionPositionSlice>,
    )
}
