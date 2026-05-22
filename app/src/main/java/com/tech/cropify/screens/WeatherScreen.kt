package com.tech.cropify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ─── Colour palette ──────────────────────────────────────────────────────────
private val BgDeep    = Color(0xFF0B1E35)
private val CardBg    = Color(0xFF132844)
private val CardBgAlt = Color(0xFF1A3252)
private val White     = Color.White
private val Muted     = Color(0xFFAABBCC)

// ─── Screen ──────────────────────────────────────────────────────────────────
@Composable
fun WeatherScreen(navController: NavHostController) {

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

        Spacer(Modifier.height(20.dp))

        // ── Location ─────────────────────────────────────────────────────────
        Text(
            text = "📍 Pune, Maharashtra",
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(16.dp))

        // ── Weather icon ─────────────────────────────────────────────────────
        Text("⛅", fontSize = 80.sp)

        Spacer(Modifier.height(8.dp))

        // ── Temperature ──────────────────────────────────────────────────────
        Text(
            text = "28°",
            color = White,
            fontSize = 80.sp,
            fontWeight = FontWeight.Thin,
            lineHeight = 84.sp
        )

        Text("Partly Cloudy", color = White, fontSize = 18.sp)

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Feels like 31° · High 33° · Low 21°",
            color = Muted,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(24.dp))

        // ── Stats card ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBg)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(icon = "💧", value = "72%",     label = "Humidity")
            StatDivider()
            StatItem(icon = "💨", value = "12 km/h", label = "Wind")
            StatDivider()
            StatItem(icon = "👁",  value = "8 km",   label = "Visibility")
            StatDivider()
            StatItem(icon = "🌡️", value = "1012",    label = "Pressure")
        }

        Spacer(Modifier.height(16.dp))

        // ── Hourly forecast ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBg)
                .padding(16.dp)
        ) {
            Text(
                "HOURLY FORECAST",
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
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                HourlyItem(time = "Now",   icon = "⛅", temp = "28°")
                HourlyItem(time = "11 AM", icon = "☀️", temp = "30°")
                HourlyItem(time = "12 PM", icon = "☀️", temp = "32°")
                HourlyItem(time = "2 PM",  icon = "🌤", temp = "33°")
                HourlyItem(time = "4 PM",  icon = "🌥", temp = "31°")
                HourlyItem(time = "6 PM",  icon = "🌙", temp = "27°")
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── 7-day forecast ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBg)
                .padding(16.dp)
        ) {
            Text(
                "7-DAY FORECAST",
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
                DayItem(day = "Today", icon = "⛅", high = "33°", low = "21°", selected = true)
                DayItem(day = "Sat",   icon = "☀️", high = "35°", low = "22°")
                DayItem(day = "Sun",   icon = "🌦", high = "30°", low = "20°")
                DayItem(day = "Mon",   icon = "🌧", high = "28°", low = "19°")
                DayItem(day = "Tue",   icon = "⛈", high = "26°", low = "18°")
                DayItem(day = "Wed",   icon = "🌤", high = "31°", low = "21°")
                DayItem(day = "Thu",   icon = "☀️", high = "34°", low = "22°")
            }
        }

        Spacer(Modifier.height(24.dp))
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
private fun HourlyItem(time: String, icon: String, temp: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(time, color = Muted, fontSize = 12.sp)
        Text(icon, fontSize = 26.sp)
        Text(temp, color = White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DayItem(
    day: String,
    icon: String,
    high: String,
    low: String,
    selected: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) CardBgAlt else Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Text(day,  color = if (selected) White else Muted, fontSize = 12.sp)
        Text(icon, fontSize = 24.sp)
        Text(high, color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Text(low,  color = Muted, fontSize = 12.sp)
    }
}