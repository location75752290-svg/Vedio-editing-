package com.example.engine

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.geometry.Size
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
import kotlin.math.*

/**
 * Newton Physics Preset Configuration for Video Clips, Overlays & Keyframe Animation
 */
data class NewtonPhysicsConfig(
    val enabled: Boolean = false,
    val presetId: String = "gravity_bounce", // gravity_bounce, spring_elastic, pendulum_swing, magnetic_snap, inertia_drift, zero_g, cradle_hit
    val gravity: Float = 9.8f,               // 1.0 .. 25.0 m/s^2
    val restitution: Float = 0.75f,          // 0.1 .. 0.95 (bounciness)
    val springTension: Float = 180f,         // 50 .. 400 (Hooke's constant k)
    val damping: Float = 0.65f,              // 0.1 .. 1.0 (friction / air resistance)
    val impactShockwave: Boolean = true,     // Shockwave screen vibration on impact
    val targetLayer: String = "All"          // "All", "Video Clip", "Text & Stickers", "Overlays"
)

data class NewtonPresetItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val gravity: Float,
    val restitution: Float,
    val springTension: Float,
    val damping: Float
)

object NewtonPhysicsEngine {

    val presets = listOf(
        NewtonPresetItem(
            id = "gravity_bounce",
            name = "Newton Gravity",
            subtitle = "Downward fall with kinetic ground rebound",
            icon = Icons.Default.VerticalAlignBottom,
            accentColor = Color(0xFF00E5FF),
            gravity = 14.0f,
            restitution = 0.78f,
            springTension = 150f,
            damping = 0.60f
        ),
        NewtonPresetItem(
            id = "spring_elastic",
            name = "Elastic Spring",
            subtitle = "Hooke's law harmonic oscillation & snap",
            icon = Icons.Default.AllInclusive,
            accentColor = RadiantPink,
            gravity = 6.0f,
            restitution = 0.88f,
            springTension = 280f,
            damping = 0.45f
        ),
        NewtonPresetItem(
            id = "pendulum_swing",
            name = "Kinetic Pendulum",
            subtitle = "Angular torque & rhythmic gravity sway",
            icon = Icons.Default.SwapHoriz,
            accentColor = Color(0xFFFFD700),
            gravity = 9.8f,
            restitution = 0.60f,
            springTension = 120f,
            damping = 0.70f
        ),
        NewtonPresetItem(
            id = "magnetic_snap",
            name = "Magnetic Snap",
            subtitle = "High-velocity inverse-square attraction",
            icon = Icons.Default.Adjust,
            accentColor = Color(0xFF00E676),
            gravity = 20.0f,
            restitution = 0.40f,
            springTension = 350f,
            damping = 0.85f
        ),
        NewtonPresetItem(
            id = "inertia_drift",
            name = "Inertial Friction",
            subtitle = "Cinematic kinetic drift with deceleration",
            icon = Icons.Default.TrendingFlat,
            accentColor = Color(0xFFFF9100),
            gravity = 3.0f,
            restitution = 0.30f,
            springTension = 80f,
            damping = 0.92f
        ),
        NewtonPresetItem(
            id = "zero_g",
            name = "Zero-G Float",
            subtitle = "Micro-gravity fluid orbital hovering",
            icon = Icons.Default.CloudQueue,
            accentColor = Color(0xFFD500F9),
            gravity = 0.8f,
            restitution = 0.95f,
            springTension = 60f,
            damping = 0.30f
        ),
        NewtonPresetItem(
            id = "cradle_hit",
            name = "Newton's Cradle",
            subtitle = "Elastic momentum collision & shockwave hit",
            icon = Icons.Default.FlashOn,
            accentColor = Color(0xFFFF1744),
            gravity = 18.0f,
            restitution = 0.92f,
            springTension = 220f,
            damping = 0.50f
        )
    )

