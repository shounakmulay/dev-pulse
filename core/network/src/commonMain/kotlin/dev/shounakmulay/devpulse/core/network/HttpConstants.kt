package dev.shounakmulay.devpulse.core.network

import io.ktor.http.HttpHeaders

object HttpHeaders {
    const val USER_AGENT = HttpHeaders.UserAgent
    const val IF_NONE_MATCH = HttpHeaders.IfNoneMatch
    const val ETAG = HttpHeaders.ETag
}

object HttpUserAgent {
    const val BROWSER = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
}

object HttpStatusCodes {
    const val OK = 200
    const val NOT_MODIFIED = 304
}