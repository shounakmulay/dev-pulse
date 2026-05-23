package dev.shounakmulay.devpulse.core.ui.text

import devpulse.core.resources.generated.resources.Res
import devpulse.core.resources.generated.resources.text_resource_greeting
import devpulse.core.resources.generated.resources.text_resource_greeting_with_name
import devpulse.core.resources.generated.resources.text_resource_item_count
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TextResourceSerializationTest {

    private val json = Json

    @Test
    fun `Given simple text When encoded and decoded Then value round trips`() {
        val resource = SimpleTextResource("Hello")

        val decoded = resource.roundTrip()

        assertEquals(resource, decoded)
    }

    @Test
    fun `Given string resource When encoded Then JSON stores resource key`() {
        val resource = TextResource.fromStringRes(Res.string.text_resource_greeting)
        val encoded = json.encodeToString<TextResource>(resource)

        val decoded = json.decodeFromString<TextResource>(encoded)

        assertTrue(encoded.contains("\"key\":\"text_resource_greeting\""))
        assertFalse(encoded.contains("\"res\""))
        val decodedResource = assertIs<StringResTextResource>(decoded)
        assertEquals(Res.string.text_resource_greeting.key, decodedResource.toStringResource().key)
    }

    @Test
    fun `Given string resource with args When encoded Then JSON stores resource key and args`() {
        val resource = TextResource.fromStringResWithArgs(
            Res.string.text_resource_greeting_with_name,
            "DevPulse"
        )
        val encoded = json.encodeToString<TextResource>(resource)

        val decoded = json.decodeFromString<TextResource>(encoded)

        assertTrue(encoded.contains("\"key\":\"text_resource_greeting_with_name\""))
        assertTrue(encoded.contains("DevPulse"))
        assertFalse(encoded.contains("\"res\""))
        val decodedResource = assertIs<StringResWithArgsTextResource>(decoded)
        assertEquals(
            Res.string.text_resource_greeting_with_name.key,
            decodedResource.toStringResource().key
        )
    }

    @Test
    fun `Given plural resource When encoded Then JSON stores resource key and quantity`() {
        val resource = TextResource.fromPluralRes(Res.plurals.text_resource_item_count, quantity = 2)
        val encoded = json.encodeToString<TextResource>(resource)

        val decoded = json.decodeFromString<TextResource>(encoded)

        assertTrue(encoded.contains("\"key\":\"text_resource_item_count\""))
        assertTrue(encoded.contains("\"quantity\":2"))
        assertFalse(encoded.contains("\"res\""))
        val decodedResource = assertIs<PluralResTextResource>(decoded)
        assertEquals(
            Res.plurals.text_resource_item_count.key,
            decodedResource.toPluralStringResource().key
        )
    }

    @Test
    fun `Given joined text When encoded and decoded Then nested values round trip`() {
        val resource = TextResource.fromJoined(
            SimpleTextResource("Hello"),
            TextResource.fromStringRes(Res.string.text_resource_greeting),
            separator = " "
        )

        val decoded = resource.roundTrip()

        assertEquals(resource, decoded)
    }

    @Test
    fun `Given styled text When encoded and decoded Then style round trips`() {
        val resource = TextResource.bold(TextResource.fromStringRes(Res.string.text_resource_greeting))

        val decoded = resource.roundTrip()

        assertEquals(resource, decoded)
    }

    @Test
    fun `Given URL text When encoded and decoded Then URL round trips`() {
        val resource = TextResource.url(
            resource = TextResource.fromText("DevPulse"),
            url = "https://example.com"
        )

        val decoded = resource.roundTrip()

        assertEquals(resource, decoded)
    }

    @Test
    fun `Given clickable text When encoded and decoded Then tag round trips`() {
        val resource = TextResource.clickable(
            resource = TextResource.fromText("Open"),
            tag = "open-settings"
        )

        val decoded = resource.roundTrip()

        assertEquals(resource, decoded)
    }

    private inline fun <reified T : TextResource> T.roundTrip(): TextResource =
        json.decodeFromString<TextResource>(json.encodeToString<TextResource>(this))
}
