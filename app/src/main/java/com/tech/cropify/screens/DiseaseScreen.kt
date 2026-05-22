package com.tech.cropify.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


private val BgCream         = Color(0xFFF5F0E8)
private val GreenDark       = Color(0xFF1E4010)
private val GreenPrimary    = Color(0xFF2D5E1A)
private val GreenLight      = Color(0xFF4A8A30)
private val GreenPale       = Color(0xFFE8F5E1)
private val BorderTan       = Color(0xFFE0D8C8)
private val DashBorder      = Color(0xFFC4B880)      // dashed upload zone border
private val TextDark        = Color(0xFF2A2010)
private val TextBrown       = Color(0xFF4A3A1E)
private val TextMuted       = Color(0xFF8A7A5A)
private val TextWarn        = Color(0xFF5A4A2E)
private val CardWhite       = Color.White
private val DangerBg        = Color(0xFFFEE8E8)
private val DangerRed       = Color(0xFF8A2020)
private val DangerRedLight  = Color(0xFFB06060)
private val SeverityOrange  = Color(0xFFE8A040)
private val SeverityRed     = Color(0xFFC43030)
private val SeverityBarBg   = Color(0xFFF0E8E8)
private val RemedyBrown     = Color(0xFF3A2A10)
private val TipLabelBrown   = Color(0xFF5A4A2E)

@Composable
fun DiseaseScreen(navController: NavHostController) {

    // State
    var selectedPart by remember { mutableStateOf("Leaf") }
    var showResult   by remember { mutableStateOf(false) }

    val plantParts = listOf(
        Pair("🍃", "Leaf"),
        Pair("🌿", "Stem"),
        Pair("🌸", "Flower"),
        Pair("🍎", "Fruit")
    )

    Scaffold(
        topBar = {
            DiseaseTopBar(onBack = { navController.popBackStack() })
        },
        containerColor = BgCream
    ){ innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .background(BgCream)
//        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        )
        {
            // Green page header
            DiseasePageHeader()

            Column(modifier = Modifier.padding(14.dp)) {

                DiseaseCard {
                    SectionLabel("Select Plant Part")
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        plantParts.forEach { (icon, label) ->
                            PlantPartChip(
                                icon      = icon,
                                label     = label,
                                selected  = selectedPart == label,
                                modifier  = Modifier.weight(1f),
                                onClick   = { selectedPart = label }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // ── Upload Zone ───────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(17.dp))
                        .background(CardWhite)
                        .border(
                            width = 2.5.dp,
                            color = DashBorder,
                            shape = RoundedCornerShape(17.dp)
                        )
                        .clickable { showResult = true }
                        .padding(horizontal = 18.dp, vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📷", fontSize = 44.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text       = "Take or Upload Photo",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color      = TextDark
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text      = "Clear photo of affected plant part for best results",
                            fontSize  = 12.sp,
                            color     = TextMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { showResult = true },
                            shape   = RoundedCornerShape(9.dp),
                            colors  = ButtonDefaults.buttonColors(
                                containerColor = GreenPrimary,
                                contentColor   = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 9.dp)
                        ) {
                            Text("📸  Scan Now", fontSize = 13.sp)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ── Gallery / Camera row ──────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MediaButton(
                        label    = "📁  Gallery",
                        modifier = Modifier.weight(1f),
                        onClick  = { /* open gallery */ }
                    )
                    MediaButton(
                        label    = "📷  Camera",
                        modifier = Modifier.weight(1f),
                        onClick  = { /* open camera */ }
                    )
                }

                Spacer(Modifier.height(12.dp))

                // ── Result Card (animated) ────────────────────────────────
                AnimatedVisibility(
                    visible = showResult,
                    enter   = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
                ) {
                    DiseaseResultCard()
                }
            }
        }

    }


}

@Composable
private fun DiseaseTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .border(width = 1.dp, color = BorderTan, shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text       = "🔬  Disease Detection",
            fontSize   = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color      = GreenDark
        )

        Spacer(Modifier.width(56.dp))
    }
}

@Composable
private fun DiseasePageHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(GreenDark, GreenLight))
            )
            .padding(horizontal = 20.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔬", fontSize = 38.sp)
            Spacer(Modifier.height(7.dp))
            Text(
                text       = "Disease Detection",
                fontSize   = 21.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text     = "Scan leaves & stems for disease",
                fontSize = 13.sp,
                color    = Color.White.copy(alpha = 0.72f)
            )
        }
    }
}

