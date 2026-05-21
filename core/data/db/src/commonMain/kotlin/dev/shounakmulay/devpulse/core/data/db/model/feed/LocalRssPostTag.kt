package dev.shounakmulay.devpulse.core.data.db.model.feed

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class LocalRssPostTag(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: Int?
)