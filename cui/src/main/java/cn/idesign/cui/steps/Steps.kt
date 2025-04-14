package cn.idesign.cui.steps

import android.annotation.SuppressLint
import androidx.annotation.IntRange
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import cn.idesign.cui.annotatedtext.AnnotatedAction
import cn.idesign.cui.annotatedtext.AnnotatedText
import cn.idesign.cui.common.Direction
import cn.idesign.cui.utils.ContentAlpha
import cn.idesign.cui.utils.calculateForegroundColor

@SuppressLint("RememberReturnType")
@Composable
fun Steps(
    modifier: Modifier = Modifier,
    state: StepState = rememberStepState(),
    data: List<StepModel> = listOf(),
    direction: Direction = Direction.Vertical,
    annotatedAction: List<AnnotatedAction> = listOf(),
    annotatedStyle: SpanStyle = SpanStyle(color = MaterialTheme.colorScheme.primary),
) {
    remember(data) {
        state.size = data.size
    }
    when (direction) {
        Direction.Vertical -> StepsVertical(modifier, data, state, annotatedAction, annotatedStyle)
        Direction.Horizontal -> StepsHorizontal(
            modifier,
            data,
            state,
            annotatedAction,
            annotatedStyle
        )
    }
}

@Composable
fun StepsVertical(
    modifier: Modifier,
    data: List<StepModel>,
    state: StepState,
    annotatedAction: List<AnnotatedAction>,
    annotatedStyle: SpanStyle,
) {
    Column(modifier) {
        repeat(data.size) { index ->
            val model = data[index]

            val status = model.status ?: when (index) {
                in 0 until state.currentValue -> StepStatus.Finish
                state.currentValue -> StepStatus.Process
                else -> StepStatus.Wait
            }

            val icon = model.icon ?: {
                StepIcon(
                    status = status,
                    index + 1,
                    iconBoxSize = model.iconBoxSize,
                    iconSize = model.iconSize,
                    showIndex = model.showIndex,
                )
            }
            VerticalStepItem(
                modifier = Modifier.fillMaxWidth(),
                text = model.text,
                textColor = model.textColor,
                secondaryText = model.secondaryText,
                secondaryTextColor = model.secondaryTextColor,
                icon = icon,
                showDivider = index != data.size - 1,
                status = status,
                annotatedAction = annotatedAction,
                annotatedStyle = annotatedStyle,
            )
        }
    }
}

@Composable
fun StepsHorizontal(
    modifier: Modifier,
    data: List<StepModel>,
    state: StepState,
    annotatedAction: List<AnnotatedAction>,
    annotatedStyle: SpanStyle
) {
    Row(
        modifier
    ) {
        repeat(data.size) { index ->
            val model = data[index]

            val status = model.status ?: when (index) {
                in 0 until state.currentValue -> StepStatus.Finish
                state.currentValue -> StepStatus.Process
                else -> StepStatus.Wait
            }

            val icon = model.icon ?: {
                StepIcon(
                    status = status,
                    index + 1,
                    iconBoxSize = model.iconBoxSize,
                    iconSize = model.iconSize,
                    showIndex = model.showIndex,
                )
            }
            val itemModifier =
                if (index != data.size - 1) Modifier.weight(1f) else Modifier.wrapContentSize()
            HorizontalStepItem(
                modifier = itemModifier,
                text = model.text,
                textColor = model.textColor,
                secondaryText = model.secondaryText,
                secondaryTextColor = model.secondaryTextColor,
                icon = icon,
                showDivider = index != data.size - 1,
                status = status,
                annotatedAction = annotatedAction,
                annotatedStyle = annotatedStyle,
            )
        }
    }
}


