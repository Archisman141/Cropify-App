// util/LocationHelper.kt
package com.tech.cropify.util

import android.content.Context
import android.location.Geocoder
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.tech.cropify.model.profile.SoilType
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume

data class ResolvedLocation(
    val label: String,
    val latitude: Double,
    val longitude: Double
)

object LocationHelper {

    @RequiresPermission(anyOf = ["android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"])
    suspend fun getCurrentLocation(context: Context): ResolvedLocation? {
        val client = LocationServices.getFusedLocationProviderClient(context)

        val location = suspendCancellableCoroutine<android.location.Location?> { cont ->
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
        } ?: return null

        val label = reverseGeocode(context, location.latitude, location.longitude)
        return ResolvedLocation(label, location.latitude, location.longitude)
    }

    private fun reverseGeocode(context: Context, lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(context)
            @Suppress("DEPRECATION")
            val results = geocoder.getFromLocation(lat, lon, 1)
            val addr = results?.firstOrNull()
            listOfNotNull(addr?.subAdminArea ?: addr?.locality, addr?.adminArea)
                .joinToString(", ")
                .ifBlank { "Lat ${"%.3f".format(lat)}, Lon ${"%.3f".format(lon)}" }
        } catch (e: Exception) {
            "Lat ${"%.3f".format(lat)}, Lon ${"%.3f".format(lon)}"
        }
    }

    /**
     * Looks up dominant soil classification for a coordinate using ISRIC SoilGrids
     * (https://rest.isric.org) and maps the WRB reference class to a simplified
     * label useful for Indian farming contexts. Falls back to UNKNOWN on any
     * network/parsing failure so the UI never crashes on this.
     *
     * Swap this out for a India-specific soil dataset (e.g. NBSS&LUP data) if
     * you have access to one — this is a reasonable global default.
     */
    suspend fun identifySoilType(lat: Double, lon: Double): SoilType {
        return try {
            val urlStr = "https://rest.isric.org/soilgrids/v2.0/classification/query" +
                    "?lat=$lat&lon=$lon&number_classes=1"
            val json = fetchJson(urlStr) ?: return SoilType.UNKNOWN
            val wrbClass = json
                .optJSONObject("wrb_class_name")
                ?.toString()
                ?: json.optString("wrb_class_name", "")

            mapWrbToSoilType(wrbClass.ifBlank { json.optString("wrb_class_name") })
        } catch (e: Exception) {
            SoilType.UNKNOWN
        }
    }

    private fun fetchJson(urlStr: String): JSONObject? {
        val connection = URL(urlStr).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            connection.requestMethod = "GET"
            if (connection.responseCode != 200) return null
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            JSONObject(body)
        } finally {
            connection.disconnect()
        }
    }

    private fun mapWrbToSoilType(wrbClass: String): SoilType {
        val c = wrbClass.lowercase()
        return when {
            "vertisol" in c -> SoilType.BLACK_COTTON   // Vertisols ≈ Indian black cotton soil
            "fluvisol" in c || "cambisol" in c -> SoilType.ALLUVIAL
            "ferralsol" in c || "acrisol" in c -> SoilType.RED_SOIL
            "nitisol" in c || "plinthosol" in c -> SoilType.LATERITE
            "arenosol" in c -> SoilType.SANDY
            "luvisol" in c || "vertic" in c -> SoilType.CLAY
            else -> SoilType.UNKNOWN
        }
    }
}