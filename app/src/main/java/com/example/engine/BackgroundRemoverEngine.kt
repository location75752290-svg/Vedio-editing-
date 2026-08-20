package com.example.engine

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.collection.LruCache
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class BgRemoverConfig(
    val enabled: Boolean = false,
    val mode: String = "green_screen", // "transparent", "solid_color", "blur", "replace_image", "green_screen"
    val colorHex: String = "#00FF00",
    val featherAmount: Int = 30, // 0 - 100
    val blurAmount: Int = 20, // 0 - 50
    val isHighQuality: Boolean = false, // false = Low Quality Fast 15fps, true = High Quality Slow
    val replaceBgUri: Uri? = null,
    val showSplitPreview: Boolean = false
)

object BackgroundRemoverEngine {

    private val maskCache = LruCache<String, Bitmap>(20)

    /**
     * Loads a Bitmap safely from a Uri with bound protection
     */
    suspend fun loadBitmapFromUri(context: Context, uri: Uri, maxDimension: Int = 1920): Bitmap? = withContext(Dispatchers.IO) {
        try {
            var inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            var sampleSize = 1
            while (options.outWidth / sampleSize > maxDimension || options.outHeight / sampleSize > maxDimension) {
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream, null, decodeOptions)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Runs MLKit Selfie Segmentation on input bitmap to extract alpha mask
     */
    suspend fun extractSegmentationMask(
        inputBitmap: Bitmap,
        isHighQuality: Boolean = false,
        featherAmount: Int = 30
    ): Bitmap = suspendCancellableCoroutine { continuation ->
        try {
            val detectorMode = if (isHighQuality) SelfieSegmenterOptions.SINGLE_IMAGE_MODE else SelfieSegmenterOptions.STREAM_MODE
            val options = SelfieSegmenterOptions.Builder()
                .setDetectorMode(detectorMode)
                .build()

            val segmenter = Segmentation.getClient(options)
            val inputImage = InputImage.fromBitmap(inputBitmap, 0)

            segmenter.process(inputImage)
                .addOnSuccessListener { mask ->
                    val maskBuffer = mask.buffer
                    val maskWidth = mask.width
                    val maskHeight = mask.height

                    val maskBitmap = Bitmap.createBitmap(maskWidth, maskHeight, Bitmap.Config.ARGB_8888)
                    maskBuffer.rewind()

                    val pixels = IntArray(maskWidth * maskHeight)
                    val threshold = if (isHighQuality) 0.35f else 0.45f

                    for (i in 0 until (maskWidth * maskHeight)) {
                        val confidence = maskBuffer.float
                        val alpha = if (confidence > threshold) {
                            (confidence * 255).toInt().coerceIn(0, 255)
                        } else 0
                        pixels[i] = Color.argb(alpha, 255, 255, 255)
                    }
                    maskBitmap.setPixels(pixels, 0, maskWidth, 0, 0, maskWidth, maskHeight)

                    // Scale mask to input bitmap dimensions if needed
                    val scaledMask = if (maskWidth != inputBitmap.width || maskHeight != inputBitmap.height) {
                        val scaled = Bitmap.createScaledBitmap(maskBitmap, inputBitmap.width, inputBitmap.height, true)
                        maskBitmap.recycle()
                        scaled
                    } else {
                        maskBitmap
                    }

                    if (!continuation.isCompleted) {
                        continuation.resume(scaledMask)
                    }
                }
                .addOnFailureListener { e ->
                    if (!continuation.isCompleted) {
                        continuation.resumeWithException(e)
                    }
                }
        } catch (e: Exception) {
            if (!continuation.isCompleted) {
                continuation.resumeWithException(e)
            }
        }
    }

    /**
     * Process a full video frame bitmap with specified BgRemoverConfig
     * supports Transparent, Solid Color, Green Screen, Blur, and Image Replacement
     */
    suspend fun processFrameComposited(
        context: Context,
        inputBitmap: Bitmap,
        config: BgRemoverConfig,
        replacementBgBitmap: Bitmap? = null,
        frameTimestampMs: Long = 0L
    ): Bitmap = withContext(Dispatchers.Default) {
        if (!config.enabled) return@withContext inputBitmap

        val cacheKey = "${config.mode}_${config.colorHex}_${config.featherAmount}_${config.blurAmount}_$frameTimestampMs"
        maskCache.get(cacheKey)?.let { return@withContext it }

        val maskBitmap = try {
            extractSegmentationMask(inputBitmap, config.isHighQuality, config.featherAmount)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext inputBitmap
        }

        // Isolate subject using alpha mask
        val subjectBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
        val canvasSubject = Canvas(subjectBitmap)
        val paintSubject = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        canvasSubject.drawBitmap(maskBitmap, 0f, 0f, paintSubject)
        paintSubject.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvasSubject.drawBitmap(inputBitmap, 0f, 0f, paintSubject)

        // Create canvas for final composited output
        val outputBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
        val canvasFinal = Canvas(outputBitmap)
        val paintFinal = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // Draw Background according to mode
        when (config.mode) {
            "green_screen" -> {
                canvasFinal.drawColor(Color.parseColor("#00FF00"))
            }
            "solid_color" -> {
                val parsedColor = try { Color.parseColor(config.colorHex) } catch (_: Exception) { Color.RED }
                canvasFinal.drawColor(parsedColor)
            }
            "blur" -> {
                val blurredBg = createBlurredBitmap(inputBitmap, config.blurAmount)
                canvasFinal.drawBitmap(blurredBg, 0f, 0f, paintFinal)
                blurredBg.recycle()
            }
            "replace_image" -> {
                if (replacementBgBitmap != null) {
                    val destRect = Rect(0, 0, inputBitmap.width, inputBitmap.height)
                    canvasFinal.drawBitmap(replacementBgBitmap, null, destRect, paintFinal)
                } else {
                    canvasFinal.drawColor(Color.parseColor("#00FF00")) // Fallback to green
                }
            }
            "transparent" -> {
                // Transparent background (keep default clear ARGB_8888)
            }
            else -> {
                canvasFinal.drawColor(Color.parseColor("#00FF00"))
            }
        }

        // Draw Subject on top of chosen background
        canvasFinal.drawBitmap(subjectBitmap, 0f, 0f, paintFinal)

        // If Split Preview is enabled, draw original on left half & BG removed on right half
        if (config.showSplitPreview) {
            val splitWidth = inputBitmap.width / 2
            val srcRectLeft = Rect(0, 0, splitWidth, inputBitmap.height)
            canvasFinal.drawBitmap(inputBitmap, srcRectLeft, srcRectLeft, paintFinal)

            // Draw vertical dividing line
            val linePaint = Paint().apply {
                color = Color.CYAN
                strokeWidth = 6f
            }
            canvasFinal.drawLine(splitWidth.toFloat(), 0f, splitWidth.toFloat(), inputBitmap.height.toFloat(), linePaint)
        }

        // Clean up intermediate bitmaps
        maskBitmap.recycle()
        subjectBitmap.recycle()

        maskCache.put(cacheKey, outputBitmap)
        outputBitmap
    }

    /**
     * Creates a simple fast box-blurred version of input bitmap for background blur
     */
    private fun createBlurredBitmap(input: Bitmap, blurAmount: Int): Bitmap {
        val radius = blurAmount.coerceIn(1, 50)
        val width = (input.width * 0.3f).toInt().coerceAtLeast(1)
        val height = (input.height * 0.3f).toInt().coerceAtLeast(1)

        val scaled = Bitmap.createScaledBitmap(input, width, height, true)
        val blurred = Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(blurred)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            alpha = (255 - radius * 3).coerceIn(120, 255)
        }

        val destRect = Rect(0, 0, input.width, input.height)
        canvas.drawBitmap(scaled, null, destRect, paint)
        scaled.recycle()

        return blurred
    }

    /**
     * Backward compatible simple removeBackground method
     */
    suspend fun removeBackground(inputBitmap: Bitmap): Bitmap = suspendCancellableCoroutine { continuation ->
        try {
            val options = SelfieSegmenterOptions.Builder()
                .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
                .build()
            val segmenter = Segmentation.getClient(options)
            val inputImage = InputImage.fromBitmap(inputBitmap, 0)

            segmenter.process(inputImage)
                .addOnSuccessListener { segmentationMask ->
                    val maskBuffer = segmentationMask.buffer
                    val maskWidth = segmentationMask.width
                    val maskHeight = segmentationMask.height

                    val maskBitmap = Bitmap.createBitmap(maskWidth, maskHeight, Bitmap.Config.ARGB_8888)
                    maskBuffer.rewind()

                    val pixels = IntArray(maskWidth * maskHeight)
                    for (i in 0 until (maskWidth * maskHeight)) {
                        val confidence = maskBuffer.float
                        val alpha = if (confidence > 0.4f) (confidence * 255).toInt().coerceIn(0, 255) else 0
                        pixels[i] = Color.argb(alpha, 255, 255, 255)
                    }
                    maskBitmap.setPixels(pixels, 0, maskWidth, 0, 0, maskWidth, maskHeight)

                    val outputBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(outputBitmap)
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

                    val scaledMask = Bitmap.createScaledBitmap(maskBitmap, inputBitmap.width, inputBitmap.height, true)
                    canvas.drawBitmap(scaledMask, 0f, 0f, paint)

                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                    canvas.drawBitmap(inputBitmap, 0f, 0f, paint)

                    if (scaledMask != maskBitmap) scaledMask.recycle()
                    maskBitmap.recycle()

                    if (!continuation.isCompleted) continuation.resume(outputBitmap)
                }
                .addOnFailureListener { exception ->
                    if (!continuation.isCompleted) continuation.resumeWithException(exception)
                }
        } catch (e: Exception) {
            if (!continuation.isCompleted) continuation.resumeWithException(e)
        }
    }

    /**
     * Saves transparent bitmap to gallery
     */
    suspend fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String = "VisionCut_BG_Removed"): Boolean = withContext(Dispatchers.IO) {
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "${fileName}_${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/VisionCutAI")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return@withContext false

            resolver.openOutputStream(uri)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            } ?: return@withContext false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
