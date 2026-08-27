package com.example.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.layout.layout
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.R
import com.example.data.StickerItem
import com.example.data.StickersRepository
import com.example.data.UrduQuote
import com.example.data.UrduQuotesRepository
import com.example.engine.PhotoAdjustments
import com.example.engine.ProAiImageEngine
import com.example.engine.ProBackgroundSet
import com.example.engine.ProBgPreset
import com.example.engine.VisionCutFilterEngine
import com.example.ui.components.FilterThumbnailBar
import com.example.ui.components.GlassCard
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.RadiantPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

enum class PhotoEditorTab(val label: String, val icon: ImageVector) {
    AI("AI Tools", Icons.Default.AutoAwesome),
    PRO_PICSART("PicsArt Pro", Icons.Default.AutoAwesome),
    BG_REMOVE("BG Replace", Icons.Default.Layers),
    DOUBLE_EXP("Double Exp", Icons.Default.Compare),
    SPLASH("Color Splash", Icons.Default.AutoFixHigh),
    SHAPES("Shape Frames", Icons.Default.Crop),
    TOOLS("Tools", Icons.Default.Tune),
    FX("FX Effects", Icons.Default.AutoFixHigh),
    BEAUTIFY("Beautify", Icons.Default.Layers),
    FILTER("Filters", Icons.Default.Filter),
    ADJUST("Adjust", Icons.Default.Adjust),
    MASKS("Mask & Light", Icons.Default.ShowChart),
    FIT("Fit & Frame", Icons.Default.Crop),
    SHAYARI("Shayari", Icons.Default.FormatQuote),
    TEXT("Text", Icons.Default.TextFields),
    STICKERS("Stickers", Icons.Default.EmojiEmotions)
}

