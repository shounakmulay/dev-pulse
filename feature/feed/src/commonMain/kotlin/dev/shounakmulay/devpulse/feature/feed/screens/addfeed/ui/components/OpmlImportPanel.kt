package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPCard
import dev.shounakmulay.devpulse.core.designsystem.components.DPCardStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPCardVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextField
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asString
import devpulse.core.resources.generated.resources.add_feed_action_paste_opml
import devpulse.core.resources.generated.resources.add_feed_opml_cancel
import devpulse.core.resources.generated.resources.add_feed_opml_placeholder
import devpulse.core.resources.generated.resources.add_feed_opml_process
import devpulse.core.resources.generated.resources.add_feed_opml_text
import devpulse.core.resources.generated.resources.add_feed_opml_title
import org.jetbrains.compose.resources.stringResource

fun LazyGridScope.opmlImportPanel(
    visible: Boolean,
    text: String,
    error: TextResource?,
    onOpen: () -> Unit,
    onTextChanged: (String) -> Unit,
    onProcess: () -> Unit,
    onCancel: () -> Unit
) {
    item(key = "OpmlImportPanel", span = { GridItemSpan(maxLineSpan) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalDPSpacing.current.sm)
                .animateItem()
        ) {
            if (visible) {
                DPCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = DPCardVariant.Default,
                    style = DPCardStyle.Elevated
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(LocalDPSpacing.current.lg)
                    ) {
                        DPTextView(
                            text = stringResource(stringRes.add_feed_opml_title),
                            variant = DPTextViewVariant.TitleMediumEmphasized
                        )
                        Spacer(Modifier.height(LocalDPSpacing.current.md))
                        DPTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = text,
                            onValueChange = onTextChanged,
                            label = stringResource(stringRes.add_feed_opml_text),
                            placeholder = stringResource(stringRes.add_feed_opml_placeholder),
                            supportingText = error?.asString(),
                            isError = error != null,
                            minLines = 8,
                            maxLines = 16
                        )
                        Spacer(Modifier.height(LocalDPSpacing.current.md))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            DPButton(
                                text = stringResource(stringRes.add_feed_opml_cancel),
                                style = DPButtonStyle.Text,
                                variant = DPButtonVariant.Secondary,
                                onClick = onCancel
                            )
                            DPButton(
                                text = stringResource(stringRes.add_feed_opml_process),
                                onClick = onProcess
                            )
                        }
                    }
                }
            } else {
                DPButton(
                    text = stringResource(stringRes.add_feed_action_paste_opml),
                    style = DPButtonStyle.Tonal,
                    variant = DPButtonVariant.Secondary,
                    onClick = onOpen
                )
            }
        }
    }
}
