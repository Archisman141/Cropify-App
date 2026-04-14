package com.tech.cropify.navigation

import kotlinx.serialization.Serializable

sealed class Routes {

    @Serializable
    data object Landing : Routes()

    @Serializable
    data object LoginScreen : Routes()

    @Serializable
    data object Signup : Routes()

    @Serializable
    data object MainScreen : Routes() // Wrapper with BottomNav

    @Serializable
    data object Dashboard : Routes()

    @Serializable
    data object Crop : Routes()

    @Serializable
    data object Disease : Routes()

    @Serializable
    data object Soil : Routes()

    @Serializable
    data object Weather : Routes() // ✅ NEW TAB

    @Serializable
    data object Profile : Routes()
}
