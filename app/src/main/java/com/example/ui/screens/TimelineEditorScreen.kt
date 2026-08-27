package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Movie
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.domain.model.ExportRecord
import com.example.domain.model.SharePlatform
import com.example.engine.MediaCodecVideoExporter
import com.example.domain.repository.VisionCutRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
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

// Models for Timeline Editor
data class TimelineClip(
    val id: String,
    val title: String,
    val durationSeconds: Float,
    val startOffsetSeconds: Float,
    val color: Color,
    val type: ClipType,
    val thumbnailRes: Int? = null,
    val textContent: String? = null
)

enum class ClipType {
    VIDEO,
    AUDIO,
    TEXT,
    OVERLAY,
    EFFECT,
    STICKER
}

enum class EditorToolCategory(val label: String, val icon: ImageVector) {
    EDIT("Edit", Icons.Default.ContentCut),
    AUDIO("Audio", Icons.Default.MusicNote),
    TEXT("Text", Icons.Default.Title),
    STICKERS("Stickers", Icons.Default.EmojiEmotions),
    EFFECTS("Effects", Icons.Default.AutoAwesome),
    FILTERS("Filters", Icons.Default.Palette),
    TRANSITIONS("Transitions", Icons.Default.Difference),
    AI("AI Magic", Icons.Default.Tune),
    CANVAS("Canvas", Icons.Default.AspectRatio),
    EXPORT("Export", Icons.Default.IosShare)
}

enum class ActiveSubEditor {
    NONE,
    SPEED_CURVE,
    KEYFRAME,
    VOLUME_ENVELOPE,
    COLOR_GRADING,
    TRANSITIONS,
    AI_TOOLS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineEditorScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    var projectTitle by remember { mutableStateOf("Cyberpunk Neo City Vlog") }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPlayheadSec by remember { mutableFloatStateOf(12.4f) }
    val totalDurationSec = 60.0f
    var zoomLevel by remember { mutableFloatStateOf(1.0f) } // 0.5x to 2.5x
    var isMagneticSnapEnabled by remember { mutableStateOf(true) }
    var isGridOverlayVisible by remember { mutableStateOf(false) }
    var isFullscreenPreview by remember { mutableStateOf(false) }
    var activeAspectRatio by remember { mutableStateOf("16:9") }

    // Selected Tool & Active Sub Editor
    var selectedCategory by remember { mutableStateOf(EditorToolCategory.EDIT) }
    var activeSubEditor by remember { mutableStateOf(ActiveSubEditor.NONE) }
    var selectedClipId by remember { mutableStateOf<String?>("v1") }

    // Export & Share Sheet State
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showExportSheet by remember { mutableStateOf(false) }
    var selectedPlatform by remember { mutableStateOf(SharePlatform.TIKTOK) }
    var exportResolution by remember { mutableStateOf("1080p Full HD") }
    var exportFramerate by remember { mutableStateOf("60 fps") }
    var isExporting by remember { mutableStateOf(false) }
    var exportProgress by remember { mutableFloatStateOf(0f) }
    var exportStatusText by remember { mutableStateOf("") }
    var lastExportedRecord by remember { mutableStateOf<ExportRecord?>(null) }
    var showShareSuccessDialog by remember { mutableStateOf(false) }
    val exportHistory = remember { mutableStateListOf<ExportRecord>() }

