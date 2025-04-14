package cn.idesign.cui.cascade

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import cn.idesign.cui.bottomsheet.BottomSheetState
import cn.idesign.cui.bottomsheet.BottomSheetValue

@OptIn(ExperimentalMaterial3Api::class)
class CascadeState(initialValue: BottomSheetValue) :BottomSheetState(initialValue) {
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberCascadeStateState(
    initialValue: BottomSheetValue = BottomSheetValue.Hidden
): BottomSheetState = rememberSaveable(saver = BottomSheetState.SAVER) {
    CascadeState(
        initialValue = initialValue,
    )
}