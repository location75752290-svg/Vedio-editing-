package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.Widgets
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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ProBadgeEnd
import com.example.ui.theme.ProBadgeStart
import com.example.ui.theme.RadiantPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class AspectRatioItem(val label: String, val ratioText: String)

data class RecentProjectData(
    val title: String,
    val duration: String,
    val resolution: String,
    val date: String,
    val imageRes: Int
)

data class AiToolPreviewData(
    val name: String,
    val badge: String,
    val iconBg: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNewProjectClick: () -> Unit,
    onOpenProjectFile: () -> Unit = {},
    onLoadVcpProject: (com.example.domain.model.VisionCutProjectData) -> Unit = {},
    onNavigateToTemplates: () -> Unit,
    onNavigateToAiTools: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToRemoveBg: () -> Unit = {},
    onOpenVideoPicker: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedAspectRatio by remember { mutableStateOf("9:16") }
    var showProjectSheet by remember { mutableStateOf(false) }
    var vcpProjectsList by remember { mutableStateOf<List<com.example.domain.model.VisionCutProjectData>>(emptyList()) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        vcpProjectsList = com.example.engine.ProjectFileManager.createDemoProjectsIfEmpty(context)
    }

    val aspectRatios = listOf(
        AspectRatioItem("Shorts/TikTok", "9:16"),
        AspectRatioItem("YouTube", "16:9"),
        AspectRatioItem("Square", "1:1"),
        AspectRatioItem("Post", "4:5")
    )

    val recentProjects = listOf(
        RecentProjectData(
            title = "Cyberpunk Neo City Vlog",
            duration = "02:45",
            resolution = "4K 60fps",
            date = "2 hours ago",
            imageRes = R.drawable.thumb_cyberpunk_1785491147932
        ),
        RecentProjectData(
            title = "Golden Hour Cinematic Reel",
            duration = "00:58",
            resolution = "4K HDR",
            date = "Yesterday",
            imageRes = R.drawable.thumb_sunset_1785491163192
        ),
        RecentProjectData(
            title = "AI Voiceover Promo Video",
            duration = "01:12",
            resolution = "1080p",
            date = "3 days ago",
            imageRes = R.drawable.thumb_cyberpunk_1785491147932
        )
    )

    val aiToolsPreviews = listOf(
        AiToolPreviewData("AI Avatar Creator", "NEW", RadiantPink),
        AiToolPreviewData("Auto Captions", "POPULAR", ElectricBlue),
        AiToolPreviewData("Magic Eraser", "PRO", DeepPurple),
        AiToolPreviewData("4K Upscaler", "PRO", NeonIndigo)
    )

    val scrollState = rememberScrollState()

    // Infinite transition for glowing animations & floating light effects
    val infiniteTransition = rememberInfiniteTransition(label = "hero_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "plus_glow_scale"
    )
    val heroGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_bg_glow"
    )
    val particleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .verticalScroll(scrollState)
            .padding(bottom = 100.dp)
    ) {
        // Top App Bar Header with Logo & Naeem Developer Profile
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // App Logo Icon with Glow
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(listOf(ElectricBlue, DeepPurple))
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_vision_cut_1787138156155),
                        contentDescription = "VisionCutAI App Icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "VisionCut ",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "AI",
                    color = ElectricBlue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Top-Right Header: Naeem Developer + Small Verified Badge + Glowing Profile Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Naeem Developer",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = ElectricBlue,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Text(
                        text = "Pro AI Creator",
                        color = RadiantPink,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Premium Glowing Profile Avatar
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .scale(glowScale)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ElectricBlue, RadiantPink, DeepPurple)
                            )
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_naeem_avatar_1785498908988),
                        contentDescription = "Naeem Developer Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }
        }

        // Search Bar Preview Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = CharcoalSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "  Search AI tools, templates, projects...",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 1. Start New Project (Hero Card with AI Cyberpunk Girl Background)
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // Ambient animated background glow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                ElectricBlue.copy(alpha = heroGlowAlpha * 0.55f),
                                DeepPurple.copy(alpha = heroGlowAlpha * 0.45f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // GlassCard with AI-Generated Cyberpunk Background & Scrim Overlay
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color.Transparent,
                borderColor = ElectricBlue.copy(alpha = heroGlowAlpha),
                borderWidth = 1.5.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // AI-Generated Background Image: Futuristic Cinematic AI Studio with Cameras & Holograms
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_studio_bg_1785515965627),
                        contentDescription = "AI Video Editing Studio Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(28.dp))
                    )

                    // Soft Dark Overlay (35% average alpha) for clear text readability & vivid background artwork
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        ObsidianBackground.copy(alpha = 0.35f),
                                        CharcoalSurface.copy(alpha = 0.45f),
                                        ObsidianBackground.copy(alpha = 0.65f)
                                    )
                                )
                            )
                    )

                    // Subtle moving light effects & glowing particles
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        RadiantPink.copy(alpha = particleAlpha * 0.25f),
                                        ElectricBlue.copy(alpha = particleAlpha * 0.15f),
                                        Color.Transparent
                                    ),
                                    radius = 500f
                                )
                            )
                    )

                    // Hero Content Column
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Header Row with Title, Subtitle, Quick Create badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Improved Animated Glowing Ring "+" Button
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .scale(glowScale)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.sweepGradient(
                                                listOf(
                                                    ElectricBlue,
                                                    RadiantPink,
                                                    DeepPurple,
                                                    ElectricBlue
                                                )
                                            )
                                        )
                                        .clickable { onOpenVideoPicker() }
                                        .padding(3.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(
                                                        CharcoalSurface.copy(alpha = 0.95f),
                                                        ObsidianBackground
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "New Project",
                                            tint = TextPrimary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .padding(start = 14.dp)
                                        .testTag("start_project_button")
                                        .clickable { onOpenVideoPicker() }
                                ) {
                                    Text(
                                        text = "Start New Project",
                                        color = TextPrimary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Import videos or create AI-powered content instantly.",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            // Premium Glowing "Quick Create" Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                ElectricBlue.copy(alpha = 0.35f),
                                                RadiantPink.copy(alpha = 0.35f)
                                            )
                                        )
                                    )
                                    .border(
                                        1.dp,
                                        Brush.horizontalGradient(listOf(ElectricBlue, RadiantPink)),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FlashOn,
                                        contentDescription = null,
                                        tint = ElectricBlue,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = " QUICK CREATE",
                                        color = TextPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Cloud Sync Indicator & Recent Project Shortcut
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "Cloud Sync",
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = " Cloud Sync Active • 12.4 GB",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Recent Shortcut Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CharcoalSurfaceVariant.copy(alpha = 0.8f))
                                    .clickable { onNewProjectClick() }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = RadiantPink,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = " Resume: Cyberpunk Vlog",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Aspect Ratio Selector Chips
                        Text(
                            text = "ASPECT RATIO",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            aspectRatios.forEach { ratio ->
                                val isSelected = ratio.ratioText == selectedAspectRatio
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) {
                                                Brush.horizontalGradient(listOf(ElectricBlue, DeepPurple))
                                            } else {
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        CharcoalSurfaceVariant.copy(alpha = 0.85f),
                                                        CharcoalSurfaceVariant.copy(alpha = 0.85f)
                                                    )
                                                )
                                            }
                                        )
                                        .then(
                                            if (isSelected) {
                                                Modifier.border(
                                                    1.5.dp,
                                                    Brush.horizontalGradient(listOf(RadiantPink, ElectricBlue)),
                                                    RoundedCornerShape(14.dp)
                                                )
                                            } else Modifier.border(
                                                0.5.dp,
                                                GlassBorder,
                                                RoundedCornerShape(14.dp)
                                            )
                                        )
                                        .clickable { selectedAspectRatio = ratio.ratioText }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = ratio.ratioText,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = ratio.label,
                                            color = if (isSelected) TextPrimary else TextMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Four Premium Action Buttons: Import Video, Open .vcp, Camera, AI Generate ✨
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Action 1: Import Video
                                Box(
                                    modifier = Modifier
                                        .testTag("home_import_video")
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.horizontalGradient(listOf(ElectricBlue, DeepPurple))
                                        )
                                        .clickable { onOpenVideoPicker() }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.FolderOpen,
                                            contentDescription = null,
                                            tint = TextPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = " Import Video",
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Action 2: Open .vcp Project File
                                Box(
                                    modifier = Modifier
                                        .testTag("home_open_vcp_project")
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(CharcoalSurfaceVariant.copy(alpha = 0.9f))
                                        .border(1.dp, ElectricBlue.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                        .clickable { onOpenProjectFile() }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.FolderOpen,
                                            contentDescription = null,
                                            tint = ElectricBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = " Open .vcp",
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Action 3: Camera
                                Box(
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(CharcoalSurfaceVariant.copy(alpha = 0.9f))
                                        .border(1.dp, RadiantPink.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                        .clickable { onNewProjectClick() }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = RadiantPink,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = " Camera",
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Action 4: AI Generate ✨
                                Box(
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.horizontalGradient(listOf(RadiantPink, DeepPurple))
                                        )
                                        .clickable { onNavigateToAiTools() }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = TextPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = " AI Generate ✨",
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // 2. Featured Bento Studio (Redesigned)
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Featured Bento Studio",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Explore All",
                color = ElectricBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onNavigateToAiTools() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bento Grid Rows (Interactive Cards)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Bento Row 1: AI Auto Cut (Left) & Trending Templates (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 1: AI Auto Cut
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                        .clickable { onNavigateToAiTools() },
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = CharcoalSurface,
                    borderColor = DeepPurple.copy(alpha = 0.5f),
                    borderWidth = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(DeepPurple.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCut,
                                    contentDescription = "Auto Cut",
                                    tint = DeepPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Animated NEW badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DeepPurple)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "NEW ✨",
                                    color = TextPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "SMART TOOL",
                                color = ElectricBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "AI Auto Cut",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Remove silence & scenes",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Card 2: Trending Templates (Visual Card with Image Thumbnail)
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                        .clickable { onNavigateToTemplates() },
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = CharcoalSurface
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.thumb_sunset_1785491163192),
                            contentDescription = "Trending Templates",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp))
                        )

                        // Dark Gradient Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Black.copy(alpha = 0.25f),
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(RadiantPink)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "HOT 🔥",
                                    color = TextPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Column {
                                Text(
                                    text = "VIRAL STUDIO",
                                    color = Color(0xFFFFD700),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Trending Templates",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Bento Row 2: Recent Drafts (Left) & AI Magic Tools (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 3: Recent Drafts
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .clickable { onNavigateToProjects() },
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = CharcoalSurface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(ElectricBlue)
                                        .border(1.5.dp, ObsidianBackground, CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(DeepPurple)
                                        .border(1.5.dp, ObsidianBackground, CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(CharcoalSurfaceVariant)
                                        .border(1.5.dp, ObsidianBackground, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+3",
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CharcoalSurfaceVariant)
                                    .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "3 DRAFTS",
                                    color = TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "WORKSPACE",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Recent Drafts",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Card 4: AI Magic Tools
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .clickable { onNavigateToAiTools() },
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = CharcoalSurface,
                    borderColor = RadiantPink.copy(alpha = 0.4f),
                    borderWidth = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(RadiantPink.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Magic Tools",
                                    tint = RadiantPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(ProBadgeStart, ProBadgeEnd)
                                        )
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "PRO ⭐",
                                    color = TextPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "CREATIVE SUITE",
                                color = RadiantPink,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "AI Magic Tools",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Bento Row 3: Text to Video (Left) & Image to Video (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 5: Text to Video
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .clickable { onNavigateToAiTools() },
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = CharcoalSurface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TextFields,
                                    contentDescription = "Text to Video",
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ElectricBlue)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "TRENDING",
                                    color = TextPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "GENERATIVE",
                                color = ElectricBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Text to Video",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Card 6: Image to Video (Visual Card with Image Thumbnail)
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .clickable { onNavigateToAiTools() },
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = CharcoalSurface
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.thumb_cyberpunk_1785491147932),
                            contentDescription = "Image to Video",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp))
                        )

                        // Dark Gradient Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Black.copy(alpha = 0.3f),
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DeepPurple)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "NEW ✨",
                                    color = TextPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Column {
                                Text(
                                    text = "ANIMATION",
                                    color = RadiantPink,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Image to Video",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // 3. Upgrade Banner (Luxury Pro Membership)
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                backgroundColor = Color.Transparent,
                borderColor = Color(0xFFFFD700).copy(alpha = 0.6f),
                borderWidth = 1.5.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF2E114D), // Deep royal purple
                                    Color(0xFF1E0E38),
                                    Color(0xFF3B122B)
                                )
                            )
                        )
                ) {
                    // Ambient gold glow
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFF59E0B).copy(alpha = 0.22f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Top Header with Crown Icon & VisionCut AI Pro Title
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFFF59E0B), Color(0xFFFBBF24))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "Crown",
                                        tint = Color.Black,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = "VisionCut AI Pro",
                                        color = Color(0xFFFBBF24),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "VIP UNLIMITED ACCESS",
                                        color = TextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Pro VIP badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                                    .border(1.dp, Color(0xFFFBBF24), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "PRO VIP",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Unlock Premium Editing",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Features List
                        val features = listOf(
                            "4K Export",
                            "AI Video Generator",
                            "Unlimited Templates",
                            "No Watermark",
                            "Faster Rendering"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            features.forEach { feature ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = feature,
                                        color = TextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Glowing "Upgrade" Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFF59E0B), Color(0xFFFBBF24), RadiantPink)
                                    )
                                )
                                .clickable { showProjectSheet = true }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Upgrade to VisionCut Pro",
                                    color = Color.Black,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Recent Projects Header & List
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Projects",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "View All",
                color = ElectricBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .testTag("home_view_all_projects")
                    .clickable { onNavigateToProjects() }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (vcpProjectsList.isNotEmpty()) {
                items(vcpProjectsList) { vcpProj ->
                    GlassCard(
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { onLoadVcpProject(vcpProj) },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(125.dp)
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = if (vcpProj.id.contains("sunset")) R.drawable.thumb_sunset_1785491163192 else R.drawable.thumb_cyberpunk_1785491147932
                                    ),
                                    contentDescription = vcpProj.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                // Play Overlay Button
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CharcoalSurface.copy(alpha = 0.8f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                // .vcp Badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ElectricBlue)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = ".VCP",
                                        color = TextPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                // Duration Pill
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black.copy(alpha = 0.75f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    val sec = (vcpProj.videoDurationMs / 1000)
                                    Text(
                                        text = String.format("%02d:%02d", sec / 60, sec % 60),
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = vcpProj.name,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${vcpProj.speed}x Speed",
                                        color = NeonIndigo,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = com.example.engine.ProjectFileManager.formatLastEditedTime(vcpProj.lastModifiedMs),
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                items(recentProjects) { project ->
                    GlassCard(
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { showProjectSheet = true },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(125.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = project.imageRes),
                                    contentDescription = project.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                // Play Overlay Button
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CharcoalSurface.copy(alpha = 0.8f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                // Duration Pill
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black.copy(alpha = 0.75f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = project.duration,
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = project.title,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = project.resolution,
                                        color = NeonIndigo,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = project.date,
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // AI Tools Section Header & Preview
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = RadiantPink,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = " AI Editing Suite",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Explore All",
                color = ElectricBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .testTag("home_explore_ai")
                    .clickable { onNavigateToAiTools() }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(aiToolsPreviews) { tool ->
                GlassCard(
                    modifier = Modifier
                        .width(150.dp)
                        .clickable { onNavigateToAiTools() },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(tool.iconBg.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = tool.iconBg,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(tool.iconBg.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tool.badge,
                                    color = tool.iconBg,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = tool.name,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Next-Gen AI",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Privacy Policy & MLKit Data Processing Card
        var showPrivacyDialog by remember { mutableStateOf(false) }

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = CharcoalSurface,
            onClick = { showPrivacyDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Privacy Policy",
                        tint = ElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Privacy & MLKit Data Policy",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "100% On-Device AI • No Cloud Data Transfer",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
                Text(
                    text = "View",
                    color = ElectricBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (showPrivacyDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                title = {
                    Text(
                        text = "Privacy Policy & MLKit Data",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "VisionCutAI uses Google MLKit Selfie Segmentation to provide real-time background removal and green screen effects.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "• All MLKit AI processing happens 100% locally on your device.\n• No video frames, images, or personal metadata are ever sent to external servers.\n• Project files (.vcp) are stored strictly in your local Documents folder.\n• You retain full ownership and control over all media created in VisionCutAI.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                },
                confirmButton = {
                    androidx.compose.material3.Button(
                        onClick = { showPrivacyDialog = false },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Text("Got it", color = TextPrimary)
                    }
                },
                containerColor = CharcoalSurface
            )
        }
    }

    // Modal Bottom Sheet Preview for Project Quick Actions
    if (showProjectSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProjectSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = CharcoalSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "New VisionCut Project",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Aspect Ratio: $selectedAspectRatio • Ready for AI Timeline",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    onClick = { showProjectSheet = false }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = ElectricBlue
                        )
                        Text(
                            text = "  Select Video Clips from Gallery",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    onClick = { showProjectSheet = false }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = RadiantPink
                        )
                        Text(
                            text = "  Generate with AI Text-to-Video",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
