package dev.shounakmulay.devpulse.core.ui.text.serialization

import devpulse.core.resources.generated.resources.Res
import devpulse.core.resources.generated.resources.allPluralStringResources
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.compose.resources.PluralStringResource

object PluralStringResourceSerializer : KSerializer<PluralStringResource> {
    override val descriptor =
        PrimitiveSerialDescriptor("PluralStringResource", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PluralStringResource) =
        encoder.encodeString(value.key)

    override fun deserialize(decoder: Decoder): PluralStringResource {
        val key = decoder.decodeString()
        return Res.allPluralStringResources[key] ?: error("StringResource not found for $key")
    }
}
