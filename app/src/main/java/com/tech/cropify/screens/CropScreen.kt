package com.tech.cropify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.tech.cropify.model.prediction.Prediction
import com.tech.cropify.model.prediction.PredictionBody
import com.tech.cropify.viewModel.PredictionViewModel

// ── Brand colours ──────────────────────────────────────────────────────────
private val DarkGreen    = Color(0xFF1E4010)
private val MedGreen     = Color(0xFF2D5E1A)
private val AccentGreen  = Color(0xFF4A8A30)
private val YellowAccent = Color(0xFFF5C842)
private val BgCream      = Color(0xFFF5F0E8)
private val CardBorder   = Color(0xFFE0D8C8)
private val TextDark     = Color(0xFF2A2010)
private val TextMuted    = Color(0xFF8A7A5A)
private val BoxBg        = Color(0xFFF5F0E8)
private val ErrorRed     = Color(0xFFB3261E)
private val ErrorBg      = Color(0xFFFCEAEA)

data class CropOption(val icon: String, val name: String)

private val crops = listOf(
    CropOption("🌽", "Maize"),
    CropOption("🌾", "Wheat"),
    CropOption("🍚", "Rice"),
    CropOption("🏵️", "Cotton"),
    CropOption("🌱", "Soybean"),
    CropOption("🎋", "Sugarcane"),
    CropOption("🍅", "Tomato"),
    CropOption("🥜", "Groundnut"),
)

private val indianStates = listOf(
    "Andhra Pradesh", "Assam", "Bihar", "Chhattisgarh", "Gujarat", "Haryana",
    "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Odisha", "Punjab",
    "Rajasthan", "Tamil Nadu", "Telangana", "Uttar Pradesh", "West Bengal"
)

@Composable
fun CropScreen(
    navController: NavHostController,
    bottomNavController: NavHostController,
    viewModel: PredictionViewModel = hiltViewModel()
) {
    var selectedCrop  by remember { mutableStateOf("Rice") }
    var selectedState by remember { mutableStateOf("West Bengal") }
    var areaHectare   by remember { mutableStateOf(12f) }
    var fertilizer    by remember { mutableStateOf(10f) }
    var pesticide     by remember { mutableStateOf(10f) }
    var rainfall      by remember { mutableStateOf(10f) }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { CropTopBar(navController) },
        containerColor = BgCream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            PageHeroHeader(
                icon = "🌾",
                title = "Smart Crop Prediction",
                subtitle = "Enter your field details for an AI yield prediction"
            )

            Column(modifier = Modifier.padding(14.dp)) {

                // ── Step 1: Select Crop ───────────────────────────────────────
                FarmCard {
                    SectionLabel("① Select Crop to Analyse")
                    Spacer(Modifier.height(11.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        crops.forEach { crop ->
                            CropChip(crop, selected = selectedCrop == crop.name) {
                                selectedCrop = crop.name
                                viewModel.clearResult()
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Step 2: State ────────────────────────────────────────────
                FarmCard {
                    SectionLabel("② Select Your State")
                    Spacer(Modifier.height(11.dp))
                    StateDropdown(selectedState) { selectedState = it }
                }

                Spacer(Modifier.height(12.dp))

                // ── Step 3: Field Details ─────────────────────────────────────
                FarmCard {
                    SectionLabel("③ Field & Input Details")
                    Spacer(Modifier.height(11.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            IngredientBox("Farm Area", areaHectare, "hectare", range = 0.5f..50f, steps = 98) { areaHectare = it }
                            IngredientBox("Fertilizer Used", fertilizer, "kg/ha", range = 0f..300f) { fertilizer = it }
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            IngredientBox("Rainfall", rainfall, "mm", range = 0f..2000f) { rainfall = it }
                            IngredientBox("Pesticide Used", pesticide, "kg/ha", range = 0f..50f, steps = 100) { pesticide = it }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Predict Button ────────────────────────────────────────────
                Button(
                    onClick = {
                        viewModel.getPrediction(
                            PredictionBody(
                                state = selectedState,
                                crop = selectedCrop,
                                area = areaHectare.toString(),
                                fertilizer = fertilizer.toString(),
                                pesticide = pesticide.toString(),
                                rainfall = rainfall.toString()
                            )
                        )
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(listOf(MedGreen, AccentGreen)),
                                RoundedCornerShape(13.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isLoading) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text("Analysing…", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
                            }
                        } else {
                            Text("🤖  Run AI Prediction", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
                        }
                    }
                }

                // ── Error state ──────────────────────────────────────────────
                uiState.error?.let { message ->
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(11.dp))
                            .background(ErrorBg)
                            .border(1.dp, Color(0xFFE8B4B0), RoundedCornerShape(11.dp))
                            .padding(12.dp)
                    ) {
                        Text("⚠️ $message", fontSize = 13.sp, color = ErrorRed)
                    }
                }

                // ── Result Card ───────────────────────────────────────────────
                uiState.result?.let { result ->
                    Spacer(Modifier.height(12.dp))
                    CropResultCard(selectedCrop, result)
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────
@Composable
fun CropTopBar(navController: NavHostController) {
    Surface(color = Color.White, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("🌾", fontSize = 15.sp)
                Text("Crop Prediction", fontFamily = FontFamily.Serif, fontSize = 15.sp, color = Color(0xFF2D4A1E))
            }
            Spacer(Modifier.width(56.dp))
        }
    }
}

@Composable
fun PageHeroHeader(icon: String, title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(Color(0xFF1E4010), Color(0xFF3A7A20))))
            .padding(vertical = 20.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 38.sp)
            Spacer(Modifier.height(7.dp))
            Text(title, fontFamily = FontFamily.Serif, fontSize = 21.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, fontSize = 13.sp, color = Color(0xB8FFFFFF), textAlign = TextAlign.Center)
        }
    }
}

//@Composable
//fun SectionLabel(text: String) {
//    Text(text.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextMuted, letterSpacing = 0.5.sp)
//}

@Composable
fun FarmCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp), content = content)
    }
}

