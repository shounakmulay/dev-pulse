package dev.shounakmulay.feature.devtools.ui

import androidx.compose.runtime.Composable
import dev.shounakmulay.core.designsystem.components.devtools.DesignSystemBoard
import dev.shounakmulay.core.navigation.Navigator

@Composable
fun DesignSystemBoardScreen(navigator: Navigator) {
    DesignSystemBoard(navigator)
}