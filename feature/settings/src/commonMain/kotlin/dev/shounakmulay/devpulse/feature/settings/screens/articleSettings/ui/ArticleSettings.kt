package dev.shounakmulay.devpulse.feature.settings.screens.articleSettings.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.content.ContentTextContainer
import dev.shounakmulay.devpulse.core.ui.content.ContentTextSettingControls
import dev.shounakmulay.devpulse.core.ui.content.LocalContentTextSettings
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import devpulse.core.resources.generated.resources.article_reader
import org.jetbrains.compose.resources.stringResource

@Composable
fun ArticleSettingsScreen(
    viewModel: ArticleSettingsViewModel,
    navigator: Navigator
) {
    Screen(
        viewModel = viewModel,
        topAppBar = {
            DPTopAppBar(
                title = stringResource(stringRes.article_reader),
                navigationIcon = {
                    DPBackNavigationIconButton {
                        navigator.navigateBack()
                    }
                },
            )
        },
        onEffect = {
            when (it) {
                else -> viewModel.unhandledEffect(it)
            }
        },
    ) {
        val settings by LocalContentTextSettings.current.collectAsStateWithLifecycle()
        ContentTextSettingControls(
            contentTextSettings = settings,
            onTextScaleChanged = {
                viewModel.onEvent(ArticleSettingsEvent.SetContentTextScale(it))
            },
            onLineHeightScaleChanged = {
                viewModel.onEvent(ArticleSettingsEvent.SetContentLineHeightScale(it))
            },
            footer = {
                Spacer(Modifier.height(LocalDPSpacing.current.lg))
                ContentTextContainer {
                    Surface(
                        modifier = Modifier.padding(bottom = LocalDPSpacing.current.xl)
                            .animateContentSize(
                                spring(Spring.DampingRatioLowBouncy)
                            ),
                        shape = MaterialTheme.shapes.large,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                vertical = LocalDPSpacing.current.md,
                                horizontal = LocalDPSpacing.current.sm
                            )
                        ) {
                            DPTextView(
                                text = "As We May Think",
                                variant = DPTextViewVariant.HeadingMedium
                            )
                            Spacer(Modifier.height(LocalDPSpacing.current.lg))
                            DPTextView(
                                text = "“Consider a future device ... in which an individual " +
                                        "stores all his books, records, and communications, " +
                                        "and which is mechanized so that it may be consulted with " +
                                        "exceeding speed and flexibility. " +
                                        "It is an enlarged intimate supplement to his memory.”",
                                variant = DPTextViewVariant.TitleMedium
                            )
                            Spacer(Modifier.height(LocalDPSpacing.current.lg))
                            DPTextView(
                                text = "By Vannevar Bush",
                                variant = DPTextViewVariant.BodyMedium
                            )
                        }
                    }
                }
            },
        )
    }
}