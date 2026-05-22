package dev.shounakmulay.devpulse.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPCard
import dev.shounakmulay.devpulse.core.designsystem.components.DPCardStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPCardVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPHorizontalDivider
import dev.shounakmulay.devpulse.core.designsystem.components.DPListItem
import dev.shounakmulay.devpulse.core.designsystem.components.DPVerticalDivider
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.DPDensity

@Composable
fun CardsAndListsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DPCardStyle.entries.forEach { style ->
            item { DPTextView(text = "DPCard – $style", variant = DPTextViewVariant.BodyMedium) }
            item {
                DPCard(modifier = Modifier.fillMaxWidth(), style = style) {
                    Text("$style card content", modifier = Modifier.padding(16.dp))
                }
            }
        }

        item { DPTextView(text = "DPCard – variant samples", variant = DPTextViewVariant.BodyMedium) }
        DPCardVariant.entries.drop(1).forEach { variant ->
            item {
                DPCard(modifier = Modifier.fillMaxWidth(), variant = variant) {
                    Text("$variant", modifier = Modifier.padding(16.dp))
                }
            }
        }

        item { DPHorizontalDivider() }

        item { DPTextView(text = "DPListItem – text-first (density)", variant = DPTextViewVariant.BodyMedium) }
        item {
            DPListItem(
                headlineText = "Default",
                supportingText = "Supporting text",
                leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
            )
        }
        DPDensity.entries.forEach { density ->
            item {
                DPListItem(
                    headlineText = "Density: $density",
                    onClick = {},
                    supportingText = "Clickable, density = $density",
                    density = density,
                )
            }
        }
        item {
            DPListItem(
                headlineText = "Selected",
                onClick = {},
                selected = true,
                supportingText = "Branches to M3 selectable ListItem",
            )
        }

        item { DPHorizontalDivider() }

        item { DPTextView(text = "DPVerticalDivider", variant = DPTextViewVariant.BodyMedium) }
        item {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Left")
                DPVerticalDivider()
                Text("Right")
            }
        }
    }
}
