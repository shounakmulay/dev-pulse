package dev.shounakmulay.devpulse.theme

import dev.shounakmulay.devpulse.core.domain.models.ThemeMode
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThemeSettingsResolverTest {
    @Test
    fun `Given light mode When resolving dark theme Then returns false`() {
        assertFalse(ThemeMode.LIGHT.resolveDarkTheme(isSystemInDarkTheme = true))
    }

    @Test
    fun `Given dark mode When resolving dark theme Then returns true`() {
        assertTrue(ThemeMode.DARK.resolveDarkTheme(isSystemInDarkTheme = false))
    }

    @Test
    fun `Given system mode When resolving dark theme Then follows system value`() {
        assertTrue(ThemeMode.SYSTEM.resolveDarkTheme(isSystemInDarkTheme = true))
        assertFalse(ThemeMode.SYSTEM.resolveDarkTheme(isSystemInDarkTheme = false))
    }
}