@Composable
fun VerticalStepItem(
    modifier: Modifier = Modifier,
    text: String? = null,
    textColor: Color,
    textTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
    secondaryText: String? = null,
    secondaryTextColor: Color,
    secondaryTextTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
    icon: @Composable () -> Unit,
    status: StepStatus,
    showDivider: Boolean = true,
    annotatedAction: List<AnnotatedAction>,
    annotatedStyle: SpanStyle,
) {

    val textColorState by animateColorAsState(targetValue = textColor.takeOrElse {
        MaterialTheme.colorScheme.onSurface.copy(
            alpha = if (status == StepStatus.Wait) ContentAlpha.medium else ContentAlpha.high
        )
    })
    val secondaryTextColorState by animateColorAsState(targetValue = secondaryTextColor.takeOrElse {
        MaterialTheme.colorScheme.onSurface.copy(
            alpha = if (status == StepStatus.Wait) ContentAlpha.disabled else ContentAlpha.medium
        )
    })
    ConstraintLayout(modifier) {
        val (iconRef, title, description, divider) = createRefs()
        Box(modifier = Modifier
            .constrainAs(iconRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, margin = 8.dp)
            }
        ) {
            icon()
        }
        if (showDivider) {
            Box(modifier = Modifier
                .constrainAs(divider) {
                    top.linkTo(iconRef.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(iconRef.start)
                    end.linkTo(iconRef.end)
                    width = Dimension.value(1.dp)
                    height = Dimension.fillToConstraints
                }
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)))

        }

        Text(
            text = text ?: "",
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(iconRef.end, margin = 8.dp)
                    end.linkTo(parent.end, margin = 8.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(description.top, margin = 8.dp, goneMargin = 0.dp)
                    width = Dimension.fillToConstraints
                    visibility = if (text == null) Visibility.Gone else Visibility.Visible
                },
            color = textColorState,
            style = textTextStyle
        )
        AnnotatedText(
            modifier = Modifier
                .constrainAs(description) {
                    start.linkTo(iconRef.end, margin = 8.dp)
                    end.linkTo(parent.end, margin = 8.dp)
                    top.linkTo(title.bottom)
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                    width = Dimension.fillToConstraints
                    visibility = if (secondaryText == null) Visibility.Gone else Visibility.Visible
                },
            text = secondaryText ?: "",
            textStyle = secondaryTextTextStyle.copy(color = secondaryTextColorState),
            annotatedStyle = annotatedStyle,
            annotatedActions = annotatedAction
        )
    }
}

@Composable
fun HorizontalStepItem(
    modifier: Modifier = Modifier,
    text: String? = null,
    textColor: Color,
    textTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
    secondaryText: String? = null,
    secondaryTextColor: Color,
    secondaryTextTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
    icon: @Composable () -> Unit,
    status: StepStatus,
    showDivider: Boolean = true,
    annotatedAction: List<AnnotatedAction>,
    annotatedStyle: SpanStyle,
) {

    val textColorState by animateColorAsState(targetValue = textColor.takeOrElse {
        MaterialTheme.colorScheme.onSurface.copy(
            alpha = if (status == StepStatus.Wait) ContentAlpha.medium else ContentAlpha.high
        )
    })
    val secondaryTextColorState by animateColorAsState(targetValue = secondaryTextColor.takeOrElse {
        MaterialTheme.colorScheme.onSurface.copy(
            alpha = if (status == StepStatus.Wait) ContentAlpha.disabled else ContentAlpha.medium
        )
    })
    ConstraintLayout(modifier) {
        val (iconRef, title, description, divider) = createRefs()
        Box(modifier = Modifier
            .constrainAs(iconRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, margin = 8.dp)
            }
        ) {
            icon()
        }
        if (showDivider) {
            Box(modifier = Modifier
                .constrainAs(divider) {
                    top.linkTo(iconRef.top)
                    bottom.linkTo(iconRef.bottom)
                    start.linkTo(iconRef.end, margin = 8.dp)
                    end.linkTo(parent.end)
                    height = Dimension.value(1.dp)
                    width = Dimension.fillToConstraints
                }
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)))

        }

        Text(
            text = text ?: "",
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(iconRef.start)
                    top.linkTo(iconRef.bottom)
                    bottom.linkTo(description.top, margin = 8.dp, goneMargin = 0.dp)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                    visibility = if (text == null) Visibility.Gone else Visibility.Visible
                }
                .wrapContentHeight(Alignment.CenterVertically),
            color = textColorState,
            style = textTextStyle,
        )
        AnnotatedText(
            modifier = Modifier
                .constrainAs(description) {
                    start.linkTo(iconRef.start)
                    top.linkTo(title.bottom, margin = 8.dp, goneMargin = 0.dp)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                    visibility = if (secondaryText == null) Visibility.Gone else Visibility.Visible
                }
                .wrapContentHeight(Alignment.CenterVertically),
            text = secondaryText ?: "",
            textStyle = secondaryTextTextStyle.copy(color = secondaryTextColorState),
            annotatedStyle = annotatedStyle,
            annotatedActions = annotatedAction
        )
    }
}

