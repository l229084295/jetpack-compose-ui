package cn.idesign.cui.bottomsheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    state: BottomSheetState = rememberBottomSheetState(),
    sheetBackgroundColor: Color = Color.Transparent,
    content: @Composable ColumnScope.() -> Unit,
    onDismissRequest: (() -> Unit)? = null,
) {
    val bottomSheetState =
        rememberModalBottomSheetState()
    LaunchedEffect(bottomSheetState) {
        state.bottomSheetState = bottomSheetState
    }
    ModalBottomSheet(
        modifier = modifier,
        sheetState = bottomSheetState,
        containerColor = sheetBackgroundColor,
        onDismissRequest = onDismissRequest ?: {},
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberBottomSheetState(
    initialValue: BottomSheetValue = BottomSheetValue.Hidden
): BottomSheetState = rememberSaveable(saver = BottomSheetState.SAVER) {
    BottomSheetState(
        initialValue = initialValue,
    )
}

@ExperimentalMaterial3Api
open class BottomSheetState(
    val initialValue: BottomSheetValue,
) {

    internal lateinit var bottomSheetState: SheetState

    suspend fun show() {
        bottomSheetState.show()

    }

    suspend fun hide() {
        bottomSheetState.hide()
    }

    val isVisible: Boolean
        get() = bottomSheetState.isVisible


    internal fun covertBottomSheetValue(value: BottomSheetValue): SheetValue {
        return when (value) {
            BottomSheetValue.Hidden -> SheetValue.Hidden
            BottomSheetValue.HalfExpanded -> SheetValue.PartiallyExpanded
            BottomSheetValue.Expanded -> SheetValue.Expanded
        }
    }

    companion object {
        val SAVER: Saver<BottomSheetState, *> = Saver(
            save = {
                it.initialValue
            },
            restore = {
                BottomSheetState(BottomSheetValue.Hidden)
            }
        )
    }
}

enum class BottomSheetValue {

    Hidden,

    Expanded,

    HalfExpanded
}
