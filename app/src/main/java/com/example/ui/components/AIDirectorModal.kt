package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay

private val cyanAccent = Color(0xFF00E5FF)
private val goldAccent = Color(0xFFFFD700)
private val radiantPink = Color(0xFFFF2A85)
private val neonPurple = Color(0xFFD500F9)
private val electricBlue = Color(0xFF2979FF)

data class AIDirectorRecommendations(
    val moodTitle: String = "Cinematic Golden Hour",
    val lutName: String = "Hollywood Teal & Orange",
    val speedRamp: String = "Hero Velocity Ramp",
    val audioPreset: String = "Stem Split + Vocal Clarity",
    val captionsStyle: String = "Cyberpunk Glow",
    val spatial3DPreset: String = "Volumetric Parallax",
    val watermarkClean: Boolean = true
)

@Composable
fun AIDirectorModal(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onApplyAll: (AIDirectorRecommendations) -> Unit,
    onCustomize: (AIDirectorRecommendations) -> Unit
) {
    if (!isOpen) return

    var isAnalyzing by remember { mutableStateOf(true) }
    var analysisProgress by remember { mutableFloatStateOf(0f) }
    var analysisStepText by remember { mutableStateOf("Initializing Gyro Reticle...") }
    var previewTab by remember { mutableStateOf("Split") } // "Split", "Before", "After"
    val recommendations = remember { AIDirectorRecommendations() }

    // Simulated Analysis Progress Timer
    LaunchedEffect(isOpen) {
        isAnalyzing = true
        analysisProgress = 0f
        
        val steps = listOf(
            "Scanning Video Lighting & Color Palette..." to 0.2f,
            "Detecting Audio Tempo & Vocals..." to 0.45f,
            "Analyzing Subject Motion Vectors..." to 0.70f,
            "Synthesizing Cinematic Color LUT..." to 0.90f,
            "Mood Analysis Complete!" to 1.0f
        )

        for ((stepText, targetProgress) in steps) {
            analysisStepText = stepText
            while (analysisProgress < targetProgress) {
                delay(40)
                analysisProgress += 0.03f
            }
            delay(180)
        }
        delay(300)
        isAnalyzing = false
    }

    // 3-Second Loop Timer for Before/After Preview
    var previewLoopMs by remember { mutableLongStateOf(0L) }
    LaunchedEffect(isAnalyzing) {
        if (!isAnalyzing) {
            while (true) {
                delay(50)
                previewLoopMs = (previewLoopMs + 50L) % 3000L
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            color = ObsidianBackground.copy(alpha = 0.96f),
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(radiantPink, cyanAccent, goldAccent)))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = radiantPink.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, radiantPink)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = radiantPink,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "AI Director ✨",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAnalyzing) "Smart Mood & Scene Analysis" else "Auto Enhance Recommendations",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CharcoalSurface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }

                    // Content Area (Analyzing OR Before/After Preview)
                    if (isAnalyzing) {
                        // Gyro Reticle Loading View
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            GyroReticleScanner(
                                progress = analysisProgress,
                                modifier = Modifier.size(220.dp)
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Text(
                                text = "Analyzing Mood...",
                                color = cyanAccent,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = analysisStepText,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Custom Neon Linear Progress Bar
                            Column(
                                modifier = Modifier.width(240.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LinearProgressIndicator(
                                    progress = { analysisProgress.coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = radiantPink,
                                    trackColor = CharcoalSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${(analysisProgress * 100).toInt()}%",
                                    color = goldAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // Before / After 3-Sec Auto-Play Preview View
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))

                            // Mood Detected Pill Banner
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = CharcoalSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, radiantPink.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.MovieFilter, contentDescription = null, tint = radiantPink, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Mood: ${recommendations.moodTitle}",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // View Mode Toggle (Split / Before / After)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CharcoalSurface)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Split", "Before", "After").forEach { tab ->
                                    val isSel = previewTab == tab
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSel) radiantPink else Color.Transparent)
                                            .clickable { previewTab = tab }
                                            .padding(horizontal = 16.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (tab == "Split") "Split Screen" else if (tab == "Before") "Original (Before)" else "AI Director (After ✨)",
                                            color = if (isSel) Color.White else TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 3-Sec Before / After Canvas Frame
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black)
                                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                BeforeAfterPreviewCanvas(
                                    viewMode = previewTab,
                                    loopMs = previewLoopMs,
                                    recommendations = recommendations,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Top Floating Badge Labels
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.TopCenter)
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (previewTab == "Split" || previewTab == "Before") {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.Black.copy(alpha = 0.7f)
                                        ) {
                                            Text(
                                                text = "BEFORE: RAW",
                                                color = Color.White.copy(alpha = 0.7f),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    if (previewTab == "Split" || previewTab == "After") {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = radiantPink.copy(alpha = 0.85f)
                                        ) {
                                            Text(
                                                text = "AFTER: AI DIRECTOR ✨",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                // Bottom 3-Sec Auto Play Scrubber Pill
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 12.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Black.copy(alpha = 0.8f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(radiantPink)
                                        )
                                        Text(
                                            text = "3-SEC PREVIEW • ${(previewLoopMs / 1000f).let { String.format("%.1f", it) }}s / 3.0s",
                                            color = cyanAccent,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Applied Features Summary Chips
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Auto-Applied AI Enhancements:",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    RecommendationChip(icon = Icons.Default.MovieFilter, label = recommendations.lutName, color = radiantPink, modifier = Modifier.weight(1f))
                                    RecommendationChip(icon = Icons.Default.Speed, label = recommendations.speedRamp, color = cyanAccent, modifier = Modifier.weight(1f))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    RecommendationChip(icon = Icons.Default.GraphicEq, label = recommendations.audioPreset, color = goldAccent, modifier = Modifier.weight(1f))
                                    RecommendationChip(icon = Icons.Default.ClosedCaption, label = recommendations.captionsStyle, color = electricBlue, modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons: [Apply All] or [Customize]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Customize Button
                            OutlinedButton(
                                onClick = { onCustomize(recommendations) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("ai_director_customize_btn"),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Customize ⚙️", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            // Apply All Button
                            Button(
                                onClick = { onApplyAll(recommendations) },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(48.dp)
                                    .testTag("ai_director_apply_all_btn"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = radiantPink,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Apply All ✨", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationChip(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = CharcoalSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun GyroReticleScanner(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gyro")
    val rotationOuter by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "outer_rot"
    )
    val rotationInner by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart),
        label = "inner_rot"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width * 0.44f
            val midRadius = size.width * 0.32f
            val innerRadius = size.width * 0.20f

            // Outer Rotating Gyro Ring
            drawArc(
                color = cyanAccent.copy(alpha = 0.8f),
                startAngle = rotationOuter,
                sweepAngle = 260f,
                useCenter = false,
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                size = Size(outerRadius * 2, outerRadius * 2),
                style = Stroke(width = 3.dp.toPx())
            )

            // Outer Dashed Accent Ring
            drawArc(
                color = radiantPink,
                startAngle = rotationOuter + 280f,
                sweepAngle = 60f,
                useCenter = false,
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                size = Size(outerRadius * 2, outerRadius * 2),
                style = Stroke(width = 4.dp.toPx())
            )

            // Inner Counter-Rotating Ring
            drawArc(
                color = goldAccent,
                startAngle = rotationInner,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(center.x - midRadius, center.y - midRadius),
                size = Size(midRadius * 2, midRadius * 2),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            )

            // Crosshair Laser Reticle Lines
            drawLine(
                color = radiantPink.copy(alpha = pulseAlpha),
                start = Offset(center.x - outerRadius - 15f, center.y),
                end = Offset(center.x + outerRadius + 15f, center.y),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = radiantPink.copy(alpha = pulseAlpha),
                start = Offset(center.x, center.y - outerRadius - 15f),
                end = Offset(center.x, center.y + outerRadius + 15f),
                strokeWidth = 1.5.dp.toPx()
            )

            // Center Pulsing Core Circle
            drawCircle(
                color = cyanAccent.copy(alpha = pulseAlpha),
                radius = innerRadius * (0.8f + progress * 0.2f),
                center = center
            )
            drawCircle(
                color = radiantPink,
                radius = 6.dp.toPx(),
                center = center
            )
        }

        // Center Sparkle Badge
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun BeforeAfterPreviewCanvas(
    viewMode: String,
    loopMs: Long,
    recommendations: AIDirectorRecommendations,
    modifier: Modifier = Modifier
) {
    val progress = (loopMs / 3000f).coerceIn(0f, 1f)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Simulated Video Base Canvas (Horizon Landscape with Sun & Particles)
        val splitX = when (viewMode) {
            "Before" -> w
            "After" -> 0f
            else -> w * 0.5f // Split Screen
        }

        // --- DRAW BEFORE SIDE (RAW ORIGINAL) ---
        clipRect(left = 0f, top = 0f, right = splitX, bottom = h) {
            // Raw Flat Colors
            drawRect(Color(0xFF2C3539)) // Flat grayish background
            
            // Raw Sun
            drawCircle(
                color = Color(0xFFE0C068),
                radius = 45.dp.toPx(),
                center = Offset(w * 0.5f, h * 0.35f)
            )

            // Raw Horizon Hills
            val rawPath = Path().apply {
                moveTo(0f, h * 0.65f)
                quadraticBezierTo(w * 0.25f, h * 0.55f, w * 0.5f, h * 0.62f)
                quadraticBezierTo(w * 0.75f, h * 0.70f, w, h * 0.60f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(rawPath, color = Color(0xFF1E242B))
        }

        // --- DRAW AFTER SIDE (AI DIRECTOR ENHANCED ✨) ---
        clipRect(left = splitX, top = 0f, right = w, bottom = h) {
            // Enhanced Vibrant Cinematic Sky Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0826), // Cyber Night
                        Color(0xFFFF2A85), // Vibrant Sunset Pink
                        Color(0xFFFF9100)  // Golden Glow
                    )
                )
            )

            // Enhanced Glowing Sun
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFFFD700), Color(0xFFFF2A85).copy(alpha = 0f)),
                    center = Offset(w * 0.5f, h * 0.35f),
                    radius = 90.dp.toPx()
                ),
                radius = 80.dp.toPx(),
                center = Offset(w * 0.5f, h * 0.35f)
            )

            // Enhanced Hollywood Teal Silhouette Horizon
            val enhancedPath = Path().apply {
                moveTo(0f, h * 0.65f)
                quadraticBezierTo(w * 0.25f, h * 0.55f, w * 0.5f, h * 0.62f)
                quadraticBezierTo(w * 0.75f, h * 0.70f, w, h * 0.60f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                enhancedPath,
                brush = Brush.verticalGradient(listOf(Color(0xFF003840), Color(0xFF00151A)))
            )

            // Simulated Auto-Captions Banner Overlay
            val textY = h * 0.80f
            drawRect(
                color = Color.Black.copy(alpha = 0.75f),
                topLeft = Offset(w * 0.15f, textY - 18.dp.toPx()),
                size = Size(w * 0.7f, 32.dp.toPx())
            )

            // Dynamic 3-Sec Moving Particle Lights
            for (i in 0..12) {
                val px = (w * 0.1f + (i * 70f + progress * 150f) % (w * 0.8f))
                val py = (h * 0.2f + (i * 45f) % (h * 0.5f))
                drawCircle(
                    color = cyanAccent.copy(alpha = 0.8f),
                    radius = 3.dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }

        // --- DRAW SPLIT SCREEN SEPARATOR LINE & LASER DRAGGER ---
        if (viewMode == "Split") {
            drawLine(
                color = Color.White,
                start = Offset(splitX, 0f),
                end = Offset(splitX, h),
                strokeWidth = 2.5.dp.toPx()
            )

            // Center Split Handle Circle
            drawCircle(
                color = radiantPink,
                radius = 12.dp.toPx(),
                center = Offset(splitX, h * 0.5f)
            )
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = Offset(splitX, h * 0.5f)
            )
        }
    }
}
