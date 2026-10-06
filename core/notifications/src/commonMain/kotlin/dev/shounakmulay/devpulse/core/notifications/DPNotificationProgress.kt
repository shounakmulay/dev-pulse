package dev.shounakmulay.devpulse.core.notifications

sealed interface DPNotificationProgress {
    data object Indeterminate : DPNotificationProgress

    data class Determinate(val current: Int, val max: Int) : DPNotificationProgress {
        init {
            require(max > 0)
            require(current in 0..max)
        }
    }
}
