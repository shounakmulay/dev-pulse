package dev.shounakmulay.devpulse.core.data.db.model.feed

import androidx.room3.TypeConverter

class LocalRssFeedQueueConverters {

    @TypeConverter
    fun actionRequestorToInt(value: LocalRssFeedQueueActionRequestor): Int {
        return value.dbValue
    }

    @TypeConverter
    fun intToActionRequestor(value: Int): LocalRssFeedQueueActionRequestor {
        return LocalRssFeedQueueActionRequestor.entries.first { it.dbValue == value }
    }

    @TypeConverter
    fun actionTypeToInt(value: LocalRssFeedQueueActionType): Int {
        return value.dbValue
    }

    @TypeConverter
    fun intToActionType(value: Int): LocalRssFeedQueueActionType {
        return LocalRssFeedQueueActionType.entries.first { it.dbValue == value }
    }

    @TypeConverter
    fun statusToInt(value: LocalRssFeedQueueStatus): Int {
        return value.dbValue
    }

    @TypeConverter
    fun intToStatus(value: Int): LocalRssFeedQueueStatus {
        return LocalRssFeedQueueStatus.entries.first { it.dbValue == value }
    }
}
