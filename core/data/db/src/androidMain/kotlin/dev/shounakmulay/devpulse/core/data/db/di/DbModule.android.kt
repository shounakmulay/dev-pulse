package dev.shounakmulay.devpulse.core.data.db.di

import android.content.Context
import androidx.room3.RoomDatabase
import dev.shounakmulay.devpulse.core.data.db.DevPulseDatabase
import dev.shounakmulay.devpulse.core.data.db.getDatabaseBuilder
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
actual class DatabasePlatformModule {

    @Single
    fun provideDatabaseBuilder(context: Context): RoomDatabase.Builder<DevPulseDatabase> {
        return getDatabaseBuilder(context)
    }
}