package dev.shounakmulay.devpulse.core.data.db.di

import androidx.room3.RoomDatabase
import dev.shounakmulay.devpulse.core.data.db.DevPulseDatabase
import dev.shounakmulay.devpulse.core.data.db.getDatabaseBuilder
import org.koin.core.annotation.Single


@org.koin.core.annotation.Module
actual class DatabasePlatformModule {

    @Single
    fun provideDatabaseBuilder(): RoomDatabase.Builder<DevPulseDatabase> {
        return  getDatabaseBuilder()
    }
}