package dev.shounakmulay.devpulse.core.sync

enum class DevPulsePeriodicWorkerExitingWorkPolicy {
    REPLACE,
    KEEP,
    UPDATE,
    CANCEL_AND_REENQUEUE;
}
