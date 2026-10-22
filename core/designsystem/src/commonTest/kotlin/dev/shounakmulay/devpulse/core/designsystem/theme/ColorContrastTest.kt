package dev.shounakmulay.devpulse.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ColorContrastTest {

    @Test
    fun `Given brand colors When appearance changes Then original orange is preserved`() {
        listOf(lightScheme, darkScheme, blackScheme).forEach { scheme ->
            assertEquals(Color(0xFFD96320), scheme.primary)
        }
    }

    @Test
    fun `Given light buttons When labels use accent content colors Then normal text contrast passes`() {
        assertReadable(lightScheme.onPrimary, lightScheme.primary)
        assertReadable(lightScheme.onSecondary, lightScheme.secondary)
        assertReadable(lightScheme.onTertiary, lightScheme.tertiary)
    }

    @Test
    fun `Given surfaces When actions use neutral content Then normal text contrast passes`() {
        listOf(lightScheme, darkScheme, blackScheme).forEach { scheme ->
            assertReadable(scheme.onSurface, scheme.surface)
            assertReadable(scheme.onSurface, scheme.surfaceContainerLow)
            assertReadable(scheme.onSurface, scheme.surfaceContainer)
            assertReadable(scheme.onSurface, scheme.surfaceContainerHigh)
        }
    }

    @Test
    fun `Given dark buttons When labels use accent content colors Then normal text contrast passes`() {
        assertReadable(darkScheme.onPrimary, darkScheme.primary)
        assertReadable(darkScheme.onSecondary, darkScheme.secondary)
        assertReadable(darkScheme.onTertiary, darkScheme.tertiary)
    }

    @Test
    fun `Given both appearances When content uses surface roles Then normal text contrast passes`() {
        listOf(lightScheme, darkScheme, blackScheme).forEach { scheme ->
            assertReadable(scheme.onSurface, scheme.surface)
            assertReadable(scheme.onSurfaceVariant, scheme.surface)
            assertReadable(scheme.inversePrimary, scheme.inverseSurface)
        }
    }

    @Test
    fun `Given semantic feedback When content uses its container Then normal text contrast passes`() {
        listOf(lightDPContextColors, darkDPContextColors).forEach { colors ->
            assertReadable(colors.onSuccessContainer, colors.successContainer)
            assertReadable(colors.onWarningContainer, colors.warningContainer)
            assertReadable(colors.onInfoContainer, colors.infoContainer)
        }
        listOf(lightScheme, darkScheme, blackScheme).forEach { scheme ->
            assertReadable(scheme.onErrorContainer, scheme.errorContainer)
        }
    }

    private fun assertReadable(content: Color, background: Color) {
        val lighter = max(content.luminance(), background.luminance())
        val darker = min(content.luminance(), background.luminance())
        val contrast = (lighter + 0.05f) / (darker + 0.05f)

        assertTrue(contrast >= 4.5f, "$content on $background has $contrast contrast")
    }
}
