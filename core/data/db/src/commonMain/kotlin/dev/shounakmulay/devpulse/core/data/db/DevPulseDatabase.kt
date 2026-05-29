package dev.shounakmulay.devpulse.core.data.db

import androidx.room3.AutoMigration
import androidx.room3.ConstructedBy
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import androidx.room3.withReadTransaction
import androidx.room3.withWriteTransaction
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.db.SchemaVersions.BASE
import dev.shounakmulay.devpulse.core.data.db.SchemaVersions.RECENT_POSTS_PUBLISHED_AT
import dev.shounakmulay.devpulse.core.data.db.dao.FeedContentDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedQueueDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedQueue
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssPostTag
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssPostToTagMapping
import dev.shounakmulay.devpulse.core.data.db.transaction.DevPulseDatabaseTransactionAccessor
import dev.shounakmulay.devpulse.core.data.db.transaction.DevPulseDatabaseTransactionScope
import dev.shounakmulay.devpulse.core.data.db.transaction.RoomTransactionScopeWrapper

private object SchemaVersions {
    const val BASE = 1
    const val RECENT_POSTS_PUBLISHED_AT = 2
}

@Database(
    entities = [
        LocalRssFeed::class,
        LocalRssContentFeedPost::class,
        LocalRssFeedQueue::class,
        LocalRssPostTag::class,
        LocalRssPostToTagMapping::class,
    ],
    autoMigrations = [
        AutoMigration(from = BASE, to = RECENT_POSTS_PUBLISHED_AT)
    ],
    version = RECENT_POSTS_PUBLISHED_AT
)
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
@ConstructedBy(DevPulseDatabaseConstructor::class)
abstract class DevPulseDatabase : RoomDatabase(), DevPulseDatabaseTransactionAccessor {

    abstract fun getFeedContentDao(): FeedContentDao
    abstract fun getFeedDao(): FeedDao
    abstract fun getFeedQueueDao(): FeedQueueDao
    override suspend fun clearAllTables() {
    }

    override suspend fun <T> readTransaction(block: DevPulseDatabaseTransactionScope<T>.() -> T): T {
        return withReadTransaction {
            val scope = RoomTransactionScopeWrapper.fromRoomScope(this)
            scope.block()
        }
    }

    override suspend fun <T> writeTransaction(block: DevPulseDatabaseTransactionScope<T>.() -> T): T {
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
    dispatcherProvider: DispatcherProvider
): DevPulseDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(dispatcherProvider.ioDispatcher)
        .fallbackToDestructiveMigration()
        .build()
}
