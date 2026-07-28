package dev.shounakmulay.devpulse.core.ui.bottomsheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
class DPModalBottomSheetController(
    val sheetState: SheetState,
    val initialPresentState: Boolean = false
) {
    val presentInComposition: State<Boolean>
        field = mutableStateOf(initialPresentState)

    fun show() {
        presentInComposition.value = true
    }

    suspend fun hide() {
        try {
            sheetState.hide()
        } finally {
            presentInComposition.value = false
        }
    }

    internal fun hideOnDismiss() {
        presentInComposition.value = false
    }

    companion object {
        fun Saver(
            sheetState: SheetState,
            coroutineScope: CoroutineScope
        ) = listSaver(
            save = { controller ->
                listOf(controller.presentInComposition.value)
            },
            restore = { savedList ->
                DPModalBottomSheetController(
                    sheetState = sheetState,
                    initialPresentState = savedList[0]
                )
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun rememberDPModalBottomSheetController(
    skipPartiallyExpanded: Boolean,
): DPModalBottomSheetController {
    return rememberDPModalBottomSheetController(
        rememberModalBottomSheetState(
            skipPartiallyExpanded = skipPartiallyExpanded
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberDPModalBottomSheetController(
    sheetState: SheetState? = null,
): DPModalBottomSheetController {
    val sheetState = sheetState ?: rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val controller =
        rememberSaveable(
            sheetState,
            coroutineScope,
            saver = DPModalBottomSheetController.Saver(sheetState, coroutineScope)
        ) {
            DPModalBottomSheetController(
                sheetState = sheetState,
            )
        }

    return controller
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPModalBottomSheet(
    modifier: Modifier = Modifier,
    controller: DPModalBottomSheetController = rememberDPModalBottomSheetController(),
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