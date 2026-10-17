package dev.shounakmulay.devpulse.core.data.feed.model.discoveryseed


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscoverySeedData(
    @SerialName("collections")
    val collections: List<DiscoverySeedDataCollection>,
    @SerialName("master")
    val master: DiscoverySeedDataMaster,
    @SerialName("schema_version")
    val schemaVersion: Int
)