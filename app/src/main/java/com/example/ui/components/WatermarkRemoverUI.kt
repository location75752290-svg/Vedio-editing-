package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.TikTokWatermarkRemoverEngine
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.RadiantPink
import com.example.ui.theme.TextSecondary

private val cyanAccent = Color(0xFF00E5FF)

fun polarToCartesian(angleDegrees: Float, radius: Float): Offset {
    val angleRad = Math.toRadians(angleDegrees.toDouble()).toFloat()
    val x = radius * kotlin.math.cos(angleRad)
    val y = radius * kotlin.math.sin(angleRad)
    return Offset(x, y)
}

@Composable
fun androidx.compose.animation.core.InfiniteTransition.animateFloat(
    initialValue: Float,
    targetValue: Float,
    durationMillis: Int
): State<Float> = animateFloat(
    initialValue = initialValue,
    targetValue = targetValue,
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    ),
    label = "LaserAngle"
)

@Composable
fun LiveVideoPlayer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Videocam,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.2f),
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("${(value * 100).toInt()}%", color = cyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        androidx.compose.material3.Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = cyanAccent,
                activeTrackColor = cyanAccent
            )
        )
    }
}

// Laser Effect
@Composable
fun LaserSweepReticle() {
    val angle = rememberInfiniteTransition().animateFloat(0f, 360f, 2000)
    Canvas(Modifier.fillMaxSize()) {
        drawLine(
            color = Color.Cyan,
            start = center,
            end = center + polarToCartesian(angle.value, 200f),
            strokeWidth = 3f
        )
        drawCircle(
            color = Color.Cyan.copy(alpha = 0.3f),
            radius = 160f,
            center = center,
            style = Stroke(width = 2f)
        )
        drawCircle(
            color = Color.Cyan,
            radius = 8f,
            center = center
        )
    }
}

@Composable
fun WatermarkRemoverUI(
    engine: TikTokWatermarkRemoverEngine = remember { TikTokWatermarkRemoverEngine() }
) {
    var feather by remember { mutableStateOf(0.5f) }
    var radius by remember { mutableStateOf(0.3f) }
    var isScanning by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Video with Laser Reticle
        LiveVideoPlayer()
        if (isScanning) {
            LaserSweepReticle() // Animated crosshair
        }

        // 2. Control Panel Bottom Sheet
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = CharcoalSurface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = RadiantPink)
                    Text("🧹 AI Watermark Cleaner", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Slider(value = radius, onValueChange = { radius = it }, label = "Radius")
                Slider(value = feather, onValueChange = { feather = it }, label = "Feather Softness")

                Button(
                    onClick = {
                        isScanning = true
                        engine.removeWatermark(radius, feather) { isScanning = false }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("clean_now_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cyanAccent,
                        contentColor = Color.Black
                    )
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cleaning Watermark...", fontWeight = FontWeight.Bold)
                    } else {
                        Text("✨ Clean Now", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
