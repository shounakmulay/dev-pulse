package dev.shounakmulay.devpulse.core.sync

data class DevPulseWorkRequestConstraints(
    val requiresNetwork: Boolean = false,
    val requiresBatteryNotLow: Boolean = false,
    val requiresCharging: Boolean = false,
    val requiresStorageNotLow: Boolean = false,
    val requiresDeviceIdle: Boolean = false,
) {
    companion object {
        fun requiresNetwork() = DevPulseWorkRequestConstraints(requiresNetwork = true)
        fun requiresBatteryNotLow() = DevPulseWorkRequestConstraints(requiresBatteryNotLow = true)
        fun requiresCharging() = DevPulseWorkRequestConstraints(requiresCharging = true)
        fun requiresStorageNotLow() = DevPulseWorkRequestConstraints(requiresStorageNotLow = true)
        fun requiresDeviceIdle() = DevPulseWorkRequestConstraints(requiresDeviceIdle = true)
    }
}