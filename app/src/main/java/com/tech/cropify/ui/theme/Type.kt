package com.tech.cropify.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tech.cropify.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

// ─────────────────────────────────────────────────────────────────────────────
// Google Fonts
// ─────────────────────────────────────────────────────────────────────────────

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

val bodyFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Montserrat"), fontProvider = provider)
)

val displayFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Montserrat"), fontProvider = provider)
)

val cormorantGaramond = FontFamily(
    Font(googleFont = GoogleFont("Cormorant Garamond"), fontProvider = provider)
)

// ─────────────────────────────────────────────────────────────────────────────
// Typography
// ─────────────────────────────────────────────────────────────────────────────

val baseline = Typography()

val AppTypography = Typography(
    displayLarge  = baseline.displayLarge .copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall  = baseline.displaySmall .copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium= baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge    = baseline.titleLarge   .copy(fontFamily = displayFontFamily),
    titleMedium   = baseline.titleMedium  .copy(fontFamily = displayFontFamily),
    titleSmall    = baseline.titleSmall   .copy(fontFamily = displayFontFamily),
    bodyLarge     = baseline.bodyLarge    .copy(fontFamily = bodyFontFamily),
    bodyMedium    = baseline.bodyMedium   .copy(fontFamily = bodyFontFamily),
    bodySmall     = baseline.bodySmall    .copy(fontFamily = bodyFontFamily),
    labelLarge    = baseline.labelLarge   .copy(fontFamily = bodyFontFamily),
    labelMedium   = baseline.labelMedium  .copy(fontFamily = bodyFontFamily),
    labelSmall    = baseline.labelSmall   .copy(fontFamily = bodyFontFamily),
)

// ─────────────────────────────────────────────────────────────────────────────
// nonScaledSp  — reliable across ALL Android versions / OEMs
//
// WHY the old approach failed:
//   value / fontScale → then → .sp
//   Compose converts sp → px by multiplying by (density * fontScale) at draw time,
//   so the manual division only cancels scaling on the frame it was composed; any
//   recomposition triggered by a system font-scale change reapplies the broken math.
//
// CORRECT approach:
//   Convert the desired "visual dp" to raw px using density alone (fontScale = 1),
//   then convert those px back to sp using the REAL (density * fontScale) factor.
//   Result: px ÷ (density × fontScale) × fontScale cancels → immune to fontScale.
//
//   Simplified formula:  desiredDp.dp.toPx() / (density * fontScale) → TextUnit in sp
//   which reduces to:    desiredDp / fontScale  (same dp for every device) → sp
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Converts an [Int] dp value to a [TextUnit] that renders at a fixed physical
 * size regardless of the user's system font-scale setting.
 */
val Int.nonScaledSp: TextUnit
    @Composable
    @ReadOnlyComposable
    get() = with(LocalDensity.current) {
        val px = this@nonScaledSp.dp.toPx()

        (px / (density * fontScale)).sp
    }

/**
 * Converts a [Double] dp value to a fixed-size [TextUnit].
 */
val Double.nonScaledSp: TextUnit
    @Composable
    @ReadOnlyComposable
    get() = with(LocalDensity.current) {
        val px = this@nonScaledSp.toFloat() * density   // dp → px
        (px / (density * fontScale)).sp
    }


/**
 * Convenience overload for [Float].
 */
val Float.nonScaledSp: TextUnit
    @Composable
    @ReadOnlyComposable
    get() = toDouble().nonScaledSp


/**
 * Pre-defined, scale-immune text sizes used across the app.
 * All values are expressed in dp-equivalent visual units via [nonScaledSp].
 *
 * Usage:
 *   Text("Hello", fontSize = TextSize.body)
 */
object TextSize {
    /** 9 dp — tiny labels, version strings */
    val xs: TextUnit     @Composable @ReadOnlyComposable get() = 9.nonScaledSp
    /** 9.5 dp — micro descriptions */
    val xsMd: TextUnit   @Composable @ReadOnlyComposable get() = 9.5.nonScaledSp
    /** 10 dp — caption, badge text */
    val caption: TextUnit @Composable @ReadOnlyComposable get() = 10.nonScaledSp
    /** 10.5 dp — sub-captions (e.g. greeting hi line) */
    val captionMd: TextUnit @Composable @ReadOnlyComposable get() = 10.5.nonScaledSp
    /** 11 dp — nav labels, small chips */
    val label: TextUnit  @Composable @ReadOnlyComposable get() = 11.nonScaledSp
    /** 12 dp — supporting body, drawer sub-items */
    val small: TextUnit  @Composable @ReadOnlyComposable get() = 12.nonScaledSp
    /** 12.5 dp — card names, semi-bold labels */
    val smallMd: TextUnit @Composable @ReadOnlyComposable get() = 12.5.nonScaledSp
    /** 13 dp — secondary body */
    val bodySmall: TextUnit @Composable @ReadOnlyComposable get() = 13.nonScaledSp
    /** 14 dp — primary body / drawer items */
    val body: TextUnit   @Composable @ReadOnlyComposable get() = 14.nonScaledSp
    /** 15 dp — slightly prominent body */
    val bodyMd: TextUnit @Composable @ReadOnlyComposable get() = 15.nonScaledSp
    /** 17 dp — emphasized body, course descriptions */
    val bodyLg: TextUnit @Composable @ReadOnlyComposable get() = 17.nonScaledSp
    /** 16 dp — section titles, rail labels */
    val subtitle: TextUnit @Composable @ReadOnlyComposable get() = 16.nonScaledSp
    /** 18 dp — card headings */
    val title: TextUnit  @Composable @ReadOnlyComposable get() = 18.nonScaledSp
    /** 19 dp — greeting name */
    val titleLg: TextUnit @Composable @ReadOnlyComposable get() = 19.nonScaledSp
    /** 20 dp — tablet nav labels */
    val titleXl: TextUnit @Composable @ReadOnlyComposable get() = 20.nonScaledSp
    /** 22 dp — screen headings */
    val heading: TextUnit @Composable @ReadOnlyComposable get() = 22.nonScaledSp
    /** 24 dp — large headings */
    val headingLg: TextUnit @Composable @ReadOnlyComposable get() = 24.nonScaledSp
    /** 26 dp — hero / display */
    val display: TextUnit @Composable @ReadOnlyComposable get() = 26.nonScaledSp
    /** 28 dp — large hero text */
    val displayMd: TextUnit @Composable @ReadOnlyComposable get() = 28.nonScaledSp
    /** 32 dp — large display / hero tablet */
    val displayLg: TextUnit @Composable @ReadOnlyComposable get() = 32.nonScaledSp
}