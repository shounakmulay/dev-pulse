@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview

@Composable
fun DPIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = IconButtonDefaults.standardShape,
    content: @Composable () -> Unit,
) = IconButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    colors = colors,
    interactionSource = interactionSource,
    shape = shape,
    content = content,
)

@Composable
fun DPIconButton(
    onClick: () -> Unit,
    shapes: IconButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = IconButton(
    onClick = onClick,
    shapes = shapes,
    modifier = modifier,
    enabled = enabled,
    colors = colors,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconToggleButtonColors = IconButtonDefaults.iconToggleButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = IconButtonDefaults.standardShape,
    content: @Composable () -> Unit,
) = IconToggleButton(
    checked = checked,
    onCheckedChange = onCheckedChange,
    modifier = modifier,
    enabled = enabled,
    colors = colors,
    interactionSource = interactionSource,
    shape = shape,
    content = content,
)

@Composable
fun DPIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shapes: IconToggleButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconToggleButtonColors = IconButtonDefaults.iconToggleButtonVibrantColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = IconToggleButton(
    checked = checked,
    onCheckedChange = onCheckedChange,
    shapes = shapes,
    modifier = modifier,
    enabled = enabled,
    colors = colors,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPFilledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = IconButtonDefaults.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = FilledIconButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = colors,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPFilledIconButton(
    onClick: () -> Unit,
    shapes: IconButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = FilledIconButton(
    onClick = onClick,
    shapes = shapes,
    modifier = modifier,
    enabled = enabled,
    colors = colors,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPFilledIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = IconButtonDefaults.filledShape,
    colors: IconToggleButtonColors = IconButtonDefaults.filledIconToggleButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = FilledIconToggleButton(
    checked = checked,
    onCheckedChange = onCheckedChange,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = colors,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPFilledIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shapes: IconToggleButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconToggleButtonColors = IconButtonDefaults.filledIconToggleButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = FilledIconToggleButton(
    checked = checked,
    onCheckedChange = onCheckedChange,
    shapes = shapes,
    modifier = modifier,
    enabled = enabled,
    colors = colors,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPFilledTonalIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = IconButtonDefaults.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = FilledTonalIconButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = colors,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPFilledTonalIconButton(
    onClick: () -> Unit,
    shapes: IconButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = FilledTonalIconButton(
    onClick = onClick,
    shapes = shapes,
    modifier = modifier,
    enabled = enabled,
    colors = colors,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPFilledTonalIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = IconButtonDefaults.filledShape,
    colors: IconToggleButtonColors = IconButtonDefaults.filledTonalIconToggleButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = FilledTonalIconToggleButton(
    checked = checked,
    onCheckedChange = onCheckedChange,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = colors,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPFilledTonalIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shapes: IconToggleButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconToggleButtonColors = IconButtonDefaults.filledTonalIconToggleButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = FilledTonalIconToggleButton(
    checked = checked,
    onCheckedChange = onCheckedChange,
    shapes = shapes,
    modifier = modifier,
    enabled = enabled,
    colors = colors,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPOutlinedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = IconButtonDefaults.outlinedShape,
    colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
    border: BorderStroke? = IconButtonDefaults.outlinedIconButtonBorder(enabled),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = OutlinedIconButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = colors,
    border = border,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPOutlinedIconButton(
    onClick: () -> Unit,
    shapes: IconButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
    border: BorderStroke? = IconButtonDefaults.outlinedIconButtonBorder(enabled),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = OutlinedIconButton(
    onClick = onClick,
    shapes = shapes,
    modifier = modifier,
    enabled = enabled,
    colors = colors,
    border = border,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPOutlinedIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = IconButtonDefaults.outlinedShape,
    colors: IconToggleButtonColors = IconButtonDefaults.outlinedIconToggleButtonColors(),
    border: BorderStroke? = IconButtonDefaults.outlinedIconToggleButtonBorder(enabled, checked),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = OutlinedIconToggleButton(
    checked = checked,
    onCheckedChange = onCheckedChange,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = colors,
    border = border,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPOutlinedIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shapes: IconToggleButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconToggleButtonColors = IconButtonDefaults.outlinedIconToggleButtonVibrantColors(),
    border: BorderStroke? =
        IconButtonDefaults.outlinedIconToggleButtonVibrantBorder(enabled, checked),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = OutlinedIconToggleButton(
    checked = checked,
    onCheckedChange = onCheckedChange,
    shapes = shapes,
    modifier = modifier,
    enabled = enabled,
    colors = colors,
    border = border,
    interactionSource = interactionSource,
    content = content,
)

@DPComponentPreview
@Composable
private fun DPIconButtonsPreview() {
    Preview {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DPIconButton(onClick = {}) { Icon(Icons.Default.Favorite, contentDescription = null) }
            DPFilledIconButton(onClick = {}) { Icon(Icons.Default.Favorite, contentDescription = null) }
            DPFilledTonalIconButton(onClick = {}) { Icon(Icons.Default.Favorite, contentDescription = null) }
            DPOutlinedIconButton(onClick = {}) { Icon(Icons.Default.Favorite, contentDescription = null) }
            DPIconToggleButton(checked = true, onCheckedChange = {}) {
                Icon(Icons.Default.Favorite, contentDescription = null)
            }
        }
    }
}
