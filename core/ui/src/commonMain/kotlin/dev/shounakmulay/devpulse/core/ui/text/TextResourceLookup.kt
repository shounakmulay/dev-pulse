package dev.shounakmulay.devpulse.core.ui.text

import devpulse.core.resources.generated.resources.Res
import devpulse.core.resources.generated.resources.allPluralStringResources
import devpulse.core.resources.generated.resources.allStringResources
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource

internal fun StringResTextResource.toStringResource(): StringResource =
    stringResourceForKey(key)

internal fun StringResWithArgsTextResource.toStringResource(): StringResource =
    stringResourceForKey(key)

internal fun PluralResTextResource.toPluralStringResource(): PluralStringResource =
    Res.allPluralStringResources[key] ?: error("PluralStringResource not found for $key")

private fun stringResourceForKey(key: String): StringResource =
    Res.allStringResources[key] ?: error("StringResource not found for $key")
