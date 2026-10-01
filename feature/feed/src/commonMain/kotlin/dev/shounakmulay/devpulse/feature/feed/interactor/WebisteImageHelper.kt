package dev.shounakmulay.devpulse.feature.feed.interactor

internal fun getWebsiteImageUrl(link: String?, sourceUrl: String): String =
    "https://www.google.com/s2/favicons?domain=${link ?: sourceUrl}&sz=128"