package dev.shounakmulay.devpulse.core.domain.models.post

import kotlinx.serialization.Serializable

@Serializable
enum class FeedsPostListItemVariant {
    XS, S, M, L, XL;

    companion object {
        val DEFAULT = M
    }
}
