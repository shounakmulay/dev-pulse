package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant

@Composable
internal fun SettingsSectionHeading(title: String) {
    DPTextView(
        modifier = Modifier.padding(16.dp),
        text = title,
        variant = DPTextViewVariant.TitleMediumEmphasized
    )
}
