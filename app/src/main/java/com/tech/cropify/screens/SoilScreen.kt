package com.tech.cropify.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────────────────────────────────────
// Colour palette  (mirrors farmsmart_app_v2.html)
// ─────────────────────────────────────────────────────────────────────────────
private val BgCream         = Color(0xFFF5F0E8)
private val GreenDark       = Color(0xFF1E4010)
private val GreenPrimary    = Color(0xFF2D5E1A)
private val GreenLight      = Color(0xFF4A8A30)
private val GreenPale       = Color(0xFFE8F5E1)
private val BorderTan       = Color(0xFFE0D8C8)
private val FieldBgActive   = Color.White
private val FieldBgInactive = BgCream
private val TextDark        = Color(0xFF2A2010)
private val TextBrown       = Color(0xFF4A3A1E)
private val CardWhite       = Color.White
private val TipRowDivider   = Color(0xFFF0E8D8)
private val TipTextColor    = Color(0xFF3A2A10)

// Nutrient status colours
private val StatusGoodBg    = Color(0xFFE8F5E1)
private val StatusGoodText  = Color(0xFF2D5E1A)
private val StatusLowBg     = Color(0xFFFFF3CD)
private val StatusLowText   = Color(0xFF7A5A10)

// pH spectrum gradient  (matches .ph-bar background in CSS)
private val PhGradient = Brush.horizontalGradient(
    listOf(
        Color(0xFFE84040),   // acid (red)
        Color(0xFFE8A040),   // (orange)
        Color(0xFFE8E840),   // (yellow)
        Color(0xFF40A840),   // neutral (green)
        Color(0xFF4040E8)    // alkaline (blue)
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// Data model for a nutrient row
// ─────────────────────────────────────────────────────────────────────────────
private data class Nutrient(
    val emoji:  String,
    val label:  String,
    val value:  String,
    val unit:   String,
    val status: String       // "Optimal" | "Low" | "Good"
)

private val nutrients = listOf(
    Nutrient("🔵", "Nitrogen",   "142",  "kg/ha", "Optimal"),
    Nutrient("🟠", "Phosphorus", "24",   "kg/ha", "Low"),
    Nutrient("🟣", "Potassium",  "198",  "kg/ha", "Optimal"),
    Nutrient("🟡", "Sulphur",    "8",    "ppm",   "Low"),
    Nutrient("🟤", "Organic C",  "0.72", "%",     "Low"),
    Nutrient("⚪", "Moisture",   "34",   "%",     "Good")
)

private data class FieldTab(val name: String, val sub: String)
private val fields = listOf(
    FieldTab("📍 Field A", "Main Plot"),
    FieldTab("📍 Field B", "West Plot"),
    FieldTab("📍 Field C", "East Plot")
)

// ─────────────────────────────────────────────────────────────────────────────
// SoilScreen
// ─────────────────────────────────────────────────────────────────────────────
@SuppressLint("Range")
@Composable
fun SoilScreen(navController: NavHostController, bottomNavController: NavHostController) {

    var selectedField by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            SoilTopBar(onBack = { navController.popBackStack() })
        },
        containerColor = BgCream
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ){

            // ── Top Nav ──────────────────────────────────────────────────────────
//            SoilTopBar(onBack = { navController.popBackStack() })

            // ── Scrollable Body ──────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // Green page header
                SoilPageHeader()

                Column(modifier = Modifier.padding(14.dp)) {

                    // ── Field Selector ────────────────────────────────────────
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        fields.forEachIndexed { index, field ->
                            FieldSelectorCard(
                                name     = field.name,
                                sub      = field.sub,
                                selected = index == selectedField,
                                modifier = Modifier.weight(1f),
                                onClick  = { selectedField = index }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // ── pH Card ───────────────────────────────────────────────
                    SoilCard {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                text       = "Soil pH Level",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color      = TextDark
                            )
                            Text(
                                text       = "6.8",
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = GreenPrimary
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        // pH rainbow bar  (matches .ph-bar)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(15.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(PhGradient)
                        ) {
                            // pH indicator needle  (matches .ph-indicator — left:57%)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .fillMaxHeight(1.5f)   // extends top & bottom slightly
                                    .padding(start = 0.dp) // positioned via fillMaxWidth fraction
                                    .offset(y = (-3).dp)
                            ) {
                                // We draw it as an absolutely-positioned vertical line at 57%
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 0.dp)
                                ) {
                                    PhIndicatorNeedle(fraction = 0.57f)
                                }
                            }
                        }

                        Spacer(Modifier.height(5.dp))

                        // pH scale labels  (matches .ph-scale)
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Acid 0",     fontSize = 10.sp, color = TextMuted)
                            Text("Neutral 7",  fontSize = 10.sp, color = TextMuted)
                            Text("14 Alkaline", fontSize = 10.sp, color = TextMuted)
                        }

                        Spacer(Modifier.height(8.dp))

                        // Ideal-range notice
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GreenPale)
                                .padding(8.dp)
                        ) {
                            Text(
                                text     = "✅ Ideal pH for most crops (6.0–7.5 optimal range)",
                                fontSize = 12.sp,
                                color    = GreenPrimary
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // ── Nutrient section title ────────────────────────────────
                    SoilSectionTitle("Nutrient Levels")
                    Spacer(Modifier.height(9.dp))

                    // ── Nutrient 3-column grid ────────────────────────────────
                    // We split the 6 nutrients into pairs of rows (3 per row)
                    nutrients.chunked(3).forEach { rowItems ->
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(9.dp)
                        ) {
                            rowItems.forEach { nutrient ->
                                NutrientCard(
                                    nutrient = nutrient,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Spacer(Modifier.height(9.dp))
                    }

                    Spacer(Modifier.height(3.dp))

                    // ── Improvement Tips Card ─────────────────────────────────
                    SoilCard {
                        Text(
                            text       = "🌿 Soil Improvement Tips",
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color      = TextBrown,
                            modifier   = Modifier.padding(bottom = 10.dp)
                        )

                        SoilTipRow(
                            emoji = "🧪",
                            text  = "Apply DAP @ 50kg/acre for Phosphorus deficiency",
                            hasDivider = true
                        )
                        SoilTipRow(
                            emoji = "🌱",
                            text  = "Add organic compost to improve carbon content",
                            hasDivider = true
                        )
                        SoilTipRow(
                            emoji = "💧",
                            text  = "Drip irrigation recommended to maintain moisture",
                            hasDivider = false
                        )
                    }
                }
            }
        }

    }

}

