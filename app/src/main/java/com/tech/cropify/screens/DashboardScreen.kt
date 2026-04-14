package com.tech.cropify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tech.cropify.navigation.Routes

// ─── Brand colours ────────────────────────────────────────────────────────────
private val DarkGreen    = Color(0xFF1E4010)
private val MedGreen     = Color(0xFF3A7A20)
private val AccentGreen  = Color(0xFF4A8A30)
private val YellowAccent = Color(0xFFF5C842)
private val BgCream      = Color(0xFFF0EBE0)
private val CardBorder   = Color(0xFFE0D8C8)
private val TextDark     = Color(0xFF2A2010)
private val TextMuted    = Color(0xFF8A7A5A)
private val AlertYellow  = Color(0xFFFFF3CD)
private val AlertBorder  = Color(0xFFF5C842)

@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
//        topBar = { DashboardTopBar(navController) },
        bottomBar = { FarmBottomNav(navController, active = "home") },
        containerColor = BgCream,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        Column(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())){
            DashboardTopBar(navController)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 8.nonScaledDp)
//                    .padding(innerPadding.calculateTopPadding())
//                .verticalScroll(rememberScrollState())
            )
            {
                item {
                    DashboardHeader(navController)
                }

                item {
                    // ── Alert strip ──────────────────────────────────────────────────
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

                        // ── Quick Actions ─────────────────────────────────────────────
                        Text(
                            "QUICK ACTIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(bottom = 9.dp)
                        )
                        QuickActionsGrid(navController)

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
}

// ── Top App Bar ───────────────────────────────────────────────────────────────
@Composable
private fun DashboardTopBar(navController: NavController) {
    // ❌ Remove Surface wrapper — Scaffold handles this

    Box(
        modifier = Modifier
            .fillMaxWidth()
//            .background(DarkGreen)
            // ✅ Let the top bar itself consume the status bar inset
//            .windowInsetsPadding(WindowInsets.statusBars)
    ){
        Column(modifier = Modifier.fillMaxWidth()){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 18.nonScaledDp, vertical = 10.nonScaledDp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text("🌾", fontSize = 17.sp)
                    Text(
                        "FarmSmart",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = Color(0xFF2D4A1E)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("🌤️", fontSize = 19.sp, modifier = Modifier.clickable { navController.navigate(Routes.Weather) })
                    Text("🔔", fontSize = 19.sp)
                    Box(
                        modifier = Modifier
                            .size(33.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AccentGreen, Color(0xFF8DB870))))
                            .clickable { navController.navigate(Routes.Profile) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("RK", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }
            }
        }
    }

}

// ── Green Dashboard Header with name + weather ────────────────────────────────
@Composable
private fun DashboardHeader(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(DarkGreen, MedGreen)))
            .padding(18.dp)
    ) {
        Column {
            Text("Good Morning ☀️", fontSize = 13.sp, color = Color(0xB3FFFFFF))
            Text(
                "Rajesh Kumar",
                fontFamily = FontFamily.Serif,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(Modifier.height(14.dp))
            // Weather strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .background(Color(0x26FFFFFF))
                    .clickable { navController.navigate(Routes.Weather) }
                    .padding(11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text("⛅", fontSize = 26.sp)
                    Column {
                        Text("28°C", fontSize = 19.sp, fontWeight = FontWeight.Medium, color = Color.White)
                        Text("Pune, Maharashtra · Tap for forecast", fontSize = 12.sp, color = Color(0xB3FFFFFF))
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("💧 72%", fontSize = 11.sp, color = Color(0xCCFFFFFF))
                    Text("🌬️ 12 km/h", fontSize = 11.sp, color = Color(0xCCFFFFFF))
                }
            }
        }
    }
}

// ── 2×2 Quick Actions Grid ────────────────────────────────────────────────────
@Composable
private fun QuickActionsGrid(navController: NavController) {
    val actions = listOf(
        Triple("🌾", "Crop Predict", "Best crop for season") to Routes.Crop,
        Triple("🔬", "Detect Disease", "Scan plant leaves") to Routes.Disease,
        Triple("🏔️", "Soil Analysis", "Soil health report") to Routes.Soil,
        Triple("🌦️", "Weather", "7-day forecast") to Routes.Weather
    )
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        for (row in actions.chunked(2)) {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                for ((info, route) in row) {
                    ActionCard(info.first, info.second, info.third, Modifier.weight(1f)) {
                        navController.navigate(route)
                    }
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

// ── Bottom Nav ────────────────────────────────────────────────────────────────
@Composable
fun FarmBottomNav(navController: NavController, active: String) {
    val items = listOf(
        Triple("🏠", "Home", "home") to Routes.Dashboard,
        Triple("🌾", "Predict", "crop") to Routes.Crop,
        Triple("🔬", "Detect", "disease") to Routes.Disease,
        Triple("🏔️", "Soil", "soil") to Routes.Soil,
        Triple("👤", "Profile", "profile") to Routes.Profile
    )
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            for ((info, route) in items) {
                val isActive = info.third == active
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { if (!isActive) navController.navigate(route) }
                        .padding(vertical = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(info.first, fontSize = 19.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        info.second,
                        fontSize = 10.sp,
                        color = if (isActive) AccentGreen else TextMuted,
                        fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
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