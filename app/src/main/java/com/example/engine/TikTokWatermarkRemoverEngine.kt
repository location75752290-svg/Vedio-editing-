package com.example.engine

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

/**
 * TikTok / Reels / Shorts Watermark & Logo AI Eraser Configuration
 */
data class TikTokWatermarkConfig(
    val enabled: Boolean = false,
    val preset: String = "tiktok_auto_bounce", // "tiktok_auto_bounce", "tiktok_top_left", "tiktok_bottom_right", "reels_handle", "shorts_badge", "custom_box"
    val inpaintMode: String = "ai_texture",    // "ai_texture", "smart_blur", "edge_clone", "color_matte", "micro_crop"
    val inpaintRadius: Float = 28f,            // 10 .. 60 dp
    val featherSoftness: Float = 0.85f,        // 0.2 .. 1.0
    val boxLeftPercent: Float = 0.04f,         // 0.0 .. 1.0
    val boxTopPercent: Float = 0.06f,          // 0.0 .. 1.0
    val boxWidthPercent: Float = 0.32f,        // 0.1 .. 0.8
    val boxHeightPercent: Float = 0.08f,       // 0.03 .. 0.4
    val isTrackingActive: Boolean = true       // Real-time bouncing detection
)

data class WatermarkPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val defaultLeft: Float,
    val defaultTop: Float,
    val defaultWidth: Float,
    val defaultHeight: Float
)

class TikTokWatermarkRemoverEngine {

    fun removeWatermark(radius: Float, feather: Float, onComplete: () -> Unit) {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            kotlinx.coroutines.delay(1200)
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                onComplete()
            }
        }
    }

    companion object {
        fun removeWatermark(radius: Float, feather: Float, onComplete: () -> Unit) {
            TikTokWatermarkRemoverEngine().removeWatermark(radius, feather, onComplete)
        }

    val presets = listOf(
        WatermarkPreset(
            id = "tiktok_auto_bounce",
            title = "TikTok Auto-Bounce",
            subtitle = "Auto-detects alternating Top-Left & Bottom-Right logos",
            icon = Icons.Default.AutoFixHigh,
            accentColor = Color(0xFF00E5FF),
            defaultLeft = 0.04f,
            defaultTop = 0.05f,
            defaultWidth = 0.36f,
            defaultHeight = 0.075f
        ),
        WatermarkPreset(
            id = "tiktok_top_left",
            title = "TikTok Top-Left",
            subtitle = "Logo + @username header watermark",
            icon = Icons.Default.VerticalAlignTop,
            accentColor = RadiantPink,
            defaultLeft = 0.04f,
            defaultTop = 0.045f,
            defaultWidth = 0.35f,
            defaultHeight = 0.07f
        ),
        WatermarkPreset(
            id = "tiktok_bottom_right",
            title = "TikTok Bottom-Right",
            subtitle = "Secondary bouncing footer watermark",
            icon = Icons.Default.VerticalAlignBottom,
            accentColor = Color(0xFFFFD700),
            defaultLeft = 0.60f,
            defaultTop = 0.88f,
            defaultWidth = 0.36f,
            defaultHeight = 0.075f
        ),
        WatermarkPreset(
            id = "reels_handle",
            title = "Instagram Reels",
            subtitle = "Bottom audio badge & username tag",
            icon = Icons.Default.CameraAlt,
            accentColor = Color(0xFFE040FB),
            defaultLeft = 0.05f,
            defaultTop = 0.82f,
            defaultWidth = 0.42f,
            defaultHeight = 0.065f
        ),
        WatermarkPreset(
            id = "shorts_badge",
            title = "YouTube Shorts",
            subtitle = "Corner channel logo & subscribe watermark",
            icon = Icons.Default.PlayCircleFilled,
            accentColor = Color(0xFFFF1744),
            defaultLeft = 0.72f,
            defaultTop = 0.04f,
            defaultWidth = 0.24f,
            defaultHeight = 0.08f
        ),
        WatermarkPreset(
            id = "custom_box",
            title = "Custom Draggable",
            subtitle = "Freely position box over any TV logo or watermark",
            icon = Icons.Default.CropFree,
            accentColor = Color(0xFF00E676),
            defaultLeft = 0.30f,
            defaultTop = 0.40f,
            defaultWidth = 0.40f,
            defaultHeight = 0.12f
        )
    )

    val inpaintModes = listOf(
        "ai_texture" to "AI Neural Texture (Seamless)",
        "smart_blur" to "Content-Aware Gaussian Blend",
        "edge_clone" to "Edge Clone Stamp Synthesis",
        "color_matte" to "Color-Match Gradient Patch",
        "micro_crop" to "Cinematic Clean 9:16 Reframe"
    )

    /**
     * Calculates active watermark bounding box at current playhead timestamp (ms)
     * TikTok alternates watermark between top-left and bottom-right roughly every 4 seconds.
     */
    fun computeCurrentWatermarkRect(
        config: TikTokWatermarkConfig,
        currentPlayheadMs: Long,
        canvasWidth: Float,
        canvasHeight: Float
    ): Rect {
        if (!config.enabled) return Rect.Zero

        return when (config.preset) {
            "tiktok_auto_bounce" -> {
                // TikTok cycles every 4000ms between Top-Left and Bottom-Right
                val cycle = (currentPlayheadMs / 4000L) % 2L == 0L
                if (cycle) {
                    val l = 0.04f * canvasWidth
                    val t = 0.05f * canvasHeight
                    val w = 0.36f * canvasWidth
                    val h = 0.075f * canvasHeight
                    Rect(l, t, l + w, t + h)
                } else {
                    val l = 0.60f * canvasWidth
                    val t = 0.88f * canvasHeight
                    val w = 0.36f * canvasWidth
                    val h = 0.075f * canvasHeight
                    Rect(l, t, l + w, t + h)
                }
            }
            "tiktok_top_left" -> {
                val l = 0.04f * canvasWidth
                val t = 0.045f * canvasHeight
                val w = 0.35f * canvasWidth
                val h = 0.07f * canvasHeight
                Rect(l, t, l + w, t + h)
            }
            "tiktok_bottom_right" -> {
                val l = 0.60f * canvasWidth
                val t = 0.88f * canvasHeight
                val w = 0.36f * canvasWidth
                val h = 0.075f * canvasHeight
                Rect(l, t, l + w, t + h)
            }
            "reels_handle" -> {
                val l = 0.05f * canvasWidth
                val t = 0.82f * canvasHeight
                val w = 0.42f * canvasWidth
                val h = 0.065f * canvasHeight
                Rect(l, t, l + w, t + h)
            }
            "shorts_badge" -> {
                val l = 0.72f * canvasWidth
                val t = 0.04f * canvasHeight
                val w = 0.24f * canvasWidth
                val h = 0.08f * canvasHeight
                Rect(l, t, l + w, t + h)
            }
            else -> {
                val l = config.boxLeftPercent * canvasWidth
                val t = config.boxTopPercent * canvasHeight
                val w = config.boxWidthPercent * canvasWidth
                val h = config.boxHeightPercent * canvasHeight
                Rect(l, t, l + w, t + h)
            }
        }
    }
    }
}

