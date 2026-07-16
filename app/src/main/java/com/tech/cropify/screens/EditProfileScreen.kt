package com.tech.cropify.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tech.cropify.util.LocationHelper
import com.tech.cropify.viewModel.ProfileViewModel
import kotlinx.coroutines.launch

private val DarkGreen   = Color(0xFF1E4010)
private val AccentGreen = Color(0xFF4A8A30)
private val BgCream     = Color(0xFFF5F0E8)
private val CardBorder  = Color(0xFFE0D8C8)
private val TextDark    = Color(0xFF2A2010)
private val TextMuted   = Color(0xFF8A7A5A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profile by viewModel.profile.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveError by viewModel.saveError.collectAsState()

    var userName by remember(profile.userName) { mutableStateOf(profile.userName) }
    var emailId by remember(profile.emailId) { mutableStateOf(profile.emailId) }
    var phone by remember(profile.phone) { mutableStateOf(profile.phone) }
    var state by remember(profile.state) { mutableStateOf(profile.state) }
    var city by remember(profile.city) { mutableStateOf(profile.city) }
    var village by remember(profile.village) { mutableStateOf(profile.village) }
    var cropSeason by remember(profile.cropSeason) { mutableStateOf(profile.cropSeason) }
    var farmSize by remember(profile.farmSizeAcres) { mutableStateOf(profile.farmSizeAcres) }

    var locationLabel by remember(profile.locationLabel) { mutableStateOf(profile.locationLabel) }
    var latitude by remember(profile.latitude) { mutableStateOf(profile.latitude) }
    var longitude by remember(profile.longitude) { mutableStateOf(profile.longitude) }
    var soilType by remember(profile.soilType) { mutableStateOf(profile.soilType) }

    var isLocatingLoading by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }

    fun runLocationLookup() {
        isLocatingLoading = true
        locationError = null
        scope.launch {
            val resolved = LocationHelper.getCurrentLocation(context)
            if (resolved == null) {
                locationError = "Couldn't get your location. Please try again."
                isLocatingLoading = false
                return@launch
            }
            locationLabel = resolved.label
            latitude = resolved.latitude
            longitude = resolved.longitude

            soilType = LocationHelper.identifySoilType(resolved.latitude, resolved.longitude)
            isLocatingLoading = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) runLocationLookup()
        else locationError = "Location permission is needed to detect your farm's soil type."
    }

    fun onUseMyLocationClick() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) runLocationLookup()
        else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        containerColor = BgCream,
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.SemiBold, color = TextDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            SectionLabel("Account Details")
            FormCard {
                LabeledField("Name", userName) { userName = it }
                LabeledField("Email", emailId, keyboardType = KeyboardType.Email) { emailId = it }
                LabeledField("Phone", phone, keyboardType = KeyboardType.Phone) { phone = it }
            }

            Spacer(Modifier.height(20.dp))

            SectionLabel("Address")
            FormCard {
                LabeledField("State", state) { state = it }
                LabeledField("City", city) { city = it }
                LabeledField("Village", village) { village = it }
            }

            Spacer(Modifier.height(20.dp))

            SectionLabel("Farm Details")
            FormCard {
                LabeledField("Crop Season", cropSeason) { cropSeason = it }
                LabeledField("Farm Size (acres)", farmSize, keyboardType = KeyboardType.Decimal) { farmSize = it }
            }

            Spacer(Modifier.height(20.dp))

            SectionLabel("Farm Location")
            FormCard {
                Column(Modifier.padding(15.dp)) {
                    Text(locationLabel, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    if (latitude != null && longitude != null) {
                        Text(
                            "Lat ${"%.4f".format(latitude)}, Lon ${"%.4f".format(longitude)}",
                            fontSize = 11.sp, color = TextMuted
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentGreen)
                            .clickable(enabled = !isLocatingLoading) { onUseMyLocationClick() }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLocatingLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Detecting location…", color = Color.White, fontSize = 13.sp)
                        } else {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Use My Current Location", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    locationError?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, fontSize = 11.sp, color = Color(0xFF8A2020))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            SectionLabel("Soil Type (auto-detected)")
            FormCard {
                Row(modifier = Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(soilType.emoji, fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(soilType.displayName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                        Text("Detected from your farm location", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            saveError?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, fontSize = 12.sp, color = Color(0xFF8A2020))
            }

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .background(if (isSaving) DarkGreen.copy(alpha = 0.6f) else DarkGreen)
                    .clickable(enabled = !isSaving) {
                        val updated = profile.copy(
                            userName = userName,
                            emailId = emailId,
                            phone = phone,
                            state = state,
                            city = city,
                            village = village,
                            cropSeason = cropSeason,
                            farmSizeAcres = farmSize,
                            locationLabel = locationLabel,
                            latitude = latitude,
                            longitude = longitude,
                            soilType = soilType
                        )
                        // password stays as whatever's already stored (empty string
                        // if never set) — only include a password field here if you
                        // want users editing it, since the API expects it every call.
                        viewModel.saveProfile(updated) {
                            navController.popBackStack()
                        }
                    }
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Save Changes", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = TextMuted,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun FormCard(content: @Composable ColumnScope.() -> Unit) {
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

@Composable
private fun LabeledField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column(Modifier.padding(horizontal = 15.dp, vertical = 8.dp)) {
        Text(label, fontSize = 11.sp, color = TextMuted)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentGreen,
                unfocusedBorderColor = CardBorder
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}



