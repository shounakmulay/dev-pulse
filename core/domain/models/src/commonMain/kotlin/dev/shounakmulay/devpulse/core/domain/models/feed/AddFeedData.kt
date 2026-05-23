package dev.shounakmulay.devpulse.core.domain.models.feed

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class AddFeedData(
    val tempId: String,
    val url: String,
    val name: String?,
    val type: RssFeedType,
    val tags: List<Int>,
    val folders: List<Int>
) {
    companion object {

        @OptIn(ExperimentalUuidApi::class)
        fun empty(): AddFeedData {
            return AddFeedData(
                tempId = Uuid.random().toString(),
                url = "",
                name = "",
                type = RssFeedType.CONTENT,
                tags = emptyList(),
                folders = emptyList()
            )
        }
    }
}