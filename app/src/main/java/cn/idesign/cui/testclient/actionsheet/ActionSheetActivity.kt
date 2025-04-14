package cn.idesign.cui.testclient.actionsheet

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cn.idesign.cui.testclient.BaseActivity
import cn.idesign.cui.testclient.ui.theme.CUITestTheme

class ActionSheetActivity : BaseActivity() {

    @Composable
    override fun Render() {
        ActionSheetTest()
    }

    override fun title(): String = "ActionSheet示例"
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CUITestTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "ActionSheet示例",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            ActionSheetTest()
        }
    }
}