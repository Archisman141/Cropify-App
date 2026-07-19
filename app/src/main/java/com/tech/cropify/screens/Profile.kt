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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import com.tech.cropify.R
import com.tech.cropify.navigation.Routes
import com.tech.cropify.util.LanguageManager
import com.tech.cropify.viewModel.LanguageViewModel
import com.tech.cropify.viewModel.LoginViewModel
import com.tech.cropify.viewModel.ProfileViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

// ── Brand colours ─────────────────────────────────────────────────────────────
private val DarkGreen    = Color(0xFF1E4010)
val MedGreen             = Color(0xFF3A7A20)
private val YellowAccent = Color(0xFFF5C842)
private val BgCream      = Color(0xFFF5F0E8)
private val TextDark     = Color(0xFF2A2010)

@Composable
fun Profile(
    navController: NavController,
    viewModel: LoginViewModel,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val profile by profileViewModel.profile.collectAsState()
    val profileImageModel by profileViewModel.profileImageUrl.collectAsState()

    // One LanguageViewModel per screen — lightweight, no Hilt needed
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguageCode by languageViewModel.selectedLanguageCode.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserDetails(context)
        languageViewModel.init(context)
    }

    Scaffold(containerColor = BgCream) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ────────────────────────────────────────────────────────
            ProfileHeader(
                name = profile.userName,
                location = profile.locationLabel,
                profileImageModel = profileImageModel
            ) {
                navController.navigate(Routes.EditProfile)
            }

            // ── Body ──────────────────────────────────────────────────────────
            Column(modifier = Modifier.padding(16.dp)) {

                // Farm Details
                SettingsSection(label = stringResource(R.string.farm_details)) {
                    SettingsRow(icon = "🌾", title = stringResource(R.string.crop_season),       sub = profile.cropSeason,                           showArrow = true)
                    SettingsRow(icon = "📍", title = stringResource(R.string.location),          sub = profile.locationLabel,                        showArrow = true)
                    SettingsRow(icon = "📐", title = stringResource(R.string.farm_size),         sub = "${profile.farmSizeAcres} ${stringResource(R.string.acres)}", showArrow = true)
                    SettingsRow(icon = "🌱", title = stringResource(R.string.soil_type),         sub = profile.soilType.displayName,                 showArrow = true)
                }

                Spacer(Modifier.height(18.dp))

                // Notifications
                SettingsSection(label = stringResource(R.string.notifications)) {
                    ToggleRow(icon = "🌧️", title = stringResource(R.string.rain_alerts),    sub = stringResource(R.string.rain_alerts_sub),    default = true)
                    ToggleRow(icon = "🦠", title = stringResource(R.string.disease_alerts), sub = stringResource(R.string.disease_alerts_sub), default = true)
                    ToggleRow(icon = "📊", title = stringResource(R.string.weekly_report),  sub = stringResource(R.string.weekly_report_sub),  default = true)
                }

                Spacer(Modifier.height(18.dp))

                // ── Language ──────────────────────────────────────────────────
                SettingsSection(label = stringResource(R.string.language)) {
                    LanguageManager.supportedLanguages.forEach { (displayName, code) ->
                        LanguageOption(
                            flag = languageFlag(code),
                            name = displayName,
                            isActive = selectedLanguageCode == code,
                            onClick = {
                                languageViewModel.changeLanguage(context, code)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // App Settings
                SettingsSection(label = stringResource(R.string.app_settings)) {
                    ToggleRow(icon = "🌙", title = stringResource(R.string.dark_mode),        sub = stringResource(R.string.dark_mode_sub),        default = false)
                    ToggleRow(icon = "📶", title = stringResource(R.string.offline_mode),     sub = stringResource(R.string.offline_mode_sub),     default = true)
                    ToggleRow(icon = "📍", title = stringResource(R.string.location_access),  sub = stringResource(R.string.location_access_sub),  default = true)
                    SettingsRow(icon = "📦", title = stringResource(R.string.my_reports),      sub = stringResource(R.string.my_reports_sub),       showArrow = true)
                    SettingsRow(icon = "🔐", title = stringResource(R.string.privacy_security),sub = stringResource(R.string.privacy_security_sub), showArrow = true)
                    SettingsRow(icon = "🆘", title = stringResource(R.string.help_support),   sub = stringResource(R.string.help_support_sub),     showArrow = true)
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    stringResource(R.string.app_version),
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
                        .clickable {
                            viewModel.logout(context)
                            navController.navigate(Routes.LoginScreen)
                        }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚪  ${stringResource(R.string.sign_out)}", fontSize = 14.sp, color = Color(0xFF8A2020))
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

/** Maps a BCP-47 language code to the right flag emoji. */
private fun languageFlag(code: String) = when (code) {
    "en" -> "🇬🇧"
    "hi" -> "🇮🇳"
    "mr" -> "🇮🇳"
    "pa" -> "🇮🇳"
    "bn" -> "🇮🇳"
    else -> "🌐"
}
// ── Profile hero header ───────────────────────────────────────────────────────
@Composable
private fun ProfileHeader(
    name: String,
    location: String,
    profileImageModel: String?,
    onEditClick: () -> Unit
) {
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
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(YellowAccent, Color(0xFFE8A030))))
                    .border(4.dp, Color(0x4DFFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (profileImageModel != null) {
                    SubcomposeAsyncImage(
                        model = profileImageModel,
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👨‍🌾", fontSize = 32.sp, textAlign = TextAlign.Center)
                            }
                        }
                    )
                } else {
                    Text("👨‍🌾", fontSize = 32.sp, textAlign = TextAlign.Center)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(name, fontFamily = FontFamily.Serif, fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text("${stringResource(R.string.farmer)} · $location", fontSize = 13.sp, color = Color(0xB3FFFFFF))
            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(YellowAccent)
                    .clickable { onEditClick() }
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(stringResource(R.string.edit_profile), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A3A0F))
            }
        }
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

// ── Plain setting row ─────────────────────────────────────────────────────────
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
    HorizontalDivider(color = Color(0xFFF0E8D8), thickness = 1.dp)
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
    HorizontalDivider(color = Color(0xFFF0E8D8), thickness = 1.dp)
}

// ── Language option row — now driven by onClick callback ──────────────────────
@Composable
private fun LanguageOption(
    flag: String,
    name: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
            modifier = Modifier.graphicsLayer(alpha = if (isActive) 1f else 0f)
        )
    }
    HorizontalDivider(color = Color(0xFFF0E8D8), thickness = 1.dp)
}

// ── Utility functions ─────────────────────────────────────────────────────────

fun copyImageToInternalStorage(context: Context, uri: android.net.Uri): String? {
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

fun getSavedImagePath(context: Context): String? {
    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPref.getString("profile_image_path", null)
}

fun initialsOf(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else            -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}