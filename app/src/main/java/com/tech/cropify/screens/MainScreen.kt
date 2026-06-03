package com.tech.cropify.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tech.cropify.navigation.Routes
import com.tech.cropify.viewModel.LoginViewModel

private val PrimaryBlue = Color(0xFF274185)
private val AccentOrange = Color(0xFFF47920)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(navController: NavHostController) {

    val bottomNavController = rememberNavController()
    var isBottomBarVisible by remember { mutableStateOf(true) }
    var bottomBarType by remember { mutableStateOf(BottomBarType.DASHBOARD) }

    val isDarkTheme = isSystemInDarkTheme()

    val viewModel: LoginViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                AnimatedContent(
                    targetState = bottomBarType,
                    transitionSpec = {
                        fadeIn(tween(250)) with fadeOut(tween(250))
                    }
                ) { type ->
                    when (type) {
                        BottomBarType.DASHBOARD, BottomBarType.CROP, BottomBarType.DISEASE, BottomBarType.SOIL->
                            BottomNavigationBar(
                                isDarkTheme,
                                navController = bottomNavController,
                                onDashBoardClick = {
                                    bottomBarType = BottomBarType.DASHBOARD
                                    bottomNavController.navigate(BottomNavItem.Home.route) {
                                        popUpTo(BottomNavItem.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onCropClick = {
                                    bottomBarType = BottomBarType.CROP
                                    bottomNavController.navigate(BottomNavItem.Crop.route) {
                                        popUpTo(BottomNavItem.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onDiseaseClick = {
                                    bottomBarType = BottomBarType.DISEASE
                                    bottomNavController.navigate(BottomNavItem.Disease.route) {
                                        popUpTo(BottomNavItem.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onSoilClick = {
                                    bottomBarType = BottomBarType.SOIL
                                    bottomNavController.navigate(BottomNavItem.Soil.route) {
                                        popUpTo(BottomNavItem.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                    }
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,  // "home"
            modifier = Modifier.padding(padding),
        ) {
            composable(BottomNavItem.Home.route) {
                DashboardScreen(navController)
            }
            composable(BottomNavItem.Crop.route) {
                CropScreen(navController)
            }
            composable(BottomNavItem.Disease.route) {
                DiseaseScreen(navController)
            }
            composable(BottomNavItem.Soil.route) {
                SoilScreen(navController)
            }
            // Keep type-safe routes for screens not in bottom nav
            composable<Routes.Weather> {
                WeatherScreen(navController)
            }
            composable<Routes.Profile> {
                Profile(navController, viewModel)
            }
        }
    }
}


@Composable
fun SliderWithLabel(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Text("$label: ${value.toInt()}")
        Slider(value = value, onValueChange = onValueChange)
    }
}

// ── Bottom Nav ────────────────────────────────────────────────────────────────
//@Composable
//fun FarmBottomNav(navController: NavController, active: String) {
//    val items = listOf(
//        Triple("🏠", "Home", "home") to Routes.Dashboard,
//        Triple("🌾", "Predict", "crop") to Routes.Crop,
//        Triple("🔬", "Detect", "disease") to Routes.Disease,
//        Triple("🏔️", "Soil", "soil") to Routes.Soil,
//        Triple("👤", "Profile", "profile") to Routes.Profile
//    )
//    Surface(
//        color = Color.White,
//        shadowElevation = 8.dp,
//        tonalElevation = 0.dp
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//        ) {
//            for ((info, route) in items) {
//                val isActive = info.third == active
//                Column(
//                    modifier = Modifier
//                        .weight(1f)
//                        .clickable { if (!isActive) navController.navigate(route) }
//                        .padding(vertical = 3.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(info.first, fontSize = 19.sp)
//                    Spacer(Modifier.height(2.dp))
//                    Text(
//                        info.second,
//                        fontSize = 10.sp,
//                        color = if (isActive) AccentGreen else TextMuted,
//                        fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
//                    )
//                }
//            }
//        }
//    }
//}

@Composable
fun BottomNavigationBar(
    isDarkTheme: Boolean,
    navController: NavController,
    onDashBoardClick: () -> Unit,
    onCropClick: () -> Unit,
    onDiseaseClick: () -> Unit,
    onSoilClick: () -> Unit,
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Crop,
        BottomNavItem.Disease,
        BottomNavItem.Soil
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val borderColor = if (isDarkTheme) Color.DarkGray else Color.LightGray

    Box(
        Modifier
            .fillMaxWidth()
            .height(80.dp)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        NavigationBar(
            containerColor = if (isDarkTheme) AlertDialogDefaults.containerColor else Color.White,
            modifier = Modifier.fillMaxSize()
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    isDarkTheme,
                    selected = currentDestination?.startsWith(item.route) == true,
                    onClick = {
                        when (item) {
                            BottomNavItem.Home -> onDashBoardClick()
                            BottomNavItem.Crop -> onCropClick()
                            BottomNavItem.Disease -> onDiseaseClick()
                            BottomNavItem.Soil -> onSoilClick()
                        }
                    },
                    icon = { selected, activeColor, inactiveColor ->
//                        Icon(
//                            painter = painterResource(id = item.icon),
//                            contentDescription = item.title,
//                            modifier = Modifier
//                                .size(23.dp)
//                                .padding(top = 6.dp),
//                            tint = if (selected) activeColor else inactiveColor
//                        )

                        Text(item.icon, fontSize = 19.sp)
                    },
                    label = { selected, activeColor, inactiveColor ->
                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (selected) activeColor else Color(0xFF888888)
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.NavigationBarItem(
    isDarkTheme: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable (selected: Boolean, active: Color, inActive: Color) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable ((selected: Boolean, active: Color, inActive: Color) -> Unit)? = null,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = Color.Unspecified,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    var itemWidth by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
            )
            .weight(1f)
            .onSizeChanged { itemWidth = it.width },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = if (selected) activeColor else inactiveColor)
        )

        Box(
            Modifier
                .weight(1f)
                .size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            icon.invoke(
                selected,
                activeColor,
                if (isDarkTheme) Color.White else Color(0xFF888888)
            )
        }

        label?.invoke(selected, activeColor, inactiveColor)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

sealed class BottomNavItem(
    val title: String,
    val route: String,
    val icon: String
) {
    object Home : BottomNavItem("Home", "home", "🏠" )
    object Crop : BottomNavItem("Crop", "crop", "🌾" )
    object Disease : BottomNavItem("Disease", "disease", "🔬")
    object Soil : BottomNavItem("Soil", "soil", "🏔")
}

enum class BottomBarType{
    DASHBOARD, CROP, DISEASE, SOIL
}