package com.tech.cropify.navigation

import kotlinx.serialization.Serializable

sealed class Routes {

    @Serializable
    data object LoginScreen: Routes()

    @Serializable
    data object MainScreen: Routes()

    @Serializable
    data object Profile: Routes()
}