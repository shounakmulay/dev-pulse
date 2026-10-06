package dev.shounakmulay.devpulse.android

import android.app.Application
import dev.shounakmulay.devpulse.initializeDevPulse

class DevPulseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeDevPulse(this)
    }
}
