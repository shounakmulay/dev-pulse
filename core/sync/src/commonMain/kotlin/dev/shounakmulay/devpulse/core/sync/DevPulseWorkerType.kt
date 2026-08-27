package dev.shounakmulay.devpulse.core.sync

enum class DevPulseWorkerType {
    FEED_SYNC;

    companion object {
        const val WORK_DATA_KEY = "devpulse_worker_type"

        fun fromString(value: String): DevPulseWorkerType {
            return when (value) {
                FEED_SYNC.name -> FEED_SYNC
                else -> throw IllegalArgumentException("Unknown worker type: $value")
            }
        }
    }
}
