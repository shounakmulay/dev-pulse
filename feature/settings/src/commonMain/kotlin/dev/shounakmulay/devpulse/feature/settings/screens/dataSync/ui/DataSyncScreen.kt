package dev.shounakmulay.devpulse.feature.settings.screens.dataSync.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import devpulse.core.resources.generated.resources.data_and_sync
import devpulse.core.resources.generated.resources.discovery_data_section
import devpulse.core.resources.generated.resources.discovery_refresh_action
import devpulse.core.resources.generated.resources.discovery_refresh_description
import devpulse.core.resources.generated.resources.discovery_refresh_failed
import devpulse.core.resources.generated.resources.discovery_refresh_succeeded
import devpulse.core.resources.generated.resources.discovery_refreshing
import org.jetbrains.compose.resources.stringResource

@Composable
fun DataSyncScreen(viewModel: DataSyncViewModel, navigator: Navigator) {
    Screen(
        viewModel = viewModel,
        onEffect = viewModel::unhandledEffect,
        topAppBar = {
            DPTopAppBar(
                title = stringResource(stringRes.data_and_sync),
                navigationIcon = { DPBackNavigationIconButton(onNavigateBack = navigator::navigateBack) },
            )
        },
    ) { state ->
        val spacing = LocalDPSpacing.current
        Column(
            modifier = Modifier.fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            DPTextView(
                text = stringResource(stringRes.discovery_data_section),
                variant = DPTextViewVariant.HeadingSmallEmphasized,
            )
            DPTextView(
                text = stringResource(stringRes.discovery_refresh_description),
                variant = DPTextViewVariant.BodyMedium,
            )
            DPButton(
                text = stringResource(stringRes.discovery_refresh_action),
                modifier = Modifier.fillMaxWidth(),
                enabled = state.discoveryRefreshStatus != DiscoveryRefreshStatus.Refreshing,
                onClick = { viewModel.onEvent(DataSyncScreenEvent.RefreshDiscovery) },
            )
            when (state.discoveryRefreshStatus) {
                DiscoveryRefreshStatus.Idle -> Unit

                DiscoveryRefreshStatus.Refreshing -> DPTextView(
                    text = stringResource(stringRes.discovery_refreshing),
                    variant = DPTextViewVariant.BodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                DiscoveryRefreshStatus.Succeeded -> DPTextView(
                    text = stringResource(stringRes.discovery_refresh_succeeded),
                    variant = DPTextViewVariant.BodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                DiscoveryRefreshStatus.Failed -> DPTextView(
                    text = stringResource(stringRes.discovery_refresh_failed),
                    variant = DPTextViewVariant.BodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
