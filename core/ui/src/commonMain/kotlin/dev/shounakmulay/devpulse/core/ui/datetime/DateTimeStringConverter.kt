package dev.shounakmulay.devpulse.core.ui.datetime

import kotlin.time.Instant

interface DateTimeStringConverter {

    fun getTimeElapsedOrDateString(instant: Instant): String
}