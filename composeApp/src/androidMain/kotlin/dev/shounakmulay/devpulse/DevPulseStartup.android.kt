package dev.shounakmulay.devpulse

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import dev.shounakmulay.devpulse.startup.DevPulseStartup
import org.koin.android.ext.koin.androidContext

fun initializeDevPulse(application: Application) {
    Firebase.initialize(application)
    DevPulseStartup.initialize {
        androidContext(application)
    }
}
