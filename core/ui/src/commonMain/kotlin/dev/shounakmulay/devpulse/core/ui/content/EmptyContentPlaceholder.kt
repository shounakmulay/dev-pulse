package dev.shounakmulay.devpulse.core.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.compose.DPScreenPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.no_results_found
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyContentPlaceholder(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    text: String = stringResource(stringRes.no_results_found)
) {
    EmptyContentPlaceholder(
        modifier = modifier,
        icon = icon,
    ) {
        DPTextView(
            text = text,
            variant = DPTextViewVariant.TitleMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun EmptyContentPlaceholder(
    modifier: Modifier = Modifier,
    icon: ImageVector?,
    content: @Composable ColumnScope.() -> Unit
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize().padding(LocalDPSpacing.current.xl),
        contentAlignment = Alignment.Center,
    ) {
        val iconSize = (maxWidth * 0.25f).coerceIn(72.dp, 96.dp)
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (icon != null) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
                Spacer(Modifier.size(LocalDPSpacing.current.xxl))
            }
            content()
        }
    }
}

@DPScreenPreview
@Composable
private fun EmptyContentPlaceholderPreview() {
    Preview {
        EmptyContentPlaceholder(text = "No articles found")
    }
}
