package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

import androidx.room3.Embedded
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeedSyncMetadata

data class LocalRssFeedSyncMetadataWithIdentity(
    @Embedded
    val syncMetadata: LocalRssFeedSyncMetadata,
    @Embedded(prefix = "identity_")
    val identitySlice: LocalRssFeedIdentitySlice
)