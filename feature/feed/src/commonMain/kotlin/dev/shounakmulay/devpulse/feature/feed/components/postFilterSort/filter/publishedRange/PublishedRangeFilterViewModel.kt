package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.publishedRange

import com.kizitonwose.calendar.core.now
import dev.shounakmulay.devpulse.core.common.time.atEndOfMonth
import dev.shounakmulay.devpulse.core.common.time.atEndOfTheDay
import dev.shounakmulay.devpulse.core.common.time.atStartOfMonth
import dev.shounakmulay.devpulse.core.common.time.atStartOfTheDay
import dev.shounakmulay.devpulse.core.common.time.lengthOfMonth
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import devpulse.core.resources.generated.resources.published_min_max
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.koin.core.annotation.KoinViewModel
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

@KoinViewModel
class PublishedRangeFilterViewModel(
    filter: RssPostFilter.PublishedRange,
) : EventHandler<PublishedRangeFilterEvent>,
    MviViewModel<PublishedRangeFilterState, PublishedRangeFilterEffect>(
        initialState = PublishedRangeFilterState(
            selectedRange = filter.toUIPublishedRange(),
            rangeValues = listOf(
                UIPublishedRange.Today,
                UIPublishedRange.Last24Hours,
                UIPublishedRange.Last7Days,
                UIPublishedRange.Last30Days,
                UIPublishedRange.ThisMonth,
                UIPublishedRange.Custom(filter.min, filter.max)
            )
        )
    ) {
    override fun createStateSerializer() = PublishedRangeFilterState.serializer()
    override fun onEvent(event: PublishedRangeFilterEvent) {
        when (event) {
            is PublishedRangeFilterEvent.OnFilterUpdated -> onFilterUpdated(event.filter)
        }
    }

    private fun onFilterUpdated(filter: UIPublishedRange?) = intent {
        val dateTimeRange = filter?.let { publishedRangeToLocalDateTime(it) }
        if (dateTimeRange == null) {
            postSideEffect(PublishedRangeFilterEffect.OnFilterUpdated(RssPostFilter.PublishedRange()))
            setState {
                copy(selectedRange = null)
            }
            return@intent
        }

        val (min, max) = dateTimeRange
        setState {
            copy(selectedRange = filter)
        }
        postSideEffect(
            PublishedRangeFilterEffect.OnFilterUpdated(
                RssPostFilter.PublishedRange(
                    min,
                    max
                )
            )
        )
    }


    internal companion object {

        @OptIn(FormatStringsInDatetimeFormats::class)
        fun publishedRangeTextResource(
            publishedRange: RssPostFilter.PublishedRange,
            res: StringResource = stringRes.published_min_max
        ): TextResource? {
            val (startDate, endDate) = publishedRangeText(publishedRange)
                ?: return null

            return TextResource.fromStringResWithArgs(
                res,
                startDate,
                endDate
            )
        }

        @OptIn(FormatStringsInDatetimeFormats::class)
        fun publishedRangeText(publishedRange: RssPostFilter.PublishedRange): Pair<String, String>? {
            val customFormat = LocalDate.Format {
                day()
                char(' ')
                monthName(MonthNames.ENGLISH_ABBREVIATED)
                char(' ')
                byUnicodePattern("yy") // Or custom year formatting
            }
            val uiPublishedRange = publishedRange.toUIPublishedRange() ?: return null
            val (start, end) = publishedRangeToLocalDateTime(uiPublishedRange) ?: return null
            val startDate = start
                .date
                .format(customFormat)
            val endDate = end
                .date
                .format(customFormat)

            return startDate to endDate
        }

        private fun publishedRangeToLocalDateTime(publishedRange: UIPublishedRange): Pair<LocalDateTime, LocalDateTime>? {
            return when (publishedRange) {
                is UIPublishedRange.Custom -> {
                    val (min, max) = publishedRange
                    if (min == null || max == null) return null
                    min.date.atStartOfTheDay() to max.date.atEndOfTheDay()
                }

                UIPublishedRange.Last24Hours -> {
                    val now = Clock.System.now()
                    now.minus(24.hours)
                        .toLocalDateTime(TimeZone.currentSystemDefault()) to now.toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    )
                }

                UIPublishedRange.Last30Days -> {
                    val now = LocalDate.now()
                    now.minus(DatePeriod(days = 30)).atStartOfTheDay() to now.atEndOfTheDay()
                }

                UIPublishedRange.Last7Days -> {
                    val now = LocalDate.now()
                    now.minus(DatePeriod(days = 7)).atStartOfTheDay() to now.atEndOfTheDay()
                }

                UIPublishedRange.ThisMonth -> {
                    val now = LocalDate.now()
                    now.atStartOfMonth() to now.atEndOfMonth()
                }

                UIPublishedRange.Today -> {
                    val now = Clock.System.now()
                    now.atStartOfTheDay() to now.atEndOfTheDay()
                }
            }
        }

        fun RssPostFilter.PublishedRange.toUIPublishedRange(): UIPublishedRange? {
            val (startDate, endDate) = this
            if (startDate == null || endDate == null) return null

            val isCurrentMonthAndYear = isCurrentMonthAndYear(startDate.date, endDate.date)
            when {
                startDate.date == endDate.date -> {
                    return UIPublishedRange.Today
                }

                isCurrentMonthAndYear && startDate.date == endDate.date.minus(DatePeriod(days = 1)) -> {
                    return UIPublishedRange.Last24Hours
                }

                isCurrentMonthAndYear && startDate.date == endDate.date.minus(DatePeriod(days = 7)) -> {
                    return UIPublishedRange.Last7Days
                }

                isCurrentMonthAndYear && startDate.day == 1 && endDate.day == endDate.date.lengthOfMonth() -> {
                    return UIPublishedRange.ThisMonth
                }

                startDate.date == endDate.date.minus(DatePeriod(days = 30)) -> {
                    return UIPublishedRange.Last30Days
                }

                else -> {
                    return UIPublishedRange.Custom(min, max)
                }
            }
        }

        private fun isCurrentMonthAndYear(vararg date: LocalDate): Boolean {
            val currentMonth = LocalDate.now().month
            val currentYear = LocalDate.now().year
            for (d in date) {
                if (d.year != currentYear || d.month != currentMonth) {
                    return false
                }
            }
            return true
        }
    }
}