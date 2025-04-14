package cn.idesign.cui.addresslist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cn.idesign.cui.utils.ContentAlpha

@Composable
fun AddressItem(
    modifier: Modifier = Modifier,
    data: AddressModel,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    secondaryTextStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(
        MaterialTheme.colorScheme.onSurface.copy(
            ContentAlpha.disabled
        )
    ),
    onClick: ((data: AddressModel) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                onClick?.invoke(data)
            }
            .padding(start = 10.dp)
            .then(modifier),
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (data.defaultAddress) {
                Box(Modifier.padding(end = 5.dp)) {
                    Text(
                        text = "默认",
                        style = secondaryTextStyle.copy(Color.White),
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp)
                            )
                            .padding(horizontal = 5.dp)
                    )
                }
            }
            data.tag?.let { tag ->
                Box(Modifier.padding(end = 5.dp)) {
                    Text(
                        text = tag,
                        style = secondaryTextStyle.copy(Color.White),
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp)
                            )
                            .padding(horizontal = 5.dp)
                    )
                }
            }
            Text(text = data.area, style = secondaryTextStyle)
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(end = 10.dp)
        ) {
            Text(text = data.address, style = textStyle)
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .alpha(ContentAlpha.medium)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = data.receiver, style = secondaryTextStyle)
            Spacer(modifier = Modifier.width(48.dp))
            Text(text = data.phoneNumber, style = secondaryTextStyle)
        }
    }
}