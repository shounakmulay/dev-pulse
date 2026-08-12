package dev.shounakmulay.devpulse.core.ui.sharing

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.koin.core.annotation.Factory

@Factory
class AndroidSharingService(private val context: Context) : SharingService {
    override fun share(text: String, link: String) {
        val shareIntent = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, link)
                putExtra(Intent.EXTRA_TITLE, text)
            },
             null
        )
        context.startActivity(
            Intent
                .createChooser(shareIntent, "Share via").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
        )
    }

}

@Composable
actual fun rememberSharingService(onCopiedToClipboard: () -> Unit): SharingService {
    val context = LocalContext.current
    val sharingService = remember(context) {
        AndroidSharingService(context)
    }
    return sharingService
}