data class PhotoEditorState(
    val bitmap: Bitmap? = null,
    val filterId: String = "normal",
    val filterIntensity: Float = 1.0f,
    val brightness: Float = 0f,
    val contrast: Float = 0f,
    val saturation: Float = 0f,
    val temperature: Float = 0f,
    val rotation: Float = 0f,
    val textOverlay: String = "",
    val textOffsetX: Float = 0f,
    val textOffsetY: Float = 0f,
    val textScale: Float = 1f,
    val textRotation: Float = 0f,
    val textColor: Color = Color.White,
    val hasTextBackground: Boolean = true,
    val overlayEmoji: String = "",
    val emojiOffsetX: Float = 0f,
    val emojiOffsetY: Float = 0f,
    val emojiScale: Float = 1f,
    val emojiRotation: Float = 0f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoEditorScreen(
    initialPhotoUri: Uri? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var photoUri by remember(initialPhotoUri) { mutableStateOf<Uri?>(initialPhotoUri) }
    var loadedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoadingBitmap by remember { mutableStateOf(false) }
    var isAiProcessing by remember { mutableStateOf(false) }
    var aiProcessingTitle by remember { mutableStateOf("") }

    LaunchedEffect(initialPhotoUri) {
        if (initialPhotoUri != null) {
            photoUri = initialPhotoUri
        }
    }

    // Active bottom tab
    var selectedTab by remember { mutableStateOf(PhotoEditorTab.FILTER) }

    // Filter & Adjustments State
    var selectedFilterId by remember { mutableStateOf("normal") }
    var filterIntensity by remember { mutableFloatStateOf(1.0f) }

    // Adjustments: Range -100 to +100
    var brightness by remember { mutableFloatStateOf(0f) }
    var contrast by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(0f) }
    var temperature by remember { mutableFloatStateOf(0f) }

    // Tools state
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    // Text & Overlay state with position/drag/scale/rotation/colors
    var textOverlay by remember { mutableStateOf("") }
    var textOffsetX by remember { mutableFloatStateOf(0f) }
    var textOffsetY by remember { mutableFloatStateOf(160f) } // default near bottom
    var textScale by remember { mutableFloatStateOf(1f) }
    var textRotation by remember { mutableFloatStateOf(0f) }
    var textColor by remember { mutableStateOf(Color.White) }
    var hasTextBackground by remember { mutableStateOf(true) }

    var overlayEmoji by remember { mutableStateOf("") }
    var emojiOffsetX by remember { mutableFloatStateOf(0f) }
    var emojiOffsetY by remember { mutableFloatStateOf(-160f) } // default near top
    var emojiScale by remember { mutableFloatStateOf(1f) }
    var emojiRotation by remember { mutableFloatStateOf(0f) }

    var showTextDialog by remember { mutableStateOf(false) }
    var showOverlayDialog by remember { mutableStateOf(false) }
    var tempTextInput by remember { mutableStateOf("") }

    // Shayari & Stickers Tabs State
    var selectedShayariCategory by remember { mutableStateOf("سب (All)") }
    var customShayariInput by remember { mutableStateOf("") }
    var selectedStickerCategory by remember { mutableStateOf("All") }

    // 50 Backgrounds Set State
    var showBg50Modal by remember { mutableStateOf(false) }
    var selectedBgCategory by remember { mutableStateOf("All") }
    var showManualCutoutDialog by remember { mutableStateOf(false) }

    // Undo / Redo History
    var history by remember { mutableStateOf(listOf<PhotoEditorState>()) }
    var historyIndex by remember { mutableIntStateOf(-1) }

    // Compare Before/After state (When true, show original unedited photo)
    var showOriginal by remember { mutableStateOf(false) }
    var originalUneditedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var splitSliderPosition by remember { mutableFloatStateOf(0.5f) }
    var bgSubTabCategory by remember { mutableStateOf("Color") }
    var bgBottomNavTab by remember { mutableStateOf("Background") }

    fun captureCurrentState(currentBitmap: Bitmap? = loadedBitmap): PhotoEditorState {
        return PhotoEditorState(
            bitmap = currentBitmap,
            filterId = selectedFilterId,
            filterIntensity = filterIntensity,
            brightness = brightness,
            contrast = contrast,
            saturation = saturation,
            temperature = temperature,
            rotation = rotationAngle,
            textOverlay = textOverlay,
            textOffsetX = textOffsetX,
            textOffsetY = textOffsetY,
            textScale = textScale,
            textRotation = textRotation,
            textColor = textColor,
            hasTextBackground = hasTextBackground,
            overlayEmoji = overlayEmoji,
            emojiOffsetX = emojiOffsetX,
            emojiOffsetY = emojiOffsetY,
            emojiScale = emojiScale,
            emojiRotation = emojiRotation
        )
    }

    fun pushState(newBitmap: Bitmap? = loadedBitmap) {
        val currentState = captureCurrentState(newBitmap)
        val newHistory = history.take(historyIndex + 1).toMutableList()
        newHistory.add(currentState)
        history = newHistory
        historyIndex = newHistory.size - 1
    }

    fun applyState(state: PhotoEditorState) {
        if (state.bitmap != null) {
            loadedBitmap = state.bitmap
        }
        selectedFilterId = state.filterId
        filterIntensity = state.filterIntensity
        brightness = state.brightness
        contrast = state.contrast
        saturation = state.saturation
        temperature = state.temperature
        rotationAngle = state.rotation
        textOverlay = state.textOverlay
        textOffsetX = state.textOffsetX
        textOffsetY = state.textOffsetY
        textScale = state.textScale
        textRotation = state.textRotation
        textColor = state.textColor
        hasTextBackground = state.hasTextBackground
        overlayEmoji = state.overlayEmoji
        emojiOffsetX = state.emojiOffsetX
        emojiOffsetY = state.emojiOffsetY
        emojiScale = state.emojiScale
        emojiRotation = state.emojiRotation
    }

    fun undo() {
        if (historyIndex > 0) {
            historyIndex--
            applyState(history[historyIndex])
        }
    }

    fun redo() {
        if (historyIndex < history.size - 1) {
            historyIndex++
            applyState(history[historyIndex])
        }
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
        }
    }

    // Custom Background Gallery Picker Launcher
    val customBgGalleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val currentBmp = loadedBitmap
            if (currentBmp != null && !isAiProcessing) {
                isAiProcessing = true
                aiProcessingTitle = "Applying Custom Background from Gallery..."
                scope.launch {
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val pickedBgBitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()

                        if (pickedBgBitmap != null) {
                            val resultBmp = ProAiImageEngine.replaceBackgroundWithCustomImage(
                                src = originalUneditedBitmap ?: currentBmp,
                                customBg = pickedBgBitmap
                            )
                            loadedBitmap = resultBmp
                            pushState(resultBmp)
                            Toast.makeText(context, "100% Real Custom Gallery Background Applied!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Could not load image from gallery", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Gallery Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isAiProcessing = false
                    }
                }
            }
        }
    }

    // Load Bitmap
    LaunchedEffect(photoUri) {
        isLoadingBitmap = true
        withContext(Dispatchers.IO) {
            try {
                val bmp = if (photoUri != null) {
                    context.contentResolver.openInputStream(photoUri!!)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                } else {
                    BitmapFactory.decodeResource(context.resources, R.drawable.user_black_kurta_portrait_1787388838760)
                }
                withContext(Dispatchers.Main) {
                    loadedBitmap = bmp
                    originalUneditedBitmap = bmp
                    isLoadingBitmap = false
                    if (history.isEmpty()) {
                        pushState()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoadingBitmap = false
                    Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Helper to get blended matrix with adjustments
    fun computeCombinedMatrixArray(): FloatArray {
        val mappedBrightness = brightness // -100 to 100
        val mappedContrast = (1f + contrast / 100f).coerceIn(0.2f, 2.5f)
        val mappedSaturation = (1f + saturation / 100f).coerceIn(0f, 2.5f)
        val mappedWarmth = temperature / 2f

        val adj = PhotoAdjustments(
            brightness = mappedBrightness,
            contrast = mappedContrast,
            saturation = mappedSaturation,
            warmth = mappedWarmth
        )
        return VisionCutFilterEngine.getBlendedMatrix(selectedFilterId, filterIntensity, adj)
    }

    // Process Final Output Bitmap with precise Text & Sticker transforms
    suspend fun renderOutputBitmap(export4K: Boolean = false): Bitmap? = withContext(Dispatchers.Default) {
        val base = loadedBitmap ?: return@withContext null
        val targetWidth = if (export4K) 3840 else base.width
        val targetHeight = if (export4K) (3840f / base.width * base.height).toInt() else base.height

        val scaled = Bitmap.createScaledBitmap(base, targetWidth, targetHeight, true)
        val result = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(result)

        // Rotation
        val matrix = Matrix()
        matrix.postRotate(rotationAngle, targetWidth / 2f, targetHeight / 2f)

        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

        val matrixArray = computeCombinedMatrixArray()
        val finalColorMatrix = android.graphics.ColorMatrix(matrixArray)
        paint.colorFilter = android.graphics.ColorMatrixColorFilter(finalColorMatrix)

        canvas.drawBitmap(scaled, matrix, paint)

        // Coordinate scaling factor based on preview box size (approx 360dp ref)
        val scaleFactor = targetHeight / 500f

        // Draw Text / Shayari if present
        if (textOverlay.isNotBlank()) {
            val textPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = textColor.toArgb()
                textSize = (20f * textScale * scaleFactor).coerceAtLeast(16f)
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setShadowLayer(10f, 0f, 4f, android.graphics.Color.BLACK)
            }

            canvas.save()
            val textCenterX = (targetWidth / 2f) + (textOffsetX * scaleFactor)
            val textCenterY = (targetHeight / 2f) + (textOffsetY * scaleFactor)
            canvas.rotate(textRotation, textCenterX, textCenterY)

            if (hasTextBackground) {
                val textBounds = android.graphics.Rect()
                textPaint.getTextBounds(textOverlay, 0, textOverlay.length, textBounds)
                val bgPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.argb(140, 0, 0, 0)
                    style = android.graphics.Paint.Style.FILL
                }
                val padX = 14f * scaleFactor
                val padY = 8f * scaleFactor
                val rectF = android.graphics.RectF(
                    textCenterX - (textBounds.width() / 2f) - padX,
                    textCenterY - textBounds.height() - padY,
                    textCenterX + (textBounds.width() / 2f) + padX,
                    textCenterY + padY
                )
                canvas.drawRoundRect(rectF, 10f * scaleFactor, 10f * scaleFactor, bgPaint)
            }

            canvas.drawText(textOverlay, textCenterX, textCenterY, textPaint)
            canvas.restore()
        }

        // Draw Emoji / Sticker if present
        if (overlayEmoji.isNotBlank()) {
            val emojiPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                textSize = (44f * emojiScale * scaleFactor).coerceAtLeast(24f)
                textAlign = android.graphics.Paint.Align.CENTER
            }

            canvas.save()
            val emojiCenterX = (targetWidth / 2f) + (emojiOffsetX * scaleFactor)
            val emojiCenterY = (targetHeight / 2f) + (emojiOffsetY * scaleFactor)
            canvas.rotate(emojiRotation, emojiCenterX, emojiCenterY)
            canvas.drawText(overlayEmoji, emojiCenterX, emojiCenterY, emojiPaint)
            canvas.restore()
        }

        result
    }

    // Save Action
    fun saveImage(export4K: Boolean = false) {
        scope.launch {
            Toast.makeText(context, if (export4K) "Exporting in 4K Ultra HD..." else "Saving photo...", Toast.LENGTH_SHORT).show()
            val finalBmp = renderOutputBitmap(export4K)
            if (finalBmp != null) {
                withContext(Dispatchers.IO) {
                    try {
                        val filename = "VisionCut_${System.currentTimeMillis()}${if (export4K) "_4K" else ""}.jpg"
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/VisionCut")
                                put(MediaStore.MediaColumns.IS_PENDING, 1)
                            }
                        }
                        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        if (uri != null) {
                            context.contentResolver.openOutputStream(uri)?.use { out ->
                                finalBmp.compress(Bitmap.CompressFormat.JPEG, if (export4K) 100 else 95, out)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                contentValues.clear()
                                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                                context.contentResolver.update(uri, contentValues, null, null)
                            }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Saved successfully to Gallery!", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    // Share Action
    fun shareImage() {
        scope.launch {
            Toast.makeText(context, "Preparing photo to share...", Toast.LENGTH_SHORT).show()
            val finalBmp = renderOutputBitmap(export4K = false)
            if (finalBmp != null) {
                withContext(Dispatchers.IO) {
                    try {
                        val cachePath = File(context.cacheDir, "shared_images")
                        cachePath.mkdirs()
                        val file = File(cachePath, "VisionCut_Share_${System.currentTimeMillis()}.jpg")
                        FileOutputStream(file).use { out ->
                            finalBmp.compress(Bitmap.CompressFormat.JPEG, 95, out)
                        }
                        val contentUri: Uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/jpeg"
                            putExtra(Intent.EXTRA_STREAM, contentUri)
                            if (textOverlay.isNotBlank()) {
                                putExtra(Intent.EXTRA_TEXT, textOverlay)
                            }
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        withContext(Dispatchers.Main) {
                            context.startActivity(Intent.createChooser(shareIntent, "Share Edited Photo Via"))
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed to share: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    // Compose ColorMatrix for Canvas Live Preview
    val activeColorMatrix = remember(selectedFilterId, filterIntensity, brightness, contrast, saturation, temperature) {
        ColorMatrix(computeCombinedMatrixArray())
    }

    // MAIN LAYOUT
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
    ) {
        // TOP BAR: Cancel | Undo/Redo | Eraser | Info | Download | Apply
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Cancel Text Action
            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("back_button")
            ) {
                Text("Cancel", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            // Center-Left: Undo & Redo Arrows
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { undo() },
                    enabled = historyIndex > 0,
                    modifier = Modifier.testTag("undo_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (historyIndex > 0) TextPrimary else TextMuted
                    )
                }

                IconButton(
                    onClick = { redo() },
                    enabled = historyIndex < history.size - 1,
                    modifier = Modifier.testTag("redo_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Redo",
                        tint = if (historyIndex < history.size - 1) TextPrimary else TextMuted
                    )
                }
            }

            // Right Actions: [Eraser] [Info] [Download] [Apply]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(onClick = { showManualCutoutDialog = true }) {
                    Icon(Icons.Default.CleaningServices, contentDescription = "Manual Eraser", tint = RadiantPink)
                }
                IconButton(onClick = {
                    Toast.makeText(context, "AI Background Replace & 4K Enhancement Tool Active", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.Info, contentDescription = "Info", tint = TextPrimary)
                }
                IconButton(onClick = { saveImage(export4K = false) }) {
                    Icon(Icons.Default.Download, contentDescription = "Download", tint = TextPrimary)
                }
                Button(
                    onClick = {
                        scope.launch {
                            if (selectedFilterId != "normal" || brightness != 0f || contrast != 0f || saturation != 0f || temperature != 0f) {
                                val baked = renderOutputBitmap(export4K = false)
                                if (baked != null) {
                                    loadedBitmap = baked
                                    selectedFilterId = "normal"
                                    filterIntensity = 1.0f
                                    brightness = 0f
                                    contrast = 0f
                                    saturation = 0f
                                    temperature = 0f
                                    pushState(baked)
                                }
                            } else {
                                pushState()
                            }
                            Toast.makeText(context, "Applied changes!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RadiantPink),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("apply_button")
                ) {
                    Text("Apply", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // MAIN PHOTO CANVAS WITH INTERACTIVE BEFORE/AFTER SPLIT SLIDER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoadingBitmap) {
                CircularProgressIndicator(color = ElectricBlue)
            } else if (loadedBitmap != null) {
                val orig = originalUneditedBitmap ?: loadedBitmap!!
                val proc = loadedBitmap!!

                val badgeText = if (selectedTab == PhotoEditorTab.AI) "AI Enhance" else "Remove BG"
                val badgeIcon = if (selectedTab == PhotoEditorTab.AI) Icons.Default.AutoAwesome else Icons.Default.ContentCut
                val showZoom = selectedTab == PhotoEditorTab.AI

                BeforeAfterComparisonCanvas(
                    originalBitmap = orig,
                    processedBitmap = proc,
                    activeColorMatrix = activeColorMatrix,
                    splitPosition = splitSliderPosition,
                    onSplitPositionChange = { splitSliderPosition = it },
                    floatingBadgeText = badgeText,
                    floatingBadgeIcon = badgeIcon,
                    showInsetZoom = showZoom,
                    isProcessing = isAiProcessing,
                    processingTitle = aiProcessingTitle,
                    textOverlay = textOverlay,
                    textColor = textColor,
                    textOffsetX = textOffsetX,
                    textOffsetY = textOffsetY,
                    textScale = textScale,
                    textRotation = textRotation,
                    hasTextBackground = hasTextBackground,
                    onTextTransform = { dx, dy, zoom, rot ->
                        textOffsetX += dx
                        textOffsetY += dy
                        textScale = (textScale * zoom).coerceIn(0.5f, 3.5f)
                        textRotation += rot
                    },
                    overlayEmoji = overlayEmoji,
                    emojiOffsetX = emojiOffsetX,
                    emojiOffsetY = emojiOffsetY,
                    emojiScale = emojiScale,
                    emojiRotation = emojiRotation,
                    onEmojiTransform = { dx, dy, zoom, rot ->
                        emojiOffsetX += dx
                        emojiOffsetY += dy
                        emojiScale = (emojiScale * zoom).coerceIn(0.4f, 4.0f)
                        emojiRotation += rot
                    }
                )
            } else {
                // Upload Photo button when no photo is loaded
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Upload Photo",
                        tint = ElectricBlue,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("upload_photo_center_button")
                    ) {
                        Text("Upload Photo", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        // BOTTOM TAB BAR (AI, Tools, Filter, Adjust, Shayari, Text, Overlay)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CharcoalSurface)
                .border(1.dp, GlassBorder)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PhotoEditorTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("tab_${tab.name.lowercase()}")
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (isSelected) ElectricBlue else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = tab.label,
                        color = if (isSelected) ElectricBlue else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // TAB CONTENT PANEL (Shows dynamic content based on selected tab)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CharcoalSurfaceVariant)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("tab_content_panel")
        ) {
            when (selectedTab) {
                PhotoEditorTab.AI -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 00. One-Tap Pro Auto Enhance AI
                        AIActionButton(
                            label = "Pro Auto Enhance",
                            icon = Icons.Default.AutoAwesome,
                            badge = "PRO GOLD"
                        ) {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Pro One-Tap AI Auto Enhancement & HDR"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyProOneTapAutoEnhance(bmp)
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Pro AI Auto Enhanced!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 01. Pro DSLR Bokeh Depth Blur
                        AIActionButton(
                            label = "DSLR Bokeh Blur",
                            icon = Icons.Default.AutoFixHigh,
                            badge = "DSLR"
                        ) {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying DSLR Bokeh Depth-of-Field"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyProBokehDepthBlur(bmp, 14)
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "DSLR Bokeh Blur Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 02. Golden Sunset Studio Light
                        AIActionButton(
                            label = "Golden Sun Ray",
                            icon = Icons.Default.AutoAwesome,
                            badge = "LIGHT"
                        ) {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Golden Hour Sunset Sunlight Rays"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyProStudioLighting(bmp, "Golden Sun")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Golden Sunset Ray Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 03. Cyberpunk Neon Rim Lighting
                        AIActionButton(
                            label = "Cyber Neon Light",
                            icon = Icons.Default.AutoFixHigh,
                            badge = "NEON"
                        ) {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Dual Cyberpunk Neon Rim Light"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyProStudioLighting(bmp, "Cyber Neon")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Cyber Neon Relight Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 04. Studio Softbox Portrait Light
                        AIActionButton(
                            label = "Studio Softbox",
                            icon = Icons.Default.Tune,
                            badge = "STUDIO"
                        ) {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Pro Studio Softbox Relighting"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyProStudioLighting(bmp, "Studio Softbox")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Studio Softbox Light Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 05. Stage Spotlight
                        AIActionButton(
                            label = "Stage Spotlight",
                            icon = Icons.Default.Adjust,
                            badge = "SPOT"
                        ) {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Dramatic Stage Spotlight"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyProStudioLighting(bmp, "Spotlight")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Stage Spotlight Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 0. AI 4K Ultra HD Expert Upscaler
                        AIActionButton(
                            label = "AI 4K Expert",
                            icon = Icons.Default.HighQuality,
                            badge = "4K Ultra"
                        ) {
                            if (loadedBitmap != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI 4K Super Resolution & Detail Upscale"
                                scope.launch {
                                    try {
                                        val upscaled = ProAiImageEngine.upscaleTo4KUltraHD(loadedBitmap!!)
                                        loadedBitmap = upscaled
                                        pushState(upscaled)
                                        Toast.makeText(context, "Upscaled to 4K Ultra HD!", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "4K Upscale failed", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 1. Remini-like Ultra HD
                        AIActionButton(
                            label = "Remini Ultra HD",
                            icon = Icons.Default.AutoAwesome,
                            badge = "PRO"
                        ) {
                            if (loadedBitmap != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Remini AI Ultra HD Enhancement"
                                scope.launch {
                                    try {
                                        val enhanced = ProAiImageEngine.enhanceReminiHD(loadedBitmap!!)
                                        loadedBitmap = enhanced
                                        pushState()
                                        Toast.makeText(context, "Remini Ultra HD Applied!", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Enhancement failed", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 2. Portrait Face Glow
                        AIActionButton(
                            label = "Face Beauty Glow",
                            icon = Icons.Default.AutoFixHigh,
                            badge = "AI"
                        ) {
                            if (loadedBitmap != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Portrait Face Glow"
                                scope.launch {
                                    try {
                                        val beautified = ProAiImageEngine.portraitFaceGlow(loadedBitmap!!)
                                        loadedBitmap = beautified
                                        pushState()
                                        Toast.makeText(context, "Portrait Glow Applied!", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Failed to apply face glow", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 3. Pro Cutout / Background Isolator
                        AIActionButton(
                            label = "AI Cutout BG",
                            icon = Icons.Default.Layers,
                            badge = "PRO"
                        ) {
                            if (loadedBitmap != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Subject Cutout"
                                scope.launch {
                                    try {
                                        val cutout = ProAiImageEngine.isolateSubject(loadedBitmap!!)
                                        loadedBitmap = cutout
                                        pushState()
                                        Toast.makeText(context, "Subject Isolated Successfully!", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Cutout failed", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 4. Ultra HDR Max
                        AIActionButton(
                            label = "Ultra HDR Max",
                            icon = Icons.Default.Compare,
                            badge = "HDR"
                        ) {
                            if (loadedBitmap != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Ultra HDR Max Tone Mapping"
                                scope.launch {
                                    try {
                                        val hdr = ProAiImageEngine.ultraHdrMax(loadedBitmap!!)
                                        loadedBitmap = hdr
                                        pushState()
                                        Toast.makeText(context, "Ultra HDR Max Applied!", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "HDR failed", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 5. Dehaze & Sky Cleanser
                        AIActionButton(
                            label = "Dehaze Clarity",
                            icon = Icons.Default.ShowChart,
                            badge = "CLEAR"
                        ) {
                            if (loadedBitmap != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Dehaze & Clarity Cleaner"
                                scope.launch {
                                    try {
                                        val dehazed = ProAiImageEngine.dehazeClarity(loadedBitmap!!)
                                        loadedBitmap = dehazed
                                        pushState()
                                        Toast.makeText(context, "Dehaze Clarity Applied!", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Dehaze failed", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 6. Magic Motion Unblur
                        AIActionButton(
                            label = "Magic Unblur",
                            icon = Icons.Default.Tune,
                            badge = "SHARP"
                        ) {
                            if (loadedBitmap != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Motion De-blur & Recovery"
                                scope.launch {
                                    try {
                                        val unblurred = ProAiImageEngine.unblurMotion(loadedBitmap!!)
                                        loadedBitmap = unblurred
                                        pushState()
                                        Toast.makeText(context, "Unblur Motion Sharpened!", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Unblur failed", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 7. AI Magic Object Eraser
                        AIActionButton(
                            label = "Magic Eraser",
                            icon = Icons.Default.AutoFixHigh,
                            badge = "ERASE"
                        ) {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Magic Eraser Spot Healing"
                                scope.launch {
                                    try {
                                        val erased = ProAiImageEngine.applyMagicEraserHeal(bmp)
                                        loadedBitmap = erased
                                        pushState(erased)
                                        Toast.makeText(context, "Object Erased & Healed!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                    }
                }

                PhotoEditorTab.PRO_PICSART -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AIActionButton(label = "Drip Art", icon = Icons.Default.AutoFixHigh, badge = "DRIP") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying PicsArt Paint Drip Art Effect"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyDripArtEffect(bmp)
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Drip Art Effect Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        AIActionButton(label = "Sticker Cutout", icon = Icons.Default.Layers, badge = "OUTLINE") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Generating Sticker Cutout Outline"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyStickerCutoutOutline(bmp, "#00F2FE")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Sticker Cutout Created!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        AIActionButton(label = "Prism Mirror", icon = Icons.Default.Compare, badge = "3D") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Prism Symmetry Mirror"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyPrismMirror(bmp)
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Prism Symmetry Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        AIActionButton(label = "Pop Halftone", icon = Icons.Default.AutoAwesome, badge = "COMIC") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Generating Pop Art Halftone Dots"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyHalftonePopArt(bmp)
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Halftone Pop Art Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        AIActionButton(label = "Clarity Boost", icon = Icons.Default.Tune, badge = "SHARP") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Boosting Detail Micro-Contrast Clarity"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyDetailSharpenerClarity(bmp)
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Pro Clarity Boosted!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        AIActionButton(label = "Vignette Dark", icon = Icons.Default.ShowChart, badge = "MOOD") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Cinematic Dark Vignette"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyVignetteCinematic(bmp)
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Cinematic Vignette Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        AIActionButton(label = "Oil Painting", icon = Icons.Default.AutoFixHigh, badge = "ART") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Master Oil Painting Canvas Effect"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyOilPaintingArt(bmp)
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Oil Painting Effect Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        AIActionButton(label = "Cyber Duotone", icon = Icons.Default.Layers, badge = "NEON") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Cyberpunk Duotone Gradient Map"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyDuotoneGradientMap(bmp, "#090979", "#00D4FF")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Cyber Duotone Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        AIActionButton(label = "3D Anaglyph", icon = Icons.Default.Compare, badge = "3D") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying 3D Anaglyph Stereo Shift"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyAnaglyph3D(bmp)
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "3D Anaglyph Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        AIActionButton(label = "Thermal Heat", icon = Icons.Default.AutoAwesome, badge = "CAM") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Generating Thermal Infrared Heat Map"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyThermalInfrared(bmp)
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Thermal Heat Map Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        AIActionButton(label = "HDR Drama", icon = Icons.Default.Tune, badge = "MAX") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Snapseed HDR Max Drama Scape"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyHdrMaxDrama(bmp)
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "HDR Max Scape Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        AIActionButton(label = "35mm Grain", icon = Icons.Default.Layers, badge = "FILM") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Adding Analog 35mm Cinema Grain"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyFilmCinemaGrain(bmp)
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "35mm Grain Added!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                    }
                }

                PhotoEditorTab.BG_REMOVE -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Top Toolbar Row: Eyedropper | Color Wheel | Square Live Preview Cards
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 1. Eyedropper / Color Picker Icon Button
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Eyedropper tool active! Tap image to pick color", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(CharcoalSurfaceVariant)
                            ) {
                                Icon(Icons.Default.Colorize, contentDescription = "Eyedropper", tint = TextPrimary)
                            }

                            // 2. Color Wheel Icon Button
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Color Palette Wheel active", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(CharcoalSurfaceVariant)
                            ) {
                                Icon(Icons.Default.Palette, contentDescription = "Color Wheel", tint = RadiantPink)
                            }

                            // 2.5 REAL USER PHONE GALLERY PICKER CARD (100% Real Gallery Backgrounds)
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121))))
                                    .border(2.dp, ElectricBlue, RoundedCornerShape(12.dp))
                                    .clickable {
                                        customBgGalleryPickerLauncher.launch("image/*")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Pick Background from Gallery", tint = Color.White, modifier = Modifier.size(22.dp))
                                    Text("My Gallery", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }

                            // 3. Square Preview Cards Row (Matching Screenshot)
                            // Thumbnail 1: Transparent Checkerboard Cutout
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CharcoalSurface)
                                    .border(2.dp, RadiantPink, RoundedCornerShape(12.dp))
                                    .clickable {
                                        val bmp = loadedBitmap
                                        if (bmp != null && !isAiProcessing) {
                                            isAiProcessing = true
                                            aiProcessingTitle = "Creating Transparent Cutout"
                                            scope.launch {
                                                try {
                                                    val res = ProAiImageEngine.removeBackgroundTransparent(bmp)
                                                    loadedBitmap = res
                                                    pushState(res)
                                                    Toast.makeText(context, "Transparent Cutout Active!", Toast.LENGTH_SHORT).show()
                                                } finally {
                                                    isAiProcessing = false
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.ContentCut, contentDescription = null, tint = RadiantPink, modifier = Modifier.size(22.dp))
                                    Text("Cutout", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Thumbnail 2: Blooming Flowers Garden (Matches dog preset in screenshot!)
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFF758C))
                                    .clickable {
                                        val bmp = loadedBitmap
                                        if (bmp != null && !isAiProcessing) {
                                            isAiProcessing = true
                                            aiProcessingTitle = "Applying Pink Flowers Garden"
                                            scope.launch {
                                                try {
                                                    val res = ProAiImageEngine.replaceBackgroundWithPreset(bmp, 19)
                                                    loadedBitmap = res
                                                    pushState(res)
                                                    Toast.makeText(context, "Flowers Garden Applied!", Toast.LENGTH_SHORT).show()
                                                } finally {
                                                    isAiProcessing = false
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🌸", fontSize = 18.sp)
                                    Text("Flowers", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Thumbnail 3: Sunset Studio Gold
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFF512F))
                                    .clickable {
                                        val bmp = loadedBitmap
                                        if (bmp != null && !isAiProcessing) {
                                            isAiProcessing = true
                                            aiProcessingTitle = "Applying Sunset Gold Studio"
                                            scope.launch {
                                                try {
                                                    val res = ProAiImageEngine.replaceBackgroundWithPreset(bmp, 2)
                                                    loadedBitmap = res
                                                    pushState(res)
                                                    Toast.makeText(context, "Sunset Gold Applied!", Toast.LENGTH_SHORT).show()
                                                } finally {
                                                    isAiProcessing = false
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🌅", fontSize = 18.sp)
                                    Text("Studio Gold", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Thumbnail 4: Solid Blue Color
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF00C6FF))
                                    .clickable {
                                        val bmp = loadedBitmap
                                        if (bmp != null && !isAiProcessing) {
                                            isAiProcessing = true
                                            aiProcessingTitle = "Applying Solid Blue Background"
                                            scope.launch {
                                                try {
                                                    val res = ProAiImageEngine.replaceBackgroundSolidColor(bmp, "#00C6FF")
                                                    loadedBitmap = res
                                                    pushState(res)
                                                    Toast.makeText(context, "Solid Blue Applied!", Toast.LENGTH_SHORT).show()
                                                } finally {
                                                    isAiProcessing = false
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🎨", fontSize = 18.sp)
                                    Text("Blue Color", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Preset 50 Gallery Button
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CharcoalSurfaceVariant)
                                    .clickable { showBg50Modal = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Wallpaper, contentDescription = null, tint = RadiantPink, modifier = Modifier.size(20.dp))
                                    Text("50 BG Set", color = TextPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Sub-Category Selector Tabs: Color | Gradient
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Color",
                                color = if (bgSubTabCategory == "Color") RadiantPink else TextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { bgSubTabCategory = "Color" }
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = "Gradient",
                                color = if (bgSubTabCategory == "Gradient") RadiantPink else TextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { bgSubTabCategory = "Gradient" }
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }

                        // Bottom Navigation Sub-Bar (Matches exact 4 items in screenshot)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CharcoalSurface, RoundedCornerShape(16.dp))
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Color
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    selectedTab = PhotoEditorTab.BG_REMOVE
                                    bgBottomNavTab = "Color"
                                }
                            ) {
                                Icon(
                                    Icons.Default.Palette,
                                    contentDescription = "Color",
                                    tint = if (bgBottomNavTab == "Color") RadiantPink else TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    "Color",
                                    color = if (bgBottomNavTab == "Color") RadiantPink else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // 2. Background
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    selectedTab = PhotoEditorTab.BG_REMOVE
                                    bgBottomNavTab = "Background"
                                }
                            ) {
                                Icon(
                                    Icons.Default.Wallpaper,
                                    contentDescription = "Background",
                                    tint = if (bgBottomNavTab == "Background") RadiantPink else TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    "Background",
                                    color = if (bgBottomNavTab == "Background") RadiantPink else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // 3. AI Background
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    selectedTab = PhotoEditorTab.BG_REMOVE
                                    bgBottomNavTab = "AI Background"
                                    showBg50Modal = true
                                }
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = "AI Background",
                                    tint = if (bgBottomNavTab == "AI Background") RadiantPink else TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    "AI Background",
                                    color = if (bgBottomNavTab == "AI Background") RadiantPink else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // 4. Ratio
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    selectedTab = PhotoEditorTab.FIT
                                    bgBottomNavTab = "Ratio"
                                }
                            ) {
                                Icon(
                                    Icons.Default.Crop,
                                    contentDescription = "Ratio",
                                    tint = if (bgBottomNavTab == "Ratio") RadiantPink else TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    "Ratio",
                                    color = if (bgBottomNavTab == "Ratio") RadiantPink else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                PhotoEditorTab.DOUBLE_EXP -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AIActionButton(label = "Galaxy Screen", icon = Icons.Default.Compare, badge = "BLEND") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Double Exposure Screen Blend"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyDoubleExposure(bmp, "SCREEN")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Double Exposure Screen Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Dark Multiply", icon = Icons.Default.Compare, badge = "DARK") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Double Exposure Multiply"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyDoubleExposure(bmp, "MULTIPLY")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Multiply Blend Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Vivid Overlay", icon = Icons.Default.Compare, badge = "ART") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Double Exposure Overlay"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyDoubleExposure(bmp, "OVERLAY")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Vivid Overlay Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                    }
                }

                PhotoEditorTab.SPLASH -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AIActionButton(label = "Red Splash", icon = Icons.Default.AutoFixHigh, badge = "RED") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Isolating Red Color Splash"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyColorSplashBW(bmp, "RED")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Red Color Splash Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Blue Splash", icon = Icons.Default.AutoFixHigh, badge = "BLUE") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Isolating Blue Color Splash"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyColorSplashBW(bmp, "BLUE")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Blue Color Splash Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Green Splash", icon = Icons.Default.AutoFixHigh, badge = "GREEN") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Isolating Green Color Splash"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyColorSplashBW(bmp, "GREEN")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Green Color Splash Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                    }
                }

                PhotoEditorTab.SHAPES -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AIActionButton(label = "Heart Frame", icon = Icons.Default.Crop, badge = "LOVE") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Heart Shape Frame Mask"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyShapeMask(bmp, "HEART")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Heart Frame Mask Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Star Frame", icon = Icons.Default.Crop, badge = "STAR") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Star Shape Frame Mask"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyShapeMask(bmp, "STAR")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Star Frame Mask Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Hexagon Frame", icon = Icons.Default.Crop, badge = "GEO") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Hexagon Frame Mask"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyShapeMask(bmp, "HEXAGON")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Hexagon Frame Mask Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Circle Ring", icon = Icons.Default.Crop, badge = "RING") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Circle Frame Ring"
                                scope.launch {
                                    try {
                                        val res = ProAiImageEngine.applyShapeMask(bmp, "CIRCLE")
                                        loadedBitmap = res
                                        pushState(res)
                                        Toast.makeText(context, "Circle Ring Mask Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                    }
                }

                PhotoEditorTab.TOOLS -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ToolActionButton("1:1 Crop", Icons.Default.Crop) {
                            val bmp = loadedBitmap
                            if (bmp != null) {
                                scope.launch {
                                    val cropped = ProAiImageEngine.cropToRatio(bmp, 1f, 1f)
                                    loadedBitmap = cropped
                                    pushState(cropped)
                                    Toast.makeText(context, "1:1 Square Crop Applied", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        ToolActionButton("4:5 Portrait", Icons.Default.Crop) {
                            val bmp = loadedBitmap
                            if (bmp != null) {
                                scope.launch {
                                    val cropped = ProAiImageEngine.cropToRatio(bmp, 4f, 5f)
                                    loadedBitmap = cropped
                                    pushState(cropped)
                                    Toast.makeText(context, "4:5 Portrait Crop Applied", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        ToolActionButton("9:16 Story", Icons.Default.Crop) {
                            val bmp = loadedBitmap
                            if (bmp != null) {
                                scope.launch {
                                    val cropped = ProAiImageEngine.cropToRatio(bmp, 9f, 16f)
                                    loadedBitmap = cropped
                                    pushState(cropped)
                                    Toast.makeText(context, "9:16 Story Crop Applied", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        ToolActionButton("16:9 Wide", Icons.Default.Crop) {
                            val bmp = loadedBitmap
                            if (bmp != null) {
                                scope.launch {
                                    val cropped = ProAiImageEngine.cropToRatio(bmp, 16f, 9f)
                                    loadedBitmap = cropped
                                    pushState(cropped)
                                    Toast.makeText(context, "16:9 Landscape Crop Applied", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        ToolActionButton("Rotate 90°", Icons.Default.RotateRight) {
                            val bmp = loadedBitmap
                            if (bmp != null) {
                                scope.launch {
                                    val rotated = ProAiImageEngine.rotateBy(bmp, 90f)
                                    loadedBitmap = rotated
                                    pushState(rotated)
                                }
                            } else {
                                rotationAngle = (rotationAngle + 90f) % 360f
                                pushState()
                            }
                        }
                        ToolActionButton("Flip H", Icons.Default.Compare) {
                            val bmp = loadedBitmap
                            if (bmp != null) {
                                scope.launch {
                                    val flipped = ProAiImageEngine.flipHorizontal(bmp)
                                    loadedBitmap = flipped
                                    pushState(flipped)
                                    Toast.makeText(context, "Flipped Horizontally", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        ToolActionButton("Flip V", Icons.Default.Compare) {
                            val bmp = loadedBitmap
                            if (bmp != null) {
                                scope.launch {
                                    val flipped = ProAiImageEngine.flipVertical(bmp)
                                    loadedBitmap = flipped
                                    pushState(flipped)
                                    Toast.makeText(context, "Flipped Vertically", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        ToolActionButton("Dispersion", Icons.Default.AutoFixHigh) {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying PicsArt Particle Dispersion"
                                scope.launch {
                                    try {
                                        val dispersed = ProAiImageEngine.applyDispersion(bmp)
                                        loadedBitmap = dispersed
                                        pushState(dispersed)
                                        Toast.makeText(context, "Dispersion Effect Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        ToolActionButton("Tilt-Shift", Icons.Default.Tune) {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Tilt-Shift DSLR Focus"
                                scope.launch {
                                    try {
                                        val ts = ProAiImageEngine.applyTiltShiftDepth(bmp)
                                        loadedBitmap = ts
                                        pushState(ts)
                                        Toast.makeText(context, "Tilt-Shift Focus Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        ToolActionButton("Motion Trail", Icons.Default.ShowChart) {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Motion Trail Action"
                                scope.launch {
                                    try {
                                        val mt = ProAiImageEngine.applyMotionBlurTrail(bmp)
                                        loadedBitmap = mt
                                        pushState(mt)
                                        Toast.makeText(context, "Motion Trail Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        ToolActionButton("Curve RGB", Icons.Default.ShowChart) {
                            contrast = (contrast + 25f).coerceAtMost(100f)
                            pushState()
                            Toast.makeText(context, "S-Curve Contrast Boosted", Toast.LENGTH_SHORT).show()
                        }
                        ToolActionButton("HSL Boost", Icons.Default.Adjust) {
                            saturation = (saturation + 30f).coerceAtMost(100f)
                            pushState()
                            Toast.makeText(context, "HSL Vibrance Boosted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                PhotoEditorTab.FX -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AIActionButton(label = "Glitch RGB", icon = Icons.Default.AutoFixHigh, badge = "FX") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Glitch RGB Split"
                                scope.launch {
                                    try {
                                        val fx = ProAiImageEngine.applyGlitchRGB(bmp)
                                        loadedBitmap = fx
                                        pushState(fx)
                                        Toast.makeText(context, "Glitch RGB Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "VHS Retro", icon = Icons.Default.AutoFixHigh, badge = "RETRO") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying VHS Scanlines"
                                scope.launch {
                                    try {
                                        val fx = ProAiImageEngine.applyVhsRetro(bmp)
                                        loadedBitmap = fx
                                        pushState(fx)
                                        Toast.makeText(context, "VHS Retro Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Pencil Sketch", icon = Icons.Default.AutoFixHigh, badge = "ART") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Generating Pencil Sketch"
                                scope.launch {
                                    try {
                                        val fx = ProAiImageEngine.applyPencilSketch(bmp)
                                        loadedBitmap = fx
                                        pushState(fx)
                                        Toast.makeText(context, "Pencil Sketch Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Radial Blur", icon = Icons.Default.Tune, badge = "BLUR") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Radial Zoom Blur"
                                scope.launch {
                                    try {
                                        val fx = ProAiImageEngine.applyRadialBlur(bmp)
                                        loadedBitmap = fx
                                        pushState(fx)
                                        Toast.makeText(context, "Radial Blur Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Cyberpunk", icon = Icons.Default.AutoAwesome, badge = "NEON") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Cyberpunk Grade"
                                scope.launch {
                                    try {
                                        val fx = ProAiImageEngine.applyCyberpunk(bmp)
                                        loadedBitmap = fx
                                        pushState(fx)
                                        Toast.makeText(context, "Cyberpunk Neon Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                    }
                }

                PhotoEditorTab.BEAUTIFY -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Face Slimming / Reshape Jawline
                        AIActionButton(label = "Face Slim", icon = Icons.Default.EmojiEmotions, badge = "RESHAPE") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Face Slimming & Jawline Reshape"
                                scope.launch {
                                    try {
                                        val beautified = ProAiImageEngine.applyFaceSlimmingReshape(bmp)
                                        loadedBitmap = beautified
                                        pushState(beautified)
                                        Toast.makeText(context, "Face Slimming Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 2. Nose Slimming & Contour
                        AIActionButton(label = "Nose Slim", icon = Icons.Default.Tune, badge = "CONTOUR") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Nose Refinement & Slimming"
                                scope.launch {
                                    try {
                                        val beautified = ProAiImageEngine.applyNoseSlimming(bmp)
                                        loadedBitmap = beautified
                                        pushState(beautified)
                                        Toast.makeText(context, "Nose Slimming Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 3. Lips & Smile Enhancer
                        AIActionButton(label = "Lips & Smile", icon = Icons.Default.AutoAwesome, badge = "RETOUCH") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Smile & Lips Color Enhancer"
                                scope.launch {
                                    try {
                                        val beautified = ProAiImageEngine.applySmileLipsEnhance(bmp)
                                        loadedBitmap = beautified
                                        pushState(beautified)
                                        Toast.makeText(context, "Smile & Lips Enhanced!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 4. Eye Sparkle & Brighten
                        AIActionButton(label = "Eye Sparkle", icon = Icons.Default.AutoFixHigh, badge = "GLOW") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Eye Brightening & Sparkle"
                                scope.launch {
                                    try {
                                        val beautified = ProAiImageEngine.applyEyeBrighten(bmp)
                                        loadedBitmap = beautified
                                        pushState(beautified)
                                        Toast.makeText(context, "Eye Sparkle Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 5. Skin Glow
                        AIActionButton(label = "Skin Glow", icon = Icons.Default.AutoFixHigh, badge = "AI") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Face Skin Glow"
                                scope.launch {
                                    try {
                                        val beautified = ProAiImageEngine.portraitFaceGlow(bmp)
                                        loadedBitmap = beautified
                                        pushState(beautified)
                                        Toast.makeText(context, "Skin Glow Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 6. Smooth Skin
                        AIActionButton(label = "Smooth Skin", icon = Icons.Default.Layers, badge = "SOFT") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Smooth Skin Polish"
                                scope.launch {
                                    try {
                                        val beautified = ProAiImageEngine.portraitFaceGlow(bmp)
                                        loadedBitmap = beautified
                                        pushState(beautified)
                                        Toast.makeText(context, "Skin Smooth Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 7. Teeth Whiten
                        AIActionButton(label = "Teeth Whiten", icon = Icons.Default.Brush, badge = "PRO") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "AI Teeth Whitening & Polish"
                                scope.launch {
                                    try {
                                        val beautified = ProAiImageEngine.applyTeethWhiten(bmp)
                                        loadedBitmap = beautified
                                        pushState(beautified)
                                        Toast.makeText(context, "Teeth Whitened Successfully!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }

                        // 8. AI Object Eraser Brush (Matches Left Screenshot in User Image!)
                        AIActionButton(label = "Object Eraser", icon = Icons.Default.CleaningServices, badge = "REMOVE") {
                            showManualCutoutDialog = true
                        }
                    }
                }

                PhotoEditorTab.FILTER -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        FilterThumbnailBar(
                            sourceBitmap = loadedBitmap,
                            selectedFilterId = selectedFilterId,
                            onFilterSelected = { filter ->
                                selectedFilterId = filter.id
                                pushState()
                            },
                            thumbnailSize = 80.dp,
                            activeBorderColor = ElectricBlue
                        )
                    }
                }

                PhotoEditorTab.ADJUST -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdjustmentSliderRow("Brightness", brightness, -100f..100f) {
                            brightness = it
                            pushState()
                        }
                        AdjustmentSliderRow("Contrast", contrast, -100f..100f) {
                            contrast = it
                            pushState()
                        }
                        AdjustmentSliderRow("Saturation", saturation, -100f..100f) {
                            saturation = it
                            pushState()
                        }
                        AdjustmentSliderRow("Temperature", temperature, -100f..100f) {
                            temperature = it
                            pushState()
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    brightness = 0f
                                    contrast = 0f
                                    saturation = 0f
                                    temperature = 0f
                                    pushState()
                                    Toast.makeText(context, "Adjustments Reset", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text("Reset All", color = RadiantPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                PhotoEditorTab.MASKS -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AIActionButton(label = "Light Leak", icon = Icons.Default.ShowChart, badge = "WARM") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Applying Golden Light Leak"
                                scope.launch {
                                    try {
                                        val masked = ProAiImageEngine.applyLightLeak(bmp)
                                        loadedBitmap = masked
                                        pushState(masked)
                                        Toast.makeText(context, "Light Leak Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Bokeh Orbs", icon = Icons.Default.AutoAwesome, badge = "GLOW") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Adding Soft Bokeh Orbs"
                                scope.launch {
                                    try {
                                        val masked = ProAiImageEngine.applyBokehOrbs(bmp)
                                        loadedBitmap = masked
                                        pushState(masked)
                                        Toast.makeText(context, "Bokeh Orbs Added!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Lens Flare", icon = Icons.Default.AutoAwesome, badge = "OPTIC") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Adding Pro Anamorphic Lens Flare"
                                scope.launch {
                                    try {
                                        val masked = ProAiImageEngine.applyLensFlarePro(bmp)
                                        loadedBitmap = masked
                                        pushState(masked)
                                        Toast.makeText(context, "Pro Lens Flare Added!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Neon Spirals", icon = Icons.Default.AutoFixHigh, badge = "NEON") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Adding 3D Neon Spirals"
                                scope.launch {
                                    try {
                                        val masked = ProAiImageEngine.applyNeonWingsSpirals(bmp)
                                        loadedBitmap = masked
                                        pushState(masked)
                                        Toast.makeText(context, "Neon Spirals Added!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                        AIActionButton(label = "Vintage Dust", icon = Icons.Default.Layers, badge = "FILM") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Adding Film Dust & Scratches"
                                scope.launch {
                                    try {
                                        val masked = ProAiImageEngine.applyVintageDust(bmp)
                                        loadedBitmap = masked
                                        pushState(masked)
                                        Toast.makeText(context, "Vintage Dust Added!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                    }
                }

                PhotoEditorTab.FIT -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AIActionButton(label = "1:1 Insta Fit", icon = Icons.Default.Crop, badge = "BLUR") {
                            val bmp = loadedBitmap
                            if (bmp != null && !isAiProcessing) {
                                isAiProcessing = true
                                aiProcessingTitle = "Fitting 1:1 with Blur Background"
                                scope.launch {
                                    try {
                                        val fit = ProAiImageEngine.applyFitBlurSquare(bmp)
                                        loadedBitmap = fit
                                        pushState(fit)
                                        Toast.makeText(context, "1:1 Insta Blur Fit Applied!", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isAiProcessing = false
                                    }
                                }
                            }
                        }
                    }
                }

                PhotoEditorTab.SHAYARI -> {
                    val filteredQuotes = remember(selectedShayariCategory) {
                        if (selectedShayariCategory.startsWith("سب") || selectedShayariCategory == "All") {
                            UrduQuotesRepository.quotes
                        } else {
                            UrduQuotesRepository.quotes.filter { it.category == selectedShayariCategory }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Top: LazyRow of Category Chips
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(UrduQuotesRepository.categories) { category ->
                                val isCatSelected = selectedShayariCategory == category
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isCatSelected) ElectricBlue else CharcoalSurface)
                                        .border(
                                            1.dp,
                                            if (isCatSelected) ElectricBlue else GlassBorder,
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clickable { selectedShayariCategory = category }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                        .testTag("category_chip_${category.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = category,
                                        color = if (isCatSelected) Color.Black else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // 2. Middle: LazyColumn of Quote Cards
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredQuotes, key = { it.id }) { quote ->
                                val isApplied = textOverlay == quote.text
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    backgroundColor = CharcoalSurface,
                                    borderColor = if (isApplied) ElectricBlue else GlassBorder,
                                    borderWidth = if (isApplied) 1.5.dp else 1.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = quote.text,
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            lineHeight = 22.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 12.dp)
                                        )
                                        Button(
                                            onClick = {
                                                textOverlay = quote.text
                                                pushState()
                                                Toast.makeText(context, "Shayari Applied!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isApplied) ElectricBlue else CharcoalSurfaceVariant
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("use_quote_${quote.id}")
                                        ) {
                                            Text(
                                                text = if (isApplied) "Applied ✓" else "Use This",
                                                color = if (isApplied) Color.Black else TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Bottom: OutlinedTextField for "Khud Likhein" + "Apply" button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customShayariInput,
                                onValueChange = { customShayariInput = it },
                                placeholder = { Text("اپنی شاعری یا کیپشن لکھیں...", color = TextMuted, fontSize = 13.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricBlue,
                                    unfocusedBorderColor = GlassBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = CharcoalSurface,
                                    unfocusedContainerColor = CharcoalSurface
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("custom_shayari_input")
                            )
                            Button(
                                onClick = {
                                    if (customShayariInput.isNotBlank()) {
                                        textOverlay = customShayariInput
                                        pushState()
                                        Toast.makeText(context, "Custom Shayari Applied!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("apply_custom_shayari_button")
                            ) {
                                Text("Apply", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                PhotoEditorTab.TEXT -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = tempTextInput,
                                onValueChange = { tempTextInput = it },
                                placeholder = { Text("Enter text, caption, or thoughts...", color = TextMuted, fontSize = 13.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricBlue,
                                    unfocusedBorderColor = GlassBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = CharcoalSurface,
                                    unfocusedContainerColor = CharcoalSurface
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("text_input_field")
                            )
                            Button(
                                onClick = {
                                    if (tempTextInput.isNotBlank()) {
                                        textOverlay = tempTextInput
                                        pushState()
                                        Toast.makeText(context, "Text Applied!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("apply_text_button")
                            ) {
                                Text("Apply", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            if (textOverlay.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        textOverlay = ""
                                        tempTextInput = ""
                                        pushState()
                                    }
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear Text", tint = RadiantPink)
                                }
                            }
                        }

                        // Text Color Palette & Styling Chips
                        val colorPalette = listOf(
                            Color.White,
                            Color(0xFFFFD700), // Gold
                            ElectricBlue,
                            RadiantPink,
                            Color(0xFF00E676), // Lime Green
                            Color(0xFFFF9100), // Neon Orange
                            Color(0xFFE040FB), // Neon Purple
                            Color.Black
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Color:", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(colorPalette) { color ->
                                    val isColorSelected = textColor == color
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                width = if (isColorSelected) 2.5.dp else 1.dp,
                                                color = if (isColorSelected) ElectricBlue else GlassBorder,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                textColor = color
                                                pushState()
                                            }
                                    )
                                }
                            }
                            // Toggle Background Card
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (hasTextBackground) ElectricBlue.copy(alpha = 0.25f) else CharcoalSurfaceVariant)
                                    .border(1.dp, if (hasTextBackground) ElectricBlue else GlassBorder, RoundedCornerShape(8.dp))
                                    .clickable {
                                        hasTextBackground = !hasTextBackground
                                        pushState()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (hasTextBackground) "Box: ON" else "Box: OFF",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasTextBackground) ElectricBlue else TextMuted
                                )
                            }
                        }

                        // Text Quick Controls (Scale & Reset Position)
                        if (textOverlay.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "💡 Tip: Pinch/Drag on canvas to scale & rotate text",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                TextButton(
                                    onClick = {
                                        textOffsetX = 0f
                                        textOffsetY = 160f
                                        textScale = 1f
                                        textRotation = 0f
                                        pushState()
                                    }
                                ) {
                                    Text("Reset Pos", color = ElectricBlue, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                PhotoEditorTab.STICKERS -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Top: Category Chips for Stickers
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(StickersRepository.categories) { cat ->
                                val isSelected = cat == selectedStickerCategory
                                Surface(
                                    color = if (isSelected) ElectricBlue else CharcoalSurface,
                                    shape = RoundedCornerShape(20.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) ElectricBlue else GlassBorder
                                    ),
                                    modifier = Modifier
                                        .clickable { selectedStickerCategory = cat }
                                        .testTag("sticker_category_$cat")
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) Color.Black else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // 2. Middle: Stickers Grid
                        val filteredStickers = remember(selectedStickerCategory) {
                            if (selectedStickerCategory == "All") StickersRepository.stickers
                            else StickersRepository.stickers.filter { it.category == selectedStickerCategory }
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            items(filteredStickers, key = { it.id }) { item ->
                                val isSelected = overlayEmoji == item.emoji
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) DeepPurple.copy(alpha = 0.6f) else CharcoalSurface)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) RadiantPink else GlassBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            overlayEmoji = item.emoji
                                            pushState()
                                            Toast.makeText(context, "${item.name} Sticker Added!", Toast.LENGTH_SHORT).show()
                                        }
                                        .testTag("sticker_item_${item.id}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(item.emoji, fontSize = 26.sp)
                                }
                            }
                        }

                        // 3. Bottom: Quick Sticker Toolbar (Remove / Reset / Tip)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "💡 Tip: Drag & pinch sticker on canvas to adjust",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            if (overlayEmoji.isNotBlank()) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    TextButton(
                                        onClick = {
                                            emojiOffsetX = 0f
                                            emojiOffsetY = -160f
                                            emojiScale = 1f
                                            emojiRotation = 0f
                                            pushState()
                                        }
                                    ) {
                                        Text("Center", color = ElectricBlue, fontSize = 11.sp)
                                    }
                                    TextButton(
                                        onClick = {
                                            overlayEmoji = ""
                                            pushState()
                                        }
                                    ) {
                                        Text("Remove", color = RadiantPink, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBg50Modal) {
        AlertDialog(
            onDismissRequest = { showBg50Modal = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wallpaper, contentDescription = null, tint = RadiantPink)
                        Spacer(Modifier.width(8.dp))
                        Text("50 Pro Backgrounds Gallery", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { showBg50Modal = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(480.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // REAL USER PHONE GALLERY PICKER BANNER BUTTON
                    Button(
                        onClick = {
                            showBg50Modal = false
                            customBgGalleryPickerLauncher.launch("image/*")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CharcoalSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("🖼️ Choose Custom Photo from My Gallery", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Category Chips inside Modal
                    val categories = listOf("All", "Studio", "Nature", "Urban", "Abstract", "Texture")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedBgCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedBgCategory = cat },
                                label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricBlue,
                                    selectedLabelColor = Color.Black,
                                    containerColor = CharcoalSurface,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }

                    // 2-Column Grid for all 50 backgrounds
                    val filteredList = remember(selectedBgCategory) {
                        if (selectedBgCategory == "All") ProBackgroundSet.ALL_50_BACKGROUNDS
                        else ProBackgroundSet.ALL_50_BACKGROUNDS.filter { it.category == selectedBgCategory }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(filteredList, key = { it.id }) { preset ->
                            Surface(
                                onClick = {
                                    showBg50Modal = false
                                    val bmp = loadedBitmap
                                    if (bmp != null && !isAiProcessing) {
                                        isAiProcessing = true
                                        aiProcessingTitle = "Applying Background: ${preset.name}"
                                        scope.launch {
                                            try {
                                                val res = ProAiImageEngine.replaceBackgroundWithPreset(bmp, preset.id)
                                                loadedBitmap = res
                                                pushState(res)
                                                Toast.makeText(context, "${preset.name} Background Applied!", Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isAiProcessing = false
                                            }
                                        }
                                    }
                                },
                                color = CharcoalSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Color Accent Preview Box
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                try { Color(android.graphics.Color.parseColor(preset.previewHex)) } catch(_: Exception) { ElectricBlue }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${preset.id}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(preset.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text(preset.category, color = TextMuted, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = ObsidianBackground,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showManualCutoutDialog && loadedBitmap != null) {
        ManualCutoutDialog(
            inputBitmap = loadedBitmap!!,
            onDismiss = { showManualCutoutDialog = false },
            onApplyCutout = { newBitmap ->
                showManualCutoutDialog = false
                loadedBitmap = newBitmap
                pushState(newBitmap)
                Toast.makeText(context, "Manual Cutout Applied!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun AIActionButton(
    label: String,
    icon: ImageVector,
    badge: String? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = CharcoalSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.testTag("ai_tool_${label.lowercase().replace(" ", "_")}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DeepPurple.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = label, tint = RadiantPink, modifier = Modifier.size(20.dp))
                }
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-4).dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElectricBlue)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badge,
                            color = Color.Black,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ToolActionButton(label: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = CharcoalSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = label, tint = ElectricBlue, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun AdjustmentSliderRow(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.width(90.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = ElectricBlue,
                activeTrackColor = ElectricBlue,
                inactiveTrackColor = CharcoalSurface
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value.toInt().toString(),
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManualCutoutDialog(
    inputBitmap: Bitmap,
    onDismiss: () -> Unit,
    onApplyCutout: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isEraser by remember { mutableStateOf(true) }
    var brushSize by remember { mutableFloatStateOf(45f) }
    var isProcessingAi by remember { mutableStateOf(false) }

    // Create editable mask bitmap matching inputBitmap
    val maskBitmap = remember(inputBitmap) {
        val bmp = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bmp)
        canvas.drawColor(android.graphics.Color.WHITE) // Initially full white (keep all)
        bmp
    }

    // Trigger state to force recomposition when mask changes
    var maskRevision by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ContentCut, contentDescription = null, tint = RadiantPink)
                    Spacer(Modifier.width(8.dp))
                    Text("PicsArt Manual Cutout & Restore", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Toolbar Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = isEraser,
                            onClick = { isEraser = true },
                            label = { Text("🧹 Erase BG", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RadiantPink,
                                selectedLabelColor = Color.White,
                                containerColor = CharcoalSurface,
                                labelColor = TextPrimary
                            )
                        )
                        FilterChip(
                            selected = !isEraser,
                            onClick = { isEraser = false },
                            label = { Text("🖌️ Restore Subject", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue,
                                selectedLabelColor = Color.Black,
                                containerColor = CharcoalSurface,
                                labelColor = TextPrimary
                            )
                        )
                    }

                    Button(
                        onClick = {
                            if (!isProcessingAi) {
                                isProcessingAi = true
                                scope.launch {
                                    try {
                                        val aiMask = com.example.engine.BackgroundRemoverEngine.extractSegmentationMask(inputBitmap, isHighQuality = true)
                                        val canvas = android.graphics.Canvas(maskBitmap)
                                        canvas.drawColor(android.graphics.Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)
                                        canvas.drawBitmap(aiMask, 0f, 0f, null)
                                        aiMask.recycle()
                                        maskRevision++
                                        Toast.makeText(context, "AI Subject Isolated! Fine-tune with brush.", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "AI Cutout Failed", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isProcessingAi = false
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CharcoalSurfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isProcessingAi) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = ElectricBlue)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(14.dp))
                        }
                        Spacer(Modifier.width(4.dp))
                        Text("AI Auto", fontSize = 11.sp, color = TextPrimary)
                    }
                }

                // Brush Size Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Brush:", fontSize = 12.sp, color = TextMuted)
                    Slider(
                        value = brushSize,
                        onValueChange = { brushSize = it },
                        valueRange = 15f..120f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(thumbColor = RadiantPink, activeTrackColor = RadiantPink)
                    )
                    Text("${brushSize.toInt()}px", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                }

                // Interactive Touch Canvas Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    // Subscribe to maskRevision
                    val revision = maskRevision

                    androidx.compose.foundation.Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(isEraser, brushSize) {
                                detectDragGestures { change, _ ->
                                    change.consume()
                                    val touchX = change.position.x
                                    val touchY = change.position.y

                                    val canvasW = size.width
                                    val canvasH = size.height
                                    if (canvasW > 0 && canvasH > 0) {
                                        val scaleX = inputBitmap.width.toFloat() / canvasW
                                        val scaleY = inputBitmap.height.toFloat() / canvasH

                                        val bitmapX = touchX * scaleX
                                        val bitmapY = touchY * scaleY
                                        val radius = (brushSize / 2f) * scaleX

                                        val canvasMask = android.graphics.Canvas(maskBitmap)
                                        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                            style = android.graphics.Paint.Style.FILL
                                        }

                                        if (isEraser) {
                                            paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)
                                        } else {
                                            paint.color = android.graphics.Color.WHITE
                                        }

                                        canvasMask.drawCircle(bitmapX, bitmapY, radius, paint)
                                        maskRevision++
                                    }
                                }
                            }
                    ) {
                        val canvasW = size.width
                        val canvasH = size.height

                        // Build preview bitmap
                        if (inputBitmap.width > 0 && inputBitmap.height > 0) {
                            val preview = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
                            val canvasPrev = android.graphics.Canvas(preview)
                            val paintPrev = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

                            canvasPrev.drawBitmap(maskBitmap, 0f, 0f, paintPrev)
                            paintPrev.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
                            canvasPrev.drawBitmap(inputBitmap, 0f, 0f, paintPrev)

                            drawImage(
                                image = preview.asImageBitmap(),
                                dstSize = androidx.compose.ui.unit.IntSize(canvasW.toInt(), canvasH.toInt())
                            )
                            preview.recycle()
                        }
                    }
                }

                // Bottom Actions: Reset & Apply
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            val canvas = android.graphics.Canvas(maskBitmap)
                            canvas.drawColor(android.graphics.Color.WHITE)
                            maskRevision++
                        }
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reset Mask", color = TextMuted, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val result = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
                            val canvas = android.graphics.Canvas(result)
                            val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

                            canvas.drawBitmap(maskBitmap, 0f, 0f, paint)
                            paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
                            canvas.drawBitmap(inputBitmap, 0f, 0f, paint)

                            onApplyCutout(result)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RadiantPink),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Apply Cutout", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = ObsidianBackground,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun BeforeAfterComparisonCanvas(
    originalBitmap: Bitmap,
    processedBitmap: Bitmap,
    activeColorMatrix: androidx.compose.ui.graphics.ColorMatrix,
    splitPosition: Float,
    onSplitPositionChange: (Float) -> Unit,
    floatingBadgeText: String,
    floatingBadgeIcon: ImageVector,
    showInsetZoom: Boolean,
    isProcessing: Boolean,
    processingTitle: String,
    textOverlay: String,
    textColor: Color,
    textOffsetX: Float,
    textOffsetY: Float,
    textScale: Float,
    textRotation: Float,
    hasTextBackground: Boolean,
    onTextTransform: (Float, Float, Float, Float) -> Unit,
    overlayEmoji: String,
    emojiOffsetX: Float,
    emojiOffsetY: Float,
    emojiScale: Float,
    emojiRotation: Float,
    onEmojiTransform: (Float, Float, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = androidx.compose.ui.platform.LocalDensity.current.density
    var isSplitMode by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(CharcoalSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        val containerWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val splitXPx = (containerWidthPx * splitPosition).coerceIn(10f, containerWidthPx - 10f)

        if (!isSplitMode) {
            // FULL SINGLE STRAIGHT PHOTO VIEW (NORMAL MODE) with real-time ColorMatrix filters & adjustments
            Image(
                bitmap = processedBitmap.asImageBitmap(),
                contentDescription = "Full Photo View",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.colorMatrix(activeColorMatrix),
                modifier = Modifier.fillMaxSize()
            )

            // Top-Left Badge: Floating Tool Badge ("Remove BG" / "AI Enhance")
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .shadow(8.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(22.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = floatingBadgeIcon,
                        contentDescription = null,
                        tint = RadiantPink,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = floatingBadgeText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Top-Right Button: Toggle Split Before/After Compare
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(CharcoalSurfaceVariant)
                    .border(1.dp, ElectricBlue, RoundedCornerShape(20.dp))
                    .clickable { isSplitMode = true }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Compare,
                        contentDescription = "Split View",
                        tint = ElectricBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Split Compare",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // SPLIT BEFORE/AFTER COMPARISON SLIDER MODE
            // 1. Processed Bitmap on Right (Base layer) with filters
            Image(
                bitmap = processedBitmap.asImageBitmap(),
                contentDescription = "Processed Photo Canvas",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.colorMatrix(activeColorMatrix),
                modifier = Modifier.fillMaxSize()
            )

            // 2. Original Unedited Bitmap on Left (Clipped to splitXPx)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
                    .graphicsLayer {
                        clip = true
                    }
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        layout(splitXPx.toInt().coerceAtLeast(1), placeable.height) {
                            placeable.placeRelative(0, 0)
                        }
                    }
            ) {
                Image(
                    bitmap = originalBitmap.asImageBitmap(),
                    contentDescription = "Original Photo Canvas",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(
                                constraints.copy(
                                    minWidth = constraints.maxWidth,
                                    maxWidth = constraints.maxWidth
                                )
                            )
                            layout(placeable.width, placeable.height) {
                                placeable.placeRelative(0, 0)
                            }
                        }
                )
            }

            // 3. Before Badge (Top-Left)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Before",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 4. After Badge & Close Split Mode Button (Top-Right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(RadiantPink)
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .clickable { isSplitMode = false }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Full Photo ✕",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 5. Vertical Split Divider Line
            Box(
                modifier = Modifier
                    .offset(x = (splitXPx / density).dp - 1.dp)
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(Color.White)
            )

            // 6. Interactive Circular Drag Knob Handle (◄►)
            Box(
                modifier = Modifier
                    .offset(x = (splitXPx / density).dp - 22.dp)
                    .size(44.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val newRatio = (splitPosition + (dragAmount.x / containerWidthPx)).coerceIn(0.05f, 0.95f)
                            onSplitPositionChange(newRatio)
                        }
                    }
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, RadiantPink, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Compare,
                    contentDescription = "Before After Split Slider Handle",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            // 8. Inset Magnifier Detail Preview Box (Bottom-Left Corner) for AI Enhance Comparison
            if (showInsetZoom) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .size(110.dp)
                        .shadow(12.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black)
                        .border(2.dp, Color.White, RoundedCornerShape(14.dp))
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().clipToBounds()) {
                            Image(
                                bitmap = originalBitmap.asImageBitmap(),
                                contentDescription = "Zoom Before",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.White))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().clipToBounds()) {
                            Image(
                                bitmap = processedBitmap.asImageBitmap(),
                                contentDescription = "Zoom After",
                                contentScale = ContentScale.Crop,
                                colorFilter = ColorFilter.colorMatrix(activeColorMatrix),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        // Interactive Text Overlay
        if (textOverlay.isNotBlank()) {
            Box(
                modifier = Modifier
                    .offset(x = textOffsetX.dp, y = textOffsetY.dp)
                    .graphicsLayer(
                        scaleX = textScale,
                        scaleY = textScale,
                        rotationZ = textRotation
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, rotation ->
                            onTextTransform(pan.x / 2.5f, pan.y / 2.5f, zoom, rotation)
                        }
                    }
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (hasTextBackground) Color.Black.copy(alpha = 0.55f) else Color.Transparent)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = textOverlay,
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Interactive Emoji / Sticker Overlay
        if (overlayEmoji.isNotBlank()) {
            Box(
                modifier = Modifier
                    .offset(x = emojiOffsetX.dp, y = emojiOffsetY.dp)
                    .graphicsLayer(
                        scaleX = emojiScale,
                        scaleY = emojiScale,
                        rotationZ = emojiRotation
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, rotation ->
                            onEmojiTransform(pan.x / 2.5f, pan.y / 2.5f, zoom, rotation)
                        }
                    }
                    .padding(8.dp)
            ) {
                Text(
                    text = overlayEmoji,
                    fontSize = 44.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // AI Processing Indicator Overlay
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = RadiantPink,
                        modifier = Modifier.size(44.dp),
                        strokeWidth = 3.dp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = processingTitle,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Processing neural pixels...",
                        color = ElectricBlue,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
