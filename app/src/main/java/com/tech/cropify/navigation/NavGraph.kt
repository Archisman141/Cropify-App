package com.tech.cropify.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tech.cropify.screens.AuthScreen
import com.tech.cropify.screens.BottomNavItem
import com.tech.cropify.screens.CropScreen
import com.tech.cropify.screens.DashboardScreen
import com.tech.cropify.screens.DiseaseScreen
import com.tech.cropify.screens.EditProfileScreen
import com.tech.cropify.screens.MainScreen
import com.tech.cropify.screens.NotificationScreen
import com.tech.cropify.screens.Profile
import com.tech.cropify.screens.SoilScreen
import com.tech.cropify.screens.WeatherScreen
import com.tech.cropify.viewModel.LoginViewModel
import com.tech.cropify.viewModel.ProfileViewModel

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraphs(
    scrollState: LazyListState,
    navController: NavHostController,
    startDestination: Routes = Routes.Landing
) {

    val viewModel: LoginViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        mainGraph(navController, viewModel, profileViewModel)
    }
}



@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    viewModel: LoginViewModel,
    profileViewModel: ProfileViewModel
) {


    // 🔹 Auth
    composable<Routes.LoginScreen> {
        AuthScreen(navController)
    }

    composable<Routes.NotificationScreen> {
        NotificationScreen(navController)
    }

    // 🔹 Main Container (Bottom Nav)
    composable<Routes.MainScreen> {
        MainScreen(navController, profileViewModel)
    }

    // 🔹 Dashboard
    composable<Routes.Dashboard> {
        DashboardScreen(navController, navController, profileViewModel)
    }

    // 🔹 Features
    composable<Routes.Crop> {
        CropScreen(navController, navController)
    }

    composable(BottomNavItem.Disease.route) {
        DiseaseScreen(navController, navController)
    }

    composable<Routes.Soil> {
        SoilScreen(navController, navController)
    }

    composable<Routes.Weather> {
        WeatherScreen(navController)
    }

    // 🔹 Profile
    composable<Routes.Profile> {
        Profile(navController, viewModel, profileViewModel)
    }

    composable<Routes.EditProfile>{
        EditProfileScreen(navController, profileViewModel)
    }
}
