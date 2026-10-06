package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextField
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asString
import devpulse.core.resources.generated.resources.add_feed_opml_process
import devpulse.core.resources.generated.resources.add_feed_opml_title
import devpulse.core.resources.generated.resources.add_feed_opml_url
import devpulse.core.resources.generated.resources.add_feed_opml_url_placeholder
import devpulse.core.resources.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ImportOpmlBottomSheet(
    opmlUrl: String,
    opmlImportError: TextResource?,
    isOpmlLoading: Boolean,
    onExtractOpml: () -> Unit,
    onUrlChanged: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .align(Alignment.CenterHorizontally)
                .navigationBarsPadding()
                .imePadding()
                .padding(
                    start = LocalDPSpacing.current.lg,
                    end = LocalDPSpacing.current.lg,
                    bottom = LocalDPSpacing.current.lg
                )
        ) {
            DPTextView(
                text = stringResource(stringRes.add_feed_opml_title),
                variant = DPTextViewVariant.TitleMediumEmphasized
            )
            Spacer(Modifier.height(LocalDPSpacing.current.md))
            DPTextField(
                modifier = Modifier.fillMaxWidth(),
                value = opmlUrl,
                onValueChange = onUrlChanged,
                label = stringResource(stringRes.add_feed_opml_url),
                placeholder = stringResource(stringRes.add_feed_opml_url_placeholder),
                supportingText = opmlImportError?.asString(),
                enabled = !isOpmlLoading,
                isError = opmlImportError != null,
                singleLine = true
            )
            Spacer(Modifier.height(LocalDPSpacing.current.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                DPButton(
                    text = stringResource(stringRes.cancel),
                    style = DPButtonStyle.Text,
                    variant = DPButtonVariant.Secondary,
                    enabled = !isOpmlLoading,
                    onClick = onDismissRequest
                )
                DPButton(
                    text = stringResource(stringRes.add_feed_opml_process),
                    enabled = !isOpmlLoading,
                    onClick = onExtractOpml
                )
            }
        }
    }
}