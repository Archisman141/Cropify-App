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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tech.cropify.model.hybrid.HybridAdvisoryBody
import com.tech.cropify.viewModel.HybridAdvisoryViewModel

private val hybridStates = listOf(
    "Andhra Pradesh", "Assam", "Bihar", "Chhattisgarh", "Gujarat", "Haryana",
    "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Odisha", "Punjab",
    "Rajasthan", "Tamil Nadu", "Telangana", "Uttar Pradesh", "West Bengal"
)

@Composable
fun HybridAdviceContent(
    viewModel: HybridAdvisoryViewModel,
    selectedTab: CropTab,
    onSelectTab: (CropTab) -> Unit
) {
    var selectedState by remember { mutableStateOf("West Bengal") }
    var n by remember { mutableStateOf(40f) }
    var p by remember { mutableStateOf(40f) }
    var k by remember { mutableStateOf(40f) }
    var ph by remember { mutableStateOf(6.5f) }
    var temperature by remember { mutableStateOf(25f) }
    var humidity by remember { mutableStateOf(60f) }
    var rainfall by remember { mutableStateOf(100f) }
    var area by remember { mutableStateOf(5f) }
    var fertilizer by remember { mutableStateOf(50f) }
    var pesticide by remember { mutableStateOf(10f) }
    var topKReturn by remember { mutableStateOf(3f) }
    var topNCandidates by remember { mutableStateOf(5f) }

    val uiState by viewModel.uiState.collectAsState()

    Column {
        PageHeroHeader(
            icon = "🧬",
            title = "Hybrid Advisory",
            subtitle = "Combine soil, climate, and farm inputs for a blended recommendation"
        )

        CropTabSwitcher(selected = selectedTab, onSelect = onSelectTab)

        Column(modifier = Modifier.padding(14.dp)) {

            FarmCard {
                SectionLabel("① Your State")
                Spacer(Modifier.height(11.dp))
                StateDropdown(selectedState) { selectedState = it }
            }

            Spacer(Modifier.height(12.dp))

            FarmCard {
                SectionLabel("② Soil Nutrients (N-P-K)")
                Spacer(Modifier.height(11.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f)) { IngredientBox("Nitrogen", n, "N", range = 0f..140f) { n = it } }
                    Column(Modifier.weight(1f)) { IngredientBox("Phosphorus", p, "P", range = 0f..145f) { p = it } }
                    Column(Modifier.weight(1f)) { IngredientBox("Potassium", k, "K", range = 0f..205f) { k = it } }
                }
            }

            Spacer(Modifier.height(12.dp))

            FarmCard {
                SectionLabel("③ Climate")
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

            FarmCard {
                SectionLabel("④ Farm Inputs")
                Spacer(Modifier.height(11.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        IngredientBox("Farm Area", area, "hectare", range = 0.5f..50f, steps = 98) { area = it }
                        IngredientBox("Fertilizer", fertilizer, "kg/ha", range = 0f..300f) { fertilizer = it }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        IngredientBox("Pesticide", pesticide, "kg/ha", range = 0f..50f, steps = 100) { pesticide = it }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            FarmCard {
                SectionLabel("⑤ Result Tuning")
                Spacer(Modifier.height(11.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f)) {
                        IngredientBox("Top Crops to Return", topKReturn, "crops", range = 1f..10f, steps = 8) { topKReturn = it }
                    }
                    Column(Modifier.weight(1f)) {
                        IngredientBox("Candidates Considered", topNCandidates, "crops", range = 1f..20f, steps = 18) { topNCandidates = it }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.getHybridAdvice(
                        HybridAdvisoryBody(
                            K = k.toInt(),
                            N = n.toInt(),
                            P = p.toInt(),
                            area = area.toInt(),
                            fertilizer = fertilizer.toInt(),
                            humidity = humidity.toInt(),
                            pesticide = pesticide.toInt(),
                            ph = ph.toLong(),
                            rainfall = rainfall.toInt(),
                            state = selectedState,
                            temperature = temperature.toInt(),
                            top_k_return = topKReturn.toInt(),
                            top_n_candidates = topNCandidates.toInt()
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
                            Text("Blending advice…", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
                        }
                    } else {
                        Text("🧬  Get Hybrid Advice", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🎯 Best Crop", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MedGreen)
                        Spacer(Modifier.height(6.dp))
                        Text(result.best_crop.toString(), fontSize = 15.sp, color = Color(0xFF2A2010))
                        Spacer(Modifier.height(12.dp))
                        Text("📋 Note", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MedGreen)
                        Spacer(Modifier.height(4.dp))
                        Text(result.note, fontSize = 13.sp, color = TextMuted)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}