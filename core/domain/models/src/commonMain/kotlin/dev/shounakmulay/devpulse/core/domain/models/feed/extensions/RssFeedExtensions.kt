package dev.shounakmulay.devpulse.core.domain.models.feed.extensions

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed

fun RssFeed.computeDisplayString(): String {
    return name.orEmpty().ifBlank { title.orEmpty() }
}
