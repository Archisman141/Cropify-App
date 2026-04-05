package com.tech.cropify.navigation

import android.annotation.SuppressLint
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
import com.tech.cropify.screens.MainScreen
import com.tech.cropify.screens.ProfileScreen

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(scrollState: LazyListState, navController: NavHostController){


    val startDestination = Routes.LoginScreen

    NavHost(
        navController,
        startDestination = startDestination
    ){
        mainGraph(navController)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
fun NavGraphBuilder.mainGraph(navController: NavHostController){

    composable<Routes.MainScreen> {
        MainScreen(navController)
    }

    composable<Routes.Profile> {
        ProfileScreen(navController)
    }


}