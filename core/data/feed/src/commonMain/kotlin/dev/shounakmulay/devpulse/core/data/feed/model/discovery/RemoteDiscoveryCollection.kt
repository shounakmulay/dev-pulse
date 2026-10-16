package dev.shounakmulay.devpulse.core.data.feed.model.discovery


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteDiscoveryCollection(
    @SerialName("description")
    val description: String,
    @SerialName("feeds")
    val feeds: List<RemoteDiscoveryFeed>,
    @SerialName("id")
    val id: String,
    @SerialName("schema_version")
    val schemaVersion: Int,
    @SerialName("slug")
    val slug: String,
    @SerialName("title")
    val title: String
)