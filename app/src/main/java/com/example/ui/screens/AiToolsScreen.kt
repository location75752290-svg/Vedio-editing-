package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    onOpenVideoEditor: (android.net.Uri, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedToolForModal by remember { mutableStateOf<AiToolData?>(null) }
    var activeToolWorkspace by remember { mutableStateOf<AiToolData?>(null) }

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

    val auto_cut = aiToolsList.first { it.id == "auto_cut" }
    val captions = aiToolsList.first { it.id == "captions" }
    val color_grade = aiToolsList.first { it.id == "color_grade" }
    val upscale = aiToolsList.first { it.id == "4k_upscale" }
    val avatar = aiToolsList.first { it.id == "avatar" }
    val voice = aiToolsList.first { it.id == "voice" }
    val text_to_video = aiToolsList.first { it.id == "text_to_video" }

    val templates = listOf(
        "YouTube Shorts" to listOf(auto_cut, captions, color_grade, upscale),
        "AI News Anchor" to listOf(avatar, voice, captions),
        "Urdu Shayari Reel" to listOf(text_to_video, voice, color_grade)
    )

    var activeTemplate by remember { mutableStateOf<Pair<String, List<AiToolData>>?>(null) }

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

        // AI Master Agent Quick Prompt Section
        var masterPrompt by remember { mutableStateOf("sad urdu rain reel with presenter avatar in 4k") }
        var activeProject by remember { mutableStateOf<com.example.engine.VideoProject?>(null) }
        var isMasterBuilding by remember { mutableStateOf(false) }
        var progressStep by remember { mutableStateOf("") }
        var progressPercent by remember { androidx.compose.runtime.mutableIntStateOf(0) }

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🤖 AI Master Agent",
                            color = ElectricBlue,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(RadiantPink.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ON-DEVICE GEMINI NANO",
                            color = RadiantPink,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = masterPrompt,
                    onValueChange = { masterPrompt = it },
                    modifier = Modifier.fillMaxWidth().testTag("ai_master_agent_prompt_input"),
                    placeholder = { Text("Kaho kya banana hai...", fontSize = 12.sp) },
                    singleLine = true
                )
                if (isMasterBuilding) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progressPercent / 100f },
                        modifier = Modifier.fillMaxWidth().testTag("ai_master_agent_progress_bar"),
                        color = ElectricBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚡ $progressStep ($progressPercent%)",
                        color = ElectricBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        scope.launch {
                            isMasterBuilding = true
                            val masterAgent = com.example.engine.AIMasterAgent()
                            val project = masterAgent.buildFromPrompt(masterPrompt) { step, pct ->
                                progressStep = step
                                progressPercent = pct
                            }
                            activeProject = project
                            isMasterBuilding = false
                            Toast.makeText(context, "AI Master Agent generated ${project.steps.size} tasks & queued renders!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isMasterBuilding && masterPrompt.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_master_agent_build_button")
                ) {
                    Text(if (isMasterBuilding) "Processing..." else "✨ Bana do")
                }

                activeProject?.let { proj ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Auto-Decided Tools Pipeline (${proj.steps.size} Steps):",
                        color = RadiantPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    proj.steps.forEach { step ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text("• ", color = ElectricBlue, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${step.name}: ",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = step.details,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val demoUri = com.example.engine.SampleVideoProvider.getOrCreateDemoVideoUri(context)
                            onOpenVideoEditor(demoUri, "AIMaster_Project.mp4")
                        },
                        modifier = Modifier.fillMaxWidth().testTag("ai_master_open_editor_button")
                    ) {
                        Text("🎬 Open in Studio Video Editor")
                    }
                }
            }
        }

        // Workflow Templates Carousel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Text(
                text = "⚡ AI Workflow Templates",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(templates) { (templateName, toolList) ->
                    GlassCard(
                        modifier = Modifier
                            .width(220.dp)
                            .testTag("workflow_template_${templateName.lowercase().replace(" ", "_")}")
                            .clickable { activeTemplate = templateName to toolList },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = templateName,
                                    color = ElectricBlue,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${toolList.size} Steps",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                toolList.forEachIndexed { idx, tool ->
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(tool.accentColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = tool.icon,
                                            contentDescription = tool.title,
                                            tint = tool.accentColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    if (idx < toolList.size - 1) {
                                        Text("→", color = TextMuted, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
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
                        val toolData = tool
                        selectedToolForModal = null
                        if (toolData.id == "bg_remover") {
                            onOpenRemoveBg()
                        } else {
                            activeToolWorkspace = toolData
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

    // Active Interactive Workspace for the selected AI tool
    activeToolWorkspace?.let { tool ->
        ModalBottomSheet(
            onDismissRequest = { activeToolWorkspace = null },
            sheetState = rememberModalBottomSheetState(),
            containerColor = ObsidianBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = null,
                            tint = tool.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tool.title,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(tool.accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "INTERACTIVE STUDIO",
                            color = tool.accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (tool.id) {
                    "avatar" -> AvatarCreatorWorkspace(
                        accentColor = tool.accentColor,
                        onOpenEditor = { uri, name ->
                            activeToolWorkspace = null
                            onOpenVideoEditor(uri, name)
                        }
                    )
                    "voice" -> VoiceoverCloningWorkspace(accentColor = tool.accentColor)
                    "captions" -> AutoCaptionsWorkspace(accentColor = tool.accentColor)
                    "color_grade" -> ColorGradingWorkspace(accentColor = tool.accentColor)
                    "auto_cut" -> AutoCutWorkspace(accentColor = tool.accentColor)
                    "text_to_video" -> TextToVideoWorkspace(
                        accentColor = tool.accentColor,
                        onOpenEditor = { uri, name ->
                            activeToolWorkspace = null
                            onOpenVideoEditor(uri, name)
                        }
                    )
                    "4k_upscale" -> Upscale4KWorkspace(accentColor = tool.accentColor)
                    else -> GenericToolWorkspace(tool = tool)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Active AI Workflow Template Executor Modal
    activeTemplate?.let { (templateName, toolList) ->
        ModalBottomSheet(
            onDismissRequest = { activeTemplate = null },
            sheetState = rememberModalBottomSheetState(),
            containerColor = ObsidianBackground
        ) {
            WorkflowTemplateWorkspace(
                templateName = templateName,
                toolList = toolList,
                onClose = { activeTemplate = null },
                onOpenEditor = { uri, name ->
                    activeTemplate = null
                    onOpenVideoEditor(uri, name)
                }
            )
        }
    }
}

@Composable
fun AvatarCreatorWorkspace(
    accentColor: Color,
    onOpenEditor: (android.net.Uri, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedPresenter by remember { mutableStateOf("Emma (Tech Host)") }
    var scriptText by remember { mutableStateOf("Welcome to VisionCut AI Studio! Let's build viral content together.") }
    var voiceStyle by remember { mutableStateOf("Energetic") }
    var isRendering by remember { mutableStateOf(false) }
    var renderProgress by remember { mutableStateOf("") }
    var renderedVideo by remember { mutableStateOf<String?>(null) }

    val presenters = listOf("Emma (Tech Host)", "David (Executive)", "Sarah (Creative)", "Alex (Gamer)")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Select AI Digital Presenter", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(presenters) { presenter ->
                FilterChip(
                    selected = selectedPresenter == presenter,
                    onClick = { selectedPresenter = presenter },
                    label = { Text(presenter) }
                )
            }
        }

        Text("Presenter Script", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        OutlinedTextField(
            value = scriptText,
            onValueChange = { scriptText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter script for AI presenter to speak...") }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Energetic", "Professional", "Friendly").forEach { style ->
                FilterChip(
                    selected = voiceStyle == style,
                    onClick = { voiceStyle = style },
                    label = { Text(style) }
                )
            }
        }

        Button(
            onClick = {
                scope.launch {
                    isRendering = true
                    renderProgress = "1/3 Loading 3D Avatar Neural Mesh..."
                    delay(800)
                    renderProgress = "2/3 Synthesizing Lip-Sync Audio Vectors..."
                    delay(800)
                    renderProgress = "3/3 Rendering Presenter Video..."
                    delay(800)
                    isRendering = false
                    renderedVideo = "avatar_presenter_${selectedPresenter.take(4).lowercase()}.mp4"
                }
            },
            enabled = !isRendering && scriptText.isNotBlank(),
            modifier = Modifier.fillMaxWidth().testTag("render_avatar_button")
        ) {
            Text(if (isRendering) "Generating Avatar Video..." else "🎬 Generate Presenter Video")
        }

        if (isRendering) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text(renderProgress, color = accentColor, fontSize = 12.sp)
        }

        renderedVideo?.let { video ->
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✅ Avatar Video Ready: $video", color = RadiantPink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { Toast.makeText(context, "Exported $video to gallery", Toast.LENGTH_SHORT).show() }) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save")
                        }
                        OutlinedButton(onClick = {
                            val demoUri = com.example.engine.SampleVideoProvider.getOrCreateDemoVideoUri(context)
                            onOpenEditor(demoUri, video)
                        }) {
                            Text("Open in Editor")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceoverCloningWorkspace(accentColor: Color) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var voiceModel by remember { mutableStateOf("Female Soft") }
    var language by remember { mutableStateOf("English") }
    var script by remember { mutableStateOf("Create studio-quality voiceovers in seconds with VisionCut AI.") }
    var pitch by remember { mutableFloatStateOf(1.0f) }
    var speed by remember { mutableFloatStateOf(1.0f) }
    var isGenerating by remember { mutableStateOf(false) }
    var isGenerated by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Voice Model & Language", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("Female Soft", "Male Deep", "News Anchor", "Storyteller", "Urdu Female", "Urdu Male")) { voice ->
                FilterChip(
                    selected = voiceModel == voice,
                    onClick = { voiceModel = voice },
                    label = { Text(voice) }
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("English", "Urdu", "Spanish", "French", "Arabic", "Hindi")) { lang ->
                FilterChip(
                    selected = language == lang,
                    onClick = { language = lang },
                    label = { Text(lang) }
                )
            }
        }

        Text("Voice Script", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        OutlinedTextField(
            value = script,
            onValueChange = { script = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter text for voice synthesis...") }
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Pitch: ${String.format("%.1f", pitch)}x", color = TextSecondary, fontSize = 12.sp)
                Slider(value = pitch, onValueChange = { pitch = it }, valueRange = 0.5f..1.5f)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Speed: ${String.format("%.1f", speed)}x", color = TextSecondary, fontSize = 12.sp)
                Slider(value = speed, onValueChange = { speed = it }, valueRange = 0.5f..2.0f)
            }
        }

        Button(
            onClick = {
                scope.launch {
                    isGenerating = true
                    delay(1200)
                    isGenerating = false
                    isGenerated = true
                }
            },
            enabled = !isGenerating && script.isNotBlank(),
            modifier = Modifier.fillMaxWidth().testTag("synthesize_voice_button")
        ) {
            Text(if (isGenerating) "Synthesizing Neural Audio..." else "🎙️ Synthesize Voiceover")
        }

        if (isGenerated) {
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = ElectricBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Neural Audio ($language - $voiceModel)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("128 kbps AAC • 00:08s", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                    Button(onClick = { Toast.makeText(context, "Audio sample playing", Toast.LENGTH_SHORT).show() }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun AutoCaptionsWorkspace(accentColor: Color) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedStyle by remember { mutableStateOf("TikTok Animated") }
    var highlightEmojis by remember { mutableStateOf(true) }
    var isTranscribing by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }

    val styles = listOf("TikTok Animated", "YouTube Bold", "Neon Glow", "Minimalist Sub", "Urdu Nastaliq")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Caption Style Preset", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(styles) { style ->
                FilterChip(
                    selected = selectedStyle == style,
                    onClick = { selectedStyle = style },
                    label = { Text(style) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Auto Emoji Highlights & Animations", color = TextPrimary, fontSize = 13.sp)
            Switch(checked = highlightEmojis, onCheckedChange = { highlightEmojis = it })
        }

        Button(
            onClick = {
                scope.launch {
                    isTranscribing = true
                    delay(1500)
                    isTranscribing = false
                    isDone = true
                }
            },
            enabled = !isTranscribing,
            modifier = Modifier.fillMaxWidth().testTag("transcribe_captions_button")
        ) {
            Text(if (isTranscribing) "Transcribing Audio Vectors..." else "📝 Transcribe & Add Captions")
        }

        if (isDone) {
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Generated Subtitles ($selectedStyle):", color = NeonIndigo, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("🔥 [00:01] WELCOME to the FUTURE of AI video editing!", color = TextPrimary, fontSize = 12.sp)
                    Text("⚡ [00:03] Generate viral reels in just ONE CLICK!", color = TextPrimary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { Toast.makeText(context, "Subtitles applied to current project!", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply to Video Timeline")
                    }
                }
            }
        }
    }
}

@Composable
fun ColorGradingWorkspace(accentColor: Color) {
    val context = LocalContext.current
    var selectedLUT by remember { mutableStateOf("Hollywood Teal & Orange") }
    var intensity by remember { mutableFloatStateOf(80f) }
    var exposure by remember { mutableFloatStateOf(0f) }

    val luts = listOf("Hollywood Teal & Orange", "Kodak Gold 200", "Fuji Film Vintage", "Cyberpunk Neon", "HDR Cinematic")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Cinematic LUT Presets", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(luts) { lut ->
                FilterChip(
                    selected = selectedLUT == lut,
                    onClick = { selectedLUT = lut },
                    label = { Text(lut) }
                )
            }
        }

        Column {
            Text("LUT Intensity: ${intensity.toInt()}%", color = TextSecondary, fontSize = 12.sp)
            Slider(value = intensity, onValueChange = { intensity = it }, valueRange = 0f..100f)
        }

        Column {
            Text("Exposure Correction: ${String.format("%.1f", exposure)}", color = TextSecondary, fontSize = 12.sp)
            Slider(value = exposure, onValueChange = { exposure = it }, valueRange = -2f..2f)
        }

        Button(
            onClick = { Toast.makeText(context, "Applied $selectedLUT (${intensity.toInt()}%)", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.fillMaxWidth().testTag("apply_color_grade_button")
        ) {
            Text("🎨 Apply Cinematic Grade")
        }
    }
}

@Composable
fun AutoCutWorkspace(accentColor: Color) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var silenceThreshold by remember { mutableFloatStateOf(-35f) }
    var removeFillers by remember { mutableStateOf(true) }
    var isScanning by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Silence Threshold: ${silenceThreshold.toInt()} dB", color = TextSecondary, fontSize = 13.sp)
        Slider(value = silenceThreshold, onValueChange = { silenceThreshold = it }, valueRange = -50f..-15f)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Remove Filler Words ('um', 'ah', 'like')", color = TextPrimary, fontSize = 13.sp)
            Switch(checked = removeFillers, onCheckedChange = { removeFillers = it })
        }

        Button(
            onClick = {
                scope.launch {
                    isScanning = true
                    delay(1200)
                    isScanning = false
                    scanResult = "Found 12 awkward pauses. Removed 14.8 seconds of silence!"
                }
            },
            enabled = !isScanning,
            modifier = Modifier.fillMaxWidth().testTag("scan_auto_cut_button")
        ) {
            Text(if (isScanning) "Scanning Audio Track..." else "✂️ Scan & Auto-Cut Silences")
        }

        scanResult?.let { msg ->
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Auto-Cut Summary:", color = RadiantPink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(msg, color = TextPrimary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun TextToVideoWorkspace(
    accentColor: Color,
    onOpenEditor: (android.net.Uri, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var prompt by remember { mutableStateOf("A futuristic cyberpunk city with flying cars in rain") }
    var aspectRatio by remember { mutableStateOf("9:16 Reel") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedVideo by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Video Prompt", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Describe the video scene...") }
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("9:16 Reel", "16:9 Landscape", "1:1 Square")) { ratio ->
                FilterChip(
                    selected = aspectRatio == ratio,
                    onClick = { aspectRatio = ratio },
                    label = { Text(ratio) }
                )
            }
        }

        Button(
            onClick = {
                scope.launch {
                    isGenerating = true
                    delay(2000)
                    isGenerating = false
                    generatedVideo = "text2video_${aspectRatio.take(4).lowercase()}.mp4"
                }
            },
            enabled = !isGenerating && prompt.isNotBlank(),
            modifier = Modifier.fillMaxWidth().testTag("generate_text2video_button")
        ) {
            Text(if (isGenerating) "Synthesizing AI Video Frames..." else "✨ Generate Video Clip")
        }

        if (isGenerating) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        generatedVideo?.let { vid ->
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✅ Rendered B-Roll Clip: $vid", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        val demoUri = com.example.engine.SampleVideoProvider.getOrCreateDemoVideoUri(context)
                        onOpenEditor(demoUri, vid)
                    }) {
                        Text("Open in Video Editor")
                    }
                }
            }
        }
    }
}

@Composable
fun Upscale4KWorkspace(accentColor: Color) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var targetRes by remember { mutableStateOf("1080p -> 4K Ultra HD") }
    var fpsInterpolation by remember { mutableStateOf("30 FPS -> 60 FPS Smooth") }
    var isProcessing by remember { mutableStateOf(false) }
    var isComplete by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Target Super Resolution", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("1080p -> 4K Ultra HD", "4K -> 8K Cinema")) { res ->
                FilterChip(
                    selected = targetRes == res,
                    onClick = { targetRes = res },
                    label = { Text(res) }
                )
            }
        }

        Text("Frame Rate Smoothness", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("30 FPS -> 60 FPS Smooth", "60 FPS -> 120 FPS Extreme")) { fps ->
                FilterChip(
                    selected = fpsInterpolation == fps,
                    onClick = { fpsInterpolation = fps },
                    label = { Text(fps) }
                )
            }
        }

        Button(
            onClick = {
                scope.launch {
                    isProcessing = true
                    delay(1800)
                    isProcessing = false
                    isComplete = true
                }
            },
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth().testTag("process_4k_button")
        ) {
            Text(if (isProcessing) "Upscaling with GPU Hardware Engine..." else "🚀 Process 4K Super Resolution")
        }

        if (isProcessing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (isComplete) {
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("✅ 4K Upscaling Complete!", color = DeepPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Output: 3840x2160 @ 60fps (ProRes 422)", color = TextPrimary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun GenericToolWorkspace(tool: AiToolData) {
    val context = LocalContext.current
    Button(
        onClick = { Toast.makeText(context, "Executing ${tool.title}", Toast.LENGTH_SHORT).show() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Execute ${tool.title}")
    }
}

@Composable
fun WorkflowTemplateWorkspace(
    templateName: String,
    toolList: List<AiToolData>,
    onClose: () -> Unit,
    onOpenEditor: (android.net.Uri, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isRunning by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableIntStateOf(-1) }
    var isCompleted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "⚡ $templateName Workflow",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Automated ${toolList.size}-step AI production pipeline",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(RadiantPink.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "CHAIN PIPELINE",
                    color = RadiantPink,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = "Pipeline Tool Chain:",
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            toolList.forEachIndexed { index, tool ->
                val isCurrent = isRunning && currentStepIndex == index
                val isDone = (isRunning && currentStepIndex > index) || isCompleted

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isDone) ElectricBlue.copy(alpha = 0.2f)
                                        else if (isCurrent) RadiantPink.copy(alpha = 0.2f)
                                        else tool.accentColor.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = ElectricBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else if (isCurrent) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = RadiantPink,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = tool.icon,
                                        contentDescription = null,
                                        tint = tool.accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Step ${index + 1}: ${tool.title}",
                                    color = if (isDone) ElectricBlue else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = tool.description,
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isRunning) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (!isCompleted) {
            Button(
                onClick = {
                    scope.launch {
                        isRunning = true
                        toolList.forEach { tool ->
                            com.example.engine.RenderQueue.add(
                                com.example.engine.RenderJob(title = tool.title)
                            )
                        }
                        for (i in toolList.indices) {
                            currentStepIndex = i
                            delay(1200)
                        }
                        isRunning = false
                        isCompleted = true
                    }
                },
                enabled = !isRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("run_workflow_button")
            ) {
                Text(if (isRunning) "Processing Workflow Chain..." else "🚀 Execute Full $templateName Workflow")
            }
        } else {
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉 $templateName Pipeline Rendered Successfully!",
                        color = RadiantPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All ${toolList.size} AI processing steps executed & assembled on master timeline.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Exporting $templateName project to gallery...", Toast.LENGTH_SHORT).show()
                                onClose()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Export")
                        }
                        Button(
                            onClick = {
                                val demoUri = com.example.engine.SampleVideoProvider.getOrCreateDemoVideoUri(context)
                                onOpenEditor(demoUri, "${templateName.replace(" ", "_")}_Render.mp4")
                            },
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text("Open in Editor")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiMasterAgentCard(
    agent: com.example.engine.AIMasterAgent = androidx.compose.runtime.remember { com.example.engine.AIMasterAgent() },
    modifier: Modifier = Modifier,
    onVideoReady: (path: String) -> Unit = {}
) {
    var prompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf("Ready to Generate ✨") }
    var progress by remember { androidx.compose.runtime.mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    ElevatedCard( // CARD LAYOUT
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            
            Text(
                "🧠 AI Master Agent", 
                style = MaterialTheme.typography.headlineSmall, 
                fontWeight = FontWeight.Bold
            )
            Text("1 prompt = Full Video", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField( // MULTI-LINE INPUT
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Describe your video...") },
                placeholder = { Text("matrix style news anchor urdu 4k") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                enabled = !isLoading,
                maxLines = 5
            )
            
            Spacer(Modifier.height(16.dp))

            // DYNAMIC PROGRESS BAR
            AnimatedVisibility(visible = isLoading) {
                Column {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡ $currentStep", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.weight(1f))
                        Text("$progress%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            // ERROR HANDLING
            AnimatedVisibility(visible = errorMessage != null) {
                Text("❌ ${errorMessage ?: ""}", color = MaterialTheme.colorScheme.error)
            }
            
            Spacer(Modifier.height(16.dp))

            Button( // EXECUTION CALLBACK
                onClick = {
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        try {
                            val result = agent.buildFromPrompt(prompt) { step, pct ->
                                currentStep = step
                                progress = pct
                            }
                            result.finalPath?.let { onVideoReady(it) } // CALLBACK
                            currentStep = "✅ Video Ready!"
                            progress = 100
                        } catch (e: Exception) {
                            errorMessage = e.localizedMessage ?: "Unknown error"
                            currentStep = "Failed"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading && prompt.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Processing..." else "✨ Generate Video")
            }
        }
    }
}

