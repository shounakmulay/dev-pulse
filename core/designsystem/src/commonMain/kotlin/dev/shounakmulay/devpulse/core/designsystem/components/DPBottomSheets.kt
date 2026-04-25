package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    sheetGesturesEnabled: Boolean = true,
    shape: Shape? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
    tonalElevation: DPElevationLevel = DPElevationLevel.Level1,
    scrimColor: Color? = null,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.modalWindowInsets },
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val resolvedShape = shape ?: BottomSheetDefaults.ExpandedShape
    val resolvedContainer = containerColor ?: BottomSheetDefaults.ContainerColor
    val resolvedContent = contentColor ?: contentColorFor(resolvedContainer)
    val resolvedScrim = scrimColor ?: BottomSheetDefaults.ScrimColor
    val resolvedTonal = tonalElevation.value()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        sheetMaxWidth = sheetMaxWidth,
        sheetGesturesEnabled = sheetGesturesEnabled,
        shape = resolvedShape,
        containerColor = resolvedContainer,
        contentColor = resolvedContent,
        tonalElevation = resolvedTonal,
        scrimColor = resolvedScrim,
        dragHandle = dragHandle,
        contentWindowInsets = contentWindowInsets,
        properties = properties,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPBottomSheetScaffold(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    sheetPeekHeight: Dp = BottomSheetDefaults.SheetPeekHeight,
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    sheetShape: Shape? = null,
    sheetContainerColor: Color? = null,
    sheetContentColor: Color? = null,
    sheetTonalElevation: DPElevationLevel = DPElevationLevel.Level1,
    sheetShadowElevation: DPElevationLevel = DPElevationLevel.Level1,
    sheetDragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    sheetSwipeEnabled: Boolean = true,
    topBar: @Composable (() -> Unit)? = null,
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    containerColor: Color? = null,
    contentColor: Color? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val resolvedSheetShape = sheetShape ?: BottomSheetDefaults.ExpandedShape
    val resolvedSheetContainer = sheetContainerColor ?: BottomSheetDefaults.ContainerColor
    val resolvedSheetContent = sheetContentColor ?: contentColorFor(resolvedSheetContainer)
    val resolvedContainer = containerColor ?: MaterialTheme.colorScheme.surface
    val resolvedContent = contentColor ?: contentColorFor(resolvedContainer)

    BottomSheetScaffold(
        sheetContent = sheetContent,
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetPeekHeight = sheetPeekHeight,
        sheetMaxWidth = sheetMaxWidth,
        sheetShape = resolvedSheetShape,
        sheetContainerColor = resolvedSheetContainer,
        sheetContentColor = resolvedSheetContent,
        sheetTonalElevation = sheetTonalElevation.value(),
        sheetShadowElevation = sheetShadowElevation.value(),
        sheetDragHandle = sheetDragHandle,
        sheetSwipeEnabled = sheetSwipeEnabled,
        topBar = topBar,
        snackbarHost = snackbarHost,
        containerColor = resolvedContainer,
        contentColor = resolvedContent,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@DPComponentPreview
@Composable
private fun DPBottomSheetScaffoldPreview() {
    Preview {
        DPBottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            sheetContent = { Text("Sheet") },
        ) {
            Text("Content")
        }
    }
}
