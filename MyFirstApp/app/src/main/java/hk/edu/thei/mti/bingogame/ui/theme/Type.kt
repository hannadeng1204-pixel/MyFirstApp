package hk.edu.thei.mti.bingogame.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The set of Material typography styles used by the app.
 *
 * This defines the default text styling (the "bodyLarge" style). Additional
 * styles such as "titleLarge" or "labelSmall" can be overridden here if the
 * app needs custom text appearances.
 */
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
