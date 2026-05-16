package dev.shounakmulay.devpulse.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey

@Composable
fun ScreenPlaceholder(
    screen: NavKey,
    onOpenSettings: () -> Unit,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            IconButton(
                onClick = onOpenSettings,
            ) {
                Icon(Icons.Default.Settings, contentDescription = "")
            }
        }
        Text(screen.toString())
    }
}