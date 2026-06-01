package dev.shounakmulay.devpulse.core.common.time

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Factory
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

@Factory
class DefaultDateTimeProvider : DateTimeProvider {
    val formats = listOf(
        DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET,
        DateTimeComponents.Formats.RFC_1123,
    )

    override fun parse(string: String): Instant? {
        for (format in formats) {
            runCatching {
                val components = format.parse(string)

                return components.toInstantUsingOffset()
            }
        }

        return null
    }

    override fun getTimeElapsed(instant: Instant): Duration {
        val now = Clock.System.now()

        return now - instant
    }

    override fun now(): Instant = Clock.System.now()

    override fun nowEpochMilliseconds(): Long = Clock.System.now().toEpochMilliseconds()

    override fun today(): LocalDate {
        return Instant
            .fromEpochMilliseconds(nowEpochMilliseconds())
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }
}
