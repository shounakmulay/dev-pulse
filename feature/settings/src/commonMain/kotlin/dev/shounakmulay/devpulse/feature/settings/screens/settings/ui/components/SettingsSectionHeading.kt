package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing

@Composable
internal fun SettingsSectionHeading(title: String, description: String? = null) {
    val spacing = LocalDPSpacing.current
    Column(
        modifier = Modifier.padding(
            start = spacing.screenHorizontal,
            top = spacing.lg,
            end = spacing.screenHorizontal,
            bottom = spacing.sm,
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        DPTextView(
            modifier = Modifier.semantics { heading() },
            text = title,
            color = MaterialTheme.colorScheme.primary,
            variant = DPTextViewVariant.TitleMediumEmphasized,
        )
        if (description != null) {
            DPTextView(
                text = description,
                variant = DPTextViewVariant.LabelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@DPComponentPreview
@Composable
private fun SettingsSectionHeadingPreview() {
    Preview {
        SettingsSectionHeading(
            title = "Appearance",
            description = "Customize how DevPulse looks and feels.",
        )
    }
}
