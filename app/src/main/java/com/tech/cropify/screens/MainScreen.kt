package com.tech.cropify.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tech.cropify.navigation.Routes

private val PrimaryBlue = Color(0xFF274185)
private val AccentOrange = Color(0xFFF47920)
@Composable
fun MainScreen(navController: NavHostController) {

    val innerNavController = rememberNavController()

    Scaffold(
        bottomBar = {
//            BottomNavBar(innerNavController)
        }
    ) { padding ->

        NavHost(
            navController = innerNavController,
            startDestination = Routes.Dashboard,
            modifier = Modifier.padding(padding)
        ) {

            composable<Routes.Dashboard> {
                DashboardScreen(navController)
            }

            composable<Routes.Crop> {
                CropScreen(navController)
            }

            composable<Routes.Disease> {
                DiseaseScreen(navController)
            }

            composable<Routes.Soil> {
                SoilScreen(navController)
            }

            composable<Routes.Weather> {
                WeatherScreen(navController) // ✅ Added
            }

            composable<Routes.Profile> {
                Profile(navController)
            }
        }
    }
}


@Composable
fun SliderWithLabel(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Text("$label: ${value.toInt()}")
        Slider(value = value, onValueChange = onValueChange)
    }
}
