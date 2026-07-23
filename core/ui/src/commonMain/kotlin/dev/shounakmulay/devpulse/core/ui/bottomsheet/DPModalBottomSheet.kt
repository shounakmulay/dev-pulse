package dev.shounakmulay.devpulse.core.ui.bottomsheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class DPModalBottomSheetController(
    val sheetState: SheetState,
    val coroutineScope: CoroutineScope,
) {
    private val _presentInComposition = mutableStateOf(false)
    val presentInComposition: State<Boolean>
        get() = _presentInComposition

    fun show() {
        _presentInComposition.value = true
    }

    fun hide() {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            _presentInComposition.value = false
        }
    }

    internal fun hideOnDismiss() {
        _presentInComposition.value = false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dpModalBottomSheetController(
    sheetState: SheetState? = null,
): DPModalBottomSheetController {
    val sheetState = sheetState ?: rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val controller = remember(sheetState, coroutineScope) {
        DPModalBottomSheetController(
            sheetState = sheetState,
            coroutineScope = coroutineScope
        )
    }

    return controller
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPModalBottomSheet(
    modifier: Modifier = Modifier,
    controller: DPModalBottomSheetController = dpModalBottomSheetController(),
    content: @Composable ColumnScope.(DPModalBottomSheetController) -> Unit
) {
    if (controller.presentInComposition.value) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = controller.sheetState,
            onDismissRequest = controller::hideOnDismiss
        ) {
            content(controller)
        }
    }
}