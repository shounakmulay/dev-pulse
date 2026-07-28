package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.publishedRange

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import devpulse.core.resources.generated.resources.custom
import devpulse.core.resources.generated.resources.last_24_hours
import devpulse.core.resources.generated.resources.last_30_days
import devpulse.core.resources.generated.resources.last_7_days
import devpulse.core.resources.generated.resources.this_month
import devpulse.core.resources.generated.resources.today
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Immutable
@Serializable
sealed class UIPublishedRange(
    val title: TextResource,
) {
    @Immutable
    @Serializable
    data object Today : UIPublishedRange(
        title = TextResource.fromStringRes(stringRes.today),
    )

    @Immutable
    @Serializable
    data object Last24Hours : UIPublishedRange(
        title = TextResource.fromStringRes(stringRes.last_24_hours),
    )

    @Immutable
    @Serializable
    data object Last7Days : UIPublishedRange(
        title = TextResource.fromStringRes(stringRes.last_7_days),
    )

    @Immutable
    @Serializable
    data object Last30Days : UIPublishedRange(
        title = TextResource.fromStringRes(stringRes.last_30_days),
    )

    @Immutable
    @Serializable
    data object ThisMonth : UIPublishedRange(
        title = TextResource.fromStringRes(stringRes.this_month),
    )

    @Immutable
    @Serializable
    data class Custom(val min: LocalDateTime?, val max: LocalDateTime?) : UIPublishedRange(
        title = TextResource.fromStringRes(stringRes.custom),
    )
}