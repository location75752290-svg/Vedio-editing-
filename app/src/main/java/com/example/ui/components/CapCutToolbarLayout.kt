package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

private val cyanAccent = Color(0xFF00E5FF)
private val goldAccent = Color(0xFFFFD700)
private val orangeAccent = Color(0xFFFF9100)
private val purpleAccent = Color(0xFFD500F9)

data class CapCutToolItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val activeBadge: String? = null,
    val accentColor: Color = Color.White
)

/**
 * Authentic CapCut-Style Top Bar with:
 * - Back button
 * - Centered Resolution & FPS Selector Pill (e.g. 1080P • 30fps)
 * - Undo & Redo icons
 * - CapCut Signature Export (Upward Arrow) Action Button
 */
@Composable
fun CapCutTopBar(
    fileName: String,
    selectedResolution: String,
    selectedFps: Int,
    canUndo: Boolean,
    canRedo: Boolean,
    onBackClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onResolutionClick: () -> Unit,
    onExportClick: () -> Unit,
    onSaveProjectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ObsidianBackground,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("capcut_top_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            // CapCut Resolution & FPS Selector Pill
            Surface(
                onClick = onResolutionClick,
                shape = RoundedCornerShape(14.dp),
                color = CharcoalSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                modifier = Modifier.testTag("capcut_resolution_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$selectedResolution",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "•",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${selectedFps}fps",
                        color = cyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Resolution Options",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Action Cluster: Undo, Redo, Save, Export
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Undo
                IconButton(
                    onClick = onUndoClick,
                    enabled = canUndo,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("capcut_undo_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (canUndo) TextPrimary else TextMuted.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Redo
                IconButton(
                    onClick = onRedoClick,
                    enabled = canRedo,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("capcut_redo_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Redo",
                        tint = if (canRedo) TextPrimary else TextMuted.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Save Project (.vcp)
                IconButton(
                    onClick = onSaveProjectClick,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("capcut_save_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // CapCut Blue Upward Arrow Export Button
                Button(
                    onClick = onExportClick,
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("capcut_export_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBlue,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Export",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Export",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * CapCut Resolution & Export Settings Dialog Sheet
 */
@Composable
fun CapCutResolutionDialog(
    selectedResolution: String,
    selectedFps: Int,
    isHdrEnabled: Boolean,
    onResolutionChange: (String) -> Unit,
    onFpsChange: (Int) -> Unit,
    onHdrToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CharcoalSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Export Settings",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Resolution Section
                Text(
                    text = "RESOLUTION",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("720p", "1080p", "4K UHD").forEach { res ->
                        val isSelected = selectedResolution.contains(res, ignoreCase = true)
                        Surface(
                            onClick = { onResolutionChange(res) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ElectricBlue.copy(alpha = 0.2f) else CharcoalSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ElectricBlue else GlassBorder
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = res,
                                    color = if (isSelected) ElectricBlue else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Frame Rate Section
                Text(
                    text = "FRAME RATE",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(24, 30, 60).forEach { fps ->
                        val isSelected = selectedFps == fps
                        Surface(
                            onClick = { onFpsChange(fps) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) cyanAccent.copy(alpha = 0.2f) else CharcoalSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) cyanAccent else GlassBorder
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${fps} fps",
                                    color = if (isSelected) cyanAccent else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Smart HDR Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CharcoalSurfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Smart HDR / Vivid Tone",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Enhance dynamic range & contrast for Reels/TikTok",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                    Switch(
                        checked = isHdrEnabled,
                        onCheckedChange = onHdrToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ElectricBlue,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = CharcoalSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply Settings", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

/**
 * Authentic CapCut Level 1 Main Root Toolbar
 */
@Composable
fun CapCutMainBottomToolbar(
    activeTool: String?,
    onToolSelected: (String) -> Unit,
    tools: List<CapCutToolItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianBackground)
            .padding(vertical = 6.dp)
    ) {
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            tools.forEach { item ->
                val isSelected = activeTool == item.id
                CapCutToolbarIconButton(
                    title = item.title,
                    icon = item.icon,
                    badge = item.activeBadge,
                    isSelected = isSelected,
                    accentColor = item.accentColor,
                    onClick = { onToolSelected(item.id) },
                    modifier = Modifier.testTag("capcut_main_tool_${item.id}")
                )
            }
        }
    }
}

/**
 * Authentic CapCut Level 2 Clip Sub-Toolbar (When "Edit" / Clip is selected)
 */
@Composable
fun CapCutClipSubToolbar(
    onBackToMain: () -> Unit,
    onSplitClick: () -> Unit,
    onSpeedClick: () -> Unit,
    onVolumeClick: () -> Unit,
    onCutoutClick: () -> Unit,
    onVoiceFxClick: () -> Unit,
    onAnimationClick: () -> Unit,
    onCropClick: () -> Unit,
    onReverseClick: () -> Unit,
    onFreezeClick: () -> Unit,
    onAiProClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianBackground)
            .padding(vertical = 6.dp)
    ) {
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back to Level 1 Button (CapCut Signature Left Back Arrow in sub-menu)
            IconButton(
                onClick = onBackToMain,
                modifier = Modifier
                    .size(38.dp)
                    .background(CharcoalSurfaceVariant, CircleShape)
                    .testTag("capcut_clip_back_to_main")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Main",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            // Sub-tools
            CapCutToolbarIconButton("Split", Icons.Default.ContentCut, null, false, RadiantPink, onSplitClick)
            CapCutToolbarIconButton("Speed", Icons.Default.Speed, null, false, ElectricBlue, onSpeedClick)
            CapCutToolbarIconButton("Volume", Icons.Default.VolumeUp, null, false, cyanAccent, onVolumeClick)
            CapCutToolbarIconButton("Cutout", Icons.Default.BlurLinear, null, false, Color(0xFF00E676), onCutoutClick)
            CapCutToolbarIconButton("Voice FX", Icons.Default.Mic, null, false, purpleAccent, onVoiceFxClick)
            CapCutToolbarIconButton("Animation", Icons.Default.Animation, null, false, goldAccent, onAnimationClick)
            CapCutToolbarIconButton("Crop & Rotate", Icons.Default.Crop, null, false, orangeAccent, onCropClick)
            CapCutToolbarIconButton("AI Pro", Icons.Default.AutoAwesome, null, false, Color(0xFF00E5FF), onAiProClick)
            CapCutToolbarIconButton("Reverse", Icons.Default.SwapHoriz, null, false, Color(0xFFFF5252), onReverseClick)
            CapCutToolbarIconButton("Freeze", Icons.Default.AcUnit, null, false, cyanAccent, onFreezeClick)
        }
    }
}

/**
 * Single CapCut-Style Minimalist Vertical Icon Button with Label
 */
@Composable
fun CapCutToolbarIconButton(
    title: String,
    icon: ImageVector,
    badge: String?,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(58.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) accentColor.copy(alpha = 0.25f)
                    else CharcoalSurfaceVariant
                )
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) accentColor else GlassBorder,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) accentColor else TextPrimary,
                modifier = Modifier.size(20.dp)
            )

            if (!badge.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-2).dp)
                        .background(accentColor, CircleShape)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.Black,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            color = if (isSelected) accentColor else TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}
