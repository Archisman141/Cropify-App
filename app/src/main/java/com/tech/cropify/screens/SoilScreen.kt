package com.tech.cropify.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun SoilScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("🏔️ Soil Analysis", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Field Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FieldCard("Field A", true, Modifier.weight(1f))
            FieldCard("Field B", false, Modifier.weight(1f))
            FieldCard("Field C", false, Modifier.weight(1f))
        }


        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 pH Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Soil pH Level")

                Spacer(modifier = Modifier.height(8.dp))

                Text("6.8", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = 0.68f,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Ideal for most crops ✅")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Nutrient Grid
        Text("Nutrient Levels")

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NutrientCard("Nitrogen", "142", "Optimal")
                NutrientCard("Phosphorus", "24", "Low")
                NutrientCard("Potassium", "198", "Optimal")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NutrientCard("Sulphur", "8", "Low")
                NutrientCard("Organic C", "0.72", "Low")
                NutrientCard("Moisture", "34", "Good")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Tips
        Card {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("🌿 Soil Improvement Tips")

                Spacer(modifier = Modifier.height(8.dp))

                Text("• Apply DAP for phosphorus")
                Text("• Add compost for carbon")
                Text("• Use drip irrigation")
            }
        }
    }
}

@Composable
fun FieldCard(
    name: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFE8F5E1) else Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(name)
        }
    }
}


@Composable
fun NutrientCard(
    title: String,
    value: String,
    status: String,
    modifier: Modifier = Modifier
) {

    val statusColor = when (status) {
        "Low" -> Color(0xFFD32F2F)
        "Optimal" -> Color(0xFF2E7D32)
        "High" -> Color(0xFFF57C00)
        else -> Color.Gray
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔹 Title
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 🔹 Value
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 🔹 Status Badge
            Text(
                text = status,
                color = statusColor,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
