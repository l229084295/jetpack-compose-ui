package cn.idesign.cui.swipe

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeBox(
    control: SwipeBoxControl = rememberSwipeBoxControl(),
    modifier: Modifier = Modifier,
    actionWidth: Dp, //锚点宽度
    startAction: List<@Composable BoxScope.() -> Unit>? = null, //左侧展开锚点
    startFillAction: (@Composable BoxScope.() -> Unit)? = null, //左侧全展开锚点
    endAction: List<@Composable BoxScope.() -> Unit>? = null, //右侧展开锚点
    endFillAction: (@Composable BoxScope.() -> Unit)? = null, //右侧全展开锚点
    content: @Composable BoxScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val actionWidthPx = with(density) {
        actionWidth.toPx()
    }
    val startWidth = actionWidthPx * (startAction?.size ?: 0) //左侧锚点宽度
    val startActionSize = (startAction?.size ?: -1) + 1 //左侧锚点总数 = startAction + startFillAction
    val startFillWidth = actionWidthPx * startActionSize
    val endWidth = actionWidthPx * (endAction?.size ?: 0)  //右侧锚点宽度
    val endActionSize = (endAction?.size ?: -1) + 1 //右侧锚点总数 =  endAction + endFillAction
    val endFillWidth = actionWidthPx * endActionSize
    var contentWidth by remember { mutableFloatStateOf(0f) } //内容组件宽度
    var contentHeight by remember { mutableFloatStateOf(0f) }
    val state = remember(startWidth, endWidth, contentWidth) {
        AnchoredDraggableState(
            initialValue = DragAnchors.Center,
            anchors = DraggableAnchors {
                DragAnchors.Start at (if (startFillAction != null) actionWidthPx else 0f) + startWidth //左侧全展开锚点宽度 + 左侧展开锚点宽度
                DragAnchors.StartFill at (if (startFillAction != null) contentWidth else 0f) + startWidth // 内容组件宽度 + 左侧展开锚点宽度
                DragAnchors.Center at 0f
                DragAnchors.End at (if (endFillAction != null) -actionWidthPx else 0f) - endWidth //右侧全展开锚点宽度 + 右侧展开锚点宽度
                DragAnchors.EndFill at (if (endFillAction != null) -contentWidth else 0f) - endWidth // 内容组件宽度 + 右侧展开锚点宽度
            },
            positionalThreshold = { distance ->
                if (startFillAction != null && distance > startFillWidth) {
                    startWidth
                } else if (endFillAction != null && distance > endFillWidth) {
                    endWidth
                } else {
                    distance
                } * 0.5f
            },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = TweenSpec(durationMillis = 350),
            decayAnimationSpec = exponentialDecay(frictionMultiplier = 0.9f),
        )
    }
    LaunchedEffect(control, state) {
        with(control) {
            handleControlEvents(
                onStart = {
                    scope.launch {
                        state.animateTo(DragAnchors.Start)
                    }
                },
                onStartFill = {
                    scope.launch {
                        state.animateTo(DragAnchors.StartFill)
                    }
                },
                onCenter = {
                    scope.launch {
                        state.animateTo(DragAnchors.Center)
                    }
                },
                onEnd = {
                    scope.launch {
                        state.animateTo(DragAnchors.End)
                    }
                },
                onEndFill = {
                    scope.launch {
                        state.animateTo(DragAnchors.EndFill)
                    }
                }
            )
        }
    }
    Box(
        modifier = modifier
            .anchoredDraggable(
                state = state,
                orientation = Orientation.Horizontal,
            )
            .clipToBounds()
    ) {
        startAction?.forEachIndexed { index, action ->
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(actionWidth)
                    .height(with(density) {
                        contentHeight.toDp()
                    })
                    .offset {
                        IntOffset(
                            x = if (state.offset <= actionWidthPx * startActionSize) {
                                (-actionWidthPx + state.offset / startActionSize * (startActionSize - index)).roundToInt()
                            } else {
                                (-actionWidthPx * (index + 1) + state.offset).roundToInt()
                            },
                            y = 0,
                        )
                    }
            ) {
                action()
            }
        }
        startFillAction?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .height(with(density) {
                        contentHeight.toDp()
                    })
                    .offset {
                        IntOffset(
                            x = if (state.offset <= actionWidthPx * startActionSize) {
                                (-contentWidth + state.offset / startActionSize).roundToInt()
                            } else {
                                (-contentWidth - startWidth + state.offset).roundToInt()
                            },
                            y = 0,
                        )
                    }
            ) {
                it()
            }
        }
        endAction?.forEachIndexed { index, action ->
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(actionWidth)
                    .height(with(density) {
                        contentHeight.toDp()
                    })
                    .offset {
                        IntOffset(
                            x = if (state.offset >= -(actionWidthPx * endActionSize)) {
                                (actionWidthPx + state.offset / endActionSize * (endActionSize - index)).roundToInt()
                            } else {
                                (actionWidthPx * (index + 1) + state.offset).roundToInt()
                            },
                            y = 0,
                        )
                    }
            ) {
                action()
            }
        }
        endFillAction?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .height(with(density) {
                        contentHeight.toDp()
                    })
                    .offset {
                        IntOffset(
                            x = if (state.offset >= -(actionWidthPx * endActionSize)) {
                                (contentWidth + state.offset / endActionSize).roundToInt()
                            } else {
                                (contentWidth + endWidth + state.offset).roundToInt()
                            },
                            y = 0,
                        )
                    }
            ) {
                it()
            }
        }
        Box(
            modifier = Modifier
                .onSizeChanged {
                    contentWidth = it.width.toFloat()
                    contentHeight = it.height.toFloat()
                }
                .offset {
                    IntOffset(
                        x = state.offset.roundToInt(),
                        y = 0,
                    )
                }
        ) {
            content()
        }
    }
}

@Stable
class SwipeBoxControl(
    private val scope: CoroutineScope
) {
    private sealed interface ControlEvent {
        data object Start : ControlEvent
        data object StartFill : ControlEvent
        data object Center : ControlEvent
        data object End : ControlEvent
        data object EndFill : ControlEvent
    }

    private val controlEvents: MutableSharedFlow<ControlEvent> = MutableSharedFlow()

    @OptIn(FlowPreview::class)
    internal suspend fun handleControlEvents(
        onStart: () -> Unit = {},
        onStartFill: () -> Unit = {},
        onCenter: () -> Unit = {},
        onEnd: () -> Unit = {},
        onEndFill: () -> Unit = {},
    ) = withContext(Dispatchers.Main) {
        controlEvents.debounce(350).collect { event ->
            when (event) {
                ControlEvent.Start -> onStart()
                ControlEvent.StartFill -> onStartFill()
                ControlEvent.Center -> onCenter()
                ControlEvent.End -> onEnd()
                ControlEvent.EndFill -> onEndFill()
            }
        }
    }

    fun start() {
        scope.launch { controlEvents.emit(ControlEvent.Start) }
    }

    fun startFill() {
        scope.launch { controlEvents.emit(ControlEvent.Start) }
    }

    fun center() {
        scope.launch { controlEvents.emit(ControlEvent.Center) }
    }

    fun end() {
        scope.launch { controlEvents.emit(ControlEvent.End) }
    }

    fun endFill() {
        scope.launch { controlEvents.emit(ControlEvent.EndFill) }
    }
}

@Composable
fun rememberSwipeBoxControl(
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): SwipeBoxControl = remember(coroutineScope) { SwipeBoxControl(coroutineScope) }

enum class DragAnchors { Start, StartFill, Center, End, EndFill }
