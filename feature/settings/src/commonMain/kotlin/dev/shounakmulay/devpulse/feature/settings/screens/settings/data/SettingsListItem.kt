package dev.shounakmulay.devpulse.feature.settings.screens.settings.data

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

enum class SettingsToggleKey {
    BLACK_MODE
}

enum class SettingsSingleChoiceKey(val values: ImmutableList<SettingsListItem.SingleChoice.SingleChoiceOptions>) {
    THEME(
        persistentListOf(
            ThemeSingleChoiceOptions.LIGHT,
            ThemeSingleChoiceOptions.DARK,
            ThemeSingleChoiceOptions.SYSTEM,
        )
    )
}

@Serializable
@Immutable
sealed interface SettingsListItem {
    @Serializable
    data class SectionHeading(val title: TextResource) : SettingsListItem

    @Serializable
    data class SubPageLink(
        val heading: TextResource,
        val supportingText: TextResource?,
        val linkToScreen: Screen
    ) : SettingsListItem

    @Serializable
    data class Toggle(
        val title: TextResource,
        val key: SettingsToggleKey
    ) : SettingsListItem

    @Serializable
    data class SingleChoice(
        val title: TextResource,
        val key: SettingsSingleChoiceKey,
    ) : SettingsListItem {

        @Immutable
        interface SingleChoiceOptions
    }
}