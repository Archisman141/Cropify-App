package com.tech.cropify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tech.cropify.model.recommend.RecommendationBody
import com.tech.cropify.model.recommend.TopCrop
import com.tech.cropify.viewModel.CropRecommendViewModel

@Composable
fun CropRecommendationContent(
    viewModel: CropRecommendViewModel,
    selectedTab: CropTab,
    onSelectTab: (CropTab) -> Unit
) {
    var n by remember { mutableStateOf(40f) }
    var p by remember { mutableStateOf(40f) }
    var k by remember { mutableStateOf(40f) }
    var ph by remember { mutableStateOf(6.5f) }
    var temperature by remember { mutableStateOf(25f) }
    var humidity by remember { mutableStateOf(60f) }
    var rainfall by remember { mutableStateOf(100f) }

    val uiState by viewModel.uiState.collectAsState()

    Column {
        PageHeroHeader(
            icon = "🌱",
            title = "Crop Recommendation",
            subtitle = "Give us your soil & climate readings for the best-fit crops"
        )

        CropTabSwitcher(selected = selectedTab, onSelect = onSelectTab)

        Column(modifier = Modifier.padding(14.dp)) {
            FarmCard {
                SectionLabel("① Soil Nutrients (N-P-K)")
                Spacer(Modifier.height(11.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f)) {
                        IngredientBox("Nitrogen", n, "N", range = 0f..140f) { n = it }
                    }
                    Column(Modifier.weight(1f)) {
                        IngredientBox("Phosphorus", p, "P", range = 0f..145f) { p = it }
                    }
                    Column(Modifier.weight(1f)) {
                        IngredientBox("Potassium", k, "K", range = 0f..205f) { k = it }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            FarmCard {
                SectionLabel("② Soil & Climate Conditions")
                Spacer(Modifier.height(11.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        IngredientBox("Soil pH", ph, "pH", range = 0f..14f, steps = 27) { ph = it }
                        IngredientBox("Temperature", temperature, "°C", range = 0f..50f) { temperature = it }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        IngredientBox("Humidity", humidity, "%", range = 0f..100f) { humidity = it }
                        IngredientBox("Rainfall", rainfall, "mm", range = 0f..300f) { rainfall = it }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.getRecommendation(
                        RecommendationBody(
                            N = n.toInt().toString(),
                            P = p.toInt().toString(),
                            K = k.toInt().toString(),
                            ph = "%.1f".format(ph),
                            temperature = temperature.toInt().toString(),
                            humidity = humidity.toInt().toString(),
                            rainfall = rainfall.toInt().toString()
                        )
                    )
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(MedGreen, AccentGreen)), RoundedCornerShape(13.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            Text("Analysing…", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
                        }
                    } else {
                        Text("🌱  Get Recommendations", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }
            }

            uiState.error?.let { message ->
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(11.dp))
                        .background(Color(0xFFFCEAEA))
                        .border(1.dp, Color(0xFFE8B4B0), RoundedCornerShape(11.dp))
                        .padding(12.dp)
                ) {
                    Text("⚠️ $message", fontSize = 13.sp, color = Color(0xFFB3261E))
                }
            }

            uiState.result?.let { result ->
                Spacer(Modifier.height(12.dp))
                RecommendationResultCard(result.top_crops)
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun RecommendationResultCard(topCrops: List<TopCrop>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🎯 Top Crop Matches", fontFamily = FontFamily.Serif, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A4010))
            Spacer(Modifier.height(12.dp))
            topCrops.sortedByDescending { it.probability }.forEachIndexed { index, crop ->
                CropProbabilityRow(rank = index + 1, crop = crop)
                if (index != topCrops.lastIndex) Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun CropProbabilityRow(rank: Int, crop: TopCrop) {
    val pct = (crop.probability * 100).coerceIn(0.0, 100.0)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#$rank  ${crop.crop}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2A2010))
            Text("%.1f%%".format(pct), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MedGreen)
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { (pct / 100f).toFloat() },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = AccentGreen,
            trackColor = Color(0xFFE0D8C8)
        )
    }
}