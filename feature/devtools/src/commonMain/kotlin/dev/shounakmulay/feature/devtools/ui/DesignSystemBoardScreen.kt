package dev.shounakmulay.feature.devtools.ui

import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.core.designsystem.components.devtools.DesignSystemBoard
import dev.shounakmulay.devpulse.core.navigation.Navigator

@Composable
fun DesignSystemBoardScreen(navigator: Navigator) {
    DesignSystemBoard(navigator)
}