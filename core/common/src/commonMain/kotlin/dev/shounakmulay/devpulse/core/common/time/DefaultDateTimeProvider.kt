package dev.shounakmulay.devpulse.core.common.time

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Factory
import kotlin.time.Clock
import kotlin.time.Instant

@Factory
class DefaultDateTimeProvider : DateTimeProvider {
    override fun now(): Long = nowEpochMilliseconds()

    override fun nowEpochMilliseconds(): Long = Clock.System.now().toEpochMilliseconds()

    override fun today(): LocalDate {
        return Instant
            .fromEpochMilliseconds(nowEpochMilliseconds())
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }
}
