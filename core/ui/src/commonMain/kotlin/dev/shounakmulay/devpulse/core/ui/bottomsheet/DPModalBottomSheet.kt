package dev.shounakmulay.devpulse.core.ui.bottomsheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.compose.DPScreenPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
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
    onDismissRequest: () -> Unit = {},
    content: @Composable ColumnScope.(DPModalBottomSheetController) -> Unit
) {
    if (controller.presentInComposition.value) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = controller.sheetState,
            onDismissRequest = {
                controller.hideOnDismiss()
                onDismissRequest()
            }
        ) {
            content(controller)
        }
    }
}

@DPScreenPreview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DPModalBottomSheetPreview() {
    Preview {
        DPModalBottomSheet(
            controller = DPModalBottomSheetController(
                sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded, skipHiddenState = false),
                initialPresentState = true,
            ),
        ) {
            DPTextView(text = "Reading preferences", variant = DPTextViewVariant.TitleLarge)
        }
    }
}
