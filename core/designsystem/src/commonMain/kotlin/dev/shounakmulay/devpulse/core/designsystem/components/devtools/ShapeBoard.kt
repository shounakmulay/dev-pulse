package dev.shounakmulay.devpulse.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

private data class ShapeEntry(val name: String, val shape: Shape)

@Composable
fun ShapeBoard(modifier: Modifier = Modifier) {
    val shapes = MaterialTheme.shapes
    val entries = listOf(
        ShapeEntry("extraSmall", shapes.extraSmall),
        ShapeEntry("small", shapes.small),
        ShapeEntry("medium", shapes.medium),
        ShapeEntry("large", shapes.large),
        ShapeEntry("extraLarge", shapes.extraLarge),
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(entries) { entry ->
            Text(
                text = entry.name,
                style = MaterialTheme.typography.labelMedium,
            )
            Surface(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                shape = entry.shape,
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 2.dp,
            ) {}
        }
    }
}