    /**
     * Compute instantaneous 2D displacement & scale transformation using Newton Physics math
     */
    fun computePhysicsOffset(
        phase: Float, // 0.0 .. 1.0 normalized progress of the physics cycle
        config: NewtonPhysicsConfig
    ): Triple<Float, Float, Float> {
        if (!config.enabled) return Triple(0f, 0f, 1f)

        val t = phase * 2f * PI.toFloat()
        return when (config.presetId) {
            "gravity_bounce" -> {
                // Parabolic bounce with exponential energy decay
                val bouncePeriod = (phase * 3f) % 1f
                val bounceNum = (phase * 3f).toInt()
                val decay = config.restitution.pow(bounceNum.toFloat())
                val y = -4f * decay * (bouncePeriod - 0.5f).pow(2) + decay
                val dispY = (1f - y) * (config.gravity * 3.5f)
                val squashX = 1f + (if (bouncePeriod > 0.85f) 0.08f * decay else 0f)
                val squashY = 1f - (if (bouncePeriod > 0.85f) 0.08f * decay else 0f)
                Triple(0f, dispY, (squashX + squashY) / 2f)
            }
            "spring_elastic" -> {
                // Damped harmonic oscillator: x(t) = e^(-bt) * cos(omega * t)
                val omega = sqrt(config.springTension) * 0.4f
                val decay = exp(-config.damping * phase * 4f)
                val disp = decay * cos(omega * phase * 2f * PI.toFloat()) * 30f
                Triple(disp * 0.5f, disp, 1f + (disp / 300f))
            }
            "pendulum_swing" -> {
                // Angular sway: theta(t) = theta0 * cos(sqrt(g/L) * t)
                val angle = sin(t * 1.5f) * 25f
                val dispX = sin(t * 1.5f) * 35f
                val dispY = (1f - cos(t * 1.5f)) * 15f
                Triple(dispX, dispY, 1f)
            }
            "magnetic_snap" -> {
                // Inverse-square dynamic snap toward center
                val snapPhase = (phase * 2f) % 1f
                val ease = FastOutSlowInEasing.transform(snapPhase)
                val disp = (1f - ease) * 45f * if (phase < 0.5f) 1f else -1f
                Triple(disp * 0.7f, disp, 1f + (1f - ease) * 0.12f)
            }
            "inertia_drift" -> {
                // Linear velocity decay with quadratic friction
                val ease = (1f - exp(-phase * 5f * config.damping))
                val dispX = (1f - ease) * 40f
                Triple(dispX, 0f, 1f)
            }
            "zero_g" -> {
                // Smooth Lissajous curve floating
                val dispX = sin(t) * 18f
                val dispY = cos(t * 1.3f) * 14f
                val scale = 1f + sin(t * 0.8f) * 0.03f
                Triple(dispX, dispY, scale)
            }
            "cradle_hit" -> {
                // Sharp elastic impact transfer
                val impactPhase = (phase * 4f) % 1f
                val impact = if (impactPhase < 0.15f) sin(impactPhase / 0.15f * PI.toFloat()) * 28f else 0f
                Triple(impact, -impact * 0.3f, 1f + (impact / 200f))
            }
            else -> Triple(0f, 0f, 1f)
        }
    }
}

/**
 * Pro Newton Physics Interactive Tuning Studio Panel
 */
