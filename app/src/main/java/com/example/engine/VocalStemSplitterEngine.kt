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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

/**
 * AI Vocal Stem Splitter & Beat Sync Configuration
 */
data class VocalStemConfig(
    val enabled: Boolean = false,
    val vocalVolume: Float = 1.0f,     // 0.0 .. 2.0 (Mute vocals to extract pure instrumental BGM)
    val drumsVolume: Float = 1.0f,     // 0.0 .. 2.0 (Boost bass / punch)
    val bassVolume: Float = 1.0f,      // 0.0 .. 2.0
    val melodyVolume: Float = 1.0f,    // 0.0 .. 2.0
    val autoBeatSyncEnabled: Boolean = true, // Auto-sync video cuts to bass drop transients
    val beatSensitivity: Float = 0.75f // 0.1 .. 1.0
)

/**
 * Audio Stem Splitter Tool Panel
 */
@Composable
fun VocalStemSplitterPanel(
    config: VocalStemConfig,
    onConfigChange: (VocalStemConfig) -> Unit,
    onAutoBeatCut: () -> Unit,
    onApply: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cyanAccent = Color(0xFF00E5FF)
    val greenAccent = Color(0xFF00E676)
    val goldAccent = Color(0xFFFFD700)

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = CharcoalSurfaceVariant,
        borderColor = greenAccent.copy(alpha = 0.6f),
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
                                    listOf(greenAccent, cyanAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "AI Stem Splitter & Beat Sync",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = greenAccent.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, greenAccent)
                            ) {
                                Text(
                                    text = "PRO AUDIO",
                                    color = greenAccent,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Separate Vocals, Drums, Bass & Auto-Cut on Drops",
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

            // Master Stem Separation Switch
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
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = if (config.enabled) greenAccent else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Enable AI Stem Isolation",
                            color = if (config.enabled) greenAccent else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Extract karaoke BGM or boost vocal clarity",
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
                        checkedTrackColor = greenAccent,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = CharcoalSurfaceVariant
                    ),
                    modifier = Modifier.testTag("toggle_stem_splitter")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stem Sliders
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Vocals Stem Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CharcoalSurface)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = RadiantPink, modifier = Modifier.size(16.dp))
                            Text("Vocals Track", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            if (config.vocalVolume == 0f) "MUTED (Karaoke)" else "${(config.vocalVolume * 100).toInt()}%",
                            color = if (config.vocalVolume == 0f) Color.Red else RadiantPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = config.vocalVolume,
                        onValueChange = { onConfigChange(config.copy(vocalVolume = it)) },
                        valueRange = 0f..2.0f,
                        colors = SliderDefaults.colors(thumbColor = RadiantPink, activeTrackColor = RadiantPink),
                        modifier = Modifier.fillMaxWidth().height(22.dp)
                    )
                }

                // Drums & Kick Stem Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CharcoalSurface)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = cyanAccent, modifier = Modifier.size(16.dp))
                            Text("Drums & Kick Bass", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("${(config.drumsVolume * 100).toInt()}%", color = cyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.drumsVolume,
                        onValueChange = { onConfigChange(config.copy(drumsVolume = it)) },
                        valueRange = 0f..2.0f,
                        colors = SliderDefaults.colors(thumbColor = cyanAccent, activeTrackColor = cyanAccent),
                        modifier = Modifier.fillMaxWidth().height(22.dp)
                    )
                }

                // Melodic Instruments Stem Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CharcoalSurface)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Piano, contentDescription = null, tint = goldAccent, modifier = Modifier.size(16.dp))
                            Text("Melody & Synth", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("${(config.melodyVolume * 100).toInt()}%", color = goldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = config.melodyVolume,
                        onValueChange = { onConfigChange(config.copy(melodyVolume = it)) },
                        valueRange = 0f..2.0f,
                        colors = SliderDefaults.colors(thumbColor = goldAccent, activeTrackColor = goldAccent),
                        modifier = Modifier.fillMaxWidth().height(22.dp)
                    )
                }

                // Auto Beat Sync Trigger Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(greenAccent.copy(alpha = 0.15f))
                        .border(1.dp, greenAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { onAutoBeatCut() }
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = greenAccent, modifier = Modifier.size(18.dp))
                        Column {
                            Text("Auto-Snap Cuts to Bass Drops ⚡", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Generates instant viral cuts synced to the rhythm", color = TextMuted, fontSize = 9.sp)
                        }
                    }
                    Button(
                        onClick = onAutoBeatCut,
                        colors = ButtonDefaults.buttonColors(containerColor = greenAccent, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Auto-Cut", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
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
                        containerColor = greenAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("apply_stem_splitter_btn")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply Stems & Beat Sync", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
