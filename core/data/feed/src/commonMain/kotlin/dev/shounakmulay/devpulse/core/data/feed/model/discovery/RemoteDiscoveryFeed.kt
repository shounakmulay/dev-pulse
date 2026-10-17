package dev.shounakmulay.devpulse.core.data.feed.model.discovery


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteDiscoveryFeed(
    @SerialName("description")
    val description: String?,
    @SerialName("feed_format")
    val feedFormat: String,
    @SerialName("feed_url")
    val feedUrl: String,
    @SerialName("icon_url")
    val iconUrl: String?,
    @SerialName("id")
    val id: String,
    @SerialName("language_tag")
    val languageTag: String?,
    @SerialName("title")
    val title: String,
    @SerialName("website_url")
    val websiteUrl: String?
)