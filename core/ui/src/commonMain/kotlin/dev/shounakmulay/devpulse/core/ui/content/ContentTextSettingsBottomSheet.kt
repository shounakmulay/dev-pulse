package dev.shounakmulay.devpulse.core.ui.content

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheet
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheetController
import dev.shounakmulay.devpulse.core.ui.bottomsheet.rememberDPModalBottomSheetController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentTextSettingsBottomSheet(
    controller: DPModalBottomSheetController = rememberDPModalBottomSheetController(),
    onTextScaleChanged: (Float) -> Unit,
    onLineHeightScaleChanged: (Float) -> Unit
) {
    DPModalBottomSheet(
        controller = controller,
    ) {
        val contentTextSettings by LocalContentTextSettings.current.collectAsStateWithLifecycle()
        ContentTextSettingControls(
            contentTextSettings = contentTextSettings,
            onTextScaleChanged = onTextScaleChanged,
            onLineHeightScaleChanged = onLineHeightScaleChanged
        )
    }
}
