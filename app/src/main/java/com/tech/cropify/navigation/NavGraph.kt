package com.tech.cropify.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tech.cropify.screens.AuthScreen
import com.tech.cropify.screens.CropScreen
import com.tech.cropify.screens.DashboardScreen
import com.tech.cropify.screens.DiseaseScreen
import com.tech.cropify.screens.MainScreen
import com.tech.cropify.screens.Profile
import com.tech.cropify.screens.SoilScreen
import com.tech.cropify.screens.WeatherScreen

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraphs(
    scrollState: LazyListState,
    navController: NavHostController,
    startDestination: Routes = Routes.Landing
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        mainGraph(navController)
    }
}



@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(navController: NavHostController) {

    // 🔹 Landing
//    composable<Routes.Landing> {
//        LandingScreen(navController)
//    }

    // 🔹 Auth
    composable<Routes.LoginScreen> {
        AuthScreen(navController)
    }

//    composable<Routes.Signup> {
//        SignupScreen(navController)
//    }

    // 🔹 Main Container (Bottom Nav)
    composable<Routes.MainScreen> {
        MainScreen(navController)
    }

    // 🔹 Dashboard
    composable<Routes.Dashboard> {
        DashboardScreen(navController)
    }

    // 🔹 Features
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
        WeatherScreen(navController) // ✅ NEW
    }

    // 🔹 Profile
    composable<Routes.Profile> {
        Profile(navController)
    }
}
