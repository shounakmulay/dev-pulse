package dev.shounakmulay.devpulse.core.data.db

import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import androidx.room3.withReadTransaction
import androidx.room3.withWriteTransaction
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.db.converter.LocalCompressedTextTypeConverter
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostCategoryDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedQueueDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedSyncMetadataDao
import dev.shounakmulay.devpulse.core.data.db.dao.PostContentDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPostFts
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeedFts
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeedQueue
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeedSyncMetadata
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostCategory
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostContent
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostContentFts
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostTag
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostToTagMapping
import dev.shounakmulay.devpulse.core.data.db.paging.FeedPostPagingSourceProvider
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import dev.shounakmulay.devpulse.core.data.db.transaction.DevPulseDatabaseTransactionAccessor
import dev.shounakmulay.devpulse.core.data.db.transaction.DevPulseDatabaseTransactionScope
import dev.shounakmulay.devpulse.core.data.db.transaction.RoomTransactionScopeWrapper
import dev.shounakmulay.devpulse.core.data.db.triggers.createPostContentFtsDeleteTrigger


@Database(
    entities = [
        LocalRssFeed::class,
        LocalRssFeedFts::class,
        LocalRssContentFeedPost::class,
        LocalRssContentFeedPostFts::class,
        LocalRssFeedQueue::class,
        LocalRssPostTag::class,
        LocalRssPostToTagMapping::class,
        LocalRssPostCategory::class,
        LocalRssPostContent::class,
        LocalRssPostContentFts::class,
        LocalRssFeedSyncMetadata::class
    ],
    version = SchemaVersions.BASE
)
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
@ColumnTypeConverters(LocalCompressedTextTypeConverter::class)
@ConstructedBy(DevPulseDatabaseConstructor::class)
abstract class DevPulseDatabase :
    RoomDatabase(),
    DevPulseDatabaseTransactionAccessor,
    FeedPostPagingSourceProvider {

    abstract fun getFeedContentDao(): FeedPostDao
    abstract fun getFeedDao(): FeedDao
    abstract fun getFeedQueueDao(): FeedQueueDao
    abstract fun getPostContentDao(): PostContentDao
    abstract fun getPostCategoryDao(): FeedPostCategoryDao
    abstract fun getFeedSyncMetadataDao(): FeedSyncMetadataDao

    override suspend fun clearAllTables() {
    }

    override fun getFeedPostPagingSource(query: LocalFeedPostQuery) =
        getFeedContentDao().getFeedPostPagingSource(this, query)

    override suspend fun <T> readTransaction(block: suspend DevPulseDatabaseTransactionScope<T>.() -> T): T {
        return withReadTransaction {
            val scope = RoomTransactionScopeWrapper.fromRoomScope(this)
            scope.block()
        }
    }

    override suspend fun <T> writeTransaction(block: suspend DevPulseDatabaseTransactionScope<T>.() -> T): T {
        return withWriteTransaction {
            val scope = RoomTransactionScopeWrapper.fromRoomScope(this)
            scope.block()
        }
    }
}

@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object DevPulseDatabaseConstructor : RoomDatabaseConstructor<DevPulseDatabase> {
    override fun initialize(): DevPulseDatabase
}

fun getDevPulseDatabase(
    builder: RoomDatabase.Builder<DevPulseDatabase>,
    dispatcherProvider: DispatcherProvider,
    compressedTextTypeConverter: LocalCompressedTextTypeConverter
): DevPulseDatabase {
    val driver = BundledSQLiteDriver().apply {
//        addExtension("vector")
    }
    return builder
        .setDriver(driver)
        .setQueryCoroutineContext(dispatcherProvider.ioDispatcher)
        .addColumnTypeConverter(compressedTextTypeConverter)
        .addCallback(object : RoomDatabase.Callback() {
            override suspend fun onCreate(connection: SQLiteConnection) {
                createPostContentFtsDeleteTrigger(connection)
                // TODO: Call vector init
                // TODO: Call vector quantize on data change not here.
            }

            override suspend fun onOpen(connection: SQLiteConnection) {

                // TODO: Call vector quantize preload
            }
        })
        .build()
}
