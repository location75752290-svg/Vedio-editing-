package com.example.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.RadiantPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Normalized 0.0..1.0 coordinate for resolution-independent sketch paths on videos.
 */
data class SketchPoint(
    val xRatio: Float,
    val yRatio: Float
)

enum class SketchBrushType(val displayName: String, val icon: ImageVector) {
    NEON_GLOW("Neon Glow", Icons.Default.AutoAwesome),
    PEN("Pen / Ink", Icons.Default.Edit),
    HIGHLIGHTER("Marker", Icons.Default.Highlight),
    ARROW("Arrow", Icons.Default.NearMe),
    ERASER("Eraser", Icons.Default.Delete)
}

data class SketchStroke(
    val points: List<SketchPoint>,
    val color: Color = Color(0xFFFF007A),
    val strokeWidthDp: Float = 6f,
    val brushType: SketchBrushType = SketchBrushType.NEON_GLOW
)

data class VideoSketchItem(
    val id: String = UUID.randomUUID().toString(),
    val startTimeMs: Long = 0L,
    val durationMs: Long = 3000L, // Default: exactly 3.0 seconds as requested!
    val strokes: List<SketchStroke> = emptyList()
) {
    val endTimeMs: Long
        get() = startTimeMs + durationMs

    fun isActiveAt(timeMs: Long): Boolean {
        return timeMs in startTimeMs..endTimeMs
    }
}

object VideoSketchEngine {

    val PRESET_COLORS = listOf(
        Color(0xFFFF007A), // Neon Pink
        Color(0xFF00E5FF), // Electric Cyan
        Color(0xFFFFD700), // Gold Yellow
        Color(0xFFFFFFFF), // Pure White
        Color(0xFFFF334B), // Vivid Red
        Color(0xFF00E676), // Lime Green
        Color(0xFFB388FF), // Neon Purple
        Color(0xFFFF9100)  // Sunset Orange
    )

    /**
     * Renders all active sketches at [currentMs] onto a Bitmap for Media3 video export pipeline.
     */
    fun renderSketchBitmap(
        sketches: List<VideoSketchItem>,
        currentMs: Long,
        width: Int = 1080,
        height: Int = 1920
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val activeSketches = sketches.filter { it.isActiveAt(currentMs) }
        if (activeSketches.isEmpty()) {
            return bitmap
        }

        for (sketch in activeSketches) {
            val elapsed = currentMs - sketch.startTimeMs
            val remaining = sketch.endTimeMs - currentMs

            // Smooth fade in (first 250ms) and fade out (last 250ms)
            val alphaMultiplier = when {
                elapsed < 250L -> (elapsed / 250f).coerceIn(0.1f, 1.0f)
                remaining < 250L -> (remaining / 250f).coerceIn(0.1f, 1.0f)
                else -> 1.0f
            }

            for (stroke in sketch.strokes) {
                if (stroke.points.size < 2) continue
                drawStrokeOnCanvas(canvas, stroke, width, height, alphaMultiplier)
            }
        }

        return bitmap
    }

    private fun drawStrokeOnCanvas(
        canvas: Canvas,
        stroke: SketchStroke,
        width: Int,
        height: Int,
        alphaMultiplier: Float
    ) {
        val density = width / 360f
        val baseWidthPx = stroke.strokeWidthDp * density

        val path = Path()
        val first = stroke.points.first()
        path.moveTo(first.xRatio * width, first.yRatio * height)

        for (i in 1 until stroke.points.size) {
            val prev = stroke.points[i - 1]
            val curr = stroke.points[i]
            val midX = (prev.xRatio + curr.xRatio) / 2f * width
            val midY = (prev.yRatio + curr.yRatio) / 2f * height
            path.quadTo(prev.xRatio * width, prev.yRatio * height, midX, midY)
        }
        val last = stroke.points.last()
        path.lineTo(last.xRatio * width, last.yRatio * height)

        val baseColor = stroke.color
        val alphaInt = (baseColor.alpha * alphaMultiplier * 255).toInt().coerceIn(0, 255)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            pathEffect = CornerPathEffect(16f)
        }

