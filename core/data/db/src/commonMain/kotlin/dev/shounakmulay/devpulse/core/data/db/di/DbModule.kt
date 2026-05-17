package dev.shounakmulay.devpulse.core.data.db.di

import androidx.room3.RoomDatabase
import dev.shounakmulay.devpulse.core.common.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.db.DevPulseDatabase
import dev.shounakmulay.devpulse.core.data.db.getDevPulseDatabase
import dev.shounakmulay.devpulse.core.data.db.transaction.DevPulseDatabaseTransactionAccessor
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@Module
expect class DatabasePlatformModule

@Module
@ComponentScan("dev.shounakmulay.devpulse.core.data.db")
class DatabaseModule {

    @Single(binds = [DevPulseDatabase::class, DevPulseDatabaseTransactionAccessor::class])
    fun provideDevPulseDatabase(
        builder: RoomDatabase.Builder<DevPulseDatabase>,
        dispatcherProvider: DispatcherProvider
    ): DevPulseDatabase {
        return getDevPulseDatabase(
            builder = builder,
            dispatcherProvider = dispatcherProvider
        )
    }
}