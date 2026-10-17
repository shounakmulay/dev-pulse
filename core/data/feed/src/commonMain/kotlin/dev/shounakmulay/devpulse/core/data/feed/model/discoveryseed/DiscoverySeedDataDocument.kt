package dev.shounakmulay.devpulse.core.data.feed.model.discoveryseed


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscoverySeedDataDocument(
    @SerialName("description")
    val description: String,
    @SerialName("feeds")
    val feeds: List<DiscoverySeedDataFeed>,
    @SerialName("id")
    val id: String,
    @SerialName("schema_version")
    val schemaVersion: Int,
    @SerialName("slug")
    val slug: String,
    @SerialName("title")
    val title: String
)