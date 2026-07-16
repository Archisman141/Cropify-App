package com.tech.cropify.model.profile

data class UserProfile(
    val userName: String = "Archisman Khanra",
    val emailId: String = "",
    val password: String = "",
    val phone: String = "",
    val state: String = "West Bengal",
    val city: String = "Hooghly",
    val village: String = "",
    val cropSeason: String = "Kharif 2024–25",
    val locationLabel: String = "Pune, Maharashtra",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val farmSizeAcres: String = "3.2",
    val soilType: SoilType = SoilType.BLACK_COTTON
)

enum class SoilType(val displayName: String, val emoji: String) {
    BLACK_COTTON("Black Cotton Soil", "🟫"),
    ALLUVIAL("Alluvial Soil", "🟨"),
    RED_SOIL("Red Soil", "🟥"),
    LATERITE("Laterite Soil", "🟧"),
    SANDY("Sandy Soil", "🟦"),
    CLAY("Clay Soil", "⬛"),
    UNKNOWN("Unknown", "❔")
}
