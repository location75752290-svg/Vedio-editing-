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
 * Hollywood 3D LUT Film Master Color Grade Configuration
 */
data class HollywoodLutConfig(
    val enabled: Boolean = false,
    val selectedLutId: String = "teal_orange_blockbuster", // "teal_orange_blockbuster", "arri_alexa_log", "kodak_portra_film", "cyberpunk_neon", "bollywood_golden", "vintage_wes_anderson"
    val lutIntensity: Float = 0.85f,                       // 0.1 .. 1.0
    val filmGrain: Float = 0.25f,                          // 0.0 .. 1.0 (Real 35mm grain simulation)
    val halationGlow: Float = 0.30f                        // 0.0 .. 1.0 (Film highlight bloom)
)

data class HollywoodLutPreset(
    val id: String,
    val name: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val previewGradient: List<Color>
)

object HollywoodLutEngine {
    val presets = listOf(
        HollywoodLutPreset(
            id = "teal_orange_blockbuster",
            name = "Teal & Orange",
            subtitle = "Hollywood Action & Blockbuster Cine Grade",
            icon = Icons.Default.MovieFilter,
            accentColor = Color(0xFF00E5FF),
            previewGradient = listOf(Color(0xFF005060), Color(0xFFFF8C00))
        ),
        HollywoodLutPreset(
            id = "arri_alexa_log",
            name = "Arri Alexa Log-C",
            subtitle = "Cinema Grade Wide Dynamic Range",
            icon = Icons.Default.CameraRoll,
            accentColor = Color(0xFFFFD700),
            previewGradient = listOf(Color(0xFF2C3E50), Color(0xFFBDC3C7))
        ),
        HollywoodLutPreset(
            id = "kodak_portra_film",
            name = "Kodak Portra 400",
            subtitle = "Analog 35mm Organic Warmth & Grain",
            icon = Icons.Default.PhotoCamera,
            accentColor = Color(0xFFFF7043),
            previewGradient = listOf(Color(0xFFE65100), Color(0xFFFFCC80))
        ),
        HollywoodLutPreset(
            id = "cyberpunk_neon",
            name = "Cyberpunk Neo-Tokyo",
            subtitle = "Electric Magenta & Deep Obsidian Shadows",
            icon = Icons.Default.FlashOn,
            accentColor = RadiantPink,
            previewGradient = listOf(Color(0xFF4A148C), Color(0xFFFF4081))
        ),
        HollywoodLutPreset(
            id = "bollywood_golden",
            name = "Bollywood Sunset Gold",
            subtitle = "Saturated Warmth, Sun Flare & Velvet Skin",
            icon = Icons.Default.WbSunny,
            accentColor = Color(0xFFFFAB00),
            previewGradient = listOf(Color(0xFFFF6F00), Color(0xFFFFD54F))
        ),
        HollywoodLutPreset(
            id = "vintage_wes_anderson",
            name = "Pastel Retro 1970",
            subtitle = "Wes Anderson Soft Pastel Symmetrical Palette",
            icon = Icons.Default.Palette,
            accentColor = Color(0xFF80CBC4),
            previewGradient = listOf(Color(0xFF80CBC4), Color(0xFFFFE082))
        )
    )
}

/**
 * Hollywood 3D LUT Color Grade Panel
 */
@Composable
fun HollywoodLutToolPanel(
    config: HollywoodLutConfig,
    onConfigChange: (HollywoodLutConfig) -> Unit,
    onApply: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goldAccent = Color(0xFFFFD700)
    val cyanAccent = Color(0xFF00E5FF)

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = CharcoalSurfaceVariant,
        borderColor = goldAccent.copy(alpha = 0.6f),
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
                                    listOf(goldAccent, Color(0xFFFF6F00))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MovieFilter,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Hollywood 3D LUT Master",
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
                                    text = "3D LUT",
                                    color = goldAccent,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Cinema Grade 35mm Emulation & Dynamic LUTs",
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
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = if (config.enabled) goldAccent else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Enable Hollywood 3D Color LUT",
                            color = if (config.enabled) goldAccent else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Applies 3D tetrahedral color cube matrix",
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
                        checkedTrackColor = goldAccent,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = CharcoalSurfaceVariant
                    ),
                    modifier = Modifier.testTag("toggle_hollywood_lut")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Content List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "SELECT HOLLYWOOD CINEMATIC LUT",
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
                    HollywoodLutEngine.presets.forEach { preset ->
                        val isSelected = config.selectedLutId == preset.id
                        Box(
                            modifier = Modifier
                                .width(125.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.horizontalGradient(preset.previewGradient.map { it.copy(alpha = if (isSelected) 0.6f else 0.2f) }))
                                .border(
                                    width = if (isSelected) 1.6.dp else 1.dp,
                                    color = if (isSelected) preset.accentColor else GlassBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onConfigChange(config.copy(selectedLutId = preset.id)) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(preset.accentColor.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = preset.icon,
                                        contentDescription = preset.name,
                                        tint = preset.accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.name,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 11.sp,
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

                // Sliders
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
                        Text("LUT Grade Blend Intensity", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${(config.lutIntensity * 100).toInt()}%", color = goldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.lutIntensity,
                        onValueChange = { onConfigChange(config.copy(lutIntensity = it)) },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = goldAccent, activeTrackColor = goldAccent),
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("35mm Analog Film Grain", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${(config.filmGrain * 100).toInt()}%", color = cyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.filmGrain,
                        onValueChange = { onConfigChange(config.copy(filmGrain = it)) },
                        valueRange = 0.0f..1.0f,
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
                        containerColor = goldAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("apply_hollywood_lut_btn")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply Hollywood Grade", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
