package com.tech.cropify.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
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
import com.tech.cropify.ui.theme.AppColors
import com.tech.cropify.viewModel.LoginViewModel
import com.tech.cropify.viewModel.ProfileViewModel

private val PrimaryBlue = Color(0xFF274185)
private val AccentOrange = Color(0xFFF47920)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(navController: NavHostController, profileViewModel: ProfileViewModel) {

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
                        BottomBarType.DASHBOARD, BottomBarType.CROP, BottomBarType.DISEASE->
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
//                                onSoilClick = {
//                                    bottomBarType = BottomBarType.SOIL
//                                    bottomNavController.navigate(BottomNavItem.Soil.route) {
//                                        popUpTo(BottomNavItem.Home.route) { saveState = true }
//                                        launchSingleTop = true
//                                        restoreState = true
//                                    }
//                                }
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
                DashboardScreen(navController,bottomNavController, profileViewModel)
            }
            composable(BottomNavItem.Crop.route) {
                CropScreen(navController, bottomNavController)
            }
            composable(BottomNavItem.Disease.route) {
                DiseaseScreen(navController, bottomNavController)
            }
//            composable(BottomNavItem.Soil.route) {
//                SoilScreen(navController, bottomNavController)
//            }
            // Keep type-safe routes for screens not in bottom nav
            composable<Routes.Weather> {
                WeatherScreen(navController)
            }
            composable<Routes.Profile> {
                Profile(navController, viewModel, profileViewModel)
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
//    onSoilClick: () -> Unit,
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Crop,
        BottomNavItem.Disease,
//        BottomNavItem.Soil
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val selectedIndex = items
        .indexOfFirst { currentDestination?.startsWith(it.route) == true }
        .coerceAtLeast(0)

    val containerColor = if (isDarkTheme) AppColors.DarkSurface else Color.White
    val borderColor = if (isDarkTheme) AppColors.DarkBorder else AppColors.CardBorder

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .shadow(elevation = 10.dp, spotColor = AppColors.DarkGreen.copy(alpha = 0.15f))
            .background(containerColor)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        val itemWidth = maxWidth / items.size
        val markerWidth = 48.dp
        val indicatorOffset by animateDpAsState(
            targetValue = itemWidth * selectedIndex + (itemWidth - markerWidth) / 2,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "indicatorOffset"
        )

        // Small marker pinned to the top edge of the selected tab
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset, y = 0.dp)
                .width(markerWidth)
                .height(3.dp)
                .clip(RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                .background(AppColors.AccentGreen)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 3.dp)
        ) {
            items.forEach { item ->
                val selected = item == items[selectedIndex]
                BottomNavTab(
                    item = item,
                    selected = selected,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = {
                        when (item) {
                            BottomNavItem.Home -> onDashBoardClick()
                            BottomNavItem.Crop -> onCropClick()
                            BottomNavItem.Disease -> onDiseaseClick()
//                            BottomNavItem.Soil -> onSoilClick()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavTab(
    item: BottomNavItem,
    selected: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val liftOffset by animateDpAsState(
        targetValue = if (selected) (-6).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "liftOffset"
    )

    val contentColor = when {
        selected -> AppColors.AccentGreen
        isDarkTheme -> AppColors.DarkTextMuted
        else -> AppColors.TextMuted
    }

    Column(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = item.icon,
            fontSize = 20.sp,
            modifier = Modifier.graphicsLayer {
                translationY = liftOffset.toPx()
            }
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = contentColor,
            modifier = Modifier.graphicsLayer {
                translationY = liftOffset.toPx()
            }
        )
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun RowScope.NavigationBarItem(
//    isDarkTheme: Boolean,
//    selected: Boolean,
//    onClick: () -> Unit,
//    icon: @Composable (selected: Boolean, active: Color, inActive: Color) -> Unit,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
//    label: @Composable ((selected: Boolean, active: Color, inActive: Color) -> Unit)? = null,
//    activeColor: Color = MaterialTheme.colorScheme.primary,
//    inactiveColor: Color = Color.Unspecified,
//    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
//) {
//    var itemWidth by remember { mutableStateOf(0) }
//    val scope = rememberCoroutineScope()
//    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//
//    Column(
//        modifier
//            .selectable(
//                selected = selected,
//                onClick = onClick,
//                enabled = enabled,
//                role = Role.Tab,
//                interactionSource = interactionSource,
//                indication = null,
//            )
//            .weight(1f)
//            .onSizeChanged { itemWidth = it.width },
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(1.dp)
//                .background(color = if (selected) activeColor else inactiveColor)
//        )
//
//        Box(
//            Modifier
//                .weight(1f)
//                .size(24.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            icon.invoke(
//                selected,
//                activeColor,
//                if (isDarkTheme) Color.White else Color(0xFF888888)
//            )
//        }
//
//        label?.invoke(selected, activeColor, inactiveColor)
//        Spacer(modifier = Modifier.height(8.dp))
//    }
//}

sealed class BottomNavItem(
    val title: String,
    val route: String,
    val icon: String
) {
    object Home : BottomNavItem("Home", "home", "🏠" )
    object Crop : BottomNavItem("Crop", "crop", "🌾" )
    object Disease : BottomNavItem("Disease", "disease", "🔬")
//    object Soil : BottomNavItem("Soil", "soil", "🏔")
}

enum class BottomBarType{
    DASHBOARD, CROP, DISEASE
}