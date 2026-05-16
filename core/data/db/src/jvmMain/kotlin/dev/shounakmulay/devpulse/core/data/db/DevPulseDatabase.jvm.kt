package dev.shounakmulay.devpulse.core.data.db

import androidx.room3.Room
import androidx.room3.RoomDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<DevPulseDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "dev_pulse.db")
    return Room.databaseBuilder<DevPulseDatabase>(
        name = dbFile.absolutePath,
    )
}