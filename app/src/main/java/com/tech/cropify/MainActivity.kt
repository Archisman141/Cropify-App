package com.tech.cropify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tech.cropify.screens.AuthScreen
import com.tech.cropify.ui.theme.CropifyTheme
import com.tech.cropify.viewModel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CropifyTheme {
                AuthScreen()
            }
        }
    }
}

