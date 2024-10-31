import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import lbtool.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.InternalResourceApi

@OptIn(InternalResourceApi::class)
@Composable
fun CustomTheme(content: @Composable () -> Unit) {
    val fontFamily = FontFamily(
        Font(
            FontResource(Res.font.),
            FontWeight.Normal,
            FontStyle.Normal
        ),
    )

    MaterialTheme(
        typography = MaterialTheme.typography.copy(defaultFontFamily = fontFamily),
        content = content
    )
}