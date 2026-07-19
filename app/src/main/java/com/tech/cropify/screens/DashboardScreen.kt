package com.tech.cropify.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.tech.cropify.model.weather.WeatherResponse
import com.tech.cropify.navigation.Routes
import com.tech.cropify.viewModel.ProfileViewModel
import com.tech.cropify.viewModel.WeatherViewModel
import kotlin.math.roundToInt

// ─── Brand colours ────────────────────────────────────────────────────────────
private val DarkGreen    = Color(0xFF1E4010)
private val YellowAccent = Color(0xFFF5C842)
private val BgCream      = Color(0xFFF0EBE0)
private val TextDark     = Color(0xFF2A2010)
private val AlertYellow  = Color(0xFFFFF3CD)
private val AlertBorder  = Color(0xFFF5C842)

@Composable
fun DashboardScreen(
    navController: NavController,
    bottomNavController: NavHostController,
    profileViewModel: ProfileViewModel,
    weatherViewModel: WeatherViewModel = hiltViewModel()
) {
    val profile by profileViewModel.profile.collectAsState()
    val profileImageModel by profileViewModel.profileImageUrl.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserDetails(context)
    }

//    val profileImageModel = remember(profile.profileUri) {
//        getProfileImageToDisplay(context) ?: profile.profileUri
//    }

    val weather by weatherViewModel.weather.collectAsState()
    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }
    var placeName by remember { mutableStateOf<String?>(null) }

    val weatherPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission = result.values.any { it }
    }

    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            fetchLocationAndWeather(context, weatherViewModel) { name -> placeName = name }
        } else {
            android.util.Log.w("Weather", "User declined to enable location services (Dashboard)")
        }
    }

    fun requestWeather() {
        ensureLocationSettingsThen(
            context = context,
            onSatisfied = { fetchLocationAndWeather(context, weatherViewModel) { name -> placeName = name } },
            onResolvable = { intentSenderRequest -> locationSettingsLauncher.launch(intentSenderRequest) },
            onUnresolvable = { e -> android.util.Log.e("Weather", "Location settings unresolvable (Dashboard)", e) }
        )
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            requestWeather()
        } else {
            weatherPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && weather == null) {
            requestWeather()
        }
    }

    Scaffold(
        topBar = {
            DashboardTopBar(
                navController = navController,
                userName = profile.userName,
                profileImageModel = profileImageModel
            )
        },
        containerColor = BgCream
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 8.dp
            )
        )
        {
            item {
                DashboardHeader(
                    navController = navController,
                    bottomNavController = bottomNavController,
                    userName = profile.userName,
                    weather = weather,
                    placeName = placeName,
                    hasLocationPermission = hasLocationPermission
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .padding(top = 14.dp)
                        .background(AlertYellow, RoundedCornerShape(topEnd = 9.dp, bottomEnd = 9.dp))
                        .drawStartBorder(AlertBorder, 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️  Alert: Risk of leaf blight in next 3 days. Check crops.",
                        fontSize = 12.sp,
                        color = Color(0xFF7A5A10),
                        modifier = Modifier.padding(vertical = 9.dp, horizontal = 13.dp)
                    )
                }
            }
            item {
                Column(modifier = Modifier.padding(14.dp)) {

                    Text(
                        "QUICK ACTIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )
                    QuickActionsGrid(navController, bottomNavController)

                    Spacer(Modifier.height(13.dp))

                    // ── Field Overview ────────────────────────────────────────────
                    Text(
                        "FIELD OVERVIEW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        MiniStat("3.2", "Acres", Modifier.weight(1f))
                        MiniStat("87%", "Health", Modifier.weight(1f))
                        MiniStat("Apr", "Best Month", Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(13.dp))

                    // ── Recent Activity ───────────────────────────────────────────
                    Text(
                        "RECENT ACTIVITY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )
                    RecentItem("🌽", Color(0xFFE8F5E1), "Maize predicted — Field A", "2h ago · 91% confidence", "Done", isOk = true)
                    RecentItem("🍃", Color(0xFFFEF3E0), "Leaf blight detected", "Yesterday · Field B", "Alert", isOk = false)
                    RecentItem("🧪", Color(0xFFE8F5E1), "Soil test completed", "3 days ago · pH 6.8", "Good", isOk = true)
                }

                Spacer(Modifier.height(8.dp))

            }
        }
    }
}

// ── Top App Bar ───────────────────────────────────────────────────────────────
@Composable
private fun DashboardTopBar(
    navController: NavController,
    userName: String,
    profileImageModel: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Text("🌾", fontSize = 17.sp)
            Text(
                "FarmPredict",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = Color(0xFF2D4A1E)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("🔔", fontSize = 19.sp)
            Box(
                modifier = Modifier
                    .size(33.dp)                                      // was 33.dp — restore hero size
                    .clip(CircleShape)
                    .clickable{
                        navController.navigate(Routes.Profile)
                    }
                    .background(Brush.linearGradient(listOf(YellowAccent, Color(0xFFE8A030))))
                    .border(4.dp, Color(0x4DFFFFFF), CircleShape),     // restore the border too
                contentAlignment = Alignment.Center
            ) {
                if (profileImageModel != null) {
                    SubcomposeAsyncImage(
                        model = profileImageModel,
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        loading = {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
                        },
                        error = { Text("👨‍🌾", fontSize = 32.sp) }   // keep the farmer emoji here, not initials
                    )
                } else {
                    Text("👨‍🌾", fontSize = 32.sp)
                }
            }
        }
    }
}

