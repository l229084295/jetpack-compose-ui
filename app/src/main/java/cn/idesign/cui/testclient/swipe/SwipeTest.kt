package cn.idesign.cui.testclient.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.idesign.cui.cell.Cell
import cn.idesign.cui.swipe.Swipe
import cn.idesign.cui.swipe.SwipeBox
import cn.idesign.cui.swipe.SwipeBoxControl
import cn.idesign.cui.swipe.SwipeDirection
import cn.idesign.cui.swipe.rememberSwipeBoxControl
import cn.idesign.cui.swipe.rememberSwipeState
import cn.idesign.cui.utils.ContentAlpha
import kotlinx.coroutines.launch

@Composable
fun SwipeTest() {
    var checked by remember {
        mutableStateOf(false)
    }
    val state = rememberSwipeState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val control: SwipeBoxControl = rememberSwipeBoxControl()
    LazyColumn(
        Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "基本用法",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.high),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
            SwipeBox(
                control = control,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                actionWidth = 70.dp,
                endAction = listOf {
                    Box(
                        modifier = Modifier
                            .width(66.dp)
                            .fillMaxHeight()
                            .background(Color.Red)
                            .clickable {

                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "删除",
                            color = Color.White,

                            )
                    }
                }
            ) {
                Cell(text = "左滑删除")
            }
        }
        item {
            Text(
                text = "右滑删除",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.high),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
            Swipe(
                direction = SwipeDirection.LeftToRight,
                background = {
                    Box(
                        modifier = Modifier
                            .width(66.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "收藏",
                            color = Color.White,

                            )
                    }
                }) {
                Cell(text = "右滑删除")
            }
        }
        item {
            Text(
                text = "多个操作",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.high),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
            Swipe(background = {
                Row {
                    Box(
                        modifier = Modifier
                            .width(66.dp)
                            .fillMaxHeight()
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "删除",
                            color = Color.White,

                            )
                    }
                    Box(
                        modifier = Modifier
                            .width(66.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "收藏",
                            color = Color.White,

                            )
                    }
                }
            }) {
                Cell(text = "多个操作")
            }
        }

        item {
            Text(
                text = "异步控制",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.high),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
            Swipe(
                state = state,
                background = {
                    Box(
                        modifier = Modifier
                            .width(66.dp)
                            .fillMaxHeight()
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "删除",
                            color = Color.White,

                            )
                    }

                }) {
                Cell(
                    text = "多个操作",
                    rightComponent = {
                        Switch(checked = checked, onCheckedChange = {
                            checked = it
                            scope.launch {
                                if (it) {
                                    state.open()
                                } else {
                                    state.close()
                                }
                            }
                        })
                    },
                )
            }
        }
    }
}