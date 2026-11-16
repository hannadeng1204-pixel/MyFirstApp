package hk.edu.thei.mti.bingogame.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// The colour scheme used when the device is in dark mode.
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// The colour scheme used when the device is in light mode.
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

/**
 * The Compose theme wrapper for the app.
 *
 * It automatically picks the light or dark colour scheme based on the system
 * setting, and (on Android 12+) can use dynamic colour from the user's
 * wallpaper when [dynamicColor] is true.
 *
 * @param darkTheme    whether the dark theme should be used.
 * @param dynamicColor whether to use Material You dynamic colour (Android 12+).
 * @param content      the Compose UI to wrap with this theme.
 */
@Composable
fun MyFirstAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic colour is only available on Android 12 (API 31) and above.
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Provide the chosen scheme and typography to the Compose tree.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
