package com.tech.cropify.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.tech.cropify.model.weather.DailyForecast
import com.tech.cropify.model.weather.WeatherResponse
import com.tech.cropify.viewModel.WeatherViewModel
import kotlin.math.roundToInt

// ─── Colour palette ──────────────────────────────────────────────────────────
private val BgDeep    = Color(0xFF0B1E35)
private val CardBg    = Color(0xFF132844)
private val CardBgAlt = Color(0xFF1A3252)
private val White     = Color.White
private val Muted     = Color(0xFFAABBCC)

// ─── Screen ──────────────────────────────────────────────────────────────────
@Composable
fun WeatherScreen(navController: NavHostController) {

    val viewModel: WeatherViewModel = hiltViewModel()
    val weather by viewModel.weather.collectAsState()
    val forecast by viewModel.forecast.collectAsState()

    val context = LocalContext.current

    // Track whether the location permission is currently granted.
    var hasLocationPermission by remember {
        mutableStateOf(context.hasLocationPermission())
    }
    // A human-readable place name resolved from the device location.
    var placeName by remember { mutableStateOf<String?>(null) }

    // Which day of the forecast is currently selected (0 = today / live weather).
    var selectedDay by remember { mutableIntStateOf(0) }
    // Keep the selection valid whenever a fresh forecast arrives.
    LaunchedEffect(forecast.size) {
        if (selectedDay >= forecast.size) selectedDay = 0
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission = result.values.any { it }
    }

    // Handles the system "Turn on Location?" dialog triggered when location
    // services are off at the OS level (the actual cause of both
    // getCurrentLocation() and lastLocation returning null).
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            fetchLocationAndWeather(context, viewModel) { name -> placeName = name }
        } else {
            Log.w("Weather", "User declined to enable location services")
        }
    }

    // Ask for the permission the first time the screen is shown if we don't have it.
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Once we have permission, make sure location services are actually ON
    // (not just the app permission) before fetching — and if they're off,
    // trigger the standard system dialog to turn them on rather than failing
    // silently the way a raw getCurrentLocation() null-check does.
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            ensureLocationSettingsThen(
                context = context,
                onSatisfied = { fetchLocationAndWeather(context, viewModel) { name -> placeName = name } },
                onResolvable = { intentSenderRequest -> locationSettingsLauncher.launch(intentSenderRequest) },
                onUnresolvable = { e -> Log.e("Weather", "Location settings unresolvable", e) }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            // ✅ Respects status bar + navigation bar insets
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Top bar ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌤 Weather Forecast",
                color = White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        when {
            // No permission → prompt the user to enable location, show no weather.
            !hasLocationPermission -> LocationPermissionPrompt(
                onGrant = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                onOpenSettings = { context.openAppSettings() }
            )

            // Permission granted, data not yet loaded → loading state.
            weather == null -> LoadingState()

            // We have data → render it.
            else -> WeatherContent(
                weather = weather!!,
                forecast = forecast,
                placeName = placeName,
                selectedDay = selectedDay,
                onSelectDay = { selectedDay = it }
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ─── Content (real API data) ──────────────────────────────────────────────────
@Composable
private fun WeatherContent(
    weather: WeatherResponse,
    forecast: List<DailyForecast>,
    placeName: String?,
    selectedDay: Int,
    onSelectDay: (Int) -> Unit
) {
    // Day 0 (or no forecast) → live current weather; otherwise the picked forecast day.
    val day = forecast.getOrNull(selectedDay)
    val isToday = selectedDay == 0 || day == null

    val info = weather.weather.firstOrNull()
    val icon = if (isToday) info?.icon else day!!.icon
    val tempC = if (isToday) weather.main.temp.kelvinToCelsius() else day!!.tempC
    val description = if (isToday) info?.description else day!!.description
    val humidity = if (isToday) weather.main.humidity else day!!.humidity
    val windKmh = if (isToday) (weather.wind.speed * 3.6).roundToInt() else day!!.windKmh
    val clouds = if (isToday) weather.clouds.all else day!!.clouds
    val pressure = if (isToday) weather.main.pressure else day!!.pressure
    val subtitle = if (isToday) {
        "Feels like ${weather.main.feels_like.kelvinToCelsius()}°"
    } else {
        "${day!!.label} · High ${day.highC}° · Low ${day.lowC}°"
    }

    // ── Location ─────────────────────────────────────────────────────────────
    Text(
        text = "📍 ${placeName ?: "Current Location"}",
        color = White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    )

    Spacer(Modifier.height(16.dp))

    // ── Animated weather icon ─────────────────────────────────────────────────
    AnimatedWeatherIcon(icon = icon, size = 120.dp)

    Spacer(Modifier.height(8.dp))

    // ── Temperature ──────────────────────────────────────────────────────────
    Text(
        text = "$tempC°",
        color = White,
        fontSize = 80.sp,
        fontWeight = FontWeight.Thin,
        lineHeight = 84.sp
    )

    Text(
        text = description?.replaceFirstChar { it.uppercase() } ?: "—",
        color = White,
        fontSize = 18.sp
    )

    Spacer(Modifier.height(4.dp))

    Text(
        text = subtitle,
        color = Muted,
        fontSize = 13.sp
    )

    Spacer(Modifier.height(24.dp))

    // ── Stats card ───────────────────────────────────────────────────────────
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(icon = "💧", value = "$humidity%", label = "Humidity")
        StatDivider()
        StatItem(icon = "💨", value = "$windKmh km/h", label = "Wind")
        StatDivider()
        StatItem(icon = "☁️", value = "$clouds%", label = "Clouds")
        StatDivider()
        StatItem(icon = "🌡️", value = "$pressure", label = "Pressure")
    }

    // ── 7-day forecast (tap a day to view it) ──────────────────────────────────
    if (forecast.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBg)
                .padding(16.dp)
        ) {
            Text(
                "${forecast.size}-DAY FORECAST",
                color = Muted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                forecast.forEachIndexed { index, d ->
                    DayItem(
                        day = d.label,
                        icon = d.icon,
                        high = "${d.highC}°",
                        low = "${d.lowC}°",
                        selected = index == selectedDay,
                        onClick = { onSelectDay(index) }
                    )
                }
            }
        }
    }
}

