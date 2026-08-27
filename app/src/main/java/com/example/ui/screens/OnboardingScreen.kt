package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.RadiantPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

data class TooltipBenefitData(
    val title: String,
    val headline: String,
    val description: String,
    val icon: ImageVector,
    val chips: List<String>,
    val metricTag: String
)

data class OnboardingPageData(
    val title: String,
    val description: String,
    val badgeText: String,
    val icon: ImageVector,
    val imageRes: Int,
    val tooltip: TooltipBenefitData
)

@Composable
fun OnboardingScreen(
    onSkip: () -> Unit,
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableIntStateOf(0) }
    var isTooltipVisible by remember { mutableStateOf(false) }
    var selectedFeatureChipIndex by remember { mutableIntStateOf(0) }

    val pages = remember {
        listOf(
            OnboardingPageData(
                title = "AI Video Editing",
                description = "Transform raw footage into viral masterpieces with intelligent auto-cuts, scene detection, and automated highlight reels.",
                badgeText = "SMART ENGINE",
                icon = Icons.Default.AutoAwesome,
                imageRes = R.drawable.thumb_cyberpunk_1785491147932,
                tooltip = TooltipBenefitData(
                    title = "AI CORE BENEFIT",
                    headline = "1-Tap Smart Cut & Beat Sync",
                    description = "Intelligent scene detection auto-trims silences and aligns cuts to the background music rhythm instantly.",
                    icon = Icons.Default.Bolt,
                    chips = listOf("⚡ Auto Cuts", "🎵 Beat Sync", "✂️ Auto B-Roll"),
                    metricTag = "80% Faster"
                )
            ),
            OnboardingPageData(
                title = "Professional Editing Tools",
                description = "Precision multi-track timeline, customizable keyframes, dynamic transitions, cinematic color grading, and speed ramping.",
                badgeText = "PRO TIMELINE",
                icon = Icons.Default.Timeline,
                imageRes = R.drawable.thumb_sunset_1785491163192,
                tooltip = TooltipBenefitData(
                    title = "PRO TIMELINE BENEFIT",
                    headline = "Bézier Speed Ramps & Keyframing",
                    description = "Fine-tune velocity curves and interpolate scale, position, and opacity with diamond keyframe markers.",
                    icon = Icons.Default.Diamond,
                    chips = listOf("📈 Velocity Curves", "💎 Keyframes", "🎨 Cinematic LUTs"),
                    metricTag = "Frame Accurate"
                )
            ),
            OnboardingPageData(
                title = "Export in 4K",
                description = "Ultra-crisp 4K 60fps HDR rendering with zero quality loss, custom bitrates, multi-aspect export, and instant social sharing.",
                badgeText = "ULTRA HD",
                icon = Icons.Default.HighQuality,
                imageRes = R.drawable.thumb_cyberpunk_1785491147932,
                tooltip = TooltipBenefitData(
                    title = "ULTRA HD BENEFIT",
                    headline = "Lossless 4K 60 FPS HDR",
                    description = "Hardware-accelerated rendering with custom bitrates formatted for YouTube, TikTok, and Instagram Reels.",
                    icon = Icons.Default.Speed,
                    chips = listOf("🚀 60 FPS HDR", "🎛️ Custom Bitrate", "📱 Multi-Ratio"),
                    metricTag = "Lossless"
                )
            )
        )
    }

    // Trigger subtle, non-intrusive tooltip animation when page changes
    LaunchedEffect(currentPage) {
        isTooltipVisible = false
        selectedFeatureChipIndex = 0
        delay(260)
        isTooltipVisible = true
    }

    // Subtle pulsing animation for tooltip glowing beacon
    val infiniteTransition = rememberInfiniteTransition(label = "beacon_pulse")
    val beaconScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val beaconAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Blue and Purple Gradient Palette for Tooltips
    val bluePurpleGradient = remember {
        Brush.horizontalGradient(
            listOf(
                ElectricBlue,
                NeonIndigo,
                DeepPurple
            )
        )
    }
    val tooltipCardBorderGradient = remember {
        Brush.linearGradient(
            listOf(
                ElectricBlue.copy(alpha = 0.8f),
                DeepPurple.copy(alpha = 0.6f),
                RadiantPink.copy(alpha = 0.4f)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar with Brand and Skip Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(CharcoalSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "VisionCut AI",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                // Interactive Tooltip Info Toggle / Skip Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Tooltip Toggle Button
                    IconButton(
                        onClick = { isTooltipVisible = !isTooltipVisible },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isTooltipVisible) ElectricBlue.copy(alpha = 0.2f) else CharcoalSurface)
                            .border(1.dp, if (isTooltipVisible) ElectricBlue.copy(alpha = 0.5f) else GlassBorder, CircleShape)
                            .testTag("onboarding_tooltip_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Toggle Benefit Insight",
                            tint = if (isTooltipVisible) CyanGlow else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .testTag("onboarding_skip")
                            .clip(RoundedCornerShape(14.dp))
                            .background(CharcoalSurface)
                            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                            .clickable { onSkip() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Skip",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Page Content
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.weight(1f),
                label = "onboarding_content"
            ) { pageIndex ->
                val page = pages[pageIndex]
                val tooltipData = page.tooltip

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Section: Preview Card + Interactive Floating Tooltip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Hero Image Card
                        GlassCard(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Image(
                                painter = painterResource(id = page.imageRes),
                                contentDescription = page.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                ObsidianBackground.copy(alpha = 0.9f)
                                            )
                                        )
                                    )
                            )
                            // Top End Icon Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(14.dp)
                                    .clip(CircleShape)
                                    .background(CharcoalSurface.copy(alpha = 0.9f))
                                    .border(1.dp, GlassBorder, CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = page.icon,
                                    contentDescription = null,
                                    tint = RadiantPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Subtle Interactive Benefit Tooltip Overlay
                        androidx.compose.animation.AnimatedVisibility(
                            visible = isTooltipVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                            ) + fadeIn() + scaleIn(initialScale = 0.92f),
                            exit = slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut(),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 20.dp)
                                .padding(horizontal = 8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = CharcoalSurface.copy(alpha = 0.95f),
                                border = BorderStroke(1.5.dp, tooltipCardBorderGradient),
                                shadowElevation = 10.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("onboarding_interactive_tooltip_${pageIndex}")
                                    .clickable {
                                        // Cycle through feature chips on tap
                                        selectedFeatureChipIndex = (selectedFeatureChipIndex + 1) % tooltipData.chips.size
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                                ) {
                                    // Tooltip Header Row (Pulsing Beacon, Tag, Metric Badge, Dismiss)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            // Glowing Beacon Dot (Blue & Purple Accent)
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .scale(beaconScale)
                                                    .clip(CircleShape)
                                                    .background(
                                                        Brush.radialGradient(
                                                            listOf(
                                                                CyanGlow.copy(alpha = beaconAlpha),
                                                                ElectricBlue
                                                            )
                                                        )
                                                    )
                                            )

                                            Text(
                                                text = tooltipData.title,
                                                color = CyanGlow,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 1.sp
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            // Metric Highlight Pill
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(bluePurpleGradient)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = tooltipData.metricTag,
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }

                                            // Dismiss "x"
                                            IconButton(
                                                onClick = { isTooltipVisible = false },
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Dismiss tooltip",
                                                    tint = TextMuted,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Headline with Leading Icon
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = tooltipData.icon,
                                            contentDescription = null,
                                            tint = RadiantPink,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = tooltipData.headline,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    // Benefit Description
                                    Text(
                                        text = tooltipData.description,
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        fontWeight = FontWeight.Normal
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Interactive Feature Benefit Chips (Blue and Purple Accents)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        tooltipData.chips.forEachIndexed { chipIdx, chipLabel ->
                                            val isSelected = chipIdx == selectedFeatureChipIndex
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        if (isSelected) {
                                                            Brush.horizontalGradient(listOf(ElectricBlue.copy(alpha = 0.35f), DeepPurple.copy(alpha = 0.35f)))
                                                        } else {
                                                            Brush.horizontalGradient(listOf(CharcoalSurfaceVariant, CharcoalSurfaceVariant))
                                                        }
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isSelected) ElectricBlue else GlassBorder,
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    .clickable { selectedFeatureChipIndex = chipIdx }
                                                    .padding(vertical = 4.dp, horizontal = 2.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = chipLabel,
                                                    color = if (isSelected) Color.White else TextMuted,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    maxLines = 1,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    // Bottom Information Section
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Badge Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            ElectricBlue.copy(alpha = 0.25f),
                                            DeepPurple.copy(alpha = 0.25f)
                                        )
                                    )
                                )
                                .border(1.dp, ElectricBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = page.badgeText,
                                color = NeonIndigo,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = page.title,
                            color = TextPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = page.description,
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Bottom Navigation Row (Page Indicator Dots & Next/Get Started Button)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator Dots with smooth gradient fill
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { index ->
                        val isSelected = index == currentPage
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) {
                                        Brush.horizontalGradient(listOf(ElectricBlue, RadiantPink))
                                    } else {
                                        Brush.horizontalGradient(listOf(TextMuted.copy(alpha = 0.3f), TextMuted.copy(alpha = 0.3f)))
                                    }
                                )
                                .size(
                                    width = if (isSelected) 28.dp else 10.dp,
                                    height = 10.dp
                                )
                                .clickable { currentPage = index }
                        )
                    }
                }

                // Action Button
                GradientButton(
                    text = if (currentPage == pages.lastIndex) "Get Started" else "Next",
                    onClick = {
                        if (currentPage < pages.lastIndex) {
                            currentPage++
                        } else {
                            onGetStarted()
                        }
                    },
                    modifier = Modifier.testTag("onboarding_next_button"),
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                )
            }
        }
    }
}
