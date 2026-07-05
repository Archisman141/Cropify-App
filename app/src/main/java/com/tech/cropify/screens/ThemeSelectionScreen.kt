package com.tech.cropify.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.tech.cropify.ui.theme.TextSize
import com.tech.cropify.util.ThemeMode
import com.tech.cropify.viewModel.ThemeViewModel

private val PrimaryBlue = Color(0xFF274185)

private val LightBackground = Color(0xFFEBECF2)
private val LightCardBackground = Color.White
private val LightSubtitleText = Color(0xFF6B7280)

private val DarkBackground = Color(0xFF101017)
private val DarkCardBackground = Color(0xFF1A1A24)
private val DarkSubtitleText = Color(0xFFA0A4B8)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionScreen(
    navController: NavHostController
) {
    val themeViewModel: ThemeViewModel = hiltViewModel()
    val themeMode by themeViewModel.theme.collectAsStateWithLifecycle()

    val isDarkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    // No animation on background — prevents white flash on theme switch
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val cardBackground = if (isDarkTheme) DarkCardBackground else LightCardBackground

    val themeOptions = remember {
        listOf(
            Triple(ThemeMode.LIGHT, "Light", Icons.Filled.LightMode),
            Triple(ThemeMode.DARK, "Dark", Icons.Filled.DarkMode),
            Triple(ThemeMode.SYSTEM, "System Default", Icons.Filled.SettingsBrightness)
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "App Theme",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = TextSize.titleXl
                        ),
                        color = if (isDarkTheme) Color.White else Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {

            Text(
                text = "Choose your preferred appearance",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkTheme) DarkSubtitleText else LightSubtitleText,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 4.dp else 1.dp),
                border = if (isDarkTheme) BorderStroke(0.5.dp, Color.White.copy(alpha = 0.06f)) else null
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    themeOptions.forEachIndexed { index, (mode, title, icon) ->
                        val selected = themeMode == mode

                        ThemeOptionItem(
                            title = title,
                            icon = icon,
                            selected = selected,
                            isDarkTheme = isDarkTheme,
                            onClick = { themeViewModel.setTheme(mode) }
                        )

                        if (index < themeOptions.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 56.dp)
                                    .height(0.5.dp)
                                    .background(
                                        if (isDarkTheme) Color.White.copy(alpha = 0.06f)
                                        else Color.Black.copy(alpha = 0.06f)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ThemeOptionItem(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        targetValue = if (selected)
            PrimaryBlue.copy(alpha = if (isDarkTheme) 0.15f else 0.08f)
        else
            Color.Transparent,
        label = "theme_bg"
    )

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.01f else 1f,
        label = "theme_scale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(background)
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (selected) PrimaryBlue.copy(alpha = 0.15f)
                    else if (isDarkTheme) Color.White.copy(alpha = 0.05f)
                    else Color.Black.copy(alpha = 0.04f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (selected) PrimaryBlue
                else if (isDarkTheme) DarkSubtitleText
                else LightSubtitleText
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = if (isDarkTheme) Color.White else Color.Black
        )

        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = PrimaryBlue,
                unselectedColor = if (isDarkTheme) DarkSubtitleText else LightSubtitleText
            )
        )
    }
}

