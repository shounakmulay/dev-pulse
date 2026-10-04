package dev.shounakmulay.devpulse.core.common.serializer

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


class ImmutableListSerializer<T>(
    elementSerializer: KSerializer<T>
) : KSerializer<ImmutableList<T>> {

    private val delegate = ListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(encoder: Encoder, value: ImmutableList<T>) {
        delegate.serialize(encoder, value.toList())
    }

    override fun deserialize(decoder: Decoder): ImmutableList<T> {
        return delegate.deserialize(decoder).toImmutableList()
    }
}