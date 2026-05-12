package dev.shounakmulay.devpulse.core.ui.text.serialization

import devpulse.core.resources.generated.resources.Res
import devpulse.core.resources.generated.resources.allStringResources
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.compose.resources.StringResource

object StringResourceSerializer : KSerializer<StringResource> {
    override val descriptor = PrimitiveSerialDescriptor("StringResource", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: StringResource) =
        encoder.encodeString(value.key)

    override fun deserialize(decoder: Decoder): StringResource {
        val key = decoder.decodeString()
        return Res.allStringResources[key] ?: error("StringResource not found for $key")
    }
}
