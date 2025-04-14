package cn.idesign.cui.testclient.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
//    primary = Color(0xFF121212),
//    onPrimary = Color.White,
//    error = Red700,
)

private val BlueColorPalette = lightColorScheme(
    primary = Blue500,
    secondary = Pink300,
//    error = Red700,
    background = Background,
    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

private val LightColorPalette = lightColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Pink300,
//    error = Red700,
    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun CUITestTheme(theme: Theme = Theme.Blue, content: @Composable () -> Unit) {
    val colors = when (theme) {
        Theme.Dark -> DarkColorPalette
        Theme.Light -> LightColorPalette
        Theme.Blue -> BlueColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}

enum class Theme {
    Light, Dark,Blue
}