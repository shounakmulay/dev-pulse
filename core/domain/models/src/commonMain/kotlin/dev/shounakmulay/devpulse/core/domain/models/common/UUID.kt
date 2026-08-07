package dev.shounakmulay.devpulse.core.domain.models.common

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class UUID(val value: String)