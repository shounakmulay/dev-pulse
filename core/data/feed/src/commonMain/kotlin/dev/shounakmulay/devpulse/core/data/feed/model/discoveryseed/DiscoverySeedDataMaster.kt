package dev.shounakmulay.devpulse.core.data.feed.model.discoveryseed


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscoverySeedDataMaster(
    @SerialName("catalog_id")
    val catalogId: String,
    @SerialName("collections")
    val collections: List<DiscoverySeedDataCollectionMetadata>,
    @SerialName("feed_membership_count")
    val feedMembershipCount: Int,
    @SerialName("publication")
    val publication: Int,
    @SerialName("schema_version")
    val schemaVersion: Int,
    @SerialName("unique_feed_count")
    val uniqueFeedCount: Int
)