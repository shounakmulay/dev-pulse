package dev.shounakmulay.devpulse.core.data.feed.model.discovery


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteDiscoveryCollectionMetadata(
    @SerialName("feed_count")
    val feedCount: Int,
    @SerialName("id")
    val id: String,
    @SerialName("path")
    val path: String,
    @SerialName("position")
    val position: Int,
    @SerialName("sha256")
    val sha256: String,
    @SerialName("slug")
    val slug: String,
    @SerialName("title")
    val title: String
)