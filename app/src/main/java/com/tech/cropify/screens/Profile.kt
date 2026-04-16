package com.tech.cropify.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tech.cropify.util.SharedPreferenceManager.getUserProfile
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

// ── Brand colours ─────────────────────────────────────────────────────────────
private val DarkGreen    = Color(0xFF1E4010)
private val MedGreen     = Color(0xFF3A7A20)
private val AccentGreen  = Color(0xFF4A8A30)
private val YellowAccent = Color(0xFFF5C842)
private val BgCream      = Color(0xFFF5F0E8)
private val CardBorder   = Color(0xFFE0D8C8)
private val TextDark     = Color(0xFF2A2010)
private val TextMuted    = Color(0xFF8A7A5A)

@Composable
fun Profile(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        containerColor = BgCream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ────────────────────────────────────────────────────────
            ProfileHeader()

            // ── Body ──────────────────────────────────────────────────────────
            Column(modifier = Modifier.padding(16.dp)) {

                // Farm Details
                SettingsSection(label = "Farm Details") {
                    SettingsRow(icon = "🌾", title = "Crop Season", sub = "Kharif 2024–25", showArrow = true)
                    SettingsRow(icon = "📍", title = "Location", sub = "Pune, Maharashtra", showArrow = true)
                    SettingsRow(icon = "📐", title = "Farm Size", sub = "3.2 acres", showArrow = true)
                    SettingsRow(icon = "🌱", title = "Soil Type", sub = "Black Cotton Soil", showArrow = true)
                }

                Spacer(Modifier.height(18.dp))

                // Notifications
                SettingsSection(label = "Notifications") {
                    ToggleRow(icon = "🌧️", title = "Rain Alerts", sub = "Rainfall predictions", default = true)
                    ToggleRow(icon = "🦠", title = "Disease Alerts", sub = "Crop disease warnings", default = true)
                    ToggleRow(icon = "📊", title = "Weekly Report", sub = "Every Sunday morning", default = true)
                }

                Spacer(Modifier.height(18.dp))

                // Language
                SettingsSection(label = "Language") {
                    LanguageOption(flag = "🇮🇳", name = "English", isActive = true)
                    LanguageOption(flag = "🇮🇳", name = "हिंदी (Hindi)")
                    LanguageOption(flag = "🇮🇳", name = "मराठी (Marathi)")
                    LanguageOption(flag = "🇮🇳", name = "ਪੰਜਾਬੀ (Punjabi)")
                }

                Spacer(Modifier.height(18.dp))

                // App Settings
                SettingsSection(label = "App Settings") {
                    ToggleRow(icon = "🌙", title = "Dark Mode", sub = "Easier reading at night", default = false)
                    ToggleRow(icon = "📶", title = "Offline Mode", sub = "Use without internet", default = true)
                    ToggleRow(icon = "📍", title = "Location Access", sub = "For weather & local data", default = true)
                    SettingsRow(icon = "📦", title = "My Reports", sub = "View all saved reports", showArrow = true)
                    SettingsRow(icon = "🔐", title = "Privacy & Security", sub = "Password, data settings", showArrow = true)
                    SettingsRow(icon = "🆘", title = "Help & Support", sub = "FAQs, contact us", showArrow = true)
                }

                Spacer(Modifier.height(6.dp))

                // App version
                Text(
                    "FarmSmart v2.4.1 · Built for Indian Farmers 🌾",
                    fontSize = 12.sp,
                    color = Color(0xFFB0A080),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                // Sign Out
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(13.dp))
                        .background(Color(0xFFFEE8E8))
                        .border(1.dp, Color(0xFFF0C0C0), RoundedCornerShape(13.dp))
                        .clickable { /* sign out */ }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚪  Sign Out", fontSize = 14.sp, color = Color(0xFF8A2020))
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

// ── Profile hero header ───────────────────────────────────────────────────────
@Composable
private fun ProfileHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(Color(0xFF1E4010), Color(0xFF3A7A20))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(YellowAccent, Color(0xFFE8A030))))
                    .border(4.dp, Color(0x4DFFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) { Text("👨‍🌾", fontSize = 32.sp) }

            Spacer(Modifier.height(12.dp))
            Text("Archisman Khanra", fontFamily = FontFamily.Serif, fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text("Farmer · Hooghly, WestBengal", fontSize = 13.sp, color = Color(0xB3FFFFFF))

            // Stats
            Row(
                modifier = Modifier.padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                ProfileStat("24", "Reports")
                ProfileStat("8", "Crops")
                ProfileStat("94%", "Accuracy")
            }

            Spacer(Modifier.height(14.dp))

            // Edit button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(YellowAccent)
                    .clickable { }
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text("Edit Profile", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A3A0F))
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = YellowAccent)
        Text(label, fontSize = 11.sp, color = Color(0xA6FFFFFF))
    }
}

// ── Settings section container ────────────────────────────────────────────────
@Composable
private fun SettingsSection(label: String, content: @Composable ColumnScope.() -> Unit) {
    Text(
        label.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = TextMuted,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
    Card(
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

// ── Plain setting row (with arrow) ────────────────────────────────────────────
@Composable
private fun SettingsRow(icon: String, title: String, sub: String, showArrow: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 15.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(32.dp), contentAlignment = Alignment.Center) {
            Text(icon, fontSize = 20.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
            Text(sub, fontSize = 11.sp, color = TextMuted)
        }
        if (showArrow) Text("›", fontSize = 16.sp, color = Color(0xFFC0B090))
    }
    Divider(color = Color(0xFFF0E8D8), thickness = 1.dp)
}

// ── Toggle row ────────────────────────────────────────────────────────────────
@Composable
private fun ToggleRow(icon: String, title: String, sub: String, default: Boolean) {
    var checked by remember { mutableStateOf(default) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(32.dp), contentAlignment = Alignment.Center) {
            Text(icon, fontSize = 20.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
            Text(sub, fontSize = 11.sp, color = TextMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFC0B090)
            )
        )
    }
    Divider(color = Color(0xFFF0E8D8), thickness = 1.dp)
}

// ── Language option row ───────────────────────────────────────────────────────
@Composable
private fun LanguageOption(flag: String, name: String, isActive: Boolean = false) {
    var active by remember { mutableStateOf(isActive) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { active = !active }
            .padding(horizontal = 15.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(flag, fontSize = 20.sp)
        Text(name, fontSize = 14.sp, color = TextDark, modifier = Modifier.weight(1f))
        Text(
            "✓",
            fontSize = 16.sp,
            color = AccentGreen,
            modifier = Modifier.alpha(if (active) 1f else 0f)
        )
    }
    Divider(color = Color(0xFFF0E8D8), thickness = 1.dp)
}

private fun Modifier.alpha(value: Float) = this.then(
    Modifier.graphicsLayer(alpha = value)
)

// ── Utility image functions (unchanged from original) ─────────────────────────

fun copyImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "${UUID.randomUUID()}.jpg"
        val outputFile = File(context.filesDir, fileName)
        inputStream?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }
        outputFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getProfileImageToDisplay(context: Context): String? {
    val backendProfileUrl = getUserProfile(context)
    if (!backendProfileUrl.isNullOrEmpty()) return backendProfileUrl
    val localImagePath = getSavedImagePath(context)
    if (!localImagePath.isNullOrEmpty()) return localImagePath
    return null
}

fun getSavedImagePath(context: Context): String? {
    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPref.getString("profile_image_path", null)
}