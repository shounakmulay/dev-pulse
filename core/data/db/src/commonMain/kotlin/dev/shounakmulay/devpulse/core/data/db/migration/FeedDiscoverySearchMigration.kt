package dev.shounakmulay.devpulse.core.data.db.migration

import androidx.room3.migration.AutoMigrationSpec
import androidx.sqlite.SQLiteConnection

class FeedDiscoverySearchMigration : AutoMigrationSpec {
    override suspend fun onPostMigrate(connection: SQLiteConnection) {
        connection.prepare("INSERT INTO LocalFeedDiscoveryFeedMetadataFts(LocalFeedDiscoveryFeedMetadataFts) VALUES ('rebuild')").use {
            it.step()
        }
    }
}