@Composable
private fun CropChip(crop: CropOption, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(13.dp))
            .border(2.dp, if (selected) AccentGreen else CardBorder, RoundedCornerShape(13.dp))
            .background(if (selected) Color(0xFFE8F5E1) else Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp)
            .width(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(crop.icon, fontSize = 24.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            crop.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) MedGreen else Color(0xFF4A3A1E),
            textAlign = TextAlign.Center
        )
    }
}

// ── State dropdown ───────────────────────────────────────────────────────────
@Composable
private fun StateDropdown(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(11.dp))
                .background(BoxBg)
                .clickable { expanded = true }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selected, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
            Text("▾", fontSize = 14.sp, color = MedGreen)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            indianStates.forEach { state ->
                DropdownMenuItem(
                    text = { Text(state) },
                    onClick = {
                        onSelect(state)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ── Ingredient box with slider ─────────────────────────────────────────────────
@Composable
private fun IngredientBox(
    label: String,
    value: Float,
    unit: String,
    range: ClosedFloatingPointRange<Float> = 0f..300f,
    steps: Int = 0,
    onChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(11.dp))
            .background(BoxBg)
            .padding(11.dp)
    ) {
        Text(label, fontSize = 11.sp, color = TextMuted)
        Spacer(Modifier.height(5.dp))
        Text(
            if (value == value.toLong().toFloat()) value.toInt().toString()
            else "%.2f".format(value),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MedGreen
        )
        Text(unit, fontSize = 10.sp, color = TextMuted)
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = range,
            steps = steps,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MedGreen,
                activeTrackColor = AccentGreen,
                inactiveTrackColor = Color(0xFFD4C9A8)
            )
        )
    }
}

