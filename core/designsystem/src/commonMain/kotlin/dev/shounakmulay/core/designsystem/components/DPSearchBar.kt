@file:OptIn(ExperimentalMaterial3Api::class)

package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview

@Composable
fun DPSearchBar(
    state: SearchBarState,
    inputField: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = SearchBarDefaults.inputFieldShape,
    colors: SearchBarColors = SearchBarDefaults.colors(),
    tonalElevation: Dp = SearchBarDefaults.TonalElevation,
    shadowElevation: Dp = SearchBarDefaults.ShadowElevation,
) {
    SearchBar(
        state = state,
        inputField = inputField,
        modifier = modifier,
        shape = shape,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
    )
}

@Composable
fun DPSearchBar(
    inputField: @Composable () -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = SearchBarDefaults.inputFieldShape,
    colors: SearchBarColors = SearchBarDefaults.colors(),
    tonalElevation: Dp = SearchBarDefaults.TonalElevation,
    shadowElevation: Dp = SearchBarDefaults.ShadowElevation,
    windowInsets: WindowInsets = SearchBarDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit,
) {
    SearchBar(
        inputField = inputField,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
        shape = shape,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        windowInsets = windowInsets,
        content = content,
    )
}

@Composable
fun DPDockedSearchBar(
    inputField: @Composable () -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = SearchBarDefaults.dockedShape,
    colors: SearchBarColors = SearchBarDefaults.colors(),
    tonalElevation: Dp = SearchBarDefaults.TonalElevation,
    shadowElevation: Dp = SearchBarDefaults.ShadowElevation,
    content: @Composable ColumnScope.() -> Unit,
) {
    DockedSearchBar(
        inputField = inputField,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
        shape = shape,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        content = content,
    )
}

@DPComponentPreview
@Composable
private fun DPSearchBarPreview() {
    Preview {
        var expanded by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DPDockedSearchBar(
                inputField = { Text("Search") },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                content = { Text("Results") },
            )
        }
    }
}
