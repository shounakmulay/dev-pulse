package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui

internal fun articleOrigin(url: String): String? {
    val scheme = url.substringBefore("://", "").lowercase()
    if (scheme !in setOf("http", "https")) return null
    val authority = url.substringAfter("://").substringBefore('/').substringBefore('?').substringBefore('#')
    if ('@' in authority || !authority.matches(Regex("[A-Za-z0-9.-]+(?::[0-9]+)?"))) return null
    return "$scheme://${authority.lowercase()}".takeIf { it.length <= 100 }
}