        when (stroke.brushType) {
            SketchBrushType.NEON_GLOW -> {
                // Outer Glow Pass
                val glowPaint = Paint(paint).apply {
                    color = android.graphics.Color.argb((alphaInt * 0.45f).toInt(), (baseColor.red * 255).toInt(), (baseColor.green * 255).toInt(), (baseColor.blue * 255).toInt())
                    strokeWidth = baseWidthPx * 2.8f
                }
                canvas.drawPath(path, glowPaint)

                // Mid Glow Pass
                val midPaint = Paint(paint).apply {
                    color = android.graphics.Color.argb((alphaInt * 0.75f).toInt(), (baseColor.red * 255).toInt(), (baseColor.green * 255).toInt(), (baseColor.blue * 255).toInt())
                    strokeWidth = baseWidthPx * 1.5f
                }
                canvas.drawPath(path, midPaint)

                // White/Core Center Pass
                val corePaint = Paint(paint).apply {
                    color = android.graphics.Color.argb(alphaInt, 255, 255, 255)
                    strokeWidth = (baseWidthPx * 0.6f).coerceAtLeast(2f)
                }
                canvas.drawPath(path, corePaint)
            }
            SketchBrushType.HIGHLIGHTER -> {
                paint.color = android.graphics.Color.argb((alphaInt * 0.40f).toInt(), (baseColor.red * 255).toInt(), (baseColor.green * 255).toInt(), (baseColor.blue * 255).toInt())
                paint.strokeWidth = baseWidthPx * 2.4f
                canvas.drawPath(path, paint)
            }
            SketchBrushType.ARROW -> {
                paint.color = android.graphics.Color.argb(alphaInt, (baseColor.red * 255).toInt(), (baseColor.green * 255).toInt(), (baseColor.blue * 255).toInt())
                paint.strokeWidth = baseWidthPx
                canvas.drawPath(path, paint)

                // Draw arrowhead on the last point
                if (stroke.points.size >= 2) {
                    val p2 = stroke.points.last()
                    val p1 = stroke.points[stroke.points.size - 2]
                    val x2 = p2.xRatio * width
                    val y2 = p2.yRatio * height
                    val x1 = p1.xRatio * width
                    val y1 = p1.yRatio * height

                    val angle = atan2((y2 - y1).toDouble(), (x2 - x1).toDouble())
                    val arrowLen = baseWidthPx * 3.5f
                    val arrowAngle = Math.PI / 6.0

                    val xA = (x2 - arrowLen * cos(angle - arrowAngle)).toFloat()
                    val yA = (y2 - arrowLen * sin(angle - arrowAngle)).toFloat()
                    val xB = (x2 - arrowLen * cos(angle + arrowAngle)).toFloat()
                    val yB = (y2 - arrowLen * sin(angle + arrowAngle)).toFloat()

                    val headPath = Path().apply {
                        moveTo(xA, yA)
                        lineTo(x2, y2)
                        lineTo(xB, yB)
                    }
                    val headPaint = Paint(paint).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = baseWidthPx * 1.2f
                    }
                    canvas.drawPath(headPath, headPaint)
                }
            }
            else -> {
                // PEN / INK
                paint.color = android.graphics.Color.argb(alphaInt, (baseColor.red * 255).toInt(), (baseColor.green * 255).toInt(), (baseColor.blue * 255).toInt())
                paint.strokeWidth = baseWidthPx
                canvas.drawPath(path, paint)
            }
        }
    }
}

/**
 * Interactive Compose Overlay for drawing Sketches live on top of the Video.
 */
@Composable
fun VideoSketchDrawingCanvas(
    modifier: Modifier = Modifier,
    strokes: List<SketchStroke>,
    currentStrokePoints: List<SketchPoint>,
    currentColor: Color,
    currentStrokeWidthDp: Float,
    currentBrushType: SketchBrushType,
    onPointAdded: (SketchPoint) -> Unit,
    onStrokeFinished: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(currentBrushType, currentColor, currentStrokeWidthDp) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val xRatio = (offset.x / size.width).coerceIn(0f, 1f)
                        val yRatio = (offset.y / size.height).coerceIn(0f, 1f)
                        onPointAdded(SketchPoint(xRatio, yRatio))
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val xRatio = (change.position.x / size.width).coerceIn(0f, 1f)
                        val yRatio = (change.position.y / size.height).coerceIn(0f, 1f)
                        onPointAdded(SketchPoint(xRatio, yRatio))
                    },
                    onDragEnd = {
                        onStrokeFinished()
                    },
                    onDragCancel = {
                        onStrokeFinished()
                    }
                )
            }
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw already completed strokes
            for (stroke in strokes) {
                drawComposeStroke(this, stroke)
            }

            // Draw current active stroke in progress
            if (currentStrokePoints.size >= 2) {
                val activeStroke = SketchStroke(
                    points = currentStrokePoints,
                    color = currentColor,
                    strokeWidthDp = currentStrokeWidthDp,
                    brushType = currentBrushType
                )
                drawComposeStroke(this, activeStroke)
            }
        }
    }
}

