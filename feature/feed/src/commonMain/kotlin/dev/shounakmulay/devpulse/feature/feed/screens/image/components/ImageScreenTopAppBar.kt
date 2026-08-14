package dev.shounakmulay.devpulse.feature.feed.screens.image.components

import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBarVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton

@Composable
internal fun ImageScreenTopAppBar(
    onBack: () -> Unit,
    isDarkTheme: Boolean,
    onDarkThemeChanged: () -> Unit
) {
    DPTopAppBar(
        variant = DPTopAppBarVariant.Small,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = "",
        navigationIcon = {
            DPBackNavigationIconButton(onNavigateBack = onBack)
        },
        actions = {

            DPIconButton(
                icon = if (isDarkTheme) DPIcons.DarkTheme else DPIcons.LightTheme,
                contentDescription = "",
                onClick = onDarkThemeChanged,
            )
        }
    )
}