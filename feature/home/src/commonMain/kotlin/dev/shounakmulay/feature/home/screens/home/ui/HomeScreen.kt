package dev.shounakmulay.feature.home.screens.home.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import dev.shounakmulay.core.designsystem.components.DPIconButton
import dev.shounakmulay.core.designsystem.components.DPTextView
import dev.shounakmulay.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.core.designsystem.theme.DPIntent
import dev.shounakmulay.core.designsystem.theme.monoFontFamily
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen
import dev.shounakmulay.core.ui.screen.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navigator: Navigator) {
    Screen(
        viewModel = viewModel,
        topAppBar = {
            TopAppBar(title = {
                Row {
                    DPTextView(
                        text = "DEV",
                        variant = DPTextViewVariant.HeadingLargeEmphasized,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = monoFontFamily(),
                    )
                    DPTextView(
                        "Pulse",
                        variant = DPTextViewVariant.HeadingLargeEmphasized,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic,
                    )
                }
            }, actions = {
                DPIconButton(
                    icon = Icons.Default.Settings,
                    intent = DPIntent.Secondary,
                    contentDescription = "",
                    onClick = { navigator.navigate(Screen.Settings, onRootStack = true) }
                )
            })
        },
        onEffect = {}
    ) {
        HomeScreenContent(state = this)
    }
}

@Composable
fun HomeScreenContent(
    state: HomeScreenState
) {

}
