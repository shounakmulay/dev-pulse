package dev.shounakmulay.devpulse.core.designsystem.components.devtools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.theme.info
import dev.shounakmulay.devpulse.core.designsystem.theme.infoContainer
import dev.shounakmulay.devpulse.core.designsystem.theme.onInfo
import dev.shounakmulay.devpulse.core.designsystem.theme.onInfoContainer
import dev.shounakmulay.devpulse.core.designsystem.theme.onSuccess
import dev.shounakmulay.devpulse.core.designsystem.theme.onSuccessContainer
import dev.shounakmulay.devpulse.core.designsystem.theme.onWarning
import dev.shounakmulay.devpulse.core.designsystem.theme.onWarningContainer
import dev.shounakmulay.devpulse.core.designsystem.theme.success
import dev.shounakmulay.devpulse.core.designsystem.theme.successContainer
import dev.shounakmulay.devpulse.core.designsystem.theme.warning
import dev.shounakmulay.devpulse.core.designsystem.theme.warningContainer

private data class SemanticRole(
    val name: String,
    val icon: ImageVector,
    val description: String,
)

private val semanticRoles = listOf(
    SemanticRole("Success", Icons.Default.Check, "Operation completed successfully"),
    SemanticRole("Warning", Icons.Default.Warning, "Proceed with caution"),
    SemanticRole("Info", Icons.Default.Info, "Here is some helpful information"),
    SemanticRole("Error", Icons.Default.Error, "Something went wrong"),
)

@Composable
fun SemanticColorsBoard(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val roleColors = listOf(
        Triple(cs.successContainer, cs.onSuccessContainer, cs.success),
        Triple(cs.warningContainer, cs.onWarningContainer, cs.warning),
        Triple(cs.infoContainer, cs.onInfoContainer, cs.info),
        Triple(cs.errorContainer, cs.onErrorContainer, cs.error),
    )
    val onRoleColors = listOf(
        cs.onSuccess,
        cs.onWarning,
        cs.onInfo,
        cs.onError,
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            SectionLabel("Alert Banners")
        }
        items(semanticRoles.zip(roleColors)) { (role, colors) ->
            val (container, onContainer, _) = colors
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = container,
                    contentColor = onContainer,
                ),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = role.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = role.name,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Text(
                            text = role.description,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }

        item {
            SectionLabel("Filled Banners")
        }
        items(semanticRoles.zip(roleColors.zip(onRoleColors))) { (role, pair) ->
            val (colors, onFilled) = pair
            val (_, _, filled) = colors
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = filled,
                    contentColor = onFilled,
                ),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = role.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = role.name,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }

        item {
            SectionLabel("Status Chips")
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(semanticRoles.zip(roleColors)) { (role, colors) ->
                    val (container, onContainer, _) = colors
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = { Text(role.name) },
                        leadingIcon = {
                            Icon(
                                imageVector = role.icon,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = container,
                            selectedLabelColor = onContainer,
                            selectedLeadingIconColor = onContainer,
                        ),
                    )
                }
            }
        }

        item {
            SectionLabel("Inline Status Labels")
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.medium,
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val textColors = listOf(
                    roleColors[0].third,
                    roleColors[1].third,
                    roleColors[2].third,
                    roleColors[3].third,
                )
                semanticRoles.zip(textColors).forEach { (role, color) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = role.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = color,
                        )
                        Text(
                            text = "${role.name}: ${role.description}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = color,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}
