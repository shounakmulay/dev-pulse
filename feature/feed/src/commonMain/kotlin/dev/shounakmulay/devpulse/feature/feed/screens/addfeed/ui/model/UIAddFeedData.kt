package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedType
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@Immutable
data class UIAddFeedData(
    val id: String,
    val url: String,
    val name: String?,
    val type: RssFeedType,
    val tags: List<Int>,
    val folders: List<Int>,
    val expanded: Boolean = true,
    val error: ValidationError? = null
) {

    enum class ValidationError {
        INVALID_SOURCE_URL,
        INVALID_RSS_URL
    }

    companion object {

        @OptIn(ExperimentalUuidApi::class)
        fun empty(): UIAddFeedData {
            return UIAddFeedData(
                id = Uuid.random().toString(),
                url = "",
                name = "",
                type = RssFeedType.CONTENT,
                tags = emptyList(),
                folders = emptyList()
            )
        }
    }
}
