package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPBody
import dev.shounakmulay.core.designsystem.components.DPCard
import dev.shounakmulay.core.designsystem.components.DPElevatedCard
import dev.shounakmulay.core.designsystem.components.DPHorizontalDivider
import dev.shounakmulay.core.designsystem.components.DPListItem
import dev.shounakmulay.core.designsystem.components.DPOutlinedCard
import dev.shounakmulay.core.designsystem.components.DPVerticalDivider

@Composable
fun CardsAndListsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { DPBody(text = "DPCard") }
        item {
            DPCard(modifier = Modifier.fillMaxWidth()) {
                Text("Card content", modifier = Modifier.padding(16.dp))
            }
        }

        item { DPBody(text = "DPElevatedCard") }
        item {
            DPElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Text("Elevated card content", modifier = Modifier.padding(16.dp))
            }
        }

        item { DPBody(text = "DPOutlinedCard") }
        item {
            DPOutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Text("Outlined card content", modifier = Modifier.padding(16.dp))
            }
        }

        item { DPHorizontalDivider() }

        item { DPBody(text = "DPListItem") }
        item {
            DPListItem(
                headlineContent = { Text("Headline") },
                supportingContent = { Text("Supporting text") },
                leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
            )
        }
        item {
            DPListItem(
                headlineContent = { Text("Headline only") },
            )
        }

        item { DPHorizontalDivider() }

        item { DPBody(text = "DPVerticalDivider") }
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