// ── Green Dashboard Header with name + weather ────────────────────────────────
@Composable
private fun DashboardHeader(
    navController: NavController,
    bottomNavController: NavHostController,
    userName: String,
    weather: WeatherResponse?,
    placeName: String?,
    hasLocationPermission: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(DarkGreen, MedGreen)))
            .padding(18.dp)
    ) {
        Column {
            Text("Good Morning ☀️", fontSize = 13.sp, color = Color(0xB3FFFFFF))
            Text(
                userName.ifBlank { "Farmer" },
                fontFamily = FontFamily.Serif,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(Modifier.height(14.dp))
            // Weather strip — live data from WeatherViewModel, refreshed every visit.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .background(Color(0x26FFFFFF))
                    .clickable { navController.navigate(Routes.Weather) }
                    .horizontalScroll(rememberScrollState())
                    .padding(11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                when {
                    !hasLocationPermission -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            Text("📍", fontSize = 22.sp)
                            Text(
                                "Enable location for live weather",
                                fontSize = 13.sp,
                                color = Color(0xE6FFFFFF)
                            )
                        }
                    }
                    weather == null -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Text("Fetching weather…", fontSize = 13.sp, color = Color(0xE6FFFFFF))
                        }
                    }
                    else -> {
                        val info = weather.weather.firstOrNull()
                        val tempC = weather.main.temp.kelvinToCelsius()
                        val humidity = weather.main.humidity
                        val windKmh = (weather.wind.speed * 3.6).roundToInt()

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(9.dp)
                        ) {
                            AnimatedWeatherIcon(icon = info?.icon ?: "01d", size = 34.dp)
                            Column {
                                Text("$tempC°C", fontSize = 19.sp, fontWeight = FontWeight.Medium, color = Color.White)
                                Text(
                                    "${placeName ?: "Current location"}",
                                    fontSize = 12.sp,
                                    color = Color(0xB3FFFFFF)
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("💧 $humidity%", fontSize = 11.sp, color = Color(0xCCFFFFFF))
                            Text("🌬️ $windKmh km/h", fontSize = 11.sp, color = Color(0xCCFFFFFF))
                        }
                    }
                }
            }
        }
    }
}

// ── 2×2 Quick Actions Grid ────────────────────────────────────────────────────
@Composable
private fun QuickActionsGrid(navController: NavController, bottomNavController: NavHostController) {
    data class QuickAction(val icon: String, val title: String, val desc: String, val onClick: () -> Unit)

    val actions = listOf(
        QuickAction("🌾", "Crop Predict", "Best crop for season") {
            bottomNavController.navigate(BottomNavItem.Crop.route) {
                popUpTo(BottomNavItem.Home.route) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
        QuickAction("🔬", "Detect Disease", "Scan plant leaves") {
            bottomNavController.navigate(BottomNavItem.Disease.route) {
                popUpTo(BottomNavItem.Home.route) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    )

    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        for (row in actions.chunked(2)) {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                for (action in row) {
                    ActionCard(action.icon, action.title, action.desc, Modifier.weight(1f), action.onClick)
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    icon: String,
    name: String,
    desc: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(icon, fontSize = 26.sp)
            Spacer(Modifier.height(7.dp))
            Text(name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
            Text(desc, fontSize = 11.sp, color = TextMuted)
        }
    }
}

// ── Mini stat tile ────────────────────────────────────────────────────────────
@Composable
private fun MiniStat(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(11.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(11.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 19.sp, fontWeight = FontWeight.SemiBold, color = AccentGreen)
            Spacer(Modifier.height(2.dp))
            Text(label, fontSize = 10.sp, color = TextMuted)
        }
    }
}

// ── Recent activity row ───────────────────────────────────────────────────────
@Composable
private fun RecentItem(
    icon: String,
    iconBg: Color,
    title: String,
    subtitle: String,
    badge: String,
    isOk: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 7.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(11.dp))
            .padding(11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) { Text(icon, fontSize = 17.sp) }
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
            Text(subtitle, fontSize = 11.sp, color = TextMuted)
        }
        val badgeBg = if (isOk) Color(0xFFE8F5E1) else Color(0xFFFFF3CD)
        val badgeText = if (isOk) Color(0xFF2D5E1A) else Color(0xFF7A5A10)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(badgeBg)
                .padding(horizontal = 9.dp, vertical = 3.dp)
        ) {
            Text(badge, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = badgeText)
        }
    }
}

// DrawStartBorder helper — draws a colored left border (like the CSS alert-strip)
private fun Modifier.drawStartBorder(color: Color, width: androidx.compose.ui.unit.Dp): Modifier =
    this.then(
        Modifier.background(
            Brush.horizontalGradient(
                0f to color,
                (width.value / 300f) to color,
                (width.value / 300f) to Color.Transparent,
                1f to Color.Transparent
            )
        )
    )

val Int.nonScaledDp: Dp
    @Composable
    get() = (this.toDouble() / LocalDensity.current.fontScale).dp