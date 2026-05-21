package dev.shounakmulay.devpulse.core.common.time

interface DateTimeProvider {
    fun now(): Long
    fun parseEpochMilliseconds(value: String): Long?
}
