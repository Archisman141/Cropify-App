package com.tech.cropify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ── Brand colours (shared constants ideally in a Theme file) ──────────────────
private val DarkGreen    = Color(0xFF1E4010)
private val MedGreen     = Color(0xFF2D5E1A)
private val AccentGreen  = Color(0xFF4A8A30)
private val YellowAccent = Color(0xFFF5C842)
private val BgCream      = Color(0xFFF5F0E8)
private val CardBorder   = Color(0xFFE0D8C8)
private val TextDark     = Color(0xFF2A2010)
private val TextMuted    = Color(0xFF8A7A5A)
private val BoxBg        = Color(0xFFF5F0E8)

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

@Composable
fun CropScreen(navController: NavHostController) {

    var nitrogen       by remember { mutableStateOf(142f) }
    var phosphorus     by remember { mutableStateOf(48f) }
    var potassium      by remember { mutableStateOf(198f) }
    var ph             by remember { mutableStateOf(6.8f) }
    var organicCarbon  by remember { mutableStateOf(0.72f) }
    var sulphur        by remember { mutableStateOf(12f) }
    var temperature    by remember { mutableStateOf(26f) }
    var rainfall       by remember { mutableStateOf(180f) }
    var humidity       by remember { mutableStateOf(72f) }
    var moisture       by remember { mutableStateOf(34f) }

    var selectedCrop   by remember { mutableStateOf("Maize") }
    var showResult     by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CropTopBar(navController)
        },
        containerColor = BgCream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Page Hero ─────────────────────────────────────────────────────
            PageHeroHeader(
                icon = "🌾",
                title = "Smart Crop Prediction",
                subtitle = "Select crop & enter soil ingredients for AI prediction"
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
                                showResult = false
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Step 2: Soil Ingredients ──────────────────────────────────
                FarmCard {
                    SectionLabel("② Soil Ingredients (NPK & More)")
                    Spacer(Modifier.height(11.dp))
                    // 2-column grid
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            IngredientBox("Nitrogen (N)", nitrogen, "kg/ha") { nitrogen = it }
                            IngredientBox("Potassium (K)", potassium, "kg/ha", range = 0f..300f) { potassium = it }
                            IngredientBox("Organic Carbon", organicCarbon, "%", range = 0.1f..3f, steps = 100) { organicCarbon = it }
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            IngredientBox("Phosphorus (P)", phosphorus, "kg/ha", range = 0f..200f) { phosphorus = it }
                            IngredientBox("Soil pH", ph, "pH level", range = 4f..9f, steps = 100) { ph = it }
                            IngredientBox("Sulphur (S)", sulphur, "ppm", range = 0f..60f) { sulphur = it }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Step 3: Climate & Field ───────────────────────────────────
                FarmCard {
                    SectionLabel("③ Climate & Field Details")
                    Spacer(Modifier.height(11.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            IngredientBox("Temperature", temperature, "°C", range = 10f..45f) { temperature = it }
                            IngredientBox("Humidity", humidity, "%", range = 20f..100f) { humidity = it }
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            IngredientBox("Rainfall", rainfall, "mm", range = 20f..400f) { rainfall = it }
                            IngredientBox("Soil Moisture", moisture, "%", range = 5f..80f) { moisture = it }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Predict Button ────────────────────────────────────────────
                Button(
                    onClick = { showResult = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
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
                        Text(
                            "🤖  Run AI Prediction",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                // ── Result Card ───────────────────────────────────────────────
                if (showResult) {
                    Spacer(Modifier.height(12.dp))
                    CropResultCard(selectedCrop)
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

// ── Page hero (green gradient banner at top of each feature screen) ───────────
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

// ── Section label ─────────────────────────────────────────────────────────────
@Composable
fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = TextMuted,
        letterSpacing = 0.5.sp
    )
}

// ── White rounded card ────────────────────────────────────────────────────────
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

// ── Crop chip (scrollable selector) ──────────────────────────────────────────
@Composable
private fun CropChip(crop: CropOption, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(13.dp))
            .border(
                2.dp,
                if (selected) AccentGreen else CardBorder,
                RoundedCornerShape(13.dp)
            )
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

// ── Prediction result card ────────────────────────────────────────────────────
@Composable
private fun CropResultCard(cropName: String) {
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
                    Text("Suitability Score: 91% · Highly Recommended", fontSize = 13.sp, color = Color(0xFF4A7A30))
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                ResultPill("✅ Soil Match: Excellent")
                ResultPill("🌧️ Rainfall: Adequate")
                ResultPill("🌡️ Temp: Ideal")
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ResultBreakdown("4.2t", "Est. Yield/acre", Modifier.weight(1f))
                ResultBreakdown("₹85K", "Revenue Est.", Modifier.weight(1f))
                ResultBreakdown("90 days", "Duration", Modifier.weight(1f))
            }

            Spacer(Modifier.height(10.dp))
            // Sowing timeline
            Card(
                shape = RoundedCornerShape(13.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(13.dp)) {
                    Text("📅 Recommended Sowing Timeline", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4A3A1E))
                    Spacer(Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        TimelineStep("Jun", "Sow")
                        TimelineLine()
                        TimelineStep("Jul", "Fertilise")
                        TimelineLine()
                        TimelineStep("Aug", "Irrigate")
                        TimelineLine()
                        TimelineStep("Sep", "Harvest")
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFF3CD))
                    .padding(10.dp)
            ) {
                Text(
                    "⚠️ Phosphorus is slightly low. Apply DAP @ 50 kg/acre before sowing for best yield.",
                    fontSize = 12.sp,
                    color = Color(0xFF7A5A10)
                )
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
            .background(Color.White)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MedGreen)
        Spacer(Modifier.height(2.dp))
        Text(label, fontSize = 10.sp, color = TextMuted, textAlign = TextAlign.Center)
    }
}

@Composable
private fun RowScope.TimelineStep(month: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(AccentGreen)
        )
        Spacer(Modifier.height(5.dp))
        Text(month, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MedGreen)
        Text(label, fontSize = 10.sp, color = Color(0xFF5A4A2E))
    }
}

@Composable
private fun RowScope.TimelineLine() {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(2.dp)
            .background(Color(0xFFC0D8A0))
            .padding(bottom = 17.dp)
    )
}