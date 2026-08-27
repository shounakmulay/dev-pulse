package dev.shounakmulay.devpulse.core.sync.di

import androidx.work.WorkManager
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun nativeSyncModule(): Module  = module {
    factory {
        WorkManager.getInstance(androidApplication())
    }
}