package dev.shounakmulay.devpulse.core.common.time

import kotlinx.datetime.LocalDate

interface DateTimeProvider {
    fun now(): Long
    fun nowEpochMilliseconds(): Long
    fun today(): LocalDate
}
