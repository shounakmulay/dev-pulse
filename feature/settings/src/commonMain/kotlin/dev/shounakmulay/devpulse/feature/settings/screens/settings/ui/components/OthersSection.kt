package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import dev.shounakmulay.devpulse.core.common.BuildConfig
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.licenses
import devpulse.core.resources.generated.resources.others
import devpulse.core.resources.generated.resources.source_code
import devpulse.core.resources.generated.resources.view_on_github
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun OthersSection(onLicensesClick: () -> Unit) {
    Column {
        SettingsSectionHeading(title = stringResource(stringRes.others))
        SettingsSubPageLink(
            headlineText = stringResource(stringRes.licenses),
            onClick = onLicensesClick
        )
        val uriHandler = LocalUriHandler.current
        SettingsExternalPageLink(
            headlineText = stringResource(stringRes.source_code),
            supportingText = stringResource(stringRes.view_on_github),
        ) {
            uriHandler.openUri(BuildConfig.GITHUB_LINK)
        }
    }
}