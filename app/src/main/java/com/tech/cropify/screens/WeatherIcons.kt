package com.tech.cropify.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─── Icon palette ─────────────────────────────────────────────────────────────
private val SunColor   = Color(0xFFFFD45E)
private val SunGlow     = Color(0x33FFD45E)
private val CloudLight = Color(0xFFEDF3FA)
private val CloudDark  = Color(0xFFC7D6E8)
private val RainColor  = Color(0xFF6FB7FF)
private val BoltColor  = Color(0xFFFFD45E)
private val SnowColor  = Color(0xFFFFFFFF)
private val MistColor  = Color(0xFFB9C9DC)

/**
 * Animated weather icon driven by an OpenWeather icon code (e.g. "01d", "10n").
 * Falls back to a sun/cloud mix for anything unrecognised.
 */
@Composable
fun AnimatedWeatherIcon(icon: String?, size: Dp, modifier: Modifier = Modifier) {
    when (icon?.take(2)) {
        "01" -> SunIcon(size, modifier)
        "02" -> SunCloudIcon(size, modifier)
        "03", "04" -> CloudIcon(size, modifier)
        "09", "10" -> RainIcon(size, modifier)
        "11" -> StormIcon(size, modifier)
        "13" -> SnowIcon(size, modifier)
        "50" -> MistIcon(size, modifier)
        else -> SunCloudIcon(size, modifier)
    }
}

// ─── Sun ──────────────────────────────────────────────────────────────────────
@Composable
private fun SunIcon(size: Dp, modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "sun")
    val angle by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "rays"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.92f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1600), repeatMode = RepeatMode.Reverse),
        label = "pulse"
    )
    Canvas(modifier.size(size)) {
        drawSun(center, this.size.minDimension * 0.20f * pulse, angle)
    }
}

private fun DrawScope.drawSun(c: Offset, radius: Float, angle: Float) {
    drawCircle(SunGlow, radius * 1.7f, c)
    rotate(angle, c) {
        repeat(8) { i ->
            rotate(i * 45f, c) {
                drawLine(
                    SunColor,
                    start = Offset(c.x, c.y - radius * 1.5f),
                    end = Offset(c.x, c.y - radius * 2.2f),
                    strokeWidth = radius * 0.28f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
    drawCircle(SunColor, radius, c)
}

// ─── Sun + cloud ──────────────────────────────────────────────────────────────
@Composable
private fun SunCloudIcon(size: Dp, modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "suncloud")
    val angle by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "rays"
    )
    val bob by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200), repeatMode = RepeatMode.Reverse),
        label = "bob"
    )
    Canvas(modifier.size(size)) {
        val dim = this.size.minDimension
        // Sun peeking from the top-left.
        drawSun(Offset(dim * 0.36f, dim * 0.34f), dim * 0.14f, angle)
        translate(top = bob * dim * 0.03f) {
            drawCloud(
                cx = dim * 0.56f, cy = dim * 0.60f, width = dim * 0.62f
            )
        }
    }
}

// ─── Cloud ──────────────────────────────────────────────────────────────────--
@Composable
private fun CloudIcon(size: Dp, modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "cloud")
    val bob by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400), repeatMode = RepeatMode.Reverse),
        label = "bob"
    )
    Canvas(modifier.size(size)) {
        val dim = this.size.minDimension
        translate(top = bob * dim * 0.035f) {
            drawCloud(cx = dim * 0.5f, cy = dim * 0.55f, width = dim * 0.74f)
        }
    }
}

// ─── Rain ──────────────────────────────────────────────────────────────────--
@Composable
private fun RainIcon(size: Dp, modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "rain")
    val fall by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
        label = "fall"
    )
    Canvas(modifier.size(size)) {
        val dim = this.size.minDimension
        drawCloud(cx = dim * 0.5f, cy = dim * 0.45f, width = dim * 0.72f)
        val dropXs = listOf(0.34f, 0.5f, 0.66f)
        dropXs.forEachIndexed { i, fx ->
            val phase = (fall + i * 0.33f) % 1f
            val startY = dim * 0.62f
            val y = startY + phase * dim * 0.28f
            drawLine(
                RainColor.copy(alpha = 1f - phase),
                start = Offset(dim * fx, y),
                end = Offset(dim * fx, y + dim * 0.10f),
                strokeWidth = dim * 0.035f,
                cap = StrokeCap.Round
            )
        }
    }
}

