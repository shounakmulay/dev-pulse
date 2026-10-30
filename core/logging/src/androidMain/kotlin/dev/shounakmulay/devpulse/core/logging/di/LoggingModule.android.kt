package dev.shounakmulay.devpulse.core.logging.di

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.perf.performance
import org.koin.dsl.module

actual fun nativeLoggingModule(): org.koin.core.module.Module = module {
    factory { Firebase.analytics }
    factory { Firebase.crashlytics }
    factory { Firebase.performance }
}
