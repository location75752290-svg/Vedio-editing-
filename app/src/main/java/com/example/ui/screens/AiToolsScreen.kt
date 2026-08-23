package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ProBadgeEnd
import com.example.ui.theme.ProBadgeStart
import com.example.ui.theme.RadiantPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class AiToolData(
    val id: String,
    val title: String,
    val description: String,
    val badge: String,
    val icon: ImageVector,
    val accentColor: Color,
    val tags: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiToolsScreen(
    onOpenRemoveBg: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedToolForModal by remember { mutableStateOf<AiToolData?>(null) }

    val aiToolsList = listOf(
        AiToolData(
            id = "avatar",
            title = "AI Avatar Creator",
            description = "Generate photo-realistic digital presenters with natural speech and expressive eye contact.",
            badge = "NEW",
            icon = Icons.Default.Face,
            accentColor = RadiantPink,
            tags = listOf("Presenters", "Lip Sync", "Virtual Human")
        ),
        AiToolData(
            id = "voice",
            title = "AI Voiceover & Cloning",
            description = "Convert text script into hyper-realistic studio voiceovers in 50+ international languages.",
            badge = "POPULAR",
            icon = Icons.Default.GraphicEq,
            accentColor = ElectricBlue,
            tags = listOf("Multilingual", "Text-to-Speech", "Clone")
        ),
        AiToolData(
            id = "captions",
            title = "Auto Captions & Subtitles",
            description = "Transcribe audio into animated, stylized word-by-word captions with 99% accuracy.",
            badge = "HOT",
            icon = Icons.Default.Subtitles,
            accentColor = NeonIndigo,
            tags = listOf("Auto Sync", "Emoji Highlights", "Custom Fonts")
        ),
        AiToolData(
            id = "bg_remover",
            title = "Magic Background Remover",
            description = "Remove or replace video backdrops instantly with zero green screen required.",
            badge = "PRO",
            icon = Icons.Default.MovieFilter,
            accentColor = DeepPurple,
            tags = listOf("Green Screen Free", "VFX Replace", "Keying")
        ),
        AiToolData(
            id = "color_grade",
            title = "AI Color Grading",
            description = "Apply Hollywood film look LUTs and cinematic color science powered by scene intelligence.",
            badge = "PRO",
            icon = Icons.Default.Palette,
            accentColor = ElectricBlue,
            tags = listOf("HDR Grade", "Film Look", "Auto Tone")
        ),
        AiToolData(
            id = "auto_cut",
            title = "Smart Auto-Cut",
            description = "Automatically cut silences, filler words, awkward pauses, and bad takes in one tap.",
            badge = "NEW",
            icon = Icons.Default.VideoSettings,
            accentColor = RadiantPink,
            tags = listOf("Silence Trim", "Jump Cut", "Fast Edit")
        ),
        AiToolData(
            id = "text_to_video",
            title = "Text to Video Generator",
            description = "Turn prompts into stunning cinematic B-roll footage and animated video clips.",
            badge = "BETA",
            icon = Icons.Default.TextSnippet,
            accentColor = NeonIndigo,
            tags = listOf("Prompt Engine", "B-Roll Gen", "4K Clips")
        ),
        AiToolData(
            id = "4k_upscale",
            title = "4K Super Resolution",
            description = "Upscale low-res videos to crystal clear 4K 60fps with AI frame interpolation.",
            badge = "PRO",
            icon = Icons.Default.HighQuality,
            accentColor = DeepPurple,
            tags = listOf("60 FPS", "Upscaling", "Denoise")
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
            .padding(bottom = 90.dp)
    ) {
        // Screen Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = RadiantPink,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = " AI Power Tools",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Future-gen video processing suite for creators",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(listOf(ProBadgeStart, ProBadgeEnd))
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "AI SUITE",
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // 2-Column AI Tools Cards Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(aiToolsList) { tool ->
                GlassCard(
                    modifier = Modifier
                        .testTag("ai_tool_${tool.id}")
                        .fillMaxWidth()
                        .clickable {
                            if (tool.id == "bg_remover") {
                                onOpenRemoveBg()
                            } else {
                                selectedToolForModal = tool
                            }
                        },
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Icon + Badge Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(tool.accentColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tool.icon,
                                    contentDescription = tool.title,
                                    tint = tool.accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(tool.accentColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = tool.badge,
                                    color = tool.accentColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = tool.title,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = tool.description,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Chip Tags Preview
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            tool.tags.take(2).forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CharcoalSurface)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        color = TextMuted,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet Preview for AI Tool Details
    selectedToolForModal?.let { tool ->
        ModalBottomSheet(
            onDismissRequest = { selectedToolForModal = null },
            sheetState = rememberModalBottomSheetState(),
            containerColor = CharcoalSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(tool.accentColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = tool.title,
                        tint = tool.accentColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = tool.title,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = tool.description,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tool.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(tool.accentColor.copy(alpha = 0.15f))
                                .border(1.dp, tool.accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tag,
                                color = tool.accentColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                GradientButton(
                    text = "Launch ${tool.title}",
                    onClick = {
                        val toolId = tool.id
                        selectedToolForModal = null
                        if (toolId == "bg_remover") {
                            onOpenRemoveBg()
                        } else {
                            val isGoogleKeyEmpty = try {
                                BuildConfig.GOOGLE_API_KEY.isEmpty() || BuildConfig.GOOGLE_API_KEY == "MY_GOOGLE_API_KEY"
                            } catch (e: Exception) {
                                true
                            }
                            val isGeminiKeyEmpty = try {
                                BuildConfig.GEMINI_API_KEY.isEmpty() || BuildConfig.GEMINI_API_KEY == "MY_GEMINI_API_KEY"
                            } catch (e: Exception) {
                                true
                            }
                            if (isGoogleKeyEmpty && isGeminiKeyEmpty) {
                                Toast.makeText(context, "Please add API Key in Secrets", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Initializing ${tool.title}...", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .testTag("ai_tool_modal_launch")
                        .fillMaxWidth(),
                    gradient = Brush.horizontalGradient(listOf(tool.accentColor, DeepPurple)),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
