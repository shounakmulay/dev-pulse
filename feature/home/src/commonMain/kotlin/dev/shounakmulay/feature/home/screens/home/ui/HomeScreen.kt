package dev.shounakmulay.feature.home.screens.home.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.shounakmulay.core.designsystem.components.DPIconButton
import dev.shounakmulay.core.designsystem.icon.DPIcons
import dev.shounakmulay.core.designsystem.theme.DPIntent
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
                Icon(
                    imageVector = DPIcons.devPulseIconLarge(),
                    contentDescription = "DevPulse",
                    tint = Color.Unspecified
                )
            }, actions = {
                DPIconButton(
                    icon = DPIcons.UserSettings,
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
