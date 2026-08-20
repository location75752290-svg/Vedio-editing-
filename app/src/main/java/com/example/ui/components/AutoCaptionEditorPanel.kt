package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.VerticalAlignCenter
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AutoCaptionEngine
import com.example.engine.CaptionAnimation
import com.example.engine.CaptionFont
import com.example.engine.CaptionItem
import com.example.engine.CaptionStyleConfig
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.RadiantPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

@Composable
fun AutoCaptionEditorPanel(
    captions: MutableList<CaptionItem>,
    captionStyle: CaptionStyleConfig,
    onStyleChange: (CaptionStyleConfig) -> Unit,
    showCaptions: Boolean,
    onToggleShowCaptions: (Boolean) -> Unit,
    videoUri: Uri?,
    videoDurationMs: Long,
    currentPlayheadMs: Long,
    onSeekTo: (Long) -> Unit,
    onSnapshotRequest: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val cyanAccent = Color(0xFF00E5FF)
    val goldAccent = Color(0xFFFFD700)
    val purpleAccent = Color(0xFFB388FF)

    var activeSubTab by remember { mutableStateOf("captions") } // "captions", "style", "animation", "export"
    var selectedLanguage by remember { mutableStateOf("English") }
    var isGenerating by remember { mutableStateOf(false) }
    var generationProgress by remember { mutableIntStateOf(0) }
    var editingCaptionId by remember { mutableStateOf<String?>(null) }

    val languages = listOf("English", "Urdu Roman", "Pashto")

    val textColors = listOf(
        Pair("White", Color(0xFFFFFFFF)),
        Pair("Yellow", Color(0xFFFFD700)),
        Pair("Cyan", Color(0xFF00E5FF)),
        Pair("Pink", Color(0xFFFF007A)),
        Pair("Lime", Color(0xFF00E676)),
        Pair("Orange", Color(0xFFFF9100))
    )

    val highlightColors = listOf(
        Pair("Gold", Color(0xFFFFD700)),
        Pair("Cyan", Color(0xFF00E5FF)),
        Pair("Neon Pink", Color(0xFFFF1493)),
        Pair("Lime", Color(0xFF76FF03)),
        Pair("Sunset", Color(0xFFFF5722))
    )

    val backgroundColors = listOf(
        Pair("Dark (60%)", Color(0x99000000)),
        Pair("Black (90%)", Color(0xE6000000)),
        Pair("Navy", Color(0xCC0A192F)),
        Pair("Deep Purple", Color(0xCC2A0845)),
        Pair("None", Color.Transparent)
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = CharcoalSurfaceVariant,
        borderColor = cyanAccent.copy(alpha = 0.5f),
        borderWidth = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Icon + Title + Visibility Toggle + Close
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
                            .background(cyanAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ClosedCaption,
                            contentDescription = null,
                            tint = cyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Auto Captions",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (captions.isNotEmpty()) "${captions.size} captions • SRT sync" else "AI speech-to-text subtitling",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Show/Hide Captions Toggle
                    IconButton(
                        onClick = {
                            onSnapshotRequest()
                            onToggleShowCaptions(!showCaptions)
                        },
                        modifier = Modifier
                            .testTag("toggle_caption_visibility_btn")
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (showCaptions) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Captions",
                            tint = if (showCaptions) cyanAccent else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Close / Apply
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .testTag("close_captions_panel_btn")
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Close",
                            tint = cyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub Navigation Tabs: Captions List, Typography & Font, Animation, Export
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val subTabs = listOf(
                    Triple("captions", "Captions", Icons.Default.Subtitles),
                    Triple("style", "Style & Font", Icons.Default.FontDownload),
                    Triple("animation", "Animation", Icons.Default.Animation),
                    Triple("export", "Export SRT", Icons.Default.Description)
                )

                subTabs.forEach { (tabKey, tabLabel, tabIcon) ->
                    val isSelected = activeSubTab == tabKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) cyanAccent.copy(alpha = 0.22f) else CharcoalSurface)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) cyanAccent else GlassBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { activeSubTab = tabKey }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = tabIcon,
                                contentDescription = null,
                                tint = if (isSelected) cyanAccent else TextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tabLabel,
                                color = if (isSelected) cyanAccent else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TAB 1: CAPTIONS LIST & GENERATOR
            if (activeSubTab == "captions") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Language Selection & Generate Button Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Language Selector Chips
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            languages.forEach { lang ->
                                val isSel = selectedLanguage == lang
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) purpleAccent.copy(alpha = 0.25f) else CharcoalSurface)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSel) purpleAccent else GlassBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedLanguage = lang }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = lang,
                                        color = if (isSel) purpleAccent else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        // Generate Captions Button
                        Button(
                            onClick = {
                                if (videoUri == null) {
                                    Toast.makeText(context, "Please load a video first", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                onSnapshotRequest()
                                isGenerating = true
                                generationProgress = 0
                                scope.launch {
                                    val generated = AutoCaptionEngine.generateCaptions(
                                        context = context,
                                        videoUri = videoUri,
                                        durationMs = videoDurationMs,
                                        language = selectedLanguage
                                    ) { prog ->
                                        generationProgress = prog
                                    }
                                    captions.clear()
                                    captions.addAll(generated)
                                    isGenerating = false
                                    Toast.makeText(context, "${generated.size} Captions Generated", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !isGenerating,
                            modifier = Modifier
                                .testTag("generate_auto_captions_btn")
                                .height(32.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = cyanAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$generationProgress%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (captions.isEmpty()) "Generate AI Captions" else "Regenerate",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Captions Track List / Timeline
                    if (captions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CharcoalSurface)
                                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Subtitles,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "No captions yet",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Tap 'Generate AI Captions' to auto-detect speech",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            captions.forEachIndexed { index, caption ->
                                val isEditing = editingCaptionId == caption.id
                                val isCurrent = currentPlayheadMs in caption.startMs..caption.endMs

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isCurrent) cyanAccent.copy(alpha = 0.15f)
                                            else CharcoalSurface
                                        )
                                        .border(
                                            width = if (isCurrent) 1.5.dp else 1.dp,
                                            color = if (isCurrent) cyanAccent else GlassBorder,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable {
                                                    onSeekTo(caption.startMs)
                                                }
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isCurrent) cyanAccent else CharcoalSurfaceVariant),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "${index + 1}",
                                                        color = if (isCurrent) Color.Black else TextSecondary,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "[${formatMs(caption.startMs)} → ${formatMs(caption.endMs)}]",
                                                    color = if (isCurrent) cyanAccent else goldAccent,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }

                                            Row {
                                                // Edit button
                                                IconButton(
                                                    onClick = {
                                                        editingCaptionId = if (isEditing) null else caption.id
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = "Edit",
                                                        tint = if (isEditing) cyanAccent else TextSecondary,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }

                                                // Delete button
                                                IconButton(
                                                    onClick = {
                                                        onSnapshotRequest()
                                                        captions.removeAt(index)
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.DeleteOutline,
                                                        contentDescription = "Delete",
                                                        tint = RadiantPink,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }

                                        if (isEditing) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            var editedText by remember { mutableStateOf(caption.text) }
                                            OutlinedTextField(
                                                value = editedText,
                                                onValueChange = {
                                                    editedText = it
                                                    onSnapshotRequest()
                                                    captions[index] = caption.copy(text = it)
                                                },
                                                maxLines = 2,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = cyanAccent,
                                                    unfocusedBorderColor = GlassBorder,
                                                    focusedTextColor = TextPrimary,
                                                    unfocusedTextColor = TextPrimary
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        } else {
                                            Text(
                                                text = caption.text,
                                                color = TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(top = 4.dp, start = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Add manual caption button
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedButton(
                                onClick = {
                                    onSnapshotRequest()
                                    val start = currentPlayheadMs
                                    val end = (start + 2500L).coerceAtMost(videoDurationMs)
                                    captions.add(
                                        CaptionItem(
                                            id = UUID.randomUUID().toString(),
                                            startMs = start,
                                            endMs = end,
                                            text = "New subtitle caption"
                                        )
                                    )
                                    captions.sortBy { it.startMs }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = cyanAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Add Caption at Playhead",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // TAB 2: STYLE & FONT
            if (activeSubTab == "style") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Font Selection (Bold, Montserrat, Poppins)
                    Text(
                        text = "FONT FAMILY",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CaptionFont.values().forEach { font ->
                            val isSel = captionStyle.font == font
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) cyanAccent.copy(alpha = 0.2f) else CharcoalSurface)
                                    .border(
                                        width = if (isSel) 1.5.dp else 1.dp,
                                        color = if (isSel) cyanAccent else GlassBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        onSnapshotRequest()
                                        onStyleChange(captionStyle.copy(font = font))
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = font.displayName,
                                    color = if (isSel) cyanAccent else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Text Color Selection
                    Text(
                        text = "TEXT COLOR",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        textColors.forEach { (colorName, colorVal) ->
                            val isSel = captionStyle.textColor == colorVal
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(colorVal)
                                    .border(
                                        width = if (isSel) 2.dp else 1.dp,
                                        color = if (isSel) cyanAccent else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onSnapshotRequest()
                                        onStyleChange(captionStyle.copy(textColor = colorVal))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSel) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (colorVal == Color.White) Color.Black else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Background Box Toggle & Style
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Background Box",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = captionStyle.showBackground,
                            onCheckedChange = {
                                onSnapshotRequest()
                                onStyleChange(captionStyle.copy(showBackground = it))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = cyanAccent,
                                checkedTrackColor = cyanAccent.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Position (Top, Center, Bottom)
                    Text(
                        text = "POSITION",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Top", "Center", "Bottom").forEach { pos ->
                            val isSel = captionStyle.position == pos
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) cyanAccent.copy(alpha = 0.2f) else CharcoalSurface)
                                    .border(
                                        width = if (isSel) 1.5.dp else 1.dp,
                                        color = if (isSel) cyanAccent else GlassBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        onSnapshotRequest()
                                        onStyleChange(captionStyle.copy(position = pos))
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pos,
                                    color = if (isSel) cyanAccent else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // TAB 3: ANIMATION (Word Highlight, Pop-In, Typewriter, Classic)
            if (activeSubTab == "animation") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val animations = listOf(
                        Triple(CaptionAnimation.WORD_HIGHLIGHT, "Word Highlight (Karaoke)", "Glows active word in real-time"),
                        Triple(CaptionAnimation.POP_IN, "Pop-In Bounce", "Spring scale pop as caption appears"),
                        Triple(CaptionAnimation.TYPEWRITER, "Typewriter", "Progressive character by character reveal"),
                        Triple(CaptionAnimation.CLASSIC, "Classic Subtitle", "Clean standard subtitle layout")
                    )

                    animations.forEach { (anim, name, desc) ->
                        val isSel = captionStyle.animation == anim
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) cyanAccent.copy(alpha = 0.2f) else CharcoalSurface)
                                .border(
                                    width = if (isSel) 1.5.dp else 1.dp,
                                    color = if (isSel) cyanAccent else GlassBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    onSnapshotRequest()
                                    onStyleChange(captionStyle.copy(animation = anim))
                                }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = name,
                                        color = if (isSel) cyanAccent else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = desc,
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                                if (isSel) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = cyanAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Highlight Color Picker
                    Text(
                        text = "ACTIVE HIGHLIGHT COLOR",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        highlightColors.forEach { (name, colorVal) ->
                            val isSel = captionStyle.highlightColor == colorVal
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(colorVal)
                                    .border(
                                        width = if (isSel) 2.dp else 1.dp,
                                        color = if (isSel) Color.White else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onSnapshotRequest()
                                        onStyleChange(captionStyle.copy(highlightColor = colorVal))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSel) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // TAB 4: EXPORT (.SRT FILE OR HARD-BURN)
            if (activeSubTab == "export") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CharcoalSurface)
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Subtitles Export Options",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "• Hard-burn Subtitles: Captions will be burned directly into the video pixels during video export.\n• Separate .SRT file: Export standard SubRip subtitle track to Downloads/VisionCutAI.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (captions.isEmpty()) {
                                Toast.makeText(context, "No captions to export. Generate captions first.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            scope.launch {
                                val uri = AutoCaptionEngine.exportSrtFile(context, captions)
                                if (uri != null) {
                                    Toast.makeText(context, "SRT file exported to Downloads/VisionCutAI", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Failed to export SRT file", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("export_srt_file_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cyanAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Export .SRT Subtitle File",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSec = ms / 1000
    val millis = (ms % 1000) / 100
    val seconds = totalSec % 60
    val minutes = totalSec / 60
    return String.format(Locale.getDefault(), "%02d:%02d.%d", minutes, seconds, millis)
}
