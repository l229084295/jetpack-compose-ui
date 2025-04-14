package cn.idesign.cui.privacypolicy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import cn.idesign.cui.annotatedtext.AnnotatedAction
import cn.idesign.cui.annotatedtext.AnnotatedText
import cn.idesign.cui.modal.Modal
import cn.idesign.cui.modal.ModalState
import cn.idesign.cui.modal.rememberModalState
import cn.idesign.cui.utils.ContentAlpha
import kotlinx.coroutines.launch

/**
 * 用户协议和隐私政策组件
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PrivacyPolicy(
    modifier: Modifier = Modifier,
    state: ModalState = rememberModalState(),
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    textTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
    secondaryText: String,
    secondaryTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium),
    secondaryTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 12.sp,
        color = secondaryTextColor
    ),
    annotatedAction: List<AnnotatedAction> = listOf(),
    annotatedStyle: SpanStyle = SpanStyle(color = MaterialTheme.colorScheme.primary),
    okText: String = "同意",
    okButtonColors: ButtonColors = ButtonDefaults.buttonColors(),
    cancelText: String = "不同意",
    okTextStyle: TextStyle = secondaryTextStyle.copy(color = MaterialTheme.colorScheme.surface),
    onOkClick: (() -> Unit)? = null,
    onCancelClick: (() -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val okClickState = rememberUpdatedState(onOkClick)
    val cancelClickState = rememberUpdatedState(onCancelClick)
    Modal(
        state = state,
        onClose = {
            scope.launch {
                state.hide()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .background(color = MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                .padding(20.dp, 20.dp, 20.dp, 10.dp)
                .then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                color = textColor,
                style = textTextStyle,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            AnnotatedText(
                modifier = Modifier
                    .height(175.dp)
                    .verticalScroll(state = rememberScrollState())
                    .padding(bottom = 15.dp),
                text = secondaryText,
                textStyle = secondaryTextStyle,
                annotatedStyle = annotatedStyle,
                annotatedActions = annotatedAction
            )
            Button(
                onClick = { okClickState.value?.invoke() },
                modifier = Modifier.fillMaxWidth(),
                colors = okButtonColors,
            ) {
                Text(text = okText, style = okTextStyle)
            }
            Text(
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = cancelClickState.value != null,
                        onClick = {
                            cancelClickState.value?.invoke()
                        }),
                text = cancelText,
                color = textColor.copy(alpha = ContentAlpha.disabled),
                style = secondaryTextStyle, textAlign = TextAlign.Center
            )
        }

    }

}