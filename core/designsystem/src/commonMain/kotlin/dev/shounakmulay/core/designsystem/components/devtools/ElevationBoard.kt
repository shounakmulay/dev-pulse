package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private data class ElevationEntry(val label: String, val elevation: Dp)

private val elevationLevels = listOf(
    ElevationEntry("0 dp (Level 0)", 0.dp),
    ElevationEntry("1 dp (Level 1)", 1.dp),
    ElevationEntry("3 dp (Level 2)", 3.dp),
    ElevationEntry("6 dp (Level 3)", 6.dp),
    ElevationEntry("8 dp (Level 4)", 8.dp),
    ElevationEntry("12 dp (Level 5)", 12.dp),
)

@Composable
fun ElevationBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(elevationLevels) { entry ->
            Surface(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = entry.elevation,
            ) {
                Box(
                    modifier = Modifier.padding(12.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = entry.label,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
