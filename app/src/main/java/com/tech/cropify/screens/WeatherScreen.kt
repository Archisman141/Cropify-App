package com.tech.cropify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun WeatherScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1F3A))
            .padding(16.dp)
    ) {

        // 🔹 Title
        Text("🌤 Weather", color = Color.White, fontSize = 22.sp)

        Spacer(modifier = Modifier.height(10.dp))

        // 🔹 Temperature
        Text("28°C", color = Color.White, fontSize = 48.sp)

        Text("Partly Cloudy", color = Color.LightGray)

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Weather Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherItem("💧", "Humidity", "72%")
            WeatherItem("🌬️", "Wind", "12 km/h")
            WeatherItem("🌡️", "Pressure", "1012")
        }
    }
}


@Composable
fun WeatherItem(
    icon: String,
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(icon, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            value,
            color = Color.White,
            fontSize = 16.sp
        )

        Text(
            label,
            color = Color.LightGray,
            fontSize = 12.sp
        )
    }
}