/**
 * Live Playback Overlay that renders saved sketches when playback is within the 3.0s window.
 */
@Composable
fun VideoSketchPlaybackOverlay(
    modifier: Modifier = Modifier,
    sketches: List<VideoSketchItem>,
    currentPlayheadMs: Long,
    isDrawingActive: Boolean
) {
    if (sketches.isEmpty() || isDrawingActive) return

    val activeSketches = sketches.filter { it.isActiveAt(currentPlayheadMs) }
    if (activeSketches.isEmpty()) return

    androidx.compose.foundation.Canvas(modifier = modifier.fillMaxSize()) {
        for (sketch in activeSketches) {
            val elapsed = currentPlayheadMs - sketch.startTimeMs
            val remaining = sketch.endTimeMs - currentPlayheadMs

            val alphaMultiplier = when {
                elapsed < 200L -> (elapsed / 200f).coerceIn(0.1f, 1.0f)
                remaining < 200L -> (remaining / 200f).coerceIn(0.1f, 1.0f)
                else -> 1.0f
            }

            for (stroke in sketch.strokes) {
                drawComposeStroke(this, stroke, alphaMultiplier)
            }
        }
    }
}

private fun drawComposeStroke(
    scope: DrawScope,
    stroke: SketchStroke,
    alphaMultiplier: Float = 1.0f
) {
    if (stroke.points.size < 2) return

    val w = scope.size.width
    val h = scope.size.height
    val density = scope.density
    val baseWidthPx = stroke.strokeWidthDp * density

    val path = androidx.compose.ui.graphics.Path()
    val first = stroke.points.first()
    path.moveTo(first.xRatio * w, first.yRatio * h)

    for (i in 1 until stroke.points.size) {
        val prev = stroke.points[i - 1]
        val curr = stroke.points[i]
        val midX = (prev.xRatio + curr.xRatio) / 2f * w
        val midY = (prev.yRatio + curr.yRatio) / 2f * h
        path.quadraticTo(prev.xRatio * w, prev.yRatio * h, midX, midY)
    }
    val last = stroke.points.last()
    path.lineTo(last.xRatio * w, last.yRatio * h)

    val strokeCap = StrokeCap.Round
    val strokeJoin = StrokeJoin.Round
    val finalColor = stroke.color.copy(alpha = stroke.color.alpha * alphaMultiplier)

    when (stroke.brushType) {
        SketchBrushType.NEON_GLOW -> {
            // Glow layer
            scope.drawPath(
                path = path,
                color = finalColor.copy(alpha = 0.35f * alphaMultiplier),
                style = Stroke(width = baseWidthPx * 2.8f, cap = strokeCap, join = strokeJoin)
            )
            scope.drawPath(
                path = path,
                color = finalColor.copy(alpha = 0.70f * alphaMultiplier),
                style = Stroke(width = baseWidthPx * 1.5f, cap = strokeCap, join = strokeJoin)
            )
            // Center white/core
            scope.drawPath(
                path = path,
                color = Color.White.copy(alpha = alphaMultiplier),
                style = Stroke(width = (baseWidthPx * 0.6f).coerceAtLeast(2f), cap = strokeCap, join = strokeJoin)
            )
        }
        SketchBrushType.HIGHLIGHTER -> {
            scope.drawPath(
                path = path,
                color = finalColor.copy(alpha = 0.40f * alphaMultiplier),
                style = Stroke(width = baseWidthPx * 2.2f, cap = strokeCap, join = strokeJoin)
            )
        }
        SketchBrushType.ARROW -> {
            scope.drawPath(
                path = path,
                color = finalColor,
                style = Stroke(width = baseWidthPx, cap = strokeCap, join = strokeJoin)
            )
            if (stroke.points.size >= 2) {
                val p2 = stroke.points.last()
                val p1 = stroke.points[stroke.points.size - 2]
                val x2 = p2.xRatio * w
                val y2 = p2.yRatio * h
                val x1 = p1.xRatio * w
                val y1 = p1.yRatio * h

                val angle = atan2((y2 - y1).toDouble(), (x2 - x1).toDouble())
                val arrowLen = baseWidthPx * 3.2f
                val arrowAngle = Math.PI / 6.0

                val xA = (x2 - arrowLen * cos(angle - arrowAngle)).toFloat()
                val yA = (y2 - arrowLen * sin(angle - arrowAngle)).toFloat()
                val xB = (x2 - arrowLen * cos(angle + arrowAngle)).toFloat()
                val yB = (y2 - arrowLen * sin(angle + arrowAngle)).toFloat()

                val arrowHeadPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(xA, yA)
                    lineTo(x2, y2)
                    lineTo(xB, yB)
                }
                scope.drawPath(
                    path = arrowHeadPath,
                    color = finalColor,
                    style = Stroke(width = baseWidthPx * 1.2f, cap = strokeCap, join = strokeJoin)
                )
            }
        }
        else -> {
            scope.drawPath(
                path = path,
                color = finalColor,
                style = Stroke(width = baseWidthPx, cap = strokeCap, join = strokeJoin)
            )
        }
    }
}

