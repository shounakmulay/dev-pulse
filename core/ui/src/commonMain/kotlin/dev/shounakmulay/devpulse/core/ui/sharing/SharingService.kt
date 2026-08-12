package dev.shounakmulay.devpulse.core.ui.sharing

import androidx.compose.runtime.Composable

interface SharingService {
    fun share(text: String, link: String)
}

@Composable
expect fun rememberSharingService(onCopiedToClipboard: () -> Unit): SharingService