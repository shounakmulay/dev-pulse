package dev.shounakmulay.devpulse.feature.settings.screens.articleSettings.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPSegmentedButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPSingleChoiceSegmentedButtonRow
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningMode
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.browser_opening_description
import devpulse.core.resources.generated.resources.external_browser
import devpulse.core.resources.generated.resources.in_app_browser
import devpulse.core.resources.generated.resources.open_in_browser
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BrowserOpeningModeSelector(
    selectedMode: BrowserOpeningMode,
    enabled: Boolean,
    onModeSelected: (BrowserOpeningMode) -> Unit,
) {
    val spacing = LocalDPSpacing.current
    DPTextView(text = stringResource(stringRes.open_in_browser), variant = DPTextViewVariant.LabelMedium)
    Spacer(Modifier.height(spacing.sm))
    DPTextView(text = stringResource(stringRes.browser_opening_description), variant = DPTextViewVariant.BodySmall)
    Spacer(Modifier.height(spacing.md))
    DPSingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        BrowserOpeningMode.entries.forEachIndexed { index, mode ->
            DPSegmentedButton(
                text = stringResource(
                    when (mode) {
                        BrowserOpeningMode.IN_APP_BROWSER -> stringRes.in_app_browser
                        BrowserOpeningMode.EXTERNAL_BROWSER -> stringRes.external_browser
                    },
                ),
                selected = selectedMode == mode,
                enabled = enabled,
                onClick = { onModeSelected(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = BrowserOpeningMode.entries.size),
            )
        }
    }
    Spacer(Modifier.height(spacing.xl))
}