// ─── Loading state ────────────────────────────────────────────────────────────
@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = White)
        Spacer(Modifier.height(16.dp))
        Text("Fetching weather…", color = Muted, fontSize = 14.sp)
    }
}

// ─── Permission prompt ────────────────────────────────────────────────────────
@Composable
private fun LocationPermissionPrompt(
    onGrant: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📍", fontSize = 56.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Location access needed",
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "We use your location to show the weather for your area. " +
                    "Please turn on location permission to continue.",
            color = Muted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onGrant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enable location")
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onOpenSettings) {
            Text("Open app settings", color = Muted)
        }
    }
}

// ─── Reusable pieces ─────────────────────────────────────────────────────────
@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp)
            .background(Color(0xFF2A4A6A))
    )
}

@Composable
private fun StatItem(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Text(label, color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun DayItem(
    day: String,
    icon: String,
    high: String,
    low: String,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(if (selected) CardBgAlt else Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Text(day, color = if (selected) White else Muted, fontSize = 12.sp)
        AnimatedWeatherIcon(icon = icon, size = 34.dp)
        Text(high, color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Text(low, color = Muted, fontSize = 12.sp)
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────
fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    startActivity(intent)
}

/** OpenWeather returns temperature in Kelvin by default; convert to whole-degree Celsius. */
fun Double.kelvinToCelsius(): Int = (this - 273.15).roundToInt()

fun resolvePlaceName(context: Context, lat: Double, lon: Double): String? = try {
    @Suppress("DEPRECATION")
    val address = Geocoder(context).getFromLocation(lat, lon, 1)?.firstOrNull()
    address?.let {
        listOfNotNull(it.locality ?: it.subAdminArea, it.adminArea)
            .joinToString(", ")
            .ifBlank { null }
    }
} catch (_: Exception) {
    null
}

fun ensureLocationSettingsThen(
    context: Context,
    onSatisfied: () -> Unit,
    onResolvable: (IntentSenderRequest) -> Unit,
    onUnresolvable: (Exception) -> Unit
) {
    val locationRequest = LocationRequest.Builder(
        com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        10_000L
    ).build()

    val settingsRequest = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .build()

    LocationServices.getSettingsClient(context)
        .checkLocationSettings(settingsRequest)
        .addOnSuccessListener { onSatisfied() }
        .addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    onResolvable(IntentSenderRequest.Builder(e.resolution).build())
                } catch (sendEx: Exception) {
                    onUnresolvable(sendEx)
                }
            } else {
                onUnresolvable(e)
            }
        }
}


@Suppress("MissingPermission")
fun fetchLocationAndWeather(
    context: Context,
    viewModel: WeatherViewModel,
    onPlaceResolved: (String?) -> Unit
) {
    if (!context.hasLocationPermission()) {
        Log.w("Weather", "fetchLocationAndWeather called without location permission")
        return
    }

    val client = LocationServices.getFusedLocationProviderClient(context)
    val cts = com.google.android.gms.tasks.CancellationTokenSource()

    fun loadWeather(lat: Double, lon: Double) {
        val positiveLon = kotlin.math.abs(lon)
        Log.d("Weather", "Loading weather for lat=$lat lon=$positiveLon")
        viewModel.getWeather(lat, positiveLon)
        onPlaceResolved(resolvePlaceName(context, lat, positiveLon))
    }

    client.getCurrentLocation(
        com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        cts.token
    ).addOnSuccessListener { location ->
        if (location != null) {
            loadWeather(location.latitude, location.longitude)
        } else {
            Log.w("Weather", "getCurrentLocation() returned null, falling back to lastLocation")
            // Fallback: a cached fix is far better than no request at all.
            client.lastLocation
                .addOnSuccessListener { last ->
                    if (last != null) {
                        loadWeather(last.latitude, last.longitude)
                    } else {
                        Log.e(
                            "Weather",
                            "No location available at all (getCurrentLocation and lastLocation " +
                                    "both null). Ensure device/emulator location is turned on and, " +
                                    "on an emulator, set a mock location via Extended Controls."
                        )
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Weather", "lastLocation fallback failed", e)
                }
        }
    }.addOnFailureListener { e ->
        Log.e("Weather", "getCurrentLocation() failed", e)
    }
}