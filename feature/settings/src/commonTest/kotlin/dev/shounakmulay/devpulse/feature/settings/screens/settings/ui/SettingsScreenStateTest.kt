package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsScreenStateTest {
    @Test
    fun `Given light mode When checking black mode toggle availability Then returns false`() {
        val state = SettingsScreenState(
            themeMode = ThemeMode.LIGHT,
        )

        assertFalse(state.canToggleBlackMode(isDarkTheme = true))
    }

    @Test
    fun `Given dark mode When checking black mode toggle availability Then returns true`() {
        val state = SettingsScreenState(
            themeMode = ThemeMode.DARK,
        )

        assertTrue(state.canToggleBlackMode(isDarkTheme = false))
    }

    @Test
    fun `Given system mode and light effective theme When checking black mode toggle availability Then returns false`() {
        val state = SettingsScreenState(
            themeMode = ThemeMode.SYSTEM,
        )

        assertFalse(state.canToggleBlackMode(isDarkTheme = false))
    }

    @Test
    fun `Given system mode and dark effective theme When checking black mode toggle availability Then returns true`() {
        val state = SettingsScreenState(
            themeMode = ThemeMode.SYSTEM,
        )

        assertTrue(state.canToggleBlackMode(isDarkTheme = true))
    }
}
