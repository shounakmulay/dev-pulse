package dev.shounakmulay.devpulse.core.ui.image

fun getWebsiteImageUrl(link: String?, sourceUrl: String): String {
    val domain = getDomain(link, sourceUrl)
    return "https://www.google.com/s2/favicons?domain=$domain&sz=128"
}

fun getWebsiteImageUrl(sourceUrl: String): String {
    return getWebsiteImageUrl(null, sourceUrl)
}

private fun getDomain(link: String?, sourceUrl: String): String {
    val candidate = link?.takeIf { it.isNotBlank() } ?: sourceUrl
    return extractHost(candidate) ?: extractHost(sourceUrl) ?: candidate
}

private fun extractHost(url: String): String? =
    url.trim()
        .substringAfter("://")      // no-op if there's no scheme
        .substringBefore('/')
        .substringBefore('?')
        .substringBefore('#')
        .substringAfterLast('@')    // strip user:pass@
        .substringBefore(':')       // strip port
        .removePrefix("www.")
        .lowercase()
        .takeIf { it.isNotEmpty() }