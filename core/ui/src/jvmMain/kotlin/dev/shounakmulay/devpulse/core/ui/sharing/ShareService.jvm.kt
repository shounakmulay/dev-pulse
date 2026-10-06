package dev.shounakmulay.devpulse.core.ui.sharing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.core.annotation.Factory
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Factory
class JvmSharingService(private val onLinkCopied: () -> Unit) : SharingService {
    override fun share(text: String) {
        Toolkit.getDefaultToolkit().systemClipboard?.setContents(
            StringSelection(text),
            null
        )
        onLinkCopied()
    }

    override fun share(text: String, link: String) {
        share(text = "$text\n$link")
    }
}

@Composable
actual fun rememberSharingService(onCopiedToClipboard: () -> Unit): SharingService {
    val sharingService = remember {
        JvmSharingService {
            onCopiedToClipboard()
        }
    }
    return sharingService
}
