package dev.shounakmulay.devpulse.core.common.time

import org.koin.core.annotation.Factory
import kotlin.time.Clock
import kotlin.time.Instant

@Factory
class DefaultDateTimeProvider : DateTimeProvider {
    override fun now(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }

    override fun parseEpochMilliseconds(value: String): Long? {
        val normalized = value.trim().takeIf { it.isNotEmpty() } ?: return null
        return parseIsoEpochMilliseconds(normalized) ?: parseInternetDateEpochMilliseconds(
            normalized
        )
    }

    private fun parseIsoEpochMilliseconds(value: String): Long? {
        return runCatching { Instant.Companion.parse(value).toEpochMilliseconds() }.getOrNull()
    }

    private fun parseInternetDateEpochMilliseconds(value: String): Long? {
        val parts = value
            .replace(",", "")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        val dayIndex = when {
            parts.firstOrNull()?.toIntOrNull() != null -> 0
            else -> 1
        }

        if (parts.size < dayIndex + 5) return null

        val day = parts[dayIndex].toIntOrNull() ?: return null
        val month = monthNumberByName[parts[dayIndex + 1].lowercase()] ?: return null
        val year = parts[dayIndex + 2].toIntOrNull() ?: return null
        val time = parseTime(parts[dayIndex + 3]) ?: return null
        val offsetSeconds = parseZoneOffsetSeconds(parts[dayIndex + 4]) ?: return null

        if (year !in 1..9999 || day !in 1..daysInMonth(year, month)) return null

        val daySeconds = epochDay(year = year, month = month, day = day) * SecondsPerDay
        return (daySeconds + time - offsetSeconds) * MillisecondsPerSecond
    }

    private fun parseTime(value: String): Long? {
        val parts = value.split(":")
        if (parts.size !in 2..3) return null

        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null
        val second = parts.getOrNull(2)?.toIntOrNull() ?: 0

        if (hour !in 0..23 || minute !in 0..59 || second !in 0..59) return null

        return hour * SecondsPerHour + minute * SecondsPerMinute + second.toLong()
    }

    private fun parseZoneOffsetSeconds(value: String): Long? {
        val normalized = value.uppercase()
        namedZoneOffsetSeconds[normalized]?.let { return it }

        val offset = normalized.replace(":", "")
        if (!numericOffsetRegex.matches(offset)) return null

        val sign = when (offset.first()) {
            '+' -> 1
            '-' -> -1
            else -> return null
        }
        val hour = offset.drop(1).take(2).toIntOrNull() ?: return null
        val minute = offset.takeLast(2).toIntOrNull() ?: return null

        if (hour !in 0..23 || minute !in 0..59) return null

        return sign * (hour * SecondsPerHour + minute * SecondsPerMinute)
    }

    private fun epochDay(
        year: Int,
        month: Int,
        day: Int
    ): Long {
        val adjustedYear = year - if (month <= 2) 1 else 0
        val era = adjustedYear / 400
        val yearOfEra = adjustedYear - era * 400
        val adjustedMonth = month + if (month > 2) -3 else 9
        val dayOfYear = (153 * adjustedMonth + 2) / 5 + day - 1
        val dayOfEra = yearOfEra * 365 + yearOfEra / 4 - yearOfEra / 100 + dayOfYear
        return era * DaysPerEra + dayOfEra - DaysFromCivilEpoch
    }

    private fun daysInMonth(
        year: Int,
        month: Int
    ): Int {
        return when (month) {
            2 -> if (isLeapYear(year)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

    private companion object {
        private const val SecondsPerMinute = 60L
        private const val SecondsPerHour = 60L * SecondsPerMinute
        private const val SecondsPerDay = 24L * SecondsPerHour
        private const val MillisecondsPerSecond = 1000L
        private const val DaysPerEra = 146097L
        private const val DaysFromCivilEpoch = 719468L

        private val numericOffsetRegex = Regex("[+-]\\d{4}")

        private val monthNumberByName = mapOf(
            "jan" to 1,
            "feb" to 2,
            "mar" to 3,
            "apr" to 4,
            "may" to 5,
            "jun" to 6,
            "jul" to 7,
            "aug" to 8,
            "sep" to 9,
            "sept" to 9,
            "oct" to 10,
            "nov" to 11,
            "dec" to 12
        )

        private val namedZoneOffsetSeconds = mapOf(
            "UT" to 0L,
            "UTC" to 0L,
            "GMT" to 0L,
            "Z" to 0L,
            "EST" to -5 * SecondsPerHour,
            "EDT" to -4 * SecondsPerHour,
            "CST" to -6 * SecondsPerHour,
            "CDT" to -5 * SecondsPerHour,
            "MST" to -7 * SecondsPerHour,
            "MDT" to -6 * SecondsPerHour,
            "PST" to -8 * SecondsPerHour,
            "PDT" to -7 * SecondsPerHour
        )
    }
}