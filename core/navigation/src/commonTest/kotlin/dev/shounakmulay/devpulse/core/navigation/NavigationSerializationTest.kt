package dev.shounakmulay.devpulse.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationSerializationTest {
    @Test
    fun `Given privacy settings on the back stack When saved and restored Then the route is retained`() {
        val json = Json { serializersModule = navigationSerializersConfig.serializersModule }
        val serializer = ListSerializer(PolymorphicSerializer(NavKey::class))
        val backStack: List<NavKey> = listOf(Screen.Tabs, Screen.Settings, Screen.Settings.PrivacyDiagnostics)

        val saved = json.encodeToString(serializer, backStack)
        val restored = json.decodeFromString(serializer, saved)

        assertEquals(backStack, restored)
    }
}
