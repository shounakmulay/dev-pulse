package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing

@Composable
fun FormFieldTitle(text: String) {
    DPTextView(
        text = text,
        modifier = Modifier.padding(LocalDPSpacing.current.md),
        variant = DPTextViewVariant.TitleMediumEmphasized
    )
}