    fun triggerAndroidShareIntent(record: ExportRecord) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "video/mp4"
            putExtra(Intent.EXTRA_SUBJECT, record.title)
            putExtra(Intent.EXTRA_TEXT, "Created with VisionCut AI Video Editor 🚀 #VisionCutAI #${record.platform.id}")
            record.platform.packageName?.let { pkg ->
                setPackage(pkg)
            }
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Share video to ${record.platform.title}")
        try {
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "video/mp4"
                putExtra(Intent.EXTRA_TEXT, "Created with VisionCut AI")
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "Share Video"))
        }
    }

    fun startBackgroundExport() {
        isExporting = true
        exportProgress = 0f
        exportStatusText = "Initializing MediaCodec Hardware Engine..."
        com.example.engine.RenderQueue.add(
            com.example.engine.RenderJob(
                title = "$exportResolution Export",
                durationSeconds = totalDurationSec
            )
        )
        val exporter = MediaCodecVideoExporter(context)
        val repository = VisionCutRepository(context)
        coroutineScope.launch {
            exporter.renderAndExportVideo(
                projectTitle = projectTitle,
                platform = selectedPlatform,
                resolutionStr = exportResolution,
                framerateStr = exportFramerate,
                durationSeconds = totalDurationSec
            ).collect { status ->
                exportProgress = status.progress
                exportStatusText = status.statusText
                status.completedRecord?.let { record ->
                    isExporting = false
                    exportHistory.add(0, record)
                    lastExportedRecord = record
                    repository.recordExport(record)
                    showShareSuccessDialog = true
                    Toast.makeText(context, "Export Complete! Saved to Gallery.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Sample Clips across multiple tracks
    var videoClips by remember {
        mutableStateOf(
            listOf(
                TimelineClip("v1", "Neon Alley Entry", 15f, 0f, NeonIndigo, ClipType.VIDEO, R.drawable.thumb_cyberpunk_1785491147932),
                TimelineClip("v2", "Sunset Highway Reel", 20f, 15f, DeepPurple, ClipType.VIDEO, R.drawable.thumb_sunset_1785491163192),
                TimelineClip("v3", "Cyber Drone Shot", 25f, 35f, ElectricBlue, ClipType.VIDEO, R.drawable.thumb_cyberpunk_1785491147932)
            )
        )
    }

    var audioClips by remember {
        mutableStateOf(
            listOf(
                TimelineClip("a1", "Synthwave Cyber BGM.mp3", 40f, 0f, ElectricBlue, ClipType.AUDIO),
                TimelineClip("a2", "AI Voiceover Narrative.wav", 18f, 10f, RadiantPink, ClipType.AUDIO)
            )
        )
    }

    var textClips by remember {
        mutableStateOf(
            listOf(
                TimelineClip("t1", "Title: NEO TOKYO 2077", 12f, 2f, Color(0xFFF59E0B), ClipType.TEXT, textContent = "NEO TOKYO 2077"),
                TimelineClip("t2", "Caption: Futuristic Vibe", 15f, 22f, Color(0xFF10B981), ClipType.TEXT, textContent = "Futuristic Vibe")
            )
        )
    }

    var effectClips by remember {
        mutableStateOf(
            listOf(
                TimelineClip("fx1", "Cyberpunk Glitch FX", 10f, 5f, RadiantPink, ClipType.EFFECT),
                TimelineClip("fx2", "HDR ColorLUT Boost", 25f, 20f, DeepPurple, ClipType.EFFECT)
            )
        )
    }

    var stickerClips by remember {
        mutableStateOf(
            listOf(
                TimelineClip("s1", "Subscribe Badge", 8f, 12f, Color(0xFFEC4899), ClipType.STICKER)
            )
        )
    }

    // History state
    val undoStack = remember { mutableStateListOf<List<TimelineClip>>() }
    val redoStack = remember { mutableStateListOf<List<TimelineClip>>() }
    var canUndo by remember { mutableStateOf(false) }
    var canRedo by remember { mutableStateOf(false) }

    fun pushSnapshot() {
        undoStack.add(videoClips.map { it.copy() })
        redoStack.clear()
        canUndo = undoStack.isNotEmpty()
        canRedo = false
    }

    fun handleUndo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(videoClips.map { it.copy() })
            videoClips = undoStack.removeAt(undoStack.lastIndex)
            canUndo = undoStack.isNotEmpty()
            canRedo = redoStack.isNotEmpty()
        }
    }

    fun handleRedo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(videoClips.map { it.copy() })
            videoClips = redoStack.removeAt(redoStack.lastIndex)
            canUndo = undoStack.isNotEmpty()
            canRedo = redoStack.isNotEmpty()
        }
    }

    fun handleSplit() {
        val target = videoClips.find { it.id == selectedClipId }
        if (target != null && currentPlayheadSec > target.startOffsetSeconds && currentPlayheadSec < (target.startOffsetSeconds + target.durationSeconds)) {
            pushSnapshot()
            val firstDuration = currentPlayheadSec - target.startOffsetSeconds
            val secondDuration = target.durationSeconds - firstDuration
            val firstClip = target.copy(durationSeconds = firstDuration)
            val secondClip = target.copy(
                id = "v_${System.currentTimeMillis()}",
                title = "${target.title} (Part 2)",
                startOffsetSeconds = currentPlayheadSec,
                durationSeconds = secondDuration
            )
            videoClips = videoClips.flatMap {
                if (it.id == selectedClipId) listOf(firstClip, secondClip) else listOf(it)
            }
        }
    }

    fun handleDuplicate() {
        val target = videoClips.find { it.id == selectedClipId }
        if (target != null) {
            pushSnapshot()
            val newClip = target.copy(
                id = "v_${System.currentTimeMillis()}",
                title = "${target.title} (Copy)",
                startOffsetSeconds = target.startOffsetSeconds + target.durationSeconds
            )
            videoClips = videoClips + newClip
            selectedClipId = newClip.id
        }
    }

    fun handleDelete() {
        if (selectedClipId != null) {
            pushSnapshot()
            videoClips = videoClips.filterNot { it.id == selectedClipId }
            audioClips = audioClips.filterNot { it.id == selectedClipId }
            textClips = textClips.filterNot { it.id == selectedClipId }
            effectClips = effectClips.filterNot { it.id == selectedClipId }
            selectedClipId = null
        }
    }

    fun handleImportMedia() {
        pushSnapshot()
        val lastEnd = (videoClips.maxOfOrNull { it.startOffsetSeconds + it.durationSeconds } ?: 0f)
        val newClip = TimelineClip(
            id = "v_${System.currentTimeMillis()}",
            title = "Imported B-Roll Clip #${videoClips.size + 1}.mp4",
            durationSeconds = 15f,
            startOffsetSeconds = lastEnd,
            color = ElectricBlue,
            type = ClipType.VIDEO,
            thumbnailRes = R.drawable.thumb_cyberpunk_1785491147932
        )
        videoClips = videoClips + newClip
        selectedClipId = newClip.id
    }

    // Sub-Editor Values
    var speedCurveMultiplier by remember { mutableFloatStateOf(1.2f) }
    var volumeLevel by remember { mutableFloatStateOf(100f) }
    var exposureValue by remember { mutableFloatStateOf(0f) }
    var contrastValue by remember { mutableFloatStateOf(10f) }
    var saturationValue by remember { mutableFloatStateOf(15f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
    ) {
        // TOP APP BAR / HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .testTag("editor_back_button")
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CharcoalSurface)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = projectTitle,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Rename",
                            tint = TextMuted,
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .size(14.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElectricBlue.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "4K 60fps • HDR",
                                color = ElectricBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = " • Saved",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Undo Button
                IconButton(
                    onClick = { handleUndo() },
                    enabled = canUndo,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (canUndo) TextPrimary else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Redo Button
                IconButton(
                    onClick = { handleRedo() },
                    enabled = canRedo,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Redo",
                        tint = if (canRedo) TextPrimary else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Magnetic Snap Toggle
                Box(
                    modifier = Modifier
                        .testTag("editor_magnetic_snap_toggle")
                        .clip(CircleShape)
                        .background(if (isMagneticSnapEnabled) ElectricBlue.copy(alpha = 0.2f) else CharcoalSurface)
                        .clickable { isMagneticSnapEnabled = !isMagneticSnapEnabled }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Grid3x3,
                        contentDescription = "Magnetic Snap",
                        tint = if (isMagneticSnapEnabled) ElectricBlue else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Prominent Export Button
                Box(
                    modifier = Modifier
                        .testTag("editor_export_button")
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(listOf(ElectricBlue, RadiantPink))
                        )
                        .clickable { showExportSheet = true }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.IosShare,
                            contentDescription = "Export",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = " Export",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // LARGE VIDEO PREVIEW AREA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (isFullscreenPreview) 1f else 0.42f)
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black)
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
        ) {
            // Preview Image Frame
            Image(
                painter = painterResource(
                    id = if (currentPlayheadSec > 15f) R.drawable.thumb_sunset_1785491163192 else R.drawable.thumb_cyberpunk_1785491147932
                ),
                contentDescription = "Video Preview Frame",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Optional Grid Overlay
            if (isGridOverlayVisible) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    // Rule of Thirds Lines
                    drawLine(GlassBorder, Offset(width / 3, 0f), Offset(width / 3, height), strokeWidth = 1f)
                    drawLine(GlassBorder, Offset(2 * width / 3, 0f), Offset(2 * width / 3, height), strokeWidth = 1f)
                    drawLine(GlassBorder, Offset(0f, height / 3), Offset(width, height / 3), strokeWidth = 1f)
                    drawLine(GlassBorder, Offset(0f, 2 * height / 3), Offset(width, 2 * height / 3), strokeWidth = 1f)
                }
            }

            // Text Track Overlay Simulation
            if (currentPlayheadSec in 2f..14f) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(1.dp, NeonIndigo, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "NEO TOKYO 2077",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                }
            }

            // Top Overlay: Aspect Ratio Badge & Grid Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Aspect Ratio Selector Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(CharcoalSurface.copy(alpha = 0.85f))
                        .clickable {
                            activeAspectRatio = when (activeAspectRatio) {
                                "16:9" -> "9:16"
                                "9:16" -> "1:1"
                                else -> "16:9"
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "📐 $activeAspectRatio",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Grid & Fullscreen Controls
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(CharcoalSurface.copy(alpha = 0.85f))
                            .clickable { isGridOverlayVisible = !isGridOverlayVisible },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Grid",
                            tint = if (isGridOverlayVisible) ElectricBlue else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(CharcoalSurface.copy(alpha = 0.85f))
                            .clickable { isFullscreenPreview = !isFullscreenPreview },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Bottom Overlay Controls & Timecode
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timecode display
                    Text(
                        text = "${formatTimecode(currentPlayheadSec)} / ${formatTimecode(totalDurationSec)}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )

                    // Transport Controls (Prev, Step Back, Play/Pause, Step Fwd, Next)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { currentPlayheadSec = 0f },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "Start", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = { currentPlayheadSec = (currentPlayheadSec - 0.1f).coerceAtLeast(0f) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.FastRewind, contentDescription = "-1 Frame", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }

                        // Play/Pause Button
                        Box(
                            modifier = Modifier
                                .testTag("editor_play_pause_button")
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(listOf(ElectricBlue, DeepPurple))
                                )
                                .clickable { isPlaying = !isPlaying },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        IconButton(
                            onClick = { currentPlayheadSec = (currentPlayheadSec + 0.1f).coerceAtMost(totalDurationSec) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.FastForward, contentDescription = "+1 Frame", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = { currentPlayheadSec = totalDurationSec },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.SkipNext, contentDescription = "End", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // QUICK CLIP ACTION TOOLBAR (Split, Trim, Speed, Volume, Color Grade, Keyframe, Duplicate, Delete)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                item {
                    QuickActionButton(
                        icon = Icons.Default.Add,
                        label = "Import",
                        testTag = "action_import",
                        iconTint = ElectricBlue,
                        onClick = { handleImportMedia() }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.ContentCut,
                        label = "Split",
                        testTag = "action_split",
                        onClick = { handleSplit() }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.Speed,
                        label = "Speed",
                        testTag = "action_speed",
                        isSelected = activeSubEditor == ActiveSubEditor.SPEED_CURVE,
                        onClick = {
                            activeSubEditor = if (activeSubEditor == ActiveSubEditor.SPEED_CURVE) ActiveSubEditor.NONE else ActiveSubEditor.SPEED_CURVE
                        }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.VolumeUp,
                        label = "Volume",
                        testTag = "action_volume",
                        isSelected = activeSubEditor == ActiveSubEditor.VOLUME_ENVELOPE,
                        onClick = {
                            activeSubEditor = if (activeSubEditor == ActiveSubEditor.VOLUME_ENVELOPE) ActiveSubEditor.NONE else ActiveSubEditor.VOLUME_ENVELOPE
                        }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.Palette,
                        label = "Color",
                        testTag = "action_color",
                        isSelected = activeSubEditor == ActiveSubEditor.COLOR_GRADING,
                        onClick = {
                            activeSubEditor = if (activeSubEditor == ActiveSubEditor.COLOR_GRADING) ActiveSubEditor.NONE else ActiveSubEditor.COLOR_GRADING
                        }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.Transform,
                        label = "+Keyframe",
                        testTag = "action_keyframe",
                        isSelected = activeSubEditor == ActiveSubEditor.KEYFRAME,
                        onClick = {
                            activeSubEditor = if (activeSubEditor == ActiveSubEditor.KEYFRAME) ActiveSubEditor.NONE else ActiveSubEditor.KEYFRAME
                        }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.Difference,
                        label = "Transition",
                        testTag = "action_transition",
                        isSelected = activeSubEditor == ActiveSubEditor.TRANSITIONS,
                        onClick = {
                            activeSubEditor = if (activeSubEditor == ActiveSubEditor.TRANSITIONS) ActiveSubEditor.NONE else ActiveSubEditor.TRANSITIONS
                        }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.ContentCopy,
                        label = "Duplicate",
                        testTag = "action_duplicate",
                        onClick = { handleDuplicate() }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.Delete,
                        label = "Delete",
                        testTag = "action_delete",
                        iconTint = RadiantPink,
                        onClick = { handleDelete() }
                    )
                }
            }
        }

        // ACTIVE SUB-EDITOR DRAWER (If any sub-editor selected: Speed Curve, Keyframe, Volume, Color Grading)
        AnimatedVisibility(
            visible = activeSubEditor != ActiveSubEditor.NONE,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = CharcoalSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (activeSubEditor) {
                                ActiveSubEditor.SPEED_CURVE -> "⚡ Speed Curve Editor"
                                ActiveSubEditor.KEYFRAME -> "💎 Motion Keyframe Interpolator"
                                ActiveSubEditor.VOLUME_ENVELOPE -> "🔊 Volume Envelope & EQ"
                                ActiveSubEditor.COLOR_GRADING -> "🎨 Color Grading & LUTs"
                                ActiveSubEditor.TRANSITIONS -> "✨ Transition Effects"
                                else -> "Clip Options"
                            },
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = { activeSubEditor = ActiveSubEditor.NONE },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    when (activeSubEditor) {
                        ActiveSubEditor.SPEED_CURVE -> {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Speed: ${String.format("%.1fx", speedCurveMultiplier)}", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Smooth Bezier Curve", color = TextMuted, fontSize = 11.sp)
                                }
                                Slider(
                                    value = speedCurveMultiplier,
                                    onValueChange = { speedCurveMultiplier = it },
                                    valueRange = 0.1f..10.0f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = ElectricBlue,
                                        activeTrackColor = ElectricBlue,
                                        inactiveTrackColor = CharcoalSurfaceVariant
                                    )
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val presets = listOf("Standard", "Montage Hero", "Bullet Time", "Jumper", "Flash Cut")
                                    items(presets) { preset ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(CharcoalSurfaceVariant)
                                                .clickable { }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(preset, color = TextPrimary, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }

                        ActiveSubEditor.VOLUME_ENVELOPE -> {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Volume: ${volumeLevel.toInt()}%", color = RadiantPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("AI Noise Reduction Active", color = TextMuted, fontSize = 11.sp)
                                }
                                Slider(
                                    value = volumeLevel,
                                    onValueChange = { volumeLevel = it },
                                    valueRange = 0f..200f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = RadiantPink,
                                        activeTrackColor = RadiantPink,
                                        inactiveTrackColor = CharcoalSurfaceVariant
                                    )
                                )
                            }
                        }

                        ActiveSubEditor.COLOR_GRADING -> {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Exposure: ${exposureValue.toInt()} • Contrast: ${contrastValue.toInt()}", color = NeonIndigo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Cyberpunk LUT Applied", color = TextMuted, fontSize = 11.sp)
                                }
                                Slider(
                                    value = exposureValue,
                                    onValueChange = { exposureValue = it },
                                    valueRange = -50f..50f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonIndigo,
                                        activeTrackColor = NeonIndigo,
                                        inactiveTrackColor = CharcoalSurfaceVariant
                                    )
                                )
                            }
                        }

                        ActiveSubEditor.TRANSITIONS -> {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val transitionsList = listOf("Glitch Dissolve", "Cyber Zoom", "Flash Cut", "Whip Pan", "Warp Speed")
                                items(transitionsList) { item ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                Brush.horizontalGradient(listOf(DeepPurple, NeonIndigo))
                                            )
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(item, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }

        // PROFESSIONAL MULTI-TRACK TIMELINE WORKSPACE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.58f)
                .background(CharcoalSurfaceVariant.copy(alpha = 0.5f))
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Timeline Controls Header: Zoom Controls & Active Track Labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CharcoalSurface)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tracks (V1, V2, T1, A1, FX)",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Zoom Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.5f) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "${(zoomLevel * 100).toInt()}%",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        IconButton(
                            onClick = { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(2.5f) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Scrollable Timeline Content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 80.dp)
                    ) {
                        // 1. TIMELINE RULER WITH TIMESTAMPS
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp)
                                .background(Color.Black.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 60.dp, end = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (sec in 0..60 step 5) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(6.dp)
                                                .background(GlassBorder)
                                        )
                                        Text(
                                            text = "${sec}s",
                                            color = TextMuted,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // TRACK 1: VIDEO TRACK (V1)
                        TrackRow(
                            trackLabel = "V1",
                            trackIcon = Icons.Default.Layers,
                            trackColor = NeonIndigo
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 60.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                videoClips.forEach { clip ->
                                    val isSelected = clip.id == selectedClipId
                                    Box(
                                        modifier = Modifier
                                            .width((clip.durationSeconds * 6 * zoomLevel).dp)
                                            .height(54.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(clip.color.copy(alpha = 0.85f))
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) ElectricBlue else GlassBorder,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { selectedClipId = clip.id }
                                    ) {
                                        clip.thumbnailRes?.let { resId ->
                                            Image(
                                                painter = painterResource(id = resId),
                                                contentDescription = clip.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.35f))
                                                .padding(6.dp)
                                        ) {
                                            Text(
                                                text = clip.title,
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // TRACK 2: OVERLAY / PIP TRACK (V2)
                        TrackRow(
                            trackLabel = "V2",
                            trackIcon = Icons.Default.Layers,
                            trackColor = DeepPurple
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 120.dp)
                                    .width((20f * 6 * zoomLevel).dp)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DeepPurple.copy(alpha = 0.6f))
                                    .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "PIP Overlay Video.mp4",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // TRACK 3: TEXT TRACK (T1)
                        TrackRow(
                            trackLabel = "T1",
                            trackIcon = Icons.Default.Title,
                            trackColor = Color(0xFFF59E0B)
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 60.dp),
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                textClips.forEach { clip ->
                                    Box(
                                        modifier = Modifier
                                            .width((clip.durationSeconds * 6 * zoomLevel).dp)
                                            .height(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(clip.color.copy(alpha = 0.7f))
                                            .border(1.dp, clip.color, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "✍️ ${clip.textContent}",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // TRACK 4: AUDIO TRACK (A1)
                        TrackRow(
                            trackLabel = "A1",
                            trackIcon = Icons.Default.MusicNote,
                            trackColor = ElectricBlue
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 60.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                audioClips.forEach { clip ->
                                    Box(
                                        modifier = Modifier
                                            .width((clip.durationSeconds * 6 * zoomLevel).dp)
                                            .height(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(ElectricBlue.copy(alpha = 0.35f))
                                            .border(1.dp, ElectricBlue, RoundedCornerShape(10.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.GraphicEq,
                                                contentDescription = null,
                                                tint = ElectricBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = " ${clip.title}",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // TRACK 5: EFFECTS TRACK (FX)
                        TrackRow(
                            trackLabel = "FX",
                            trackIcon = Icons.Default.AutoAwesome,
                            trackColor = RadiantPink
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 100.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                effectClips.forEach { clip ->
                                    Box(
                                        modifier = Modifier
                                            .width((clip.durationSeconds * 6 * zoomLevel).dp)
                                            .height(30.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(RadiantPink.copy(alpha = 0.4f))
                                            .border(1.dp, RadiantPink, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "✨ ${clip.title}",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // PLAYHEAD NEEDLE INDICATOR LINE OVERLAY
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(2.dp)
                            .offset(x = (60 + currentPlayheadSec * 6 * zoomLevel).dp)
                            .background(ElectricBlue)
                    ) {
                        // Playhead Diamond Handle at Top
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .offset(x = (-6).dp, y = 0.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }
                }
            }
        }

        // BOTTOM CATEGORY NAVIGATION TOOLBAR
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(CharcoalSurface)
                .border(1.dp, GlassBorder)
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(EditorToolCategory.entries.toTypedArray()) { category ->
                val isSelected = category == selectedCategory
                Column(
                    modifier = Modifier
                        .testTag("category_${category.name.lowercase()}")
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(listOf(ElectricBlue.copy(alpha = 0.25f), DeepPurple.copy(alpha = 0.25f)))
                            } else {
                                Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                            }
                        )
                        .clickable {
                            selectedCategory = category
                            if (category == EditorToolCategory.EXPORT) {
                                showExportSheet = true
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.label,
                        tint = if (isSelected) ElectricBlue else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = category.label,
                        color = if (isSelected) TextPrimary else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }

    // EXPORT RENDER & SHARE MODAL BOTTOM SHEET
    if (showExportSheet) {
        ModalBottomSheet(
            onDismissRequest = { if (!isExporting) showExportSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = CharcoalSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Export & Share Studio",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Direct Multi-Platform Social Media Sharing Engine",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    if (!isExporting) {
                        IconButton(onClick = { showExportSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (isExporting) {
                    // Export Progress State
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Exporting Video in Progress...",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = exportStatusText,
                                color = ElectricBlue,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                progress = { exportProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = ElectricBlue,
                                trackColor = CharcoalSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "${(exportProgress * 100).toInt()}% Completed",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Platform Presets Selector
                    Text("Select Target Platform", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(SharePlatform.values()) { platform ->
                            val isSelected = platform == selectedPlatform
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) ElectricBlue.copy(alpha = 0.25f) else CharcoalSurfaceVariant)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (isSelected) ElectricBlue else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        selectedPlatform = platform
                                        if (platform.supportedResolutions.isNotEmpty()) {
                                            exportResolution = platform.supportedResolutions.first()
                                        }
                                        if (platform.supportedFramerates.isNotEmpty()) {
                                            exportFramerate = platform.supportedFramerates.first()
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (platform) {
                                            SharePlatform.GALLERY -> Icons.Default.Download
                                            SharePlatform.FILES -> Icons.Default.Folder
                                            else -> Icons.Default.Share
                                        },
                                        contentDescription = platform.title,
                                        tint = if (isSelected) ElectricBlue else TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = platform.title,
                                            color = if (isSelected) TextPrimary else TextSecondary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = platform.defaultAspectRatio,
                                            color = TextMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Resolution Options (Dynamic per selected platform)
                    Text("Export Resolution", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        selectedPlatform.supportedResolutions.forEach { res ->
                            val isSelected = res == exportResolution
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) ElectricBlue else CharcoalSurfaceVariant)
                                    .clickable { exportResolution = res }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(res, color = if (isSelected) Color.White else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Framerate Options
                    Text("Frame Rate", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        selectedPlatform.supportedFramerates.forEach { fps ->
                            val isSelected = fps == exportFramerate
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) RadiantPink else CharcoalSurfaceVariant)
                                    .clickable { exportFramerate = fps }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(fps, color = if (isSelected) Color.White else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Export Specs Summary
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Preset Format", color = TextMuted, fontSize = 11.sp)
                                Text("${selectedPlatform.title} (${selectedPlatform.defaultAspectRatio})", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Est. File Size", color = TextMuted, fontSize = 11.sp)
                                Text("340 MB", color = ElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Start Export Button
                    GradientButton(
                        text = "🚀 Export for ${selectedPlatform.title}",
                        onClick = { startBackgroundExport() },
                        modifier = Modifier.testTag("export_sheet_start_button")
                    )
                }

                // Recent Exports Section
                if (exportHistory.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Recent Export History", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        exportHistory.take(3).forEach { record ->
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { triggerAndroidShareIntent(record) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(record.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("${record.platform.title} • ${record.resolution} @ ${record.framerate}", color = TextSecondary, fontSize = 11.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Share", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = ElectricBlue, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // SHARE CONFIRMATION DIALOG
    if (showShareSuccessDialog && lastExportedRecord != null) {
        val record = lastExportedRecord!!
        ModalBottomSheet(
            onDismissRequest = { showShareSuccessDialog = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = CharcoalSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Export Ready!",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your video has been rendered and saved to Gallery.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                GradientButton(
                    text = "📤 Share to ${record.platform.title}",
                    onClick = {
                        showShareSuccessDialog = false
                        showExportSheet = false
                        triggerAndroidShareIntent(record)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CharcoalSurfaceVariant)
                        .clickable { showShareSuccessDialog = false }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Close", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// HELPER COMPONENTS
@Composable
private fun TrackRow(
    trackLabel: String,
    trackIcon: ImageVector,
    trackColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        // Track Header Badge on Left
        Box(
            modifier = Modifier
                .width(52.dp)
                .fillMaxHeight()
                .background(CharcoalSurface)
                .border(1.dp, GlassBorder)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = trackIcon,
                    contentDescription = trackLabel,
                    tint = trackColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = trackLabel,
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Track Content
        content()
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    testTag: String,
    isSelected: Boolean = false,
    iconTint: Color = ElectricBlue,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) ElectricBlue.copy(alpha = 0.25f) else CharcoalSurface)
            .border(
                width = 1.dp,
                color = if (isSelected) ElectricBlue else GlassBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) ElectricBlue else iconTint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = " $label",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun formatTimecode(seconds: Float): String {
    val totalMs = (seconds * 1000).toLong()
    val mins = (totalMs / 1000) / 60
    val secs = (totalMs / 1000) % 60
    val frames = (totalMs % 1000) / 40
    return String.format("%02d:%02d:%02d", mins, secs, frames)
}
