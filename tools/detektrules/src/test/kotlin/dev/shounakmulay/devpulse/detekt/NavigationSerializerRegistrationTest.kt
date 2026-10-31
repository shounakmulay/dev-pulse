package dev.shounakmulay.devpulse.detekt

import io.github.detekt.test.utils.compileContentForTest
import org.jetbrains.kotlin.psi.KtProperty
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationSerializerRegistrationTest {
    private val routes = compileContentForTest(
        """
        sealed interface Screen : NavKey {
            data object Settings : Screen {
                data object Privacy : Screen
                data class Details(val id: String) : Screen
                sealed interface Filter { data object All : Filter }
            }
            data object DeveloperTools { data object Board : Screen }
        }
        """.trimIndent(),
    )

    private fun missing(configuration: String): List<String> {
        val file = compileContentForTest("val navigationSerializersConfig = $configuration")
        return missingRoutes(routes, file.declarations.filterIsInstance<KtProperty>().single())
    }

    @Test
    fun registeredRoutesAreAccepted() {
        assertEquals(
            emptyList(),
            missing(
                """SavedStateConfiguration { serializersModule = SerializersModule { polymorphic(NavKey::class) {
                    subclass(Screen.Settings::class, Screen.Settings.serializer())
                    subclass(Screen.Settings.Privacy::class, Screen.Settings.Privacy.serializer())
                    subclass(Screen.Settings.Details::class, Screen.Settings.Details.serializer())
                    subclass(Screen.DeveloperTools.Board::class, Screen.DeveloperTools.Board.serializer())
                } } }""",
            ),
        )
    }

    @Test
    fun missingAndWrongScopeRegistrationsAreReported() {
        assertEquals(
            listOf("Screen.Settings.Privacy", "Screen.Settings.Details", "Screen.DeveloperTools.Board"),
            missing(
                """SavedStateConfiguration { serializersModule = SerializersModule {
                    polymorphic(NavKey::class) {
                        subclass(Screen.Settings::class, Screen.Settings.serializer())
                        subclass(Screen.Settings.Details::class, Screen.Settings.Privacy.serializer())
                    }
                    polymorphic(Other::class) { subclass(Screen.Settings.Privacy::class, Screen.Settings.Privacy.serializer()) }
                } }""",
            ),
        )
    }
}
