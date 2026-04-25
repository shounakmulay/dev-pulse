package dev.shounakmulay.devpulse.core.ui.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.navigate_back
import org.jetbrains.compose.resources.stringResource

@Composable
fun DPBackNavigationIconButton(modifier: Modifier = Modifier, onNavigateBack: () -> Unit, ) {
    DPIconButton(
        modifier = modifier,
        icon = Icons.AutoMirrored.Default.ArrowBack,
        contentDescription = stringResource(stringRes.navigate_back),
        onClick = onNavigateBack
    )
}