// ─────────────────────────────────────────────────────────────────────────────
// Top Nav
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SoilTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .border(width = 1.dp, color = BorderTan, shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text       = "🏔️  Soil Analysis",
            fontSize   = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color      = GreenDark
        )

        Spacer(Modifier.width(56.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Green page header  (matches .page-header ph-green)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SoilPageHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(GreenDark, GreenLight)))
            .padding(horizontal = 20.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🏔️", fontSize = 38.sp)
            Spacer(Modifier.height(7.dp))
            Text(
                text       = "Soil Health Analysis",
                fontSize   = 21.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text     = "Live data from your field sensors",
                fontSize = 13.sp,
                color    = Color.White.copy(alpha = 0.72f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Field tab card  (the three pills at the top of the soil content area)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FieldSelectorCard(
    name:     String,
    sub:      String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick:  () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(if (selected) FieldBgActive else FieldBgInactive)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) GreenLight else BorderTan,
                shape = RoundedCornerShape(11.dp)
            )
            .clickable(onClick = onClick)
            .padding(9.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text       = name,
            fontSize   = 12.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            color      = if (selected) GreenPrimary else Color(0xFF5A4A2E)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text     = sub,
            fontSize = 10.sp,
            color    = TextMuted
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// pH indicator needle  (matches .ph-indicator — thin dark vertical bar)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PhIndicatorNeedle(fraction: Float) {
    // We embed this inside a BoxWithConstraints so we can offset by fraction of width
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val barWidth = maxWidth
        Box(
            modifier = Modifier
                .offset(x = barWidth * fraction - 1.5.dp)   // centre the 3dp needle
                .width(3.dp)
                .height(23.dp)                               // slightly taller than bar (22px → ~23dp)
                .offset(y = (-4).dp)                         // .ph-indicator top:-4px
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF1A1A1A))
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Nutrient card  (matches .nutrient-card)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun NutrientCard(
    nutrient: Nutrient,
    modifier: Modifier = Modifier
) {
    val (statusBg, statusText) = when (nutrient.status) {
        "Optimal", "Good" -> Pair(StatusGoodBg, StatusGoodText)
        else              -> Pair(StatusLowBg,  StatusLowText)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(CardWhite)
            .border(1.dp, BorderTan, RoundedCornerShape(13.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(nutrient.emoji, fontSize = 22.sp)
        Spacer(Modifier.height(5.dp))
        Text(
            text     = nutrient.label,
            fontSize = 11.sp,
            color    = TextMuted
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text       = nutrient.value,
            fontSize   = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color      = TextDark
        )
        Text(
            text     = nutrient.unit,
            fontSize = 10.sp,
            color    = TextMuted
        )
        Spacer(Modifier.height(4.dp))
        // Status badge  (matches .nc-status / .status-good / .status-low)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(statusBg)
                .padding(horizontal = 7.dp, vertical = 2.dp)
        ) {
            Text(
                text     = nutrient.status,
                fontSize = 10.sp,
                color    = statusText
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Soil improvement tip row  (matches the flex rows inside the tips card)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SoilTipRow(
    emoji:      String,
    text:       String,
    hasDivider: Boolean
) {
    Column {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            modifier              = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(emoji, fontSize = 17.sp)
            Text(text, fontSize = 12.sp, color = TipTextColor)
        }
        if (hasDivider) {
            Divider(color = TipRowDivider, thickness = 1.dp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable white card shell  (matches .card)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SoilCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(15.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        border    = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.linearGradient(listOf(BorderTan, BorderTan))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp), content = content)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section title label  (matches .section-title)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SoilSectionTitle(text: String) {
    Text(
        text          = text.uppercase(),
        fontSize      = 12.sp,
        fontWeight    = FontWeight.Medium,
        color         = Color(0xFF5A4A2E),
        letterSpacing = 0.5.sp
    )
}