// ── Prediction result card — bound to real API response ──────────────────────
@Composable
private fun CropResultCard(cropName: String, result: Prediction) {
    Column {
        // ── Prediction vs Optimized summary ─────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(Brush.linearGradient(listOf(Color(0xFFE8F5E1), Color(0xFFD4F0B0))))
                .border(1.dp, Color(0xFFB0D890), RoundedCornerShape(15.dp))
                .padding(18.dp)
        ) {
            Column {
                Text("🎯  AI Prediction Result", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A6B2E))
                Spacer(Modifier.height(9.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🌽", fontSize = 44.sp)
                    Column {
                        Text(cropName, fontFamily = FontFamily.Serif, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A4010))
                        Text(
                            "Predicted Yield: %.2f t/ha".format(result.prediction.predicted_yield_ton_per_hectare),
                            fontSize = 13.sp,
                            color = Color(0xFF4A7A30)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResultBreakdown(
                        "%.2f t/ha".format(result.prediction.predicted_yield_ton_per_hectare),
                        "Predicted Yield",
                        Modifier.weight(1f)
                    )
                    ResultBreakdown(
                        "%.2f t".format(result.prediction.total_production_ton),
                        "Total Production",
                        Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))
                // ── Optimization card ──────────────────────────────────────
                Card(
                    shape = RoundedCornerShape(13.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(13.dp)) {
                        Text("📈 If You Optimize Inputs", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4A3A1E))
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ResultBreakdown(
                                "%.2f t/ha".format(result.optimization.optimized_yield_ton_per_hectare),
                                "Optimized Yield",
                                Modifier.weight(1f)
                            )
                            ResultBreakdown(
                                "%.2f t".format(result.optimization.optimized_total_production),
                                "Optimized Total",
                                Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            ResultPill("🧪 Fert: %.1f kg/ha".format(result.optimization.recommended_fert_kg_ha))
                            ResultPill("🐛 Pest: %.1f kg/ha".format(result.optimization.recommended_pest_kg_ha))
                        }
                    }
                }
            }
        }

        // ── AI Advice card ───────────────────────────────────────────────────
        Spacer(Modifier.height(12.dp))
        Card(
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🧑‍🌾 AI Farm Advisory", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MedGreen)
                Spacer(Modifier.height(10.dp))
                AdviceMarkdownText(result.ai_advice )
            }
        }

        Spacer(Modifier.height(10.dp))
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(11.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MedGreen)
        ) { Text("📄 Save Report", fontSize = 13.sp, color = Color.White) }
    }
}

@Composable
private fun ResultPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFB0D890), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) { Text(text, fontSize = 12.sp, color = Color(0xFF3A6B2E)) }
}

@Composable
private fun ResultBreakdown(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(BoxBg)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MedGreen)
        Spacer(Modifier.height(2.dp))
        Text(label, fontSize = 10.sp, color = TextMuted, textAlign = TextAlign.Center)
    }
}

// ── Lightweight markdown renderer for ai_advice ──────────────────────────────
// Handles **bold**, "*  " / "-  " bullets, and blank-line paragraph breaks.
// Good enough for the backend's formatting without pulling in a markdown library.
@Composable
private fun AdviceMarkdownText(raw: String) {
    val lines = raw.split("\n")
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        lines.forEach { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) {
                Spacer(Modifier.height(4.dp))
                return@forEach
            }
            val isBullet = line.startsWith("*") || line.startsWith("-") || line.startsWith("⭐")
            val cleaned = when {
                line.startsWith("*   ") -> line.removePrefix("*   ")
                line.startsWith("* ") -> line.removePrefix("* ")
                line.startsWith("- ") -> line.removePrefix("- ")
                else -> line
            }
            Row {
                if (isBullet) {
                    Text("•  ", fontSize = 13.sp, color = MedGreen)
                }
                Text(
                    text = boldAnnotatedString(cleaned),
                    fontSize = 13.sp,
                    color = TextDark,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

private fun boldAnnotatedString(text: String) = buildAnnotatedString {
    var remaining = text
    while (true) {
        val start = remaining.indexOf("**")
        if (start == -1) {
            append(remaining)
            break
        }
        val end = remaining.indexOf("**", start + 2)
        if (end == -1) {
            append(remaining)
            break
        }
        append(remaining.substring(0, start))
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = MedGreen)) {
            append(remaining.substring(start + 2, end))
        }
        remaining = remaining.substring(end + 2)
    }
}