import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.Sector_Bold
import lbtool.composeapp.generated.resources.Sector_ExtraBold
import lbtool.composeapp.generated.resources.Sector_Regular
import org.jetbrains.compose.resources.Font

@Composable
fun CustomTheme(content: @Composable () -> Unit) {
    val ff = FontFamily(
        Font(Res.font.Sector_Regular, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.Sector_Bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.Sector_ExtraBold, FontWeight.ExtraBold, FontStyle.Normal)
    )
    MaterialTheme(
        content = content,
        typography = Typography().copy(
            headlineSmall = Typography().headlineSmall.copy(
                fontFamily = ff,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal
            ),
            headlineMedium = Typography().headlineMedium.copy(
                fontFamily = ff,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal
            ),
            headlineLarge = Typography().headlineLarge.copy(
                fontFamily = ff,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Normal
            ),
        ),
    )
}