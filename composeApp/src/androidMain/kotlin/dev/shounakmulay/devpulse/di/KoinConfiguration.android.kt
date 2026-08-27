package dev.shounakmulay.devpulse.di

import org.koin.androidx.workmanager.koin.workManagerFactory

actual fun org.koin.core.KoinApplication.registerNativeDependencies() {
    workManagerFactory()
}