package dev.shounakmulay.devpulse.core.data.feed.model.discoveryseed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class DiscoverySeedDataCollection(
    @SerialName("document")
    val document: DiscoverySeedDataDocument,
    @SerialName("path")
    val path: String
)