@Composable
fun NewtonPhysicsToolPanel(
    config: NewtonPhysicsConfig,
    onConfigChange: (NewtonPhysicsConfig) -> Unit,
    onApply: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cyanAccent = Color(0xFF00E5FF)
    val goldAccent = Color(0xFFFFD700)

    // Infinite live simulation cycle for real-time physics curve visualizer
    val infiniteTransition = rememberInfiniteTransition(label = "newton_sim")
    val simPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "newton_phase"
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = CharcoalSurfaceVariant,
        borderColor = cyanAccent.copy(alpha = 0.6f),
        borderWidth = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with Pro Badge
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
                                    listOf(cyanAccent, RadiantPink)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterTiltShift,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Newton's Pro Dynamics",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = goldAccent.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, goldAccent)
                            ) {
                                Text(
                                    text = "PRO PHYSICS",
                                    color = goldAccent,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "CapCut & After Effects Dynamic Physics Engine",
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

            // Master Enable Physics Switch
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
                        imageVector = Icons.Default.Animation,
                        contentDescription = null,
                        tint = if (config.enabled) cyanAccent else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Enable Newton Physics Simulation",
                            color = if (config.enabled) cyanAccent else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Applies real velocity, mass & kinetic restitution",
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
                        checkedTrackColor = cyanAccent,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = CharcoalSurfaceVariant
                    ),
                    modifier = Modifier.testTag("toggle_newton_physics")
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
                // 1. LIVE NEWTON OSCILLOSCOPE & TRAJECTORY GRAPH
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, if (config.enabled) cyanAccent.copy(alpha = 0.5f) else GlassBorder, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Draw grid lines
                        val gridLines = 4
                        for (i in 1..gridLines) {
                            val y = size.height * (i.toFloat() / (gridLines + 1))
                            drawLine(
                                color = Color.White.copy(alpha = 0.08f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1f
                            )
                        }

                        // Draw real-time Newton waveform curve
                        val path = Path()
                        val steps = 60
                        for (step in 0..steps) {
                            val phase = step.toFloat() / steps
                            val (dx, dy, sc) = NewtonPhysicsEngine.computePhysicsOffset(phase, config)
                            val px = phase * size.width
                            val py = (size.height / 2f) + (dy * 0.8f).coerceIn(-size.height / 2f + 4f, size.height / 2f - 4f)
                            if (step == 0) path.moveTo(px, py) else path.lineTo(px, py)
                        }

                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(
                                listOf(cyanAccent, RadiantPink, Color(0xFFFFD700))
                            ),
                            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                        )

                        // Live animated tracker ball
                        val (curDx, curDy, curSc) = NewtonPhysicsEngine.computePhysicsOffset(simPhase, config)
                        val trackerX = simPhase * size.width
                        val trackerY = (size.height / 2f) + (curDy * 0.8f).coerceIn(-size.height / 2f + 4f, size.height / 2f - 4f)
                        drawCircle(
                            color = Color.White,
                            radius = 4.5f * curSc,
                            center = Offset(trackerX, trackerY)
                        )
                        drawCircle(
                            color = cyanAccent.copy(alpha = 0.5f),
                            radius = 8f * curSc,
                            center = Offset(trackerX, trackerY)
                        )
                    }

                    // Top label inside oscilloscope
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "NEWTON OSCILLOGRAM • ${config.presetId.uppercase()}",
                            color = cyanAccent,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "LIVE SIMULATION",
                            color = Color(0xFF00E676),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 2. NEWTON PRESETS HORIZONTAL ROW
                Text(
                    text = "SELECT NEWTON DYNAMICS PRESET",
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
                    NewtonPhysicsEngine.presets.forEach { preset ->
                        val isSelected = config.presetId == preset.id
                        Box(
                            modifier = Modifier
                                .width(115.dp)
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
                                            presetId = preset.id,
                                            gravity = preset.gravity,
                                            restitution = preset.restitution,
                                            springTension = preset.springTension,
                                            damping = preset.damping
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
                                        contentDescription = preset.name,
                                        tint = preset.accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.name,
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

                // 3. FINE-TUNING SLIDERS (Gravity, Restitution / Bounce, Tension, Damping)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CharcoalSurface)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Gravity Slider (g)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Gravity Force (g)", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${String.format("%.1f", config.gravity)} m/s²", color = cyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.gravity,
                        onValueChange = { onConfigChange(config.copy(gravity = it)) },
                        valueRange = 1f..25f,
                        colors = SliderDefaults.colors(thumbColor = cyanAccent, activeTrackColor = cyanAccent),
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )

                    // Restitution (Bounce Elasticity)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Kinetic Bounce Restitution (e)", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${(config.restitution * 100).toInt()}%", color = RadiantPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.restitution,
                        onValueChange = { onConfigChange(config.copy(restitution = it)) },
                        valueRange = 0.1f..0.98f,
                        colors = SliderDefaults.colors(thumbColor = RadiantPink, activeTrackColor = RadiantPink),
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )

                    // Spring Tension (k)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Hooke's Spring Tension (k)", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${config.springTension.toInt()} N/m", color = goldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.springTension,
                        onValueChange = { onConfigChange(config.copy(springTension = it)) },
                        valueRange = 50f..400f,
                        colors = SliderDefaults.colors(thumbColor = goldAccent, activeTrackColor = goldAccent),
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )

                    // Damping Friction
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Damping Air Friction", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${(config.damping * 100).toInt()}%", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.damping,
                        onValueChange = { onConfigChange(config.copy(damping = it)) },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF00E676), activeTrackColor = Color(0xFF00E676)),
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )
                }

                // 4. Target Layer Selection (All, Video Clip, Text & Stickers)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Apply Target Layer:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("All", "Clip", "Text/Sticker").forEach { layer ->
                            val isSelected = config.targetLayer == layer
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) cyanAccent.copy(alpha = 0.25f) else CharcoalSurface)
                                    .border(1.dp, if (isSelected) cyanAccent else GlassBorder, RoundedCornerShape(8.dp))
                                    .clickable { onConfigChange(config.copy(targetLayer = layer)) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = layer,
                                    color = if (isSelected) cyanAccent else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onApply,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cyanAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("apply_newton_physics_btn")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply Newton Physics", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
