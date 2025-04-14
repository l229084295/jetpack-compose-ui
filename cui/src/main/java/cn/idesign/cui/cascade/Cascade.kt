package cn.idesign.cui.cascade

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import cn.idesign.cui.bottomsheet.BottomSheet
import cn.idesign.cui.bottomsheet.BottomSheetState
import cn.idesign.cui.bottomsheet.BottomSheetValue
import cn.idesign.cui.bottomsheet.rememberBottomSheetState
import cn.idesign.cui.cascade.TabRowDefaults.tabIndicatorOffset
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState", "RememberReturnType")
@Composable
fun Cascade(
    state: BottomSheetState = rememberBottomSheetState(),
    options: List<CascadeModel>,
    cancelText: String = "取消",
    confirmText: String = "确定",
    selectColor: Color = MaterialTheme.colorScheme.primary,
    normalColor: Color = MaterialTheme.colorScheme.onSurface,
    cancelColor: Color = MaterialTheme.colorScheme.primary,
    confirmColor: Color = MaterialTheme.colorScheme.primary,
    onCancel: (() -> Unit)? = null,
    onConfirm: ((List<CascadeModel>) -> Unit)? = null,
) {
    var tabList: MutableList<CascadeModel> by remember(options) {
        mutableStateOf(
            mutableListOf(
                CascadeModel(
                    CascadeConstant.UNSELECT_TIP,
                    CascadeConstant.UNSELECT_TIP,
                    CascadeConstant.UNSELECT_POSITION,
                    options
                )
            )
        )
    }
    var currentTabIndex by remember {
        mutableStateOf(0)
    }

    val selectModel by remember(tabList, currentTabIndex) {
        derivedStateOf {
            if (currentTabIndex < tabList.size) tabList[currentTabIndex] else null
        }

    }

    val scope = rememberCoroutineScope()
    DisposableEffect(Unit) {
        onDispose {
            println("DisposableEffect")
        }

    }
    BottomSheet(
        state = state,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Header(
                    cancelText,
                    confirmText,
                    cancelColor,
                    confirmColor,
                    onCancel = {

                        scope.launch {

                            state.hide()
                            onCancel?.invoke()
                            //重置数据
                            tabList = mutableListOf(
                                CascadeModel(
                                    CascadeConstant.UNSELECT_TIP,
                                    CascadeConstant.UNSELECT_TIP,
                                    CascadeConstant.UNSELECT_POSITION,
                                    options
                                )
                            )
                            currentTabIndex = 0
                        }
                    },
                    onConfirm = {
                        scope.launch {

                            state.hide()
                            onConfirm?.invoke(tabList.filter { it.label != CascadeConstant.UNSELECT_TIP })
                            //重置数据
                            tabList = mutableListOf(
                                CascadeModel(
                                    CascadeConstant.UNSELECT_TIP,
                                    CascadeConstant.UNSELECT_TIP,
                                    CascadeConstant.UNSELECT_POSITION,
                                    options
                                )
                            )
                            currentTabIndex = 0
                        }
                    }
                )
                Box(
                    Modifier.fillMaxWidth()
                ) {

                    TabLayout(selectedTabIndex = currentTabIndex) {
                        repeat(tabList.size) { index ->
                            Box(modifier = Modifier
                                .clickable {
                                    currentTabIndex = index
                                }
                                .height(40.dp)
                                .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val color =
                                    if (currentTabIndex == index) selectColor else normalColor
                                Text(
                                    text = tabList[index].label,
                                    color = color,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()

                LazyColumn {
                    selectModel?.children?.let {
                        items(selectModel?.children!!.size) { position ->
                            val option = selectModel!!.children!![position]
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val item = tabList.removeAt(currentTabIndex)
                                        if (item.index == CascadeConstant.UNSELECT_POSITION) {
                                            //还未选择
                                            item.label = option.label
                                            item.value = option.value
                                            item.index = position
                                        } else if (item.index == position) {
                                            //点击已经选择的
                                            item.label = CascadeConstant.UNSELECT_TIP
                                            item.value = CascadeConstant.UNSELECT_TIP
                                            item.index = CascadeConstant.UNSELECT_POSITION

                                        } else {
                                            //选择其他
                                            item.label = option.label
                                            item.value = option.value
                                            item.index = position
                                            tabList = tabList.subList(0, currentTabIndex)
                                        }
                                        tabList = tabList.plus(
                                            item
                                        ) as MutableList<CascadeModel>
                                        if (item.index != CascadeConstant.UNSELECT_POSITION && !option.children.isNullOrEmpty()) {
                                            //判断是否已添加过到列表中
                                            tabList = tabList.plus(
                                                CascadeModel(
                                                    CascadeConstant.UNSELECT_TIP,
                                                    CascadeConstant.UNSELECT_TIP,
                                                    CascadeConstant.UNSELECT_POSITION,
                                                    option.children
                                                )
                                            ) as MutableList<CascadeModel>
                                            currentTabIndex++
                                        }
                                    }
                                    .padding(12.dp)
                            ) {
                                val color =
                                    if (selectModel?.index != CascadeConstant.UNSELECT_POSITION && position == selectModel?.index) selectColor else normalColor
                                Text(
                                    text = option.label,
                                    color = color,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                    }
                }

            }
        }
    )
}


object CascadeConstant {
    const val UNSELECT_POSITION = -1;
    const val UNSELECT_TIP = "请选择";
}


@Composable
private fun TabLayout(
    edgePadding: Dp = 0.dp,
    selectedTabIndex: Int = 0,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->
        if (selectedTabIndex < tabPositions.size) {
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
            )
        }
    }, tabs: @Composable () -> Unit
) {

    SubcomposeLayout(
        Modifier
            .fillMaxWidth()
            .wrapContentSize(align = Alignment.CenterStart)
    ) { constraints ->
        val padding = edgePadding.roundToPx()
        val tabConstraints = constraints.copy()

        val tabPlaceables = subcompose("Tabs", tabs)
            .fastMap { it.measure(tabConstraints) }

        var layoutWidth = padding * 2
        var layoutHeight = 0
        tabPlaceables.fastForEach {
            layoutWidth += it.width
            layoutHeight = maxOf(layoutHeight, it.height)
        }
        // Position the children.
        layout(layoutWidth, layoutHeight) {
            // Place the tabs
            val tabPositions = mutableListOf<TabPosition>()
            var left = padding
            tabPlaceables.fastForEach {
                it.placeRelative(left, 0)
                tabPositions.add(TabPosition(left = left.toDp(), width = it.width.toDp()))
                left += it.width
            }

            subcompose("Indicator") {
                indicator(tabPositions)
            }.fastForEach {
                it.measure(Constraints.fixed(layoutWidth, layoutHeight)).placeRelative(0, 0)
            }
        }
    }
}


@Composable
private fun Header(
    cancelText: String,
    confirmText: String,
    cancelColor: Color,
    confirmColor: Color,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(40.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clickable {
                    onCancel()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cancelText,
                color = cancelColor,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clickable {
                    onConfirm()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = confirmText,
                color = confirmColor,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Cascade")
@Composable
fun CascadePreview() {
    Surface {
        val state = rememberBottomSheetState(BottomSheetValue.Expanded)
//        Cascade(state,)
    }
}