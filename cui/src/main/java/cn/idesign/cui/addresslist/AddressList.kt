package cn.idesign.cui.addresslist

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.idesign.cui.swipe.Swipe
import cn.idesign.cui.swipe.SwipeState
import cn.idesign.cui.swipe.rememberSwipeState
import cn.idesign.cui.utils.ContentAlpha
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun AddressList(
    modifier: Modifier = Modifier,
    data: List<AddressModel> = Collections.emptyList(),
    divider: Boolean = true,
    onClick: ((data: AddressModel) -> Unit)? = null,
    onDelete: ((data: AddressModel) -> Unit)? = null,
    onDefault: ((data: AddressModel) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    var currentState by remember {
        mutableStateOf<SwipeState?>(null)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
    ) {
        items(data.size) { index ->
            val model = data[index]
            Box {
                val state = rememberSwipeState()
                Swipe(state = state,
                    onChange = { open ->
                        scope.launch {
                            if (open) {
                                if (currentState != null && currentState != state) {
                                    currentState!!.close()
                                }
                                currentState = state
                            } else if (currentState != null && currentState == state) {
                                currentState = null
                            }
                        }
                    },
                    background = {
                        Row {
                            if (!model.defaultAddress) {
                                Box(
                                    modifier = Modifier
                                        .width(66.dp)
                                        .fillMaxHeight()
                                        .clickable {
                                            onDefault?.invoke(model)
                                        }
                                        .background(
                                            MaterialTheme.colorScheme.background.copy(
                                                ContentAlpha.medium
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "设为默认",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                            }
                            Box(
                                modifier = Modifier
                                    .width(66.dp)
                                    .fillMaxHeight()
                                    .clickable {
                                        onDelete?.invoke(model)
                                    }
                                    .background(Color.Red),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "删除",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }) {
                    AddressItem(
                        data = model,
                        onClick = {
                            Log.d("AddressList", "onChange,onClick:${currentState}")
                            if (currentState != null) {
                                if (currentState!!.isOpen()) {
                                    scope.launch {
                                        currentState!!.close()
                                    }
                                } else {
                                    onClick?.invoke(model)
                                }
                            } else {
                                onClick?.invoke(model)
                            }
                        }
                    )

                }

                if (divider) {
                    HorizontalDivider(modifier = Modifier.align(Alignment.BottomStart))
                }
            }
        }
    }
}