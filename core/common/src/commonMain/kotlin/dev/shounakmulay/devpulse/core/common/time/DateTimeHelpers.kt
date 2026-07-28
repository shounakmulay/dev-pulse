package dev.shounakmulay.devpulse.core.common.time

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun Instant.atStartOfTheDay(): LocalDateTime {
    return toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .atStartOfTheDay()
}

fun Instant.atEndOfTheDay(): LocalDateTime {
    return toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .atEndOfTheDay()
}

fun LocalDate.atStartOfTheDay(): LocalDateTime {
    return atTime(0, 0)
}

fun LocalDate.atEndOfTheDay(): LocalDateTime {
    return atTime(23, 59)
}

fun LocalDate.atEndOfMonth(): LocalDateTime {
    return LocalDate(year, month, lengthOfMonth())
        .atEndOfTheDay()
}

fun LocalDate.atStartOfMonth(): LocalDateTime {
    return LocalDate(year, month, 1)
        .atStartOfTheDay()
}

fun LocalDateTime.toUTCMillis(): Long {
    return toInstant(TimeZone.UTC).toEpochMilliseconds()
}

fun LocalDateTime.toMillis(): Long {
    return toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDate.lengthOfMonth(): Int {
    return when (month.number) {
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
}