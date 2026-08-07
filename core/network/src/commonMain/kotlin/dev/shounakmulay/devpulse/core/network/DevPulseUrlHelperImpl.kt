package dev.shounakmulay.devpulse.core.network

import io.ktor.http.Url
import io.ktor.http.protocolWithAuthority
import org.koin.core.annotation.Factory

@Factory
class DevPulseUrlHelperImpl : DevPulseUrlHelper {
    override fun getBaseUrl(url: String): String {
        return Url(url).protocolWithAuthority
    }
}