@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun StepIcon(
    status: StepStatus,
    index: Int,
    iconBoxSize: Dp = 20.dp,
    iconSize: Dp = 20.dp,
    showIndex: Boolean = true
) {
    val background by animateColorAsState(
        targetValue = when (status) {
            StepStatus.Finish -> MaterialTheme.colorScheme.primary
            StepStatus.Error -> MaterialTheme.colorScheme.error
            StepStatus.Wait -> {
                val backgroundColor = MaterialTheme.colorScheme.surface
                val absoluteElevation = LocalAbsoluteTonalElevation.current + 1.dp
                val foregroundColor = calculateForegroundColor(backgroundColor, absoluteElevation)
                foregroundColor.compositeOver(backgroundColor)
            }
            StepStatus.Process -> MaterialTheme.colorScheme.primary
        }
    )
    Box(
        modifier = Modifier
            .size(iconBoxSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .background(background, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (showIndex) {
                when (status) {
                    StepStatus.Finish -> Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    StepStatus.Error -> Text(
                        text = index.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                    )
                    StepStatus.Wait -> Text(
                        text = index.toString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                    )
                    StepStatus.Process -> Text(
                        text = index.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                    )
                }
            }
        }
    }
}


@Composable
fun StepIconCopy(status: StepStatus) {
    val largeWidth = 14.dp
    val smallWidth = 8.dp
    val alpha = ContentAlpha.disabled
    val tripe = when (status) {
        StepStatus.Finish -> Triple(
            Pair(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(alpha)
            ), largeWidth, smallWidth
        )
        StepStatus.Error -> Triple(
            Pair(
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.error.copy(alpha)
            ), largeWidth, smallWidth
        )
        StepStatus.Wait -> Triple(
            Pair(
                MaterialTheme.colorScheme.onSurface.copy(0.12f),
                Color.Transparent,
            ),
            largeWidth,
            smallWidth
        )
        StepStatus.Process -> Triple(
            Pair(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(alpha)
            ), largeWidth, largeWidth
        )
    }

    Canvas(
        modifier = Modifier
            .size(tripe.second)
    ) {
        drawCircle(color = tripe.first.second)
        drawCircle(color = tripe.first.first, radius = tripe.third.toPx() / 2)
    }
}

@Composable
fun rememberStepState(
    initialValue: Int = 0,
): StepState = rememberSaveable(saver = StepState.SAVER) {
    StepState(
        initialValue = initialValue,
    )
}

class StepState(
    val initialValue: Int,
) {

    private var _initialValue: Int by mutableStateOf(initialValue)
    private var _size: Int by mutableStateOf(0)

    @get:IntRange(from = 0)
    var size: Int
        get() = _size
        internal set(value) {
            if (value != _size) {
                _size = value
            }
        }

    val currentValue: Int
        get() = _initialValue

    fun next() {
        _initialValue = (_initialValue + 1).coerceIn(0, size)
    }

    fun back() {
        _initialValue = (_initialValue - 1).coerceIn(0, size)
    }

    companion object {
        val SAVER: Saver<StepState, *> = Saver(
            save = {
                it.initialValue
            },
            restore = {
                StepState(it)
            }
        )
    }
}



