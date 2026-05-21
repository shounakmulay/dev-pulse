package dev.shounakmulay.devpulse.core.data.db

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<DevPulseDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("dev_pulse.db")
    return Room.databaseBuilder<DevPulseDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}