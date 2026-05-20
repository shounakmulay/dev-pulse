package dev.shounakmulay.devpulse.logging

import dev.shounakmulay.devpulse.core.logging.DPLog
import dev.shounakmulay.devpulse.core.logging.DPLogConfigs

internal object DevPulseLogging {
    private const val Tag = "DevPulseApp"
    private var configured = false

    fun configure() {
        if (configured) return

        val config = DPLogConfigs.debug()
        DPLog.configure(config)
        configured = true
        DPLog.i(Tag) { "Logging configured minLevel=${config.minLevel}" }
    }
}
