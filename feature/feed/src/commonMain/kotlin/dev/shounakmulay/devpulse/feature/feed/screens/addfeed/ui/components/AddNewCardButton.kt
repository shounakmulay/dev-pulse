package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing

@Composable
fun LazyItemScope.AddNewCardButton(onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(LocalDPSpacing.current.md).animateItem(),
        horizontalArrangement = Arrangement.Center
    ) {
        DPIconButton(
            icon = DPIcons.Add,
            style = DPIconButtonStyle.Filled,
            variant = DPIconButtonVariant.Secondary,
            contentDescription = "",
            onClick = onClick
        )
    }
}