package dev.shounakmulay.devpulse

import android.app.Application
import org.koin.android.ext.koin.androidContext

fun initializeDevPulse(application: Application) {
    DevPulseStartup.initialize {
        androidContext(application)
    }
}