@Composable
private fun PlantPartChip(
    icon:     String,
    label:    String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick:  () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(if (selected) GreenPale else CardWhite)
            .border(
                width = if (selected) 1.5.dp else 1.5.dp,
                color = if (selected) GreenLight else BorderTan,
                shape = RoundedCornerShape(11.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp),
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            text       = label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            color      = TextBrown
        )
    }
}

@Composable
private fun MediaButton(
    label:    String,
    modifier: Modifier = Modifier,
    onClick:  () -> Unit
) {
    OutlinedButton(
        onClick        = onClick,
        modifier       = modifier.height(38.dp),
        shape          = RoundedCornerShape(9.dp),
        colors         = ButtonDefaults.outlinedButtonColors(
            containerColor = CardWhite,
            contentColor   = TextBrown
        ),
        border         = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.linearGradient(
                listOf(BorderTan, BorderTan)
            )
        ),
        contentPadding = PaddingValues(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 12.sp)
    }
}

@Composable
private fun DiseaseResultCard() {

    val remedies = listOf(
        "Apply Mancozeb 75% WP @ 2g/L water",
        "Remove & destroy infected leaves immediately",
        "Avoid overhead irrigation for 7 days",
        "Spray Copper Oxychloride after 10 days"
    )

    DiseaseCard {

        // Header row
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier              = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier         = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(DangerBg),
                contentAlignment = Alignment.Center
            ) {
                Text("🦠", fontSize = 20.sp)
            }
            Column {
                Text(
                    text       = "Leaf Blight Detected",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = DangerRed
                )
                Text(
                    text     = "Moderate severity",
                    fontSize = 12.sp,
                    color    = DangerRedLight
                )
            }
        }

        // Severity bar  (matches .severity-bar / .severity-fill)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(SeverityBarBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.72f)       // 72 %
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(SeverityOrange, SeverityRed)
                        )
                    )
            )
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text     = "Severity: 72% — Treatment needed within 3 days",
            fontSize = 12.sp,
            color    = Color(0xFF8A6A4A),
            modifier = Modifier.padding(bottom = 13.dp)
        )

        // Remedies header label
        Text(
            text          = "RECOMMENDED TREATMENT",
            fontSize      = 11.sp,
            fontWeight    = FontWeight.Medium,
            color         = TipLabelBrown,
            letterSpacing = 0.5.sp,
            modifier      = Modifier.padding(bottom = 7.dp)
        )

        // Remedy items  (matches .remedy-item / .remedy-dot)
        remedies.forEach { remedy ->
            RemedyItem(text = remedy)
            Spacer(Modifier.height(6.dp))
        }

        Spacer(Modifier.height(12.dp))

        // Save report button
        Button(
            onClick        = { /* save/download report */ },
            modifier       = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape          = RoundedCornerShape(11.dp),
            colors         = ButtonDefaults.buttonColors(
                containerColor = GreenPrimary,
                contentColor   = Color.White
            )
        ) {
            Text("📄  Save & Download Report", fontSize = 13.sp)
        }
    }
}

@Composable
private fun RemedyItem(text: String) {
    Row(
        verticalAlignment     = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // remedy-dot  (6×6 green circle, margin-top 5px)
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(GreenPrimary)
        )
        Text(text, fontSize = 13.sp, color = RemedyBrown)
    }
}

@Composable
private fun DiseaseCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(15.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        border    = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.linearGradient(listOf(BorderTan, BorderTan))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp), content = content)
    }
}
