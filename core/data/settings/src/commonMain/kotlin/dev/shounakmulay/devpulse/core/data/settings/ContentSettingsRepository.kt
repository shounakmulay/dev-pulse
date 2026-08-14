package dev.shounakmulay.devpulse.core.data.settings

import dev.shounakmulay.devpulse.core.domain.models.contentSettings.ContentTextSettings
import kotlinx.coroutines.flow.Flow

interface ContentSettingsRepository {
    fun observeContentTextSettings(defaultSettings: ContentTextSettings): Flow<ContentTextSettings>
    suspend fun updateContentTextSettings(settings: ContentTextSettings)
    suspend fun setContentTextScale(scale: Float)
    suspend fun setContentLineHeightScale(spacing: Float)
}