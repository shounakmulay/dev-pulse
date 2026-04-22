package dev.shounakmulay.feature.devtools.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.core.designsystem.components.DPClickableRow
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen

private val developerToolsScreens = listOf(
    Screen.DeveloperTools.DesignSystemBoard
)

@Composable
fun DeveloperToolsScreen(navigator: Navigator, modifier: Modifier = Modifier) {
    Scaffold { padding ->
        LazyColumn(modifier = modifier.padding(padding)) {
            items(developerToolsScreens) {
                DPClickableRow(
                    title = it.toString(),
                    onClick = {
                        navigator.navigate(
                            it,
                            onRootStack = true
                        )
                    },
                    trailingIcon = Icons.Filled.ChevronRight,
                )
            }
        }
    }
}