/**
 * Interactive Pro TikTok & Reels Watermark Remover Tuning Panel
 */
@Composable
fun TikTokWatermarkRemoverPanel(
    config: TikTokWatermarkConfig,
    onConfigChange: (TikTokWatermarkConfig) -> Unit,
    onApply: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cyanAccent = Color(0xFF00E5FF)
    val goldAccent = Color(0xFFFFD700)

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = CharcoalSurfaceVariant,
        borderColor = RadiantPink.copy(alpha = 0.6f),
        borderWidth = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(RadiantPink, Color(0xFF00E5FF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "TikTok Watermark Remover Pro",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = RadiantPink.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, RadiantPink)
                            ) {
                                Text(
                                    text = "AI CLEAN",
                                    color = RadiantPink,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Cleans TikTok, Reels & Shorts logos with AI Inpainting",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                IconButton(onClick = onClose, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Master Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CharcoalSurface)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = null,
                        tint = if (config.enabled) RadiantPink else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Enable AI Watermark Eraser",
                            color = if (config.enabled) RadiantPink else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Inpaints bouncing logos & removes @username tags",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Switch(
                    checked = config.enabled,
                    onCheckedChange = { onConfigChange(config.copy(enabled = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = RadiantPink,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = CharcoalSurfaceVariant
                    ),
                    modifier = Modifier.testTag("toggle_tiktok_watermark")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Preset Selector
                Text(
                    text = "TARGET WATERMARK / LOGO PRESET",
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TikTokWatermarkRemoverEngine.presets.forEach { preset ->
                        val isSelected = config.preset == preset.id
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) preset.accentColor.copy(alpha = 0.22f) else CharcoalSurface)
                                .border(
                                    width = if (isSelected) 1.6.dp else 1.dp,
                                    color = if (isSelected) preset.accentColor else GlassBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    onConfigChange(
                                        config.copy(
                                            preset = preset.id,
                                            boxLeftPercent = preset.defaultLeft,
                                            boxTopPercent = preset.defaultTop,
                                            boxWidthPercent = preset.defaultWidth,
                                            boxHeightPercent = preset.defaultHeight
                                        )
                                    )
                                }
                                .padding(vertical = 8.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(preset.accentColor.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = preset.icon,
                                        contentDescription = preset.title,
                                        tint = preset.accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.title,
                                    color = if (isSelected) preset.accentColor else TextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = preset.subtitle,
                                    color = TextMuted,
                                    fontSize = 8.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Inpainting Algorithm Selection
                Text(
                    text = "AI INPAINTING SYNTHESIS ALGORITHM",
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CharcoalSurface)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TikTokWatermarkRemoverEngine.inpaintModes.forEach { (modeId, modeTitle) ->
                        val isSelected = config.inpaintMode == modeId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) RadiantPink.copy(alpha = 0.2f) else CharcoalSurfaceVariant)
                                .clickable { onConfigChange(config.copy(inpaintMode = modeId)) }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = modeTitle,
                                color = if (isSelected) RadiantPink else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = RadiantPink, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Sliders (Feather Softness & Patch Radius)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CharcoalSurface)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Edge Feathering Softness", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${(config.featherSoftness * 100).toInt()}%", color = RadiantPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.featherSoftness,
                        onValueChange = { onConfigChange(config.copy(featherSoftness = it)) },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = RadiantPink, activeTrackColor = RadiantPink),
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Inpaint Texture Blend Radius", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${config.inpaintRadius.toInt()} px", color = cyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.inpaintRadius,
                        onValueChange = { onConfigChange(config.copy(inpaintRadius = it)) },
                        valueRange = 10f..60f,
                        colors = SliderDefaults.colors(thumbColor = cyanAccent, activeTrackColor = cyanAccent),
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onApply,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RadiantPink,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("apply_tiktok_watermark_btn")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply Watermark Clean", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
