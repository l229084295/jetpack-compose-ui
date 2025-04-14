package cn.idesign.cui.annotatedtext

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import cn.idesign.cui.utils.ContentAlpha
import java.util.regex.Pattern

@Composable
fun AnnotatedText(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium)
    ),
    annotatedStyle: SpanStyle = SpanStyle(color = MaterialTheme.colorScheme.primary),
    annotatedActions: List<AnnotatedAction> = listOf()
) {
    val regexTag = annotatedActions.map { it.regex }
    val regex = regexTag.joinToString("|")

    val pattern = Pattern.compile(regex)
    val length = text.length
    val annotationText = buildAnnotatedString {
        val matcher = pattern.matcher(text)
        var startIndex = 0
        while (matcher.find()) {
            append(text.subSequence(startIndex, matcher.start()).toString())
            pushStringAnnotation(
                tag = matcher.group(),
                annotation = matcher.group()
            )
            withStyle(
                style = annotatedStyle
            ) {
                append(matcher.group())
            }
            startIndex = matcher.end()
            pop()

        }
        if (startIndex < length) {
            append(text.subSequence(startIndex, length).toString())
        }
    }

    ClickableText(
        modifier = modifier,
        text = annotationText,
        style = textStyle,
        onClick = { offset ->
            annotatedActions.forEach { annotated ->
                val action = annotated.action
                val pattern = Pattern.compile(annotated.regex)
                val matcher = pattern.matcher(annotationText)
                if (matcher.find()) {
                    val tag = matcher.group()
                    annotationText.getStringAnnotations(
                        tag = tag,
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let { annotation ->
                        action?.invoke(annotation.item)
                    }
                }

            }

        }
    )
}

data class AnnotatedAction(
    val regex: String,
    val action: ((match: String) -> Unit)? = null,
)