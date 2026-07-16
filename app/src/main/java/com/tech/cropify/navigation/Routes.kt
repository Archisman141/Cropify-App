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
    data object MainScreen : Routes()

    @Serializable
    data object Dashboard : Routes()

    @Serializable
    data object Crop : Routes()

    @Serializable
    data object Disease : Routes()

    @Serializable
    data object Soil : Routes()

    @Serializable
    data object Weather : Routes()
    @Serializable
    data object Profile : Routes()

    @Serializable
    data object NotificationScreen: Routes()

    @Serializable
    data object EditProfile : Routes()
}
