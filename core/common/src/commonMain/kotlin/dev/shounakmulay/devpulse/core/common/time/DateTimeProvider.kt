package dev.shounakmulay.devpulse.core.common.time

import kotlinx.datetime.LocalDate
import kotlin.time.Duration
import kotlin.time.Instant

interface DateTimeProvider {

    fun parse(string: String): Instant?

    fun getTimeElapsed(instant: Instant): Duration

    fun now(): Instant
    fun nowEpochMilliseconds(): Long
    fun today(): LocalDate
}
