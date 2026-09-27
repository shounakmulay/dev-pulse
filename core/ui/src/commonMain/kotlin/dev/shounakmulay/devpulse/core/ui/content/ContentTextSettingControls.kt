package dev.shounakmulay.devpulse.core.ui.content

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import dev.shounakmulay.devpulse.core.designsystem.components.DPSelectionVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPSlider
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.text_size
import devpulse.core.resources.generated.resources.text_spacing
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun ContentTextSettingControls(
    contentTextSettings: UIContentTextSettings,
    scrollState: ScrollState = rememberScrollState(),
    onTextScaleChanged: (Float) -> Unit,
    onLineHeightScaleChanged: (Float) -> Unit,
    header: @Composable ColumnScope.() -> Unit = {},
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    val haptics = LocalHapticFeedback.current
    Column(
        Modifier
            .padding(horizontal = LocalDPSpacing.current.lg)
            .verticalScroll(scrollState)
    ) {
        header()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                LocalDPSpacing.current.sm,
                Alignment.Start
            )
        ) {
            Icon(
                DPIcons.FormatText,
                contentDescription = null
            )
            DPTextView(
                text = stringResource(stringRes.text_size),
                variant = DPTextViewVariant.LabelMedium
            )
            Spacer(Modifier.weight(1f))
            val percentage = remember(contentTextSettings.textScale) {
                (contentTextSettings.textScale * 100).roundToInt()
            }
            DPTextView(
                text = "$percentage%",
                variant = DPTextViewVariant.LabelMediumEmphasized
            )
        }
        Spacer(Modifier.height(LocalDPSpacing.current.md))
        DPSlider(
            modifier = Modifier.padding(horizontal = LocalDPSpacing.current.md),
            value = contentTextSettings.textScale,
            onValueChange = {
                if (it != contentTextSettings.textScale) {
                    haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    onTextScaleChanged(it)
                }
            },
            valueRange = 0.8f..1.5f,
            steps = 6,
            variant = DPSelectionVariant.Tertiary
        )
        Spacer(Modifier.height(LocalDPSpacing.current.xl))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                LocalDPSpacing.current.sm,
                Alignment.Start
            )
        ) {
            Icon(
                DPIcons.FormatLineSpacing,
                contentDescription = null
            )
            DPTextView(
                text = stringResource(stringRes.text_spacing),
                variant = DPTextViewVariant.LabelMedium
            )
            Spacer(Modifier.weight(1f))
            val percentage = remember(contentTextSettings.lineHeightScale) {
                (contentTextSettings.lineHeightScale * 100).toInt()
            }
            DPTextView(
                text = "$percentage%",
                variant = DPTextViewVariant.LabelMediumEmphasized
            )
        }
        Spacer(Modifier.height(LocalDPSpacing.current.md))
        DPSlider(
            modifier = Modifier.padding(horizontal = LocalDPSpacing.current.md),
            value = contentTextSettings.lineHeightScale,
            onValueChange = {
                if (it != contentTextSettings.lineHeightScale) {
                    haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    onLineHeightScaleChanged(it)
                }
            },
            valueRange = 1f..1.5f,
            steps = 4,
            variant = DPSelectionVariant.Tertiary
        )
        Spacer(Modifier.height(LocalDPSpacing.current.lg))
        footer()
    }
}