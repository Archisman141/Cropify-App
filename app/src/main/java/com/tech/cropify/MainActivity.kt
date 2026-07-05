package com.tech.cropify

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.tech.cropify.navigation.NavGraphs
import com.tech.cropify.navigation.Routes
import com.tech.cropify.screens.AuthScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.tech.cropify.ui.theme.CropifyTheme
import com.tech.cropify.util.SharedPreferenceManager
import com.tech.cropify.viewModel.StateHolder
import com.tech.cropify.viewModel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.O)
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeMode by themeViewModel.theme.collectAsState()

            CropifyTheme(themeMode = themeMode) {
                val accessToken = SharedPreferenceManager.getToken(this) ?: StateHolder.accessToken?.text

                Log.d("MainActivity", "Access Token:$accessToken")

                val navController = rememberNavController()
                val scrollState = rememberLazyListState()

                val startDestination = if (accessToken != null) {
                    Routes.MainScreen
                } else {
                    Routes.LoginScreen
                }

                NavGraphs(scrollState = scrollState, navController = navController, startDestination)
            }
        }
    }
}

