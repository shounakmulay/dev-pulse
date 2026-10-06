package dev.shounakmulay.devpulse.core.ui.sharing

import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import org.koin.core.annotation.Factory
import platform.Foundation.NSString
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Factory
class AppleSharingService : SharingService {
    @OptIn(BetaInteropApi::class)
    override fun share(text: String) {
        val activityItems = listOf(
            NSString.create(string = text)
        )
        val activityViewController = UIActivityViewController(
            activityItems = activityItems,
            applicationActivities = null
        )

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }

    override fun share(text: String, link: String) {
        share(text = "$text\n$link")
    }

}

@androidx.compose.runtime.Composable
actual fun rememberSharingService(onCopiedToClipboard: () -> Unit): dev.shounakmulay.devpulse.core.ui.sharing.SharingService {
    val sharingService = remember {
        AppleSharingService()
    }

    return sharingService
}
