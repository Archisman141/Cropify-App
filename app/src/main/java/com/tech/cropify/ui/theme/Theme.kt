package com.tech.cropify.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object CropifyColors {
    val ForestGreen  = Color(0xFF1B3A2D)
    val Amber        = Color(0xFFC8A535)
    val AmberDark    = Color(0xFFB8941F)
    val OffWhite     = Color(0xFFF8F6F1)
    val SageGreen    = Color(0xFF7AB87A)
    val LeafDark     = Color(0xFF2D5A40)
    val LeafMid      = Color(0xFF3D7A50)
    val LeafLight    = Color(0xFF4A9A5A)
    val ToggleBg     = Color(0xFFE8E4DD)
    val InputBorder  = Color(0xFFD8D3C8)
    val InputBorderFocus = Color(0xFF1B3A2D)
    val TextPrimary  = Color(0xFF1B3A2D)
    val TextMuted    = Color(0xFF7A7060)
    val TextLabel    = Color(0xFF5A5040)
    val TextHint     = Color(0xFF9A9080)
    val BadgeBg      = Color(0xFFE8F0E4)
    val BadgeText    = Color(0xFF2D6040)
    val BadgeDot     = Color(0xFF4A9A5A)
    val White        = Color(0xFFFFFFFF)
    val DividerColor = Color(0xFFD8D3C8)
    val StrengthWeak = Color(0xFFC8A535)
    val StrengthBg   = Color(0xFFD8D3C8)
    val Surface      = Color(0xFFF8F6F1)
    val GoogleBorder = Color(0xFFD8D3C8)
}

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)



@Composable
fun CropifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}