/**
 * Bottom Control Panel for the Video Sketch Tool.
 */
@Composable
fun VideoSketchToolPanel(
    modifier: Modifier = Modifier,
    currentPlayheadMs: Long,
    strokesCount: Int,
    selectedBrushType: SketchBrushType,
    onBrushTypeChange: (SketchBrushType) -> Unit,
    selectedColor: Color,
    onColorChange: (Color) -> Unit,
    strokeWidthDp: Float,
    onStrokeWidthChange: (Float) -> Unit,
    durationMs: Long, // Default 3000L
    onDurationChange: (Long) -> Unit,
    onUndoStroke: () -> Unit,
    onClearAllStrokes: () -> Unit,
    onApplySketch: () -> Unit,
    onClose: () -> Unit
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = CharcoalSurfaceVariant,
        borderColor = RadiantPink.copy(alpha = 0.7f),
        borderWidth = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header with Title, Duration Badge & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(RadiantPink.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = null,
                            tint = RadiantPink,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Video Sketchware ✏️",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ElectricBlue.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "${durationMs / 1000}s Duration",
                                    color = ElectricBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        val startSec = currentPlayheadMs / 1000f
                        val endSec = (currentPlayheadMs + durationMs) / 1000f
                        Text(
                            text = "Starts: ${String.format("%.1fs", startSec)} ➔ Visible to ${String.format("%.1fs", endSec)}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Undo Stroke Button
                    IconButton(
                        onClick = onUndoStroke,
                        enabled = strokesCount > 0,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo Stroke",
                            tint = if (strokesCount > 0) TextPrimary else TextMuted,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Clear All Strokes
                    IconButton(
                        onClick = onClearAllStrokes,
                        enabled = strokesCount > 0,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear All",
                            tint = if (strokesCount > 0) RadiantPink else TextMuted,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Close Button
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Brush Styles Row: Neon Glow, Pen, Highlighter, Arrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SketchBrushType.values().forEach { brush ->
                    val isSelected = selectedBrushType == brush
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) RadiantPink.copy(alpha = 0.25f)
                                else CharcoalSurface
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) RadiantPink else GlassBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onBrushTypeChange(brush) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = brush.icon,
                                contentDescription = brush.displayName,
                                tint = if (isSelected) RadiantPink else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = brush.displayName,
                                color = if (isSelected) TextPrimary else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 8 Vivid Colors Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VideoSketchEngine.PRESET_COLORS.forEach { col ->
                    val isSelected = selectedColor == col
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(col)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onColorChange(col) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (col == Color.White || col == Color(0xFFFFD700)) Color.Black else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Thickness Slider & 3s Duration Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brush Thickness
                Row(
                    modifier = Modifier.weight(1.2f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Size",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Slider(
                        value = strokeWidthDp,
                        onValueChange = onStrokeWidthChange,
                        valueRange = 2f..24f,
                        colors = SliderDefaults.colors(
                            thumbColor = RadiantPink,
                            activeTrackColor = RadiantPink,
                            inactiveTrackColor = CharcoalSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Duration Selector (1s, 2s, 3s, 5s)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(1000L to "1s", 2000L to "2s", 3000L to "3s", 5000L to "5s").forEach { (dur, label) ->
                        val isSel = durationMs == dur
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSel) ElectricBlue.copy(alpha = 0.35f)
                                    else CharcoalSurface
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSel) ElectricBlue else GlassBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onDurationChange(dur) }
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) ElectricBlue else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Save / Apply Sketch Button
            Button(
                onClick = onApplySketch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RadiantPink,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (strokesCount > 0) "Apply Sketch (${durationMs / 1000}s on Video) ✓" else "Touch Video to Draw ✏️",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
