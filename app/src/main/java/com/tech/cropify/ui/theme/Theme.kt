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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.tech.cropify.util.ThemeMode

object AppColors {
    val DarkGreen    = Color(0xFF1E4010)
    val MedGreen     = Color(0xFF3A7A20)
    val AccentGreen  = Color(0xFF4A8A30)
    val YellowAccent = Color(0xFFF5C842)
    val BgCream      = Color(0xFFF0EBE0)
    val CardBorder   = Color(0xFFE0D8C8)
    val TextDark     = Color(0xFF2A2010)
    val TextMuted    = Color(0xFF8A7A5A)

    // Dark theme variants
    val DarkSurface     = Color(0xFF16240E)
    val DarkBorder      = Color(0xFF2E4A1E)
    val DarkTextMuted   = Color(0xFF9BAE8C)
}

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

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Composable
fun CropifyTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkScheme
        else -> lightScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, true)
            window.statusBarColor = if (darkTheme) primaryDark.toArgb() else primaryLight.toArgb()
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
