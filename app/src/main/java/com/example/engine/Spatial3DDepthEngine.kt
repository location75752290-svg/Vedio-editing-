package com.example.engine

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

/**
 * 3D Spatial Hologram & Depth Parallax Motion Configuration
 */
data class Spatial3DConfig(
    val enabled: Boolean = false,
    val presetId: String = "capcut_3d_zoom", // "capcut_3d_zoom", "hologram_tilt", "spatial_depth_cube", "gyro_parallax", "matrix_orbit", "vertigo_dolly"
    val depthIntensity: Float = 0.65f,      // 0.1 .. 1.0
    val cameraFov: Float = 60f,             // 30 .. 120 deg
    val motionSpeed: Float = 1.0f,          // 0.2 .. 3.0x
    val meshGridLines: Boolean = false,     // Cyberpunk spatial wireframe overlay
    val layerSeparation: Float = 35f        // 5 .. 100 px Z-displacement
)

data class Spatial3DPreset(
    val id: String,
    val name: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color
)

object Spatial3DDepthEngine {
    val presets = listOf(
        Spatial3DPreset(
            id = "capcut_3d_zoom",
            name = "CapCut 3D Zoom Pro",
            subtitle = "Multiplane depth displacement & forward push",
            icon = Icons.Default.ZoomOutMap,
            accentColor = Color(0xFF00E5FF)
        ),
        Spatial3DPreset(
            id = "hologram_tilt",
            name = "Spatial Hologram",
            subtitle = "3D Gyro perspective tilt with light refraction",
            icon = Icons.Default.ViewInAr,
            accentColor = RadiantPink
        ),
        Spatial3DPreset(
            id = "spatial_depth_cube",
            name = "3D Extrusion Cube",
            subtitle = "Volumetric depth extrusion along Z-axis",
            icon = Icons.Default.AllOut,
            accentColor = Color(0xFFFFD700)
        ),
        Spatial3DPreset(
            id = "gyro_parallax",
            name = "Dynamic Parallax",
            subtitle = "Foreground & background multi-speed shift",
            icon = Icons.Default.Layers,
            accentColor = Color(0xFF00E676)
        ),
        Spatial3DPreset(
            id = "matrix_orbit",
            name = "Orbital Bullet-Time",
            subtitle = "360° rotational camera perspective sweep",
            icon = Icons.Default.Cameraswitch,
            accentColor = Color(0xFFD500F9)
        ),
        Spatial3DPreset(
            id = "vertigo_dolly",
            name = "Vertigo Dolly Zoom",
            subtitle = "Reverse optical zoom with focal depth stretch",
            icon = Icons.Default.CameraAlt,
            accentColor = Color(0xFFFF9100)
        )
    )
}

/**
 * 3D Spatial Hologram & Depth Parallax Tool Panel
 */
@Composable
fun Spatial3DToolPanel(
    config: Spatial3DConfig,
    onConfigChange: (Spatial3DConfig) -> Unit,
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
        borderColor = cyanAccent.copy(alpha = 0.6f),
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
                                    listOf(cyanAccent, Color(0xFFD500F9))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "3D Spatial Parallax Pro",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = cyanAccent.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, cyanAccent)
                            ) {
                                Text(
                                    text = "3D DEPTH",
                                    color = cyanAccent,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "AI Multi-Plane Depth Extrusion & CapCut 3D Zoom",
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
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = null,
                        tint = if (config.enabled) cyanAccent else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Enable 3D Spatial Depth Motion",
                            color = if (config.enabled) cyanAccent else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Extrudes 2D frames into dynamic 3D camera space",
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
                    modifier = Modifier.testTag("toggle_spatial_3d")
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
                // Preset Row
                Text(
                    text = "SELECT 3D SPATIAL PRESET",
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
                    Spatial3DDepthEngine.presets.forEach { preset ->
                        val isSelected = config.presetId == preset.id
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
                                .clickable { onConfigChange(config.copy(presetId = preset.id)) }
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

                // Tuning Sliders
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
                        Text("Depth Extrusion Intensity", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${(config.depthIntensity * 100).toInt()}%", color = cyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.depthIntensity,
                        onValueChange = { onConfigChange(config.copy(depthIntensity = it)) },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = cyanAccent, activeTrackColor = cyanAccent),
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Camera Field Of View (FOV)", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${config.cameraFov.toInt()}°", color = RadiantPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.cameraFov,
                        onValueChange = { onConfigChange(config.copy(cameraFov = it)) },
                        valueRange = 30f..120f,
                        colors = SliderDefaults.colors(thumbColor = RadiantPink, activeTrackColor = RadiantPink),
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Z-Layer Separation Depth", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${config.layerSeparation.toInt()} px", color = goldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.layerSeparation,
                        onValueChange = { onConfigChange(config.copy(layerSeparation = it)) },
                        valueRange = 5f..100f,
                        colors = SliderDefaults.colors(thumbColor = goldAccent, activeTrackColor = goldAccent),
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
                        containerColor = cyanAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("apply_spatial_3d_btn")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply 3D Spatial Depth", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