// ─── Storm ──────────────────────────────────────────────────────────────────--
@Composable
private fun StormIcon(size: Dp, modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "storm")
    val flash by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), repeatMode = RepeatMode.Reverse),
        label = "flash"
    )
    Canvas(modifier.size(size)) {
        val dim = this.size.minDimension
        drawCloud(cx = dim * 0.5f, cy = dim * 0.42f, width = dim * 0.72f)
        val bolt = Path().apply {
            moveTo(dim * 0.52f, dim * 0.55f)
            lineTo(dim * 0.40f, dim * 0.78f)
            lineTo(dim * 0.50f, dim * 0.78f)
            lineTo(dim * 0.44f, dim * 0.95f)
            lineTo(dim * 0.64f, dim * 0.70f)
            lineTo(dim * 0.53f, dim * 0.70f)
            lineTo(dim * 0.60f, dim * 0.55f)
            close()
        }
        drawPath(bolt, BoltColor.copy(alpha = 0.55f + flash * 0.45f))
    }
}

// ─── Snow ──────────────────────────────────────────────────────────────────--
@Composable
private fun SnowIcon(size: Dp, modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "snow")
    val fall by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
        label = "fall"
    )
    Canvas(modifier.size(size)) {
        val dim = this.size.minDimension
        drawCloud(cx = dim * 0.5f, cy = dim * 0.45f, width = dim * 0.72f)
        val flakeXs = listOf(0.36f, 0.5f, 0.64f)
        flakeXs.forEachIndexed { i, fx ->
            val phase = (fall + i * 0.33f) % 1f
            val y = dim * 0.62f + phase * dim * 0.28f
            drawCircle(
                SnowColor.copy(alpha = 1f - phase),
                radius = dim * 0.035f,
                center = Offset(dim * fx, y)
            )
        }
    }
}

// ─── Mist ──────────────────────────────────────────────────────────────────--
@Composable
private fun MistIcon(size: Dp, modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "mist")
    val drift by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600), repeatMode = RepeatMode.Reverse),
        label = "drift"
    )
    Canvas(modifier.size(size)) {
        val dim = this.size.minDimension
        val ys = listOf(0.40f, 0.55f, 0.70f)
        ys.forEachIndexed { i, fy ->
            val dir = if (i % 2 == 0) 1f else -1f
            drawLine(
                MistColor,
                start = Offset(dim * 0.22f + drift * dim * 0.06f * dir, dim * fy),
                end = Offset(dim * 0.78f + drift * dim * 0.06f * dir, dim * fy),
                strokeWidth = dim * 0.07f,
                cap = StrokeCap.Round
            )
        }
    }
}

// ─── Cloud shape helper ─────────────────────────────────────────────────────--
private fun DrawScope.drawCloud(cx: Float, cy: Float, width: Float) {
    val w = width
    // Soft outline for depth.
    drawCloudBody(cx, cy + w * 0.02f, w, CloudDark)
    drawCloudBody(cx, cy, w * 0.96f, CloudLight)
}

private fun DrawScope.drawCloudBody(cx: Float, cy: Float, w: Float, color: Color) {
    drawCircle(color, w * 0.24f, Offset(cx - w * 0.28f, cy + w * 0.04f))
    drawCircle(color, w * 0.30f, Offset(cx, cy - w * 0.10f))
    drawCircle(color, w * 0.25f, Offset(cx + w * 0.30f, cy + w * 0.02f))
    drawRoundRectCompat(color, cx, cy, w)
}

private fun DrawScope.drawRoundRectCompat(color: Color, cx: Float, cy: Float, w: Float) {
    val left = cx - w * 0.5f
    val top = cy + w * 0.02f
    drawCircle(color, w * 0.18f, Offset(left + w * 0.18f, top))
    drawCircle(color, w * 0.18f, Offset(left + w * 0.82f, top))
    drawLine(
        color,
        start = Offset(left + w * 0.10f, top),
        end = Offset(left + w * 0.90f, top),
        strokeWidth = w * 0.36f,
        cap = StrokeCap.Round
    )
}
