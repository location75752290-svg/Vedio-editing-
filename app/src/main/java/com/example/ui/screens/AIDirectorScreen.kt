package com.example.ui.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface Effect {
    val name: String
}

data class SpeedRamp(val startSpeed: Float, val endSpeed: Float) : Effect {
    override val name: String get() = "Speed Ramp (${startSpeed}x -> ${endSpeed}x)"
}

data class ZoomShake(val intensity: Float) : Effect {
    override val name: String get() = "Zoom Shake (${(intensity * 100).toInt()}%)"
}

data class SFX(val soundName: String) : Effect {
    override val name: String get() = "SFX: $soundName"
}

data class SlowMo(val factor: Float) : Effect {
    override val name: String get() = "SlowMo (${factor}x)"
}

data class GlowEffect(val style: String) : Effect {
    override val name: String get() = "Glow: $style"
}

data class Vignette(val amount: Float) : Effect {
    override val name: String get() = "Vignette (${(amount * 100).toInt()}%)"
}

class AutoCutSilence : Effect {
    override val name: String get() = "Auto-Cut Silence"
}

class Stabilization : Effect {
    override val name: String get() = "AI Stabilization"
}

data class AutoCaptions(val lang: String) : Effect {
    override val name: String get() = "Auto Captions ($lang)"
}

data class CutAt(val timestampMs: Long) : Effect {
    override val name: String get() = "Beat Cut @ ${timestampMs}ms"
}

data class EditProfile(
    val mood: String,
    val effects: List<Effect> = emptyList()
)

data class SceneInfo(val type: String)
data class SceneDetectionResult(val mainMood: String, val scenes: List<SceneInfo>)

private fun detectScenes(uri: Uri): SceneDetectionResult {
    return SceneDetectionResult(
        mainMood = "Action & Emotional Cinematic Fusion ✨",
        scenes = listOf(
            SceneInfo("ACTION"),
            SceneInfo("EMOTIONAL"),
            SceneInfo("TALKING")
        )
    )
}

private fun detectBeats(uri: Uri): List<Long> {
    return listOf(1200L, 2400L, 3600L, 4800L)
}

fun analyzeVideoWithAI(uri: Uri, callback: (EditProfile) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        delay(1200) // Simulated MediaPipe / TFLite & librosa beat processing
        val sceneResult = detectScenes(uri)
        val beats = detectBeats(uri)
        val effects = mutableListOf<Effect>()

        sceneResult.scenes.forEach { scene ->
            when (scene.type) {
                "ACTION" -> {
                    effects.add(SpeedRamp(1f, 3f))
                    effects.add(ZoomShake(intensity = 0.8f))
                    effects.add(SFX("whoosh"))
                }
                "EMOTIONAL" -> {
                    effects.add(SlowMo(0.5f))
                    effects.add(GlowEffect("SoftDreamy"))
                    effects.add(Vignette(0.6f))
                }
                "TALKING" -> {
                    effects.add(AutoCutSilence())
                    effects.add(Stabilization())
                    effects.add(AutoCaptions(lang = "ur"))
                }
            }
        }
        // Add beat sync cuts
        beats.forEach { effects.add(CutAt(it)) }

        withContext(Dispatchers.Main) {
            callback(EditProfile(mood = sceneResult.mainMood, effects = effects))
        }
    }
}

@Composable
fun LiveVideoPlayer(
    uri: Uri,
    effects: List<Effect>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black)
            .border(1.5.dp, GlassBorder, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayCircleFilled,
                contentDescription = "Video Live Player",
                tint = Color(0xFFFF2A85),
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Live Video Preview",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            if (effects.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF))
                ) {
                    Text(
                        text = "✨ ${effects.size} Active Live FX Filters",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AIDirectorScreen(
    videoUri: Uri,
    onApply: (EditProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var isAnalyzing by remember { mutableStateOf(false) }
    var editProfile by remember { mutableStateOf<EditProfile?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFFF2A85).copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF2A85))
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFFF2A85),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
            Column {
                Text(
                    text = "AI Cinematic Director ✨",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Automated AI Scene Analysis & Beat Detection Engine",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // 1. Video Preview with Live Effects Overlay
        LiveVideoPlayer(
            uri = videoUri,
            effects = editProfile?.effects ?: emptyList()
        )

        // 2. AI Director Button
        Button(
            onClick = {
                isAnalyzing = true
                analyzeVideoWithAI(videoUri) { profile ->
                    editProfile = profile
                    isAnalyzing = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("ai_director_analyze_btn"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF2A85),
                contentColor = Color.White
            )
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = " Analyzing Mood...",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "✨ AI Cinematic Director",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 3. Preview Chips of what AI decided
        editProfile?.let { profile ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MovieFilter,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "AI Detected: ${profile.mood}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Auto-Generated Effects (${profile.effects.size}):",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(profile.effects) { effect ->
                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = effect.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (effect) {
                                            is SpeedRamp -> Icons.Default.Speed
                                            is ZoomShake -> Icons.Default.Vibration
                                            is SFX -> Icons.Default.VolumeUp
                                            is SlowMo -> Icons.Default.SlowMotionVideo
                                            is GlowEffect -> Icons.Default.AutoAwesome
                                            is Vignette -> Icons.Default.Gradient
                                            is AutoCutSilence -> Icons.Default.ContentCut
                                            is Stabilization -> Icons.Default.Camera
                                            is AutoCaptions -> Icons.Default.ClosedCaption
                                            is CutAt -> Icons.Default.ContentCut
                                        },
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = CharcoalSurfaceVariant,
                                    labelColor = Color.White
                                ),
                                border = AssistChipDefaults.assistChipBorder(
                                    borderColor = Color(0xFF00E5FF).copy(alpha = 0.4f),
                                    enabled = true
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { onApply(profile) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("apply_all_edits_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E5FF),
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Apply All Edits",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
