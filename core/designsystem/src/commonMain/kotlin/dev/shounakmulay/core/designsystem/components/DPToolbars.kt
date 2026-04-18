package dev.shounakmulay.core.designsystem.components

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarColors
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarHorizontalFabPosition
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.FloatingToolbarVerticalFabPosition
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPHorizontalFloatingToolbar(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    colors: FloatingToolbarColors = FloatingToolbarDefaults.standardFloatingToolbarColors(),
    contentPadding: PaddingValues = FloatingToolbarDefaults.ContentPadding,
    scrollBehavior: FloatingToolbarScrollBehavior? = null,
    shape: Shape = FloatingToolbarDefaults.ContainerShape,
    leadingContent: @Composable (RowScope.() -> Unit)? = null,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    expandedShadowElevation: Dp = FloatingToolbarDefaults.ContainerExpandedElevation,
    collapsedShadowElevation: Dp = FloatingToolbarDefaults.ContainerCollapsedElevation,
    content: @Composable RowScope.() -> Unit,
) {
    HorizontalFloatingToolbar(
        expanded = expanded,
        modifier = modifier,
        colors = colors,
        contentPadding = contentPadding,
        scrollBehavior = scrollBehavior,
        shape = shape,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        expandedShadowElevation = expandedShadowElevation,
        collapsedShadowElevation = collapsedShadowElevation,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPHorizontalFloatingToolbar(
    expanded: Boolean,
    floatingActionButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    colors: FloatingToolbarColors = FloatingToolbarDefaults.standardFloatingToolbarColors(),
    contentPadding: PaddingValues = FloatingToolbarDefaults.ContentPadding,
    scrollBehavior: FloatingToolbarScrollBehavior? = null,
    shape: Shape = FloatingToolbarDefaults.ContainerShape,
    floatingActionButtonPosition: FloatingToolbarHorizontalFabPosition =
        FloatingToolbarHorizontalFabPosition.End,
    animationSpec: FiniteAnimationSpec<Float> = FloatingToolbarDefaults.animationSpec(),
    expandedShadowElevation: Dp = FloatingToolbarDefaults.ContainerExpandedElevationWithFab,
    collapsedShadowElevation: Dp = FloatingToolbarDefaults.ContainerCollapsedElevationWithFab,
    content: @Composable RowScope.() -> Unit,
) {
    HorizontalFloatingToolbar(
        expanded = expanded,
        floatingActionButton = floatingActionButton,
        modifier = modifier,
        colors = colors,
        contentPadding = contentPadding,
        scrollBehavior = scrollBehavior,
        shape = shape,
        floatingActionButtonPosition = floatingActionButtonPosition,
        animationSpec = animationSpec,
        expandedShadowElevation = expandedShadowElevation,
        collapsedShadowElevation = collapsedShadowElevation,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPVerticalFloatingToolbar(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    colors: FloatingToolbarColors = FloatingToolbarDefaults.standardFloatingToolbarColors(),
    contentPadding: PaddingValues = FloatingToolbarDefaults.ContentPadding,
    scrollBehavior: FloatingToolbarScrollBehavior? = null,
    shape: Shape = FloatingToolbarDefaults.ContainerShape,
    leadingContent: @Composable (ColumnScope.() -> Unit)? = null,
    trailingContent: @Composable (ColumnScope.() -> Unit)? = null,
    expandedShadowElevation: Dp = FloatingToolbarDefaults.ContainerExpandedElevation,
    collapsedShadowElevation: Dp = FloatingToolbarDefaults.ContainerCollapsedElevation,
    content: @Composable ColumnScope.() -> Unit,
) {
    VerticalFloatingToolbar(
        expanded = expanded,
        modifier = modifier,
        colors = colors,
        contentPadding = contentPadding,
        scrollBehavior = scrollBehavior,
        shape = shape,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        expandedShadowElevation = expandedShadowElevation,
        collapsedShadowElevation = collapsedShadowElevation,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPVerticalFloatingToolbar(
    expanded: Boolean,
    floatingActionButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    colors: FloatingToolbarColors = FloatingToolbarDefaults.standardFloatingToolbarColors(),
    contentPadding: PaddingValues = FloatingToolbarDefaults.ContentPadding,
    scrollBehavior: FloatingToolbarScrollBehavior? = null,
    shape: Shape = FloatingToolbarDefaults.ContainerShape,
    floatingActionButtonPosition: FloatingToolbarVerticalFabPosition =
        FloatingToolbarVerticalFabPosition.Bottom,
    animationSpec: FiniteAnimationSpec<Float> = FloatingToolbarDefaults.animationSpec(),
    expandedShadowElevation: Dp = FloatingToolbarDefaults.ContainerExpandedElevationWithFab,
    collapsedShadowElevation: Dp = FloatingToolbarDefaults.ContainerCollapsedElevationWithFab,
    content: @Composable ColumnScope.() -> Unit,
) {
    VerticalFloatingToolbar(
        expanded = expanded,
        floatingActionButton = floatingActionButton,
        modifier = modifier,
        colors = colors,
        contentPadding = contentPadding,
        scrollBehavior = scrollBehavior,
        shape = shape,
        floatingActionButtonPosition = floatingActionButtonPosition,
        animationSpec = animationSpec,
        expandedShadowElevation = expandedShadowElevation,
        collapsedShadowElevation = collapsedShadowElevation,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@DPComponentPreview
@Composable
private fun DPToolbarsPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            DPHorizontalFloatingToolbar(expanded = true) {
                Text("Horizontal")
            }
            DPHorizontalFloatingToolbar(
                expanded = true,
                floatingActionButton = {
                    FloatingToolbarDefaults.StandardFloatingActionButton(onClick = {}) {
                        Text("+")
                    }
                },
            ) {
                Text("With FAB")
            }
            DPVerticalFloatingToolbar(expanded = true) {
                Text("Vertical")
            }
            DPVerticalFloatingToolbar(
                expanded = true,
                floatingActionButton = {
                    FloatingToolbarDefaults.StandardFloatingActionButton(onClick = {}) {
                        Text("+")
                    }
                },
            ) {
                Text("Vertical FAB")
            }
        }
    }
}
