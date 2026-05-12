package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import dev.shounakmulay.devpulse.core.domain.models.ThemeMode
import dev.shounakmulay.devpulse.feature.settings.screens.settings.data.ThemeSingleChoiceOptions
import kotlin.test.Test
import kotlin.test.assertEquals

class ThemeSingleChoiceOptionsMapperTest {
    @Test
    fun `Given theme option When mapping to theme mode Then returns matching domain mode`() {
        assertEquals(ThemeMode.LIGHT, ThemeSingleChoiceOptions.LIGHT.toThemeMode())
        assertEquals(ThemeMode.DARK, ThemeSingleChoiceOptions.DARK.toThemeMode())
        assertEquals(ThemeMode.SYSTEM, ThemeSingleChoiceOptions.SYSTEM.toThemeMode())
    }
}
