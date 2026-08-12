package dev.shounakmulay.devpulse.core.webview.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton

@Composable
fun WebViewScreen(url: String, navigator: Navigator) {
    Scaffold(
        topBar = {
            DPTopAppBar(title = "", navigationIcon = {
                DPBackNavigationIconButton {
                    navigator.navigateBack()
                }
            })
        }
    ) {
        val state = rememberWebViewState(url)
        WebView(modifier = Modifier.padding(it), state = state)
    }
}
