package dev.shounakmulay.devpulse.core.data.db

import androidx.room3.ConstructedBy
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import androidx.room3.withReadTransaction
import androidx.room3.withWriteTransaction
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.shounakmulay.devpulse.core.common.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.db.dao.FeedContentDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.transaction.DevPulseDatabaseTransactionAccessor
import dev.shounakmulay.devpulse.core.data.db.transaction.DevPulseDatabaseTransactionScope
import dev.shounakmulay.devpulse.core.data.db.transaction.RoomTransactionScopeWrapper

@Database(
    entities = [
        LocalRssFeed::class,
        LocalRssContentFeedPost::class
    ],
    autoMigrations = [],
    version = 1
)
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
@ConstructedBy(DevPulseDatabaseConstructor::class)
abstract class DevPulseDatabase : RoomDatabase(), DevPulseDatabaseTransactionAccessor {

    abstract fun getFeedContentDao(): FeedContentDao
    abstract fun getFeedDao(): FeedDao
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
        .build()
}