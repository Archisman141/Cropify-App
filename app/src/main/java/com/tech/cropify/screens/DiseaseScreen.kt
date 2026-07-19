package com.tech.cropify.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── Brand colours ─────────────────────────────────────────────────────────────
private val BgCream        = Color(0xFFF5F0E8)
private val GreenDark      = Color(0xFF1E4010)
private val GreenPrimary   = Color(0xFF2D5E1A)
private val GreenLight     = Color(0xFF4A8A30)
private val GreenPale      = Color(0xFFE8F5E1)
private val BorderTan      = Color(0xFFE0D8C8)
private val DashBorder     = Color(0xFFC4B880)
private val TextDark       = Color(0xFF2A2010)
private val TextBrown      = Color(0xFF4A3A1E)
val TextMuted              = Color(0xFF8A7A5A)
private val CardWhite      = Color.White
private val DangerBg       = Color(0xFFFEE8E8)
private val DangerRed      = Color(0xFF8A2020)
private val DangerRedLight = Color(0xFFB06060)
private val SeverityOrange = Color(0xFFE8A040)
private val SeverityRed    = Color(0xFFC43030)
private val SeverityBarBg  = Color(0xFFF0E8E8)
private val RemedyBrown    = Color(0xFF3A2A10)
private val TipLabelBrown  = Color(0xFF5A4A2E)
private val SuccessGreen   = Color(0xFF2D7A1F)
private val SuccessBg      = Color(0xFFE8F5E1)

// ── Disease data model ────────────────────────────────────────────────────────
private data class DiseaseResult(
    val name: String,
    val severity: Float,
    val severityLabel: String,
    val remedies: List<String>
)

private val mockResult = DiseaseResult(
    name          = "Leaf Blight Detected",
    severity      = 0.72f,
    severityLabel = "Severity: 72% — Treatment needed within 3 days",
    remedies      = listOf(
        "Apply Mancozeb 75% WP @ 2g/L water",
        "Remove & destroy infected leaves immediately",
        "Avoid overhead irrigation for 7 days",
        "Spray Copper Oxychloride after 10 days"
    )
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun DiseaseScreen(
    navController: NavHostController,
    bottomNavController: NavHostController
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    var selectedPart      by remember { mutableStateOf("Leaf") }
    var showResult        by remember { mutableStateOf(false) }
    var capturedImageUri  by remember { mutableStateOf<Uri?>(null) }
    var showSuccessBanner by remember { mutableStateOf(false) }

    // ── Camera URI ────────────────────────────────────────────────────────────
    val photoFile = remember {
        File(context.cacheDir, "images").also { it.mkdirs() }
            .let { File(it, "disease_scan_${System.currentTimeMillis()}.jpg") }
    }
    val photoUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            photoFile
        )
    }

    // ── Camera launcher ───────────────────────────────────────────────────────
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedImageUri = photoUri
            showResult       = true
        }
    }

    // ── Camera permission launcher ────────────────────────────────────────────
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(photoUri)
        } else {
            Toast.makeText(
                context,
                "Camera permission is required to scan plants",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun launchCamera() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                cameraLauncher.launch(photoUri)
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // ── Gallery launcher ──────────────────────────────────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            capturedImageUri = uri
            showResult       = true
        }
    }

    val plantParts = listOf(
        "🍃" to "Leaf",
        "🌿" to "Stem",
        "🌸" to "Flower",
        "🍎" to "Fruit"
    )

    Scaffold(
        topBar         = { DiseaseTopBar(onBack = { navController.popBackStack() }) },
        containerColor = BgCream
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                DiseasePageHeader()

                Column(modifier = Modifier.padding(14.dp)) {

                    // ── Plant part selector ───────────────────────────────────
                    DiseaseCard {
                        SectionLabel("Select Plant Part")
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(9.dp)
                        ) {
                            plantParts.forEach { (icon, label) ->
                                PlantPartChip(
                                    icon     = icon,
                                    label    = label,
                                    selected = selectedPart == label,
                                    modifier = Modifier.weight(1f),
                                    onClick  = { selectedPart = label }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // ── Upload zone ───────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(17.dp))
                            .background(CardWhite)
                            .border(2.5.dp, DashBorder, RoundedCornerShape(17.dp))
                            .clickable { launchCamera() }   // ← permission-safe
                            .padding(horizontal = 18.dp, vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (capturedImageUri != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AsyncImage(
                                    model              = capturedImageUri,
                                    contentDescription = "Scanned plant",
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    "Tap to retake photo",
                                    fontSize = 12.sp,
                                    color    = TextMuted
                                )
                            }
                        } else {
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
                                    onClick        = { launchCamera() },   // ← permission-safe
                                    shape          = RoundedCornerShape(9.dp),
                                    colors         = ButtonDefaults.buttonColors(
                                        containerColor = GreenPrimary,
                                        contentColor   = Color.White
                                    ),
                                    contentPadding = PaddingValues(
                                        horizontal = 22.dp,
                                        vertical   = 9.dp
                                    )
                                ) {
                                    Text("📸  Scan Now", fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // ── Gallery / Camera row ──────────────────────────────────
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MediaButton(
                            label    = "📁  Gallery",
                            modifier = Modifier.weight(1f),
                            onClick  = { galleryLauncher.launch("image/*") }
                        )
                        MediaButton(
                            label    = "📷  Camera",
                            modifier = Modifier.weight(1f),
                            onClick  = { launchCamera() }   // ← permission-safe
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // ── Result card ───────────────────────────────────────────
                    AnimatedVisibility(
                        visible = showResult,
                        enter   = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
                    ) {
                        DiseaseResultCard(
                            result       = mockResult,
                            imageUri     = capturedImageUri,
                            onSaveReport = {
                                val saved = saveDiseaseReportPdf(context, mockResult)
                                if (saved) {
                                    showSuccessBanner = true
                                    scope.launch {
                                        delay(3500)
                                        showSuccessBanner = false
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to save report. Try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                }
            }

            // ── Success banner ────────────────────────────────────────────────
            AnimatedVisibility(
                visible  = showSuccessBanner,
                enter    = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 72.dp, start = 16.dp, end = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SuccessBg)
                        .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("✅", fontSize = 20.sp)
                        Column {
                            Text(
                                "Report Saved!",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = SuccessGreen
                            )
                            Text(
                                "Check your Downloads folder",
                                fontSize = 12.sp,
                                color    = SuccessGreen.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── PDF generation ────────────────────────────────────────────────────────────
private fun saveDiseaseReportPdf(context: Context, result: DiseaseResult): Boolean {
    return try {
        val dateStr  = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val fileName = "Cropify_DiseaseReport_${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        }.pdf"

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page     = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // ── Paints ────────────────────────────────────────────────────────────
        val headerPaint = Paint().apply {
            color    = android.graphics.Color.parseColor("#1E4010")
            textSize = 26f
            isFakeBoldText = true
        }
        val titlePaint = Paint().apply {
            color    = android.graphics.Color.parseColor("#8A2020")
            textSize = 18f
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply {
            color    = android.graphics.Color.parseColor("#3A2A10")
            textSize = 13f
        }
        val mutedPaint = Paint().apply {
            color    = android.graphics.Color.parseColor("#8A7A5A")
            textSize = 11f
        }
        val labelPaint = Paint().apply {
            color    = android.graphics.Color.parseColor("#2D5E1A")
            textSize = 12f
            isFakeBoldText = true
        }
        val bgPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1E4010")
        }
        val linePaint = Paint().apply {
            color       = android.graphics.Color.parseColor("#E0D8C8")
            strokeWidth = 1f
        }

        // ── Header block ──────────────────────────────────────────────────────
        canvas.drawRect(0f, 0f, 595f, 80f, bgPaint)
        canvas.drawText("🌿 Cropify", 36f, 38f, headerPaint.apply { color = android.graphics.Color.WHITE })
        canvas.drawText("Disease Detection Report", 36f, 62f, mutedPaint.apply {
            color    = android.graphics.Color.parseColor("#B0C8A0")
            textSize = 12f
        })

        var y = 110f

        // ── Date ──────────────────────────────────────────────────────────────
        canvas.drawText("Generated: $dateStr", 36f, y, mutedPaint)
        y += 24f
        canvas.drawLine(36f, y, 559f, y, linePaint)
        y += 20f

        // ── Disease name ──────────────────────────────────────────────────────
        canvas.drawText(result.name, 36f, y, titlePaint)
        y += 22f
        canvas.drawText("Moderate severity · Immediate treatment recommended", 36f, y, mutedPaint.apply {
            color    = android.graphics.Color.parseColor("#B06060")
            textSize = 11f
        })
        y += 30f

        // ── Severity ──────────────────────────────────────────────────────────
        canvas.drawText("SEVERITY", 36f, y, labelPaint)
        y += 16f

        // Severity bar background
        val barBgPaint = Paint().apply { color = android.graphics.Color.parseColor("#F0E8E8") }
        canvas.drawRoundRect(36f, y, 559f, y + 12f, 6f, 6f, barBgPaint)

        // Severity bar fill
        val fillPaint = Paint().apply { color = android.graphics.Color.parseColor("#C43030") }
        canvas.drawRoundRect(
            36f, y,
            36f + (523f * result.severity), y + 12f,
            6f, 6f, fillPaint
        )
        y += 22f
        canvas.drawText(result.severityLabel, 36f, y, mutedPaint.apply {
            color    = android.graphics.Color.parseColor("#8A6A4A")
            textSize = 11f
        })
        y += 30f

        // ── Divider ───────────────────────────────────────────────────────────
        canvas.drawLine(36f, y, 559f, y, linePaint)
        y += 20f

        // ── Remedies ──────────────────────────────────────────────────────────
        canvas.drawText("RECOMMENDED TREATMENT", 36f, y, labelPaint)
        y += 20f

        result.remedies.forEach { remedy ->
            // Bullet dot
            val dotPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#2D5E1A")
                isAntiAlias = true
            }
            canvas.drawCircle(44f, y - 4f, 4f, dotPaint)
            canvas.drawText(remedy, 58f, y, bodyPaint.apply {
                color    = android.graphics.Color.parseColor("#3A2A10")
                textSize = 13f
            })
            y += 24f
        }

        y += 16f
        canvas.drawLine(36f, y, 559f, y, linePaint)
        y += 20f

        // ── Footer ────────────────────────────────────────────────────────────
        canvas.drawText(
            "This report was generated by Cropify · Built for Indian Farmers 🌾",
            36f, y,
            mutedPaint.apply {
                color    = android.graphics.Color.parseColor("#8A7A5A")
                textSize = 10f
            }
        )

        document.finishPage(page)

        // ── Save via MediaStore (API 29+) or direct file (API 28-) ────────────
        val outputStream: OutputStream?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
            )
            outputStream = uri?.let { context.contentResolver.openOutputStream(it) }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            downloadsDir.mkdirs()
            outputStream = File(downloadsDir, fileName).outputStream()
        }

        outputStream?.use { document.writeTo(it) }
        document.close()

        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────
@Composable
private fun DiseaseTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .border(1.dp, BorderTan, RoundedCornerShape(0.dp))
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

// ── Page header ───────────────────────────────────────────────────────────────
@Composable
private fun DiseasePageHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(GreenDark, GreenLight)))
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

// ── Plant part chip ───────────────────────────────────────────────────────────
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
                width = 1.5.dp,
                color = if (selected) GreenLight else BorderTan,
                shape = RoundedCornerShape(11.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextBrown)
    }
}

// ── Media button ──────────────────────────────────────────────────────────────
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
            brush = Brush.linearGradient(listOf(BorderTan, BorderTan))
        ),
        contentPadding = PaddingValues(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 12.sp)
    }
}

// ── Disease result card ───────────────────────────────────────────────────────
@Composable
private fun DiseaseResultCard(
    result: DiseaseResult,
    imageUri: Uri?,
    onSaveReport: () -> Unit
) {
    DiseaseCard {

        // ── Captured image preview inside result card ─────────────────────────
        if (imageUri != null) {
            AsyncImage(
                model              = imageUri,
                contentDescription = "Scanned plant",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(Modifier.height(12.dp))
        }

        // ── Header row ────────────────────────────────────────────────────────
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
                    text       = result.name,
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

        // ── Severity bar ──────────────────────────────────────────────────────
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
                    .fillMaxWidth(result.severity)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(listOf(SeverityOrange, SeverityRed))
                    )
            )
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text     = result.severityLabel,
            fontSize = 12.sp,
            color    = Color(0xFF8A6A4A),
            modifier = Modifier.padding(bottom = 13.dp)
        )

        // ── Remedies ──────────────────────────────────────────────────────────
        Text(
            text          = "RECOMMENDED TREATMENT",
            fontSize      = 11.sp,
            fontWeight    = FontWeight.Medium,
            color         = TipLabelBrown,
            letterSpacing = 0.5.sp,
            modifier      = Modifier.padding(bottom = 7.dp)
        )

        result.remedies.forEach { remedy ->
            RemedyItem(text = remedy)
            Spacer(Modifier.height(6.dp))
        }

        Spacer(Modifier.height(12.dp))

        // ── Save report button ────────────────────────────────────────────────
        Button(
            onClick        = onSaveReport,
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

// ── Remedy item ───────────────────────────────────────────────────────────────
@Composable
private fun RemedyItem(text: String) {
    Row(
        verticalAlignment     = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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

// ── Section label ─────────────────────────────────────────────────────────────
//@Composable
//private fun SectionLabel(text: String) {
//    Text(
//        text          = text,
//        fontSize      = 13.sp,
//        fontWeight    = FontWeight.SemiBold,
//        color         = TextBrown,
//        letterSpacing = 0.3.sp
//    )
//}

// ── Disease card container ────────────────────────────────────────────────────
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