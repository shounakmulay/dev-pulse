package dev.shounakmulay.devpulse.core.ui.datetime

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Factory
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

@Factory
class DateTimeStringConverterImpl : DateTimeStringConverter {
    override fun getTimeElapsedOrDateString(instant: Instant): String {
        val now = Clock.System.now()
        val duration = now - instant
        if (duration < 1.hours) {
            return "${duration.inWholeMinutes}m"
        }

        if (duration < 24.hours) {
            return "${duration.inWholeHours}h"
        }

        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date

        val daysDiff = today.toEpochDays() - date.toEpochDays()

        return when (daysDiff) {
            0L -> "Today"
            1L -> "Yesterday"
            else -> {
                val day = date.day
                val month = date.month.name.take(3).lowercase()
                    .replaceFirstChar(Char::uppercase)
                val year = (date.year % 100).toString().padStart(2, '0')
                "$day $month $year"
            }
        }
    }
}