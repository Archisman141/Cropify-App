package com.tech.cropify.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import com.tech.cropify.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val SplashDarkGreen  = Color(0xFF0F2408)
private val SplashMidGreen   = Color(0xFF1E4010)
private val SplashAccentGold = Color(0xFFF5C842)

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    // ── Animation states ──────────────────────────────────────────────────────
    val logoScale   = remember { Animatable(0.4f) }
    val logoAlpha   = remember { Animatable(0f) }
    val titleAlpha  = remember { Animatable(0f) }
    val titleSlide  = remember { Animatable(24f) }
    val taglineAlpha = remember { Animatable(0f) }
    val barWidth    = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo pop-in
        launch {
            logoAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        }

        delay(300)

        // Title slides up
        launch {
            titleAlpha.animateTo(1f, tween(400))
        }
        launch {
            titleSlide.animateTo(0f, tween(400, easing = FastOutSlowInEasing))
        }

        delay(200)

        // Tagline fades in
        taglineAlpha.animateTo(1f, tween(400))

        delay(100)

        // Progress bar fills
        barWidth.animateTo(1f, tween(700, easing = FastOutSlowInEasing))

        delay(300)

        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0.00f to SplashDarkGreen,
                        0.55f to SplashMidGreen,
                        1.00f to Color(0xFF2D6018)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Logo ──────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(logoScale.value)
                    .graphicsLayer(alpha = logoAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_cropify_logo),
                    contentDescription = "Cropify Logo",
                    modifier = Modifier.size(110.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── App name ──────────────────────────────────────────────────────
            Text(
                text = "Cropify",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = SplashAccentGold,
                modifier = Modifier.graphicsLayer(
                    alpha = titleAlpha.value,
                    translationY = titleSlide.value
                )
            )

            Spacer(Modifier.height(6.dp))

            // ── Tagline ───────────────────────────────────────────────────────
            Text(
                text = "Smart farming, better harvests",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp,
                modifier = Modifier.graphicsLayer(alpha = taglineAlpha.value)
            )

            Spacer(Modifier.height(48.dp))

            // ── Animated progress bar ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.12f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(barWidth.value)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(SplashAccentGold, Color(0xFF5AAC2E))
                            )
                        )
                )
            }
        }

        // ── Bottom label ──────────────────────────────────────────────────────
        Text(
            text = "Built for Indian Farmers 🌾",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.30f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer(alpha = taglineAlpha.value)
                .then(Modifier.then(
                    androidx.compose.ui.Modifier.padding(bottom = 32.dp)
                ))
        )
    }
}