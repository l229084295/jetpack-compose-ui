package cn.idesign.cui.swipe

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FloatDecayAnimationSpec
import androidx.compose.animation.core.KeyframesSpec
import androidx.compose.animation.core.KeyframesSpec.KeyframesSpecConfig
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * 左滑删除组件
 */
@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("RememberReturnType")
@Composable
fun Swipe(
    state: SwipeState = rememberSwipeState(),
    direction: SwipeDirection = SwipeDirection.RightToLeft,
    onChange: ((open: Boolean) -> Unit)? = null,
    background: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    var boxWidthPx by remember {
        mutableStateOf(0)
    }
    var boxHeightPx by remember {
        mutableStateOf(0)
    }
    var backgroundWidthPx by remember {
        mutableStateOf(0)
    }

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val anchors = DraggableAnchors {
        SwipeValue.Open at -backgroundWidthPx.toFloat()
        SwipeValue.Hidden at backgroundWidthPx.toFloat()
    }
    val decay = exponentialDecay<Float>()
    val swipeableState = remember {
        AnchoredDraggableState(
            initialValue = SwipeValue.Hidden,
            anchors = anchors,
            positionalThreshold = { with(density) { 56.dp.toPx() } },
            velocityThreshold = { with(density) { 125.dp.toPx() } },
            snapAnimationSpec = keyframes { durationMillis = 375 },
            decayAnimationSpec = decay
        )
    }
    state.swipeableState = swipeableState
//    val anchors by remember(backgroundWidthPx, direction) {
//        if (direction == SwipeDirection.RightToLeft) {
//            mutableStateOf(mapOf(0f to 0, -backgroundWidthPx.toFloat() to 1))
//        } else {
//            mutableStateOf(mapOf(0f to 0, backgroundWidthPx.toFloat() to 1))
//        }
//    }

    remember(swipeableState.currentValue) {
        Log.d("Swipe", "swipeableState.currentValue:${swipeableState.currentValue}")
        onChange?.invoke(swipeableState.currentValue == SwipeValue.Open)
    }

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            swipeableState.animateTo(state.currentValue)
        }
    }
    val swipeModifier = if (backgroundWidthPx > 0) Modifier.anchoredDraggable(
        state = swipeableState,
        orientation = Orientation.Horizontal,
    ) else Modifier
    Box(modifier = Modifier
        .onSizeChanged {
            boxWidthPx = it.width
            boxHeightPx = it.height
        }
        .clipToBounds()
        .then(swipeModifier)
    ) {

        val backgroundOffsetX = when (direction) {
            SwipeDirection.LeftToRight -> -backgroundWidthPx + swipeableState.offset.roundToInt()
            SwipeDirection.RightToLeft -> boxWidthPx + swipeableState.offset.roundToInt()
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    x = swipeableState.offset.roundToInt(),
                    y = 0
                )
            }) {
            content()
        }
        Box(modifier = Modifier
            .height(with(LocalDensity.current) { boxHeightPx.toDp() })
            .onSizeChanged {
                backgroundWidthPx = it.width
            }
            .offset {
                IntOffset(
                    x = backgroundOffsetX,
                    y = 0
                )
            }
        ) {
            background()
        }

    }
}

sealed class SwipeDirection {
    object LeftToRight : SwipeDirection()
    object RightToLeft : SwipeDirection()
}

enum class SwipeValue {
    Hidden,
    Open,
}


@Composable
fun rememberSwipeState(
    initialValue: SwipeValue = SwipeValue.Hidden
): SwipeState = rememberSaveable(saver = SwipeState.SAVER) {
    SwipeState(
        initialValue = initialValue,
    )
}

@OptIn(ExperimentalFoundationApi::class)
class SwipeState(
    val initialValue: SwipeValue,
) {

    internal lateinit var swipeableState: AnchoredDraggableState<SwipeValue>
    private var _currentValue: SwipeValue by mutableStateOf(initialValue)
    val currentValue: SwipeValue
        get() = _currentValue


    suspend fun open() {
        if (swipeableState.currentValue != SwipeValue.Open) {
            swipeableState.animateTo(SwipeValue.Open)
        }
    }

    suspend fun close() {
        if (swipeableState.currentValue != SwipeValue.Hidden) {
            swipeableState.animateTo(SwipeValue.Hidden)
        }
    }

    fun isOpen(): Boolean = swipeableState.currentValue == SwipeValue.Open

    companion object {
        val SAVER: Saver<SwipeState, *> = Saver(
            save = {
                it.initialValue
            },
            restore = {
                SwipeState(it)
            }
        )
    }
}