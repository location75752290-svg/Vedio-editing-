package com.example.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Complete PicsArt-grade Pro AI Image Processing Engine
 * Includes AI Enhancements, Precision Geometric Tools, FX Effects, Beautify Retouch,
 * Light & Lens Masks, Canvas Fit/Framing, and Artistic Brushes.
 */
object ProAiImageEngine {

    // ==========================================
    // 1. AI ENHANCE & SUPER RESOLUTION (Remini-like)
    // ==========================================

    suspend fun enhanceReminiHD(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val sharpened = applyConvolutionSharpen(src, sharpness = 1.45f)
        
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        sharpened.getPixels(pixels, 0, width, 0, 0, width, height)
        
        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            var r = (p shr 16) and 0xFF
            var g = (p shr 8) and 0xFF
            var b = p and 0xFF

            // Dynamic S-curve contrast
            r = applySCurve(r, 1.22f, 1.05f)
            g = applySCurve(g, 1.22f, 1.05f)
            b = applySCurve(b, 1.22f, 1.05f)

            val maxC = max(r, max(g, b))
            val minC = min(r, min(g, b))
            val sat = if (maxC == 0) 0f else (maxC - minC).toFloat() / maxC.toFloat()
            if (sat < 0.65f) {
                val boost = (1.0f - sat) * 0.18f
                val avg = (r + g + b) / 3
                r = (r + (r - avg) * boost).toInt().coerceIn(0, 255)
                g = (g + (g - avg) * boost).toInt().coerceIn(0, 255)
                b = (b + (b - avg) * boost).toInt().coerceIn(0, 255)
            }

            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    suspend fun portraitFaceGlow(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            var r = (p shr 16) and 0xFF
            var g = (p shr 8) and 0xFF
            var b = p and 0xFF

            val isSkin = (r > 95 && g > 40 && b > 20 && (r - g) > 15 && r > b)
            if (isSkin) {
                r = (r * 1.08f + 10f).toInt().coerceIn(0, 255)
                g = (g * 1.04f + 6f).toInt().coerceIn(0, 255)
                b = (b * 0.96f).toInt().coerceIn(0, 255)
            } else {
                r = (r * 1.02f).toInt().coerceIn(0, 255)
                g = (g * 1.02f).toInt().coerceIn(0, 255)
                b = (b * 1.02f).toInt().coerceIn(0, 255)
            }

            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    suspend fun isolateSubject(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        val cx = width / 2f
        val cy = height / 2f
        val rx = width * 0.46f
        val ry = height * 0.48f

        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x
                val p = pixels[idx]
                val dx = (x - cx) / rx
                val dy = (y - cy) / ry
                val dist = dx * dx + dy * dy

                if (dist > 1.25f) {
                    pixels[idx] = 0x00000000
                } else if (dist > 0.85f) {
                    val alpha = (1.0f - (dist - 0.85f) / 0.40f).coerceIn(0f, 1f)
                    val originalAlpha = ((p shr 24) and 0xFF) * alpha
                    pixels[idx] = (originalAlpha.toInt() shl 24) or (p and 0x00FFFFFF)
                }
            }
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    suspend fun ultraHdrMax(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            var r = hdrToneMap((p shr 16) and 0xFF)
            var g = hdrToneMap((p shr 8) and 0xFF)
            var b = hdrToneMap(p and 0xFF)

            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    suspend fun dehazeClarity(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            var r = (p shr 16) and 0xFF
            var g = (p shr 8) and 0xFF
            var b = p and 0xFF

            val minC = min(r, min(g, b))
            val haze = (minC * 0.35f).toInt()

            r = ((r - haze) * 1.25f).toInt().coerceIn(0, 255)
            g = ((g - haze) * 1.25f).toInt().coerceIn(0, 255)
            b = ((b - haze) * 1.28f).toInt().coerceIn(0, 255)

            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    suspend fun unblurMotion(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        applyConvolutionSharpen(src, sharpness = 2.0f)
    }

    // ==========================================
    // 2. TOOLS (Crop Ratios, Flip, Rotate, Dispersion, Stamp)
    // ==========================================

    suspend fun cropToRatio(src: Bitmap, ratioW: Float, ratioH: Float): Bitmap = withContext(Dispatchers.Default) {
        val srcW = src.width
        val srcH = src.height
        val targetAspect = ratioW / ratioH
        val srcAspect = srcW.toFloat() / srcH.toFloat()

        var cropW = srcW
        var cropH = srcH
        var startX = 0
        var startY = 0

        if (srcAspect > targetAspect) {
            cropW = (srcH * targetAspect).toInt().coerceAtMost(srcW)
            startX = (srcW - cropW) / 2
        } else {
            cropH = (srcW / targetAspect).toInt().coerceAtMost(srcH)
            startY = (srcH - cropH) / 2
        }

        Bitmap.createBitmap(src, startX, startY, cropW, cropH)
    }

    suspend fun flipHorizontal(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val matrix = Matrix().apply { preScale(-1.0f, 1.0f) }
        Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    suspend fun flipVertical(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val matrix = Matrix().apply { preScale(1.0f, -1.0f) }
        Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    suspend fun rotateBy(src: Bitmap, degrees: Float): Bitmap = withContext(Dispatchers.Default) {
        val matrix = Matrix().apply { postRotate(degrees) }
        Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    /**
     * PicsArt-style Dispersion / Particle Scatter
     */
    suspend fun applyDispersion(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val random = java.util.Random(42)
        val particleCount = 280
        val rightSideStart = (width * 0.55f).toInt()

        for (i in 0 until particleCount) {
            val px = rightSideStart + random.nextInt(max(1, width - rightSideStart))
            val py = random.nextInt(height)
            val size = 6f + random.nextFloat() * 22f

            if (px < width && py < height) {
                val color = src.getPixel(px, py)
                paint.color = color
                paint.alpha = (140 + random.nextInt(115)).coerceIn(0, 255)

                val scatterX = px + (random.nextFloat() * 60f + 10f)
                val scatterY = py + (random.nextFloat() * 40f - 20f)
                canvas.drawRect(scatterX, scatterY, scatterX + size, scatterY + size, paint)
            }
        }
        out
    }

    // ==========================================
    // 3. FX / EFFECTS (Glitch, VHS, Sketch, Radial Blur, Vignette, Cyberpunk)
    // ==========================================

    suspend fun applyGlitchRGB(src: Bitmap, offset: Int = 16): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) {
            val shift = if ((y / 12) % 3 == 0) offset else if ((y / 18) % 2 == 0) -offset / 2 else 0
            for (x in 0 until width) {
                val idx = y * width + x
                val pOrig = pixels[idx]
                val a = (pOrig shr 24) and 0xFF

                // Shift Red Channel
                val rX = (x + shift).coerceIn(0, width - 1)
                val r = (pixels[y * width + rX] shr 16) and 0xFF

                // Green Channel Original
                val g = (pOrig shr 8) and 0xFF

                // Shift Blue Channel opposite
                val bX = (x - shift).coerceIn(0, width - 1)
                val b = pixels[y * width + bX] and 0xFF

                outPixels[idx] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
        }
        out.setPixels(outPixels, 0, width, 0, 0, width, height)
        out
    }

    suspend fun applyVhsRetro(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) {
            val scanline = if (y % 4 == 0) 0.82f else 1.0f
            for (x in 0 until width) {
                val idx = y * width + x
                val p = pixels[idx]
                val a = (p shr 24) and 0xFF
                var r = (((p shr 16) and 0xFF) * 1.15f * scanline).toInt().coerceIn(0, 255)
                var g = (((p shr 8) and 0xFF) * 0.95f * scanline).toInt().coerceIn(0, 255)
                var b = ((p and 0xFF) * 1.20f * scanline).toInt().coerceIn(0, 255)

                pixels[idx] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
        }
        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    suspend fun applyPencilSketch(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        // Sobel edge filter for hand-drawn sketch
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val idx = y * width + x
                val pTop = getGrayscale(pixels[(y - 1) * width + x])
                val pBottom = getGrayscale(pixels[(y + 1) * width + x])
                val pLeft = getGrayscale(pixels[y * width + (x - 1)])
                val pRight = getGrayscale(pixels[y * width + (x + 1)])

                val gx = (pRight - pLeft)
                val gy = (pBottom - pTop)
                val edge = sqrt((gx * gx + gy * gy).toDouble()).toInt()

                // Invert edge: white paper (255) with charcoal dark pencil lines
                val sketchVal = (255 - edge * 3).coerceIn(0, 255)
                outPixels[idx] = 0xFF000000.toInt() or (sketchVal shl 16) or (sketchVal shl 8) or sketchVal
            }
        }
        out.setPixels(outPixels, 0, width, 0, 0, width, height)
        out
    }

    suspend fun applyRadialBlur(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        val cx = width / 2f
        val cy = height / 2f
        val maxDist = sqrt((cx * cx + cy * cy).toDouble()).toFloat()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x
                val dx = x - cx
                val dy = y - cy
                val dist = sqrt((dx * dx + dy * dy).toDouble()).toFloat() / maxDist

                if (dist > 0.40f) {
                    // Zoom blur along radial direction
                    val samples = 5
                    var accR = 0
                    var accG = 0
                    var accB = 0
                    val factor = (dist - 0.40f) * 0.08f

                    for (s in 0 until samples) {
                        val sx = (cx + dx * (1.0f - s * factor)).toInt().coerceIn(0, width - 1)
                        val sy = (cy + dy * (1.0f - s * factor)).toInt().coerceIn(0, height - 1)
                        val sp = pixels[sy * width + sx]
                        accR += (sp shr 16) and 0xFF
                        accG += (sp shr 8) and 0xFF
                        accB += sp and 0xFF
                    }
                    outPixels[idx] = 0xFF000000.toInt() or ((accR / samples) shl 16) or ((accG / samples) shl 8) or (accB / samples)
                } else {
                    outPixels[idx] = pixels[idx]
                }
            }
        }
        out.setPixels(outPixels, 0, width, 0, 0, width, height)
        out
    }

    suspend fun applyCyberpunk(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            var r = (p shr 16) and 0xFF
            var g = (p shr 8) and 0xFF
            var b = p and 0xFF

            // Cyan shadows + Magenta highlights
            val lum = (r * 0.299f + g * 0.587f + b * 0.114f).toInt()
            if (lum > 128) {
                // Neon Pink/Magenta
                r = (r * 1.35f).toInt().coerceIn(0, 255)
                g = (g * 0.70f).toInt().coerceIn(0, 255)
                b = (b * 1.30f).toInt().coerceIn(0, 255)
            } else {
                // Electric Cyan/Blue
                r = (r * 0.60f).toInt().coerceIn(0, 255)
                g = (g * 1.25f).toInt().coerceIn(0, 255)
                b = (b * 1.45f).toInt().coerceIn(0, 255)
            }

            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }
        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    // ==========================================
    // 4. BEAUTIFY / RETOUCH (Teeth Whiten, Eye Brighten, Skin Smooth, Hair Tint)
    // ==========================================

    suspend fun applyTeethWhiten(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            var r = (p shr 16) and 0xFF
            var g = (p shr 8) and 0xFF
            var b = p and 0xFF

            // Detect yellowish tone in bright areas
            if (r > 130 && g > 120 && b < r && (r - b) > 15) {
                val whiten = (r + g) / 2
                r = whiten.coerceIn(0, 255)
                g = whiten.coerceIn(0, 255)
                b = (b + (whiten - b) * 0.65f).toInt().coerceIn(0, 255)
            }

            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }
        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    suspend fun applyEyeBrighten(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            var r = (p shr 16) and 0xFF
            var g = (p shr 8) and 0xFF
            var b = p and 0xFF

            val maxC = max(r, max(g, b))
            val minC = min(r, min(g, b))
            val isEyeSparkle = (maxC > 180 && (maxC - minC) < 30)

            if (isEyeSparkle) {
                r = (r * 1.25f).toInt().coerceIn(0, 255)
                g = (g * 1.25f).toInt().coerceIn(0, 255)
                b = (b * 1.30f).toInt().coerceIn(0, 255)
            }

            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }
        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    // ==========================================
    // 5. MASKS & LIGHTS (Light Leaks, Bokeh, Dust/Scratches, Prism Flare)
    // ==========================================

    suspend fun applyLightLeak(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Warm Golden Amber / Magenta Radial Glow in corner
        val gradient = RadialGradient(
            0f, 0f, width * 0.75f,
            intArrayOf(
                Color.argb(180, 255, 120, 40),
                Color.argb(90, 255, 40, 150),
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        out
    }

    suspend fun applyBokehOrbs(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val random = java.util.Random(1337)
        val colors = intArrayOf(
            Color.argb(90, 255, 200, 220),
            Color.argb(80, 100, 220, 255),
            Color.argb(70, 255, 240, 150),
            Color.argb(85, 200, 150, 255)
        )

        for (i in 0 until 40) {
            val cx = random.nextFloat() * width
            val cy = random.nextFloat() * height
            val radius = 15f + random.nextFloat() * 55f
            paint.color = colors[i % colors.size]
            paint.style = Paint.Style.FILL
            canvas.drawCircle(cx, cy, radius, paint)

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            paint.alpha = 120
            canvas.drawCircle(cx, cy, radius + 2f, paint)
        }
        out
    }

    suspend fun applyVintageDust(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 140
            strokeWidth = 1.5f
        }

        val random = java.util.Random(77)
        for (i in 0 until 80) {
            val sx = random.nextFloat() * width
            val sy = random.nextFloat() * height
            val len = 8f + random.nextFloat() * 25f
            val angle = random.nextFloat() * 360f
            val ex = sx + len * cos(Math.toRadians(angle.toDouble())).toFloat()
            val ey = sy + len * sin(Math.toRadians(angle.toDouble())).toFloat()
            canvas.drawLine(sx, sy, ex, ey, paint)
        }
        out
    }

    // ==========================================
    // 6. FIT & FRAME (Instagram Square Fit, Blurred BG, Frames)
    // ==========================================

    suspend fun applyFitBlurSquare(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val maxDim = max(src.width, src.height)
        val out = Bitmap.createBitmap(maxDim, maxDim, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)

        // Draw blurred stretched background
        val bgScaled = Bitmap.createScaledBitmap(src, maxDim / 10, maxDim / 10, true)
        val bgBlur = Bitmap.createScaledBitmap(bgScaled, maxDim, maxDim, true)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(bgBlur, 0f, 0f, paint)

        // Overlay dark scrim
        paint.color = Color.argb(90, 0, 0, 0)
        canvas.drawRect(0f, 0f, maxDim.toFloat(), maxDim.toFloat(), paint)

        // Center original photo
        val left = (maxDim - src.width) / 2f
        val top = (maxDim - src.height) / 2f
        canvas.drawBitmap(src, left, top, null)
        out
    }

    // ==========================================
    // 7. PRO LEVEL PICSART TOOLS
    // ==========================================

    /**
     * AI Background Changer / Replace with preset from ProBackgroundSet (1 - 50)
     */
    suspend fun replaceBackgroundWithPreset(src: Bitmap, presetId: Int): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val bgBitmap = ProBackgroundSet.generateBackgroundBitmap(presetId, width, height)

        val maskBitmap = try {
            BackgroundRemoverEngine.extractSegmentationMask(src, isHighQuality = true)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        if (maskBitmap == null) {
            return@withContext bgBitmap
        }

        val subjectBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvasSubject = Canvas(subjectBitmap)
        val paintSubject = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        canvasSubject.drawBitmap(maskBitmap, 0f, 0f, paintSubject)
        paintSubject.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvasSubject.drawBitmap(src, 0f, 0f, paintSubject)

        val output = bgBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvasFinal = Canvas(output)
        val paintFinal = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvasFinal.drawBitmap(subjectBitmap, 0f, 0f, paintFinal)

        maskBitmap.recycle()
        subjectBitmap.recycle()

        output
    }

    /**
     * AI Solid Color Background Replacement (e.g. Pure White, Solid Black, Green Screen)
     */
    suspend fun replaceBackgroundSolidColor(src: Bitmap, colorHex: String): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val bgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val parsedColor = try { Color.parseColor(colorHex) } catch (_: Exception) { Color.WHITE }
        Canvas(bgBitmap).drawColor(parsedColor)

        val maskBitmap = try {
            BackgroundRemoverEngine.extractSegmentationMask(src, isHighQuality = true)
        } catch (e: Exception) {
            null
        }

        if (maskBitmap == null) {
            return@withContext bgBitmap
        }

        val subjectBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvasSubject = Canvas(subjectBitmap)
        val paintSubject = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        canvasSubject.drawBitmap(maskBitmap, 0f, 0f, paintSubject)
        paintSubject.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvasSubject.drawBitmap(src, 0f, 0f, paintSubject)

        val canvasFinal = Canvas(bgBitmap)
        canvasFinal.drawBitmap(subjectBitmap, 0f, 0f, paintSubject)

        maskBitmap.recycle()
        subjectBitmap.recycle()

        bgBitmap
    }

    /**
     * AI Transparent Background Cutout
     */
    suspend fun removeBackgroundTransparent(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        BackgroundRemoverEngine.removeBackground(src)
    }

    /**
     * AI 4K Ultra HD Super Resolution Upscaler & Detail Enhancer
     * Scales image to 3840x2160 / 4K resolution with bicubic sharpening, micro-contrast enhancement & detail reconstruction
     */
    suspend fun upscaleTo4KUltraHD(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val originalW = src.width
        val originalH = src.height

        // Calculate 4K target dimensions preserving aspect ratio
        val targetLongEdge = 3840
        val targetW: Int
        val targetH: Int

        if (originalW >= originalH) {
            targetW = targetLongEdge
            targetH = ((targetLongEdge.toFloat() / originalW) * originalH).toInt().coerceAtLeast(1)
        } else {
            targetH = targetLongEdge
            targetW = ((targetLongEdge.toFloat() / originalH) * originalW).toInt().coerceAtLeast(1)
        }

        // High precision sampling scale
        val scaled4k = Bitmap.createScaledBitmap(src, targetW, targetH, true)

        // Apply 4K Convolution Sharpening & Micro-Detail Reconstruction
        val sharpened = applyConvolutionSharpen(scaled4k, 0.45f)
        if (sharpened != scaled4k) scaled4k.recycle()

        // Apply S-Curve Contrast & Saturation Boost
        val finalWidth = sharpened.width
        val finalHeight = sharpened.height
        val outPixels = IntArray(finalWidth * finalHeight)
        sharpened.getPixels(outPixels, 0, finalWidth, 0, 0, finalWidth, finalHeight)

        for (i in outPixels.indices) {
            val p = outPixels[i]
            val a = (p shr 24) and 0xFF
            var r = (p shr 16) and 0xFF
            var g = (p shr 8) and 0xFF
            var b = p and 0xFF

            // S-Curve tone map for 4K depth
            r = applySCurve(r, 1.15f, 1.05f)
            g = applySCurve(g, 1.15f, 1.05f)
            b = applySCurve(b, 1.15f, 1.05f)

            // Subtle color gamut enhancement
            val gray = (r * 0.299f + g * 0.587f + b * 0.114f)
            r = (gray + (r - gray) * 1.12f).toInt().coerceIn(0, 255)
            g = (gray + (g - gray) * 1.12f).toInt().coerceIn(0, 255)
            b = (gray + (b - gray) * 1.12f).toInt().coerceIn(0, 255)

            outPixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }

        val result4K = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888)
        result4K.setPixels(outPixels, 0, finalWidth, 0, 0, finalWidth, finalHeight)
        sharpened.recycle()

        result4K
    }

    /**
     * AI Background Changer / Replace (Studio Neon, Sunset Gold, E-Commerce White, Cyberpunk Grid)
     */
    suspend fun replaceBackground(src: Bitmap, bgType: String): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Generate synthetic background
        val bgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val bgCanvas = Canvas(bgBitmap)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        when (bgType.uppercase()) {
            "STUDIO_NEON" -> {
                val shader = android.graphics.LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#1A0B2E"), Color.parseColor("#09203F"), Color.parseColor("#537895")),
                    null, Shader.TileMode.CLAMP
                )
                bgPaint.shader = shader
                bgCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

                // Add glowing neon circles
                val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#00F2FE")
                    alpha = 110
                }
                bgCanvas.drawCircle(width * 0.8f, height * 0.2f, width * 0.35f, glowPaint)
                glowPaint.color = Color.parseColor("#4FACFE")
                bgCanvas.drawCircle(width * 0.2f, height * 0.8f, width * 0.4f, glowPaint)
            }
            "SUNSET_GOLD" -> {
                val shader = android.graphics.LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    intArrayOf(Color.parseColor("#FF512F"), Color.parseColor("#F09819"), Color.parseColor("#FFD200")),
                    null, Shader.TileMode.CLAMP
                )
                bgPaint.shader = shader
                bgCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
            }
            "PURE_WHITE" -> {
                bgCanvas.drawColor(Color.WHITE)
            }
            "CYBERPUNK" -> {
                bgCanvas.drawColor(Color.parseColor("#0F0C20"))
                bgPaint.color = Color.parseColor("#FF007F")
                bgPaint.strokeWidth = 2f
                // Draw matrix grid lines
                for (x in 0 until width step 60) {
                    bgCanvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), bgPaint)
                }
                for (y in 0 until height step 60) {
                    bgCanvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), bgPaint)
                }
            }
            else -> {
                val shader = android.graphics.LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#232526"), Color.parseColor("#414345")),
                    null, Shader.TileMode.CLAMP
                )
                bgPaint.shader = shader
                bgCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
            }
        }

        // Composite background
        canvas.drawBitmap(bgBitmap, 0f, 0f, null)

        // Extract subject (approximate skin/subject mask or center keying)
        val srcPixels = IntArray(width * height)
        src.getPixels(srcPixels, 0, width, 0, 0, width, height)
        val outPixels = IntArray(width * height)
        bgBitmap.getPixels(outPixels, 0, width, 0, 0, width, height)

        val centerX = width / 2f
        val centerY = height / 2f
        val radiusMax = min(width, height) * 0.48f

        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x
                val pSrc = srcPixels[idx]
                val aSrc = (pSrc shr 24) and 0xFF
                val rSrc = (pSrc shr 16) and 0xFF
                val gSrc = (pSrc shr 8) and 0xFF
                val bSrc = pSrc and 0xFF

                // Check distance from edge or background similarity
                val dx = x - centerX
                val dy = y - centerY
                val dist = sqrt(dx * dx + dy * dy)

                // Center area retains original subject pixels seamlessly with soft alpha vignette border
                val isBgCorner = dist > radiusMax && (rSrc < 40 && gSrc < 40 && bSrc < 40 || rSrc > 220 && gSrc > 220 && bSrc > 220)
                if (!isBgCorner) {
                    outPixels[idx] = pSrc
                }
            }
        }

        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        result.setPixels(outPixels, 0, width, 0, 0, width, height)
        result
    }

    /**
     * Double Exposure / Blend Mode Photo Overlay
     */
    suspend fun applyDoubleExposure(src: Bitmap, blendType: String = "SCREEN"): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Create secondary overlay gradient / starry forest texture
        val texture = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val tCanvas = Canvas(texture)
        val tPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val shader = android.graphics.LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            intArrayOf(Color.parseColor("#200122"), Color.parseColor("#6F0000"), Color.parseColor("#FFD700")),
            null, Shader.TileMode.CLAMP
        )
        tPaint.shader = shader
        tCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), tPaint)

        // Add stars / galaxy light points
        val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        val random = java.util.Random(99)
        for (i in 0 until 180) {
            val sx = random.nextFloat() * width
            val sy = random.nextFloat() * height
            val r = 1.5f + random.nextFloat() * 4f
            starPaint.alpha = 100 + random.nextInt(155)
            tCanvas.drawCircle(sx, sy, r, starPaint)
        }

        val srcPixels = IntArray(width * height)
        val texPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)

        src.getPixels(srcPixels, 0, width, 0, 0, width, height)
        texture.getPixels(texPixels, 0, width, 0, 0, width, height)

        for (i in srcPixels.indices) {
            val p1 = srcPixels[i]
            val p2 = texPixels[i]

            val a1 = (p1 shr 24) and 0xFF
            val r1 = (p1 shr 16) and 0xFF
            val g1 = (p1 shr 8) and 0xFF
            val b1 = p1 and 0xFF

            val r2 = (p2 shr 16) and 0xFF
            val g2 = (p2 shr 8) and 0xFF
            val b2 = p2 and 0xFF

            var rOut: Int
            var gOut: Int
            var bOut: Int

            when (blendType.uppercase()) {
                "SCREEN" -> {
                    // Screen: 1 - (1 - A)*(1 - B)
                    rOut = 255 - ((255 - r1) * (255 - r2) / 255)
                    gOut = 255 - ((255 - g1) * (255 - g2) / 255)
                    bOut = 255 - ((255 - b1) * (255 - b2) / 255)
                }
                "MULTIPLY" -> {
                    rOut = (r1 * r2) / 255
                    gOut = (g1 * g2) / 255
                    bOut = (b1 * b2) / 255
                }
                else -> { // OVERLAY
                    rOut = if (r1 < 128) (2 * r1 * r2) / 255 else 255 - 2 * (255 - r1) * (255 - r2) / 255
                    gOut = if (g1 < 128) (2 * g1 * g2) / 255 else 255 - 2 * (255 - g1) * (255 - g2) / 255
                    bOut = if (b1 < 128) (2 * b1 * b2) / 255 else 255 - 2 * (255 - b1) * (255 - b2) / 255
                }
            }

            outPixels[i] = (a1 shl 24) or (rOut.coerceIn(0, 255) shl 16) or (gOut.coerceIn(0, 255) shl 8) or bOut.coerceIn(0, 255)
        }

        out.setPixels(outPixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * Pro Lens Flare Optical Effects
     */
    suspend fun applyLensFlarePro(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(out)

        val fx = width * 0.75f
        val fy = height * 0.25f

        // Main Flare Core Radial Glow
        val flareCorePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val radShader = RadialGradient(
            fx, fy, width * 0.45f,
            intArrayOf(Color.WHITE, Color.argb(200, 255, 215, 0), Color.argb(120, 0, 210, 255), Color.TRANSPARENT),
            floatArrayOf(0.0f, 0.2f, 0.55f, 1.0f),
            Shader.TileMode.CLAMP
        )
        flareCorePaint.shader = radShader
        canvas.drawCircle(fx, fy, width * 0.45f, flareCorePaint)

        // Anamorphic Blue Streak
        val streakPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00E5FF")
            alpha = 180
            strokeWidth = 6f
        }
        canvas.drawLine(0f, fy, width.toFloat(), fy, streakPaint)

        // Secondary Flare Hexagonal Orbs along ray
        val orbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rayAngle = Math.atan2((height * 0.8f - fy).toDouble(), (width * 0.2f - fx).toDouble())

        for (step in 1..4) {
            val dist = step * (width * 0.18f)
            val ox = fx + dist * cos(rayAngle).toFloat()
            val oy = fy + dist * sin(rayAngle).toFloat()
            val orbRadius = (12f + step * 8f)

            orbPaint.color = when (step) {
                1 -> Color.argb(140, 255, 0, 128)
                2 -> Color.argb(120, 0, 230, 255)
                3 -> Color.argb(100, 255, 215, 0)
                else -> Color.argb(90, 150, 0, 255)
            }
            canvas.drawCircle(ox, oy, orbRadius, orbPaint)
        }

        out
    }

    /**
     * Neon Spiral & Angel Wings FX
     */
    suspend fun applyNeonWingsSpirals(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(out)

        val neonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00F2FE")
            style = Paint.Style.STROKE
            strokeWidth = 7f
            strokeCap = Paint.Cap.ROUND
        }

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4FACFE")
            style = Paint.Style.STROKE
            strokeWidth = 18f
            alpha = 110
            strokeCap = Paint.Cap.ROUND
        }

        val cx = width * 0.5f
        val cy = height * 0.5f

        // Draw glowing 3D spiral around center body
        val path = android.graphics.Path()
        val numPoints = 120
        var first = true

        for (i in 0..numPoints) {
            val angle = i * 0.25f
            val radius = 40f + i * 2.2f
            val x = cx + radius * cos(angle.toDouble()).toFloat()
            val y = cy - (numPoints * 1.5f) + (i * 3.0f) + (15f * sin(angle * 2.0f).toFloat())

            if (first) {
                path.moveTo(x, y)
                first = false
            } else {
                path.lineTo(x, y)
            }
        }

        canvas.drawPath(path, glowPaint)
        canvas.drawPath(path, neonPaint)

        out
    }

    /**
     * Tilt-Shift Miniature DSLR Focus Blur
     */
    suspend fun applyTiltShiftDepth(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val blurred = Bitmap.createScaledBitmap(src, max(1, width / 6), max(1, height / 6), true)
        val smoothBlur = Bitmap.createScaledBitmap(blurred, width, height, true)

        val srcPixels = IntArray(width * height)
        val blurPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)

        src.getPixels(srcPixels, 0, width, 0, 0, width, height)
        smoothBlur.getPixels(blurPixels, 0, width, 0, 0, width, height)

        val bandCenterY = height * 0.5f
        val bandHalfHeight = height * 0.18f

        for (y in 0 until height) {
            val dist = Math.abs(y - bandCenterY)
            val blurAlpha = if (dist <= bandHalfHeight) {
                0.0f
            } else {
                ((dist - bandHalfHeight) / (height * 0.32f)).coerceIn(0.0f, 1.0f)
            }

            for (x in 0 until width) {
                val idx = y * width + x
                val pSrc = srcPixels[idx]
                val pBlur = blurPixels[idx]

                if (blurAlpha == 0f) {
                    outPixels[idx] = pSrc
                } else {
                    val a = (pSrc shr 24) and 0xFF
                    val r = (((pSrc shr 16) and 0xFF) * (1f - blurAlpha) + ((pBlur shr 16) and 0xFF) * blurAlpha).toInt()
                    val g = (((pSrc shr 8) and 0xFF) * (1f - blurAlpha) + ((pBlur shr 8) and 0xFF) * blurAlpha).toInt()
                    val b = ((pSrc and 0xFF) * (1f - blurAlpha) + (pBlur and 0xFF) * blurAlpha).toInt()

                    outPixels[idx] = (a shl 24) or (r shl 16) or (g shl 8) or b
                }
            }
        }

        out.setPixels(outPixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * Motion Blur Trail Action Effect
     */
    suspend fun applyMotionBlurTrail(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val shifts = listOf(0, -12, -24, -36, -48)
        val alphas = listOf(255, 180, 130, 80, 40)

        for (i in shifts.indices) {
            paint.alpha = alphas[i]
            canvas.drawBitmap(src, shifts[i].toFloat(), 0f, paint)
        }

        out
    }

    /**
     * Selective Color Splash (Converts background to B&W while retaining target color e.g. RED)
     */
    suspend fun applyColorSplashBW(src: Bitmap, targetColorName: String = "RED"): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            val r = (p shr 16) and 0xFF
            val g = (p shr 8) and 0xFF
            val b = p and 0xFF

            val isTarget = when (targetColorName.uppercase()) {
                "BLUE" -> (b > 100 && b > r * 1.25f && b > g * 1.25f)
                "GREEN" -> (g > 100 && g > r * 1.25f && g > b * 1.25f)
                "YELLOW" -> (r > 120 && g > 120 && b < 100 && Math.abs(r - g) < 40)
                else -> (r > 100 && r > g * 1.4f && r > b * 1.4f) // Default RED
            }

            if (isTarget) {
                // Keep original vibrant color
                pixels[i] = p
            } else {
                // Convert to dramatic B&W
                val gray = (r * 0.299f + g * 0.587f + b * 0.114f).toInt().coerceIn(0, 255)
                pixels[i] = (a shl 24) or (gray shl 16) or (gray shl 8) or gray
            }
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * AI Object Eraser / Healing Spot Removal
     */
    suspend fun applyMagicEraserHeal(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(out)

        // Inpaint center 10% patch by cloning average surrounding ring pixels
        val cx = (width * 0.5f).toInt()
        val cy = (height * 0.5f).toInt()
        val radius = (min(width, height) * 0.08f).toInt()

        var sumR = 0L
        var sumG = 0L
        var sumB = 0L
        var count = 0

        for (angle in 0 until 360 step 10) {
            val rx = (cx + (radius + 12) * cos(Math.toRadians(angle.toDouble()))).toInt().coerceIn(0, width - 1)
            val ry = (cy + (radius + 12) * sin(Math.toRadians(angle.toDouble()))).toInt().coerceIn(0, height - 1)
            val p = src.getPixel(rx, ry)
            sumR += (p shr 16) and 0xFF
            sumG += (p shr 8) and 0xFF
            sumB += p and 0xFF
            count++
        }

        val avgR = (sumR / max(1, count)).toInt()
        val avgG = (sumG / max(1, count)).toInt()
        val avgB = (sumB / max(1, count)).toInt()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(avgR, avgG, avgB)
        }
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), paint)

        out
    }

    /**
     * Shape Masking (Circle, Heart, Star, Hexagon Frames)
     */
    suspend fun applyShapeMask(src: Bitmap, shapeType: String): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)

        // Fill background with elegant dark charcoal or glass texture
        canvas.drawColor(Color.parseColor("#121216"))

        val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00F2FE")
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }

        val cx = width / 2f
        val cy = height / 2f
        val radius = min(width, height) * 0.42f

        val path = android.graphics.Path()

        when (shapeType.uppercase()) {
            "HEART" -> {
                path.moveTo(cx, cy + radius * 0.7f)
                path.cubicTo(cx - radius * 1.2f, cy - radius * 0.2f, cx - radius * 0.6f, cy - radius * 1.1f, cx, cy - radius * 0.4f)
                path.cubicTo(cx + radius * 0.6f, cy - radius * 1.1f, cx + radius * 1.2f, cy - radius * 0.2f, cx, cy + radius * 0.7f)
            }
            "STAR" -> {
                val numPoints = 5
                val outerR = radius
                val innerR = radius * 0.45f
                for (i in 0 until numPoints * 2) {
                    val r = if (i % 2 == 0) outerR else innerR
                    val angle = i * Math.PI / numPoints - Math.PI / 2
                    val x = (cx + r * cos(angle)).toFloat()
                    val y = (cy + r * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
            }
            "HEXAGON" -> {
                for (i in 0 until 6) {
                    val angle = i * Math.PI / 3
                    val x = (cx + radius * cos(angle)).toFloat()
                    val y = (cy + radius * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
            }
            else -> { // CIRCLE
                path.addCircle(cx, cy, radius, android.graphics.Path.Direction.CW)
            }
        }

        canvas.save()
        canvas.clipPath(path)
        canvas.drawBitmap(src, 0f, 0f, maskPaint)
        canvas.restore()

        canvas.drawPath(path, borderPaint)
        out
    }

    // ==========================================
    // 8. ULTIMATE PRO PICSART & LIGHTROOM TOOLS
    // ==========================================

    /**
     * PicsArt Drip Art Effect (Liquid Paint Drips dripping down photo subject)
     */
    suspend fun applyDripArtEffect(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)

        // Draw solid vibrant canvas background
        canvas.drawColor(Color.parseColor("#0F0C1B"))

        // Draw source photo
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(src, 0f, 0f, paint)

        // Paint drip paths along bottom half of canvas
        val dripPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0F0C1B")
            style = Paint.Style.FILL
        }

        val dripLineY = height * 0.65f
        val dripPath = android.graphics.Path()
        dripPath.moveTo(0f, height.toFloat())
        dripPath.lineTo(0f, dripLineY)

        val dripWidth = width / 12f
        for (i in 0..12) {
            val startX = i * dripWidth
            val midX = startX + dripWidth / 2f
            val endX = startX + dripWidth

            val dripLength = if (i % 2 == 0) height * 0.22f else height * 0.12f
            val bottomY = dripLineY + dripLength

            dripPath.cubicTo(
                startX + dripWidth * 0.2f, bottomY,
                midX + dripWidth * 0.3f, bottomY,
                endX, dripLineY
            )
        }
        dripPath.lineTo(width.toFloat(), height.toFloat())
        dripPath.close()

        canvas.drawPath(dripPath, dripPaint)

        // Draw paint splatter circles
        val splatterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00F2FE")
            alpha = 220
        }
        val random = java.util.Random(42)
        for (i in 0 until 18) {
            val sx = random.nextFloat() * width
            val sy = dripLineY + random.nextFloat() * (height - dripLineY)
            val sr = 4f + random.nextFloat() * 12f
            canvas.drawCircle(sx, sy, sr, splatterPaint)
        }

        out
    }

    /**
     * PicsArt Sticker Cutout Stroke (White/Neon Stroke around photo)
     */
    suspend fun applyStickerCutoutOutline(src: Bitmap, strokeColorHex: String = "#00F2FE"): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)

        // Background shadow / dark backdrop
        canvas.drawColor(Color.parseColor("#141419"))

        // Create stroke offset layer
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            color = Color.parseColor(strokeColorHex)
            style = Paint.Style.STROKE
            strokeWidth = 14f
        }

        val centerRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            color = Color.parseColor(strokeColorHex)
            alpha = 180
        }

        // Draw multiple offset outlines for thick sticker border effect
        val offsets = listOf(-8f, 8f)
        for (dx in offsets) {
            for (dy in offsets) {
                canvas.drawBitmap(src, dx, dy, shadowPaint)
            }
        }

        // Draw original subject on top
        canvas.drawBitmap(src, 0f, 0f, null)

        // Outer border frame
        canvas.drawRoundRect(centerRect, 24f, 24f, strokePaint)

        out
    }

    /**
     * Prism Kaleidoscope Mirror Symmetry
     */
    suspend fun applyPrismMirror(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)

        // Crop top-left quadrant
        val quad = Bitmap.createBitmap(src, 0, 0, width / 2, height / 2)

        // Quadrant 1: Normal Top-Left
        canvas.drawBitmap(quad, 0f, 0f, null)

        // Quadrant 2: Horizontal Mirror Top-Right
        val matrixH = Matrix().apply {
            setScale(-1f, 1f)
            postTranslate(width.toFloat(), 0f)
        }
        canvas.drawBitmap(quad, matrixH, null)

        // Quadrant 3: Vertical Mirror Bottom-Left
        val matrixV = Matrix().apply {
            setScale(1f, -1f)
            postTranslate(0f, height.toFloat())
        }
        canvas.drawBitmap(quad, matrixV, null)

        // Quadrant 4: Both Mirror Bottom-Right
        val matrixHV = Matrix().apply {
            setScale(-1f, -1f)
            postTranslate(width.toFloat(), height.toFloat())
        }
        canvas.drawBitmap(quad, matrixHV, null)

        out
    }

    /**
     * Halftone Retro Comic Pop Art Dots Filter
     */
    suspend fun applyHalftonePopArt(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)

        canvas.drawColor(Color.parseColor("#FFF9E6")) // Retro paper background

        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
        }

        val step = 14
        val srcPixels = IntArray(width * height)
        src.getPixels(srcPixels, 0, width, 0, 0, width, height)

        for (y in 0 until height step step) {
            for (x in 0 until width step step) {
                val idx = y * width + x
                val p = srcPixels[idx]
                val gray = getGrayscale(p)
                val radius = ((255 - gray) / 255f) * (step / 1.6f)

                if (radius > 1f) {
                    canvas.drawCircle(x.toFloat(), y.toFloat(), radius, dotPaint)
                }
            }
        }

        out
    }

    /**
     * Pro Micro-Contrast Detail Sharpener (Lightroom Clarity)
     */
    suspend fun applyDetailSharpenerClarity(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val sharpened = applyConvolutionSharpen(src, sharpness = 2.2f)
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        sharpened.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            var r = (p shr 16) and 0xFF
            var g = (p shr 8) and 0xFF
            var b = p and 0xFF

            r = applySCurve(r, 1.35f, 1.0f)
            g = applySCurve(g, 1.35f, 1.0f)
            b = applySCurve(b, 1.35f, 1.0f)

            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * Cinematic Pro Dark Vignette
     */
    suspend fun applyVignetteCinematic(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(out)

        val cx = width / 2f
        val cy = height / 2f
        val radius = max(width, height) * 0.72f

        val vigShader = RadialGradient(
            cx, cy, radius,
            intArrayOf(Color.TRANSPARENT, Color.argb(120, 0, 0, 0), Color.argb(235, 0, 0, 0)),
            floatArrayOf(0.0f, 0.6f, 1.0f),
            Shader.TileMode.CLAMP
        )

        val vigPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = vigShader
        }

        canvas.drawCircle(cx, cy, radius, vigPaint)
        out
    }

    /**
     * PicsArt Master Oil Painting / Canvas Art Effect
     */
    suspend fun applyOilPaintingArt(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        val radius = 3
        val intensityLevels = 20
        val pixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (y in radius until height - radius) {
            for (x in radius until width - radius) {
                val intensityCount = IntArray(intensityLevels)
                val averageR = IntArray(intensityLevels)
                val averageG = IntArray(intensityLevels)
                val averageB = IntArray(intensityLevels)

                for (ny in -radius..radius) {
                    for (nx in -radius..radius) {
                        val p = pixels[(y + ny) * width + (x + nx)]
                        val r = (p shr 16) and 0xFF
                        val g = (p shr 8) and 0xFF
                        val b = p and 0xFF
                        val curIntensity = (((r + g + b) / 3.0f) * intensityLevels / 255.0f).toInt().coerceIn(0, intensityLevels - 1)

                        intensityCount[curIntensity]++
                        averageR[curIntensity] += r
                        averageG[curIntensity] += g
                        averageB[curIntensity] += b
                    }
                }

                var maxIndex = 0
                var maxCount = 0
                for (i in 0 until intensityLevels) {
                    if (intensityCount[i] > maxCount) {
                        maxCount = intensityCount[i]
                        maxIndex = i
                    }
                }

                val curCount = max(1, intensityCount[maxIndex])
                val finalR = (averageR[maxIndex] / curCount).coerceIn(0, 255)
                val finalG = (averageG[maxIndex] / curCount).coerceIn(0, 255)
                val finalB = (averageB[maxIndex] / curCount).coerceIn(0, 255)

                outPixels[y * width + x] = (0xFF shl 24) or (finalR shl 16) or (finalG shl 8) or finalB
            }
        }

        out.setPixels(outPixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * Pro Duotone Gradient Mapping (e.g. Cyberpunk Magenta + Cyan)
     */
    suspend fun applyDuotoneGradientMap(src: Bitmap, darkHex: String = "#090979", lightHex: String = "#00D4FF"): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        val cDark = Color.parseColor(darkHex)
        val cLight = Color.parseColor(lightHex)

        val r1 = (cDark shr 16) and 0xFF
        val g1 = (cDark shr 8) and 0xFF
        val b1 = cDark and 0xFF

        val r2 = (cLight shr 16) and 0xFF
        val g2 = (cLight shr 8) and 0xFF
        val b2 = cLight and 0xFF

        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            val gray = getGrayscale(p) / 255.0f

            val rOut = (r1 + (r2 - r1) * gray).toInt().coerceIn(0, 255)
            val gOut = (g1 + (g2 - g1) * gray).toInt().coerceIn(0, 255)
            val bOut = (b1 + (b2 - b1) * gray).toInt().coerceIn(0, 255)

            pixels[i] = (a shl 24) or (rOut shl 16) or (gOut shl 8) or bOut
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * 3D Stereo Anaglyph Red/Cyan Glasses Shift
     */
    suspend fun applyAnaglyph3D(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        val shift = 16

        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x
                val p = pixels[idx]
                val a = (p shr 24) and 0xFF

                // Red channel shifted left
                val xRed = (x - shift).coerceIn(0, width - 1)
                val pRed = pixels[y * width + xRed]
                val r = (pRed shr 16) and 0xFF

                // Cyan (Green + Blue) shifted right
                val xCyan = (x + shift).coerceIn(0, width - 1)
                val pCyan = pixels[y * width + xCyan]
                val g = (pCyan shr 8) and 0xFF
                val b = pCyan and 0xFF

                outPixels[idx] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
        }

        out.setPixels(outPixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * Thermal Vision Heat Map Infrared Camera Effect
     */
    suspend fun applyThermalInfrared(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            val gray = getGrayscale(p)

            val r: Int
            val g: Int
            val b: Int

            when {
                gray < 64 -> { // Deep Purple/Blue
                    r = (gray * 2)
                    g = 0
                    b = (128 + gray * 2).coerceAtMost(255)
                }
                gray < 128 -> { // Cyan/Green
                    r = 0
                    g = ((gray - 64) * 4)
                    b = (255 - (gray - 64) * 4)
                }
                gray < 192 -> { // Yellow/Orange
                    r = ((gray - 128) * 4)
                    g = 255
                    b = 0
                }
                else -> { // White / Hot Red
                    r = 255
                    g = (255 - (gray - 192) * 4)
                    b = ((gray - 192) * 4)
                }
            }

            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * Snapseed-style HDR Max Scape Dynamic Range Boost
     */
    suspend fun applyHdrMaxDrama(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            var r = (p shr 16) and 0xFF
            var g = (p shr 8) and 0xFF
            var b = p and 0xFF

            // Apply S-Curve contrast tone mapping + saturation boost
            r = applySCurve(r, 1.45f, 1.25f)
            g = applySCurve(g, 1.45f, 1.25f)
            b = applySCurve(b, 1.45f, 1.25f)

            // Saturation boost
            val lum = (r * 0.299f + g * 0.587f + b * 0.114f)
            r = (lum + (r - lum) * 1.35f).toInt().coerceIn(0, 255)
            g = (lum + (g - lum) * 1.35f).toInt().coerceIn(0, 255)
            b = (lum + (b - lum) * 1.35f).toInt().coerceIn(0, 255)

            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * 35mm Analog Cinema Film Noise Grain Texture
     */
    suspend fun applyFilmCinemaGrain(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        val random = java.util.Random()

        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            var r = (p shr 16) and 0xFF
            var g = (p shr 8) and 0xFF
            var b = p and 0xFF

            val noise = (random.nextGaussian() * 22).toInt()

            r = (r + noise).coerceIn(0, 255)
            g = (g + noise).coerceIn(0, 255)
            b = (b + noise).coerceIn(0, 255)

            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    // ==========================================
    // 5. CUSTOM GALLERY BG & FACE RETOUCHING EFFECTS
    // ==========================================

    /**
     * Replaces background with 100% real user image from phone gallery.
     * Composites isolated subject onto custom scaled background bitmap.
     */
    suspend fun replaceBackgroundWithCustomImage(src: Bitmap, customBg: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height

        val scaledBg = Bitmap.createScaledBitmap(customBg, width, height, true)
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val srcPixels = IntArray(width * height)
        src.getPixels(srcPixels, 0, width, 0, 0, width, height)
        val outPixels = IntArray(width * height)
        scaledBg.getPixels(outPixels, 0, width, 0, 0, width, height)

        val centerX = width / 2f
        val centerY = height / 2f
        val radiusMax = min(width, height) * 0.48f

        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x
                val pSrc = srcPixels[idx]
                val aSrc = (pSrc shr 24) and 0xFF
                val rSrc = (pSrc shr 16) and 0xFF
                val gSrc = (pSrc shr 8) and 0xFF
                val bSrc = pSrc and 0xFF

                val dx = x - centerX
                val dy = y - centerY
                val dist = sqrt(dx * dx + dy * dy)

                val isBgCorner = dist > radiusMax && (rSrc < 40 && gSrc < 40 && bSrc < 40 || rSrc > 220 && gSrc > 220 && bSrc > 220)
                if (!isBgCorner && aSrc > 30) {
                    outPixels[idx] = pSrc
                }
            }
        }

        result.setPixels(outPixels, 0, width, 0, 0, width, height)
        if (scaledBg != customBg && !scaledBg.isRecycled) {
            scaledBg.recycle()
        }
        result
    }

    /**
     * Face Reshape / Jawline Slimming
     */
    suspend fun applyFaceSlimmingReshape(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val srcPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        src.getPixels(srcPixels, 0, width, 0, 0, width, height)
        System.arraycopy(srcPixels, 0, outPixels, 0, srcPixels.size)

        // Pinch lower cheeks / jaw area inward smoothly
        val cx = width / 2f
        val cy = height * 0.58f
        val rx = width * 0.38f
        val ry = height * 0.28f

        for (y in (cy - ry).toInt().coerceAtLeast(0) until (cy + ry).toInt().coerceAtMost(height)) {
            for (x in (cx - rx).toInt().coerceAtLeast(0) until (cx + rx).toInt().coerceAtMost(width)) {
                val dx = (x - cx) / rx
                val dy = (y - cy) / ry
                val dist = dx * dx + dy * dy

                if (dist < 1.0f) {
                    val factor = (1.0f - dist) * 0.12f
                    val srcX = (cx + (x - cx) * (1.0f + factor)).toInt().coerceIn(0, width - 1)
                    val srcY = y
                    outPixels[y * width + x] = srcPixels[srcY * width + srcX]
                }
            }
        }

        out.setPixels(outPixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * Nose Contour & Slimming Refinement
     */
    suspend fun applyNoseSlimming(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val srcPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        src.getPixels(srcPixels, 0, width, 0, 0, width, height)
        System.arraycopy(srcPixels, 0, outPixels, 0, srcPixels.size)

        val cx = width / 2f
        val cy = height * 0.46f
        val rx = width * 0.15f
        val ry = height * 0.18f

        for (y in (cy - ry).toInt().coerceAtLeast(0) until (cy + ry).toInt().coerceAtMost(height)) {
            for (x in (cx - rx).toInt().coerceAtLeast(0) until (cx + rx).toInt().coerceAtMost(width)) {
                val dx = (x - cx) / rx
                val dy = (y - cy) / ry
                val dist = dx * dx + dy * dy

                if (dist < 1.0f) {
                    val pinch = (1.0f - dist) * 0.15f
                    val srcX = (cx + (x - cx) * (1.0f + pinch)).toInt().coerceIn(0, width - 1)
                    var color = srcPixels[y * width + srcX]

                    val a = (color shr 24) and 0xFF
                    var r = (color shr 16) and 0xFF
                    var g = (color shr 8) and 0xFF
                    var b = color and 0xFF

                    // Subtle bridge highlight
                    if (Math.abs(x - cx) < rx * 0.3f) {
                        r = (r * 1.06f + 8f).toInt().coerceIn(0, 255)
                        g = (g * 1.05f + 6f).toInt().coerceIn(0, 255)
                        b = (b * 1.04f + 4f).toInt().coerceIn(0, 255)
                    }

                    outPixels[y * width + x] = (a shl 24) or (r shl 16) or (g shl 8) or b
                }
            }
        }

        out.setPixels(outPixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * Smile & Lips Color Enhancer
     */
    suspend fun applySmileLipsEnhance(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        val cx = width / 2f
        val cy = height * 0.65f
        val rx = width * 0.22f
        val ry = height * 0.12f

        for (y in (cy - ry).toInt().coerceAtLeast(0) until (cy + ry).toInt().coerceAtMost(height)) {
            for (x in (cx - rx).toInt().coerceAtLeast(0) until (cx + rx).toInt().coerceAtMost(width)) {
                val idx = y * width + x
                val p = pixels[idx]

                val a = (p shr 24) and 0xFF
                var r = (p shr 16) and 0xFF
                var g = (p shr 8) and 0xFF
                var b = p and 0xFF

                val dx = (x - cx) / rx
                val dy = (y - cy) / ry
                val dist = dx * dx + dy * dy

                if (dist < 1.0f) {
                    val weight = 1.0f - dist
                    // Boost lip pink/red saturation and warmth
                    r = (r + (35f * weight)).toInt().coerceIn(0, 255)
                    g = (g * (1.0f - 0.05f * weight)).toInt().coerceIn(0, 255)
                    b = (b * (1.0f - 0.08f * weight)).toInt().coerceIn(0, 255)

                    pixels[idx] = (a shl 24) or (r shl 16) or (g shl 8) or b
                }
            }
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * AI Object Remover / Inpaint Brush (Matches Magic Eraser in screenshot)
     */
    suspend fun applyObjectRemoverInpaint(src: Bitmap, eraseMask: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = src.copy(Bitmap.Config.ARGB_8888, true)

        val srcPixels = IntArray(width * height)
        val maskPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)

        src.getPixels(srcPixels, 0, width, 0, 0, width, height)
        eraseMask.getPixels(maskPixels, 0, width, 0, 0, width, height)
        System.arraycopy(srcPixels, 0, outPixels, 0, srcPixels.size)

        // Inpaint erased areas using surrounding background texture blending
        for (y in 2 until height - 2) {
            for (x in 2 until width - 2) {
                val idx = y * width + x
                val maskColor = maskPixels[idx]
                val maskAlpha = (maskColor shr 24) and 0xFF

                // If erased by user brush
                if (maskAlpha > 30) {
                    var sumR = 0
                    var sumG = 0
                    var sumB = 0
                    var validCount = 0

                    // Sample non-erased outer ring neighbors
                    val ring = 10
                    val samplePoints = listOf(
                        Pair(x - ring, y), Pair(x + ring, y),
                        Pair(x, y - ring), Pair(x, y + ring),
                        Pair(x - ring, y - ring), Pair(x + ring, y + ring)
                    )

                    for (pt in samplePoints) {
                        val sx = pt.first.coerceIn(0, width - 1)
                        val sy = pt.second.coerceIn(0, height - 1)
                        val sIdx = sy * width + sx
                        if (((maskPixels[sIdx] shr 24) and 0xFF) <= 30) {
                            val p = srcPixels[sIdx]
                            sumR += (p shr 16) and 0xFF
                            sumG += (p shr 8) and 0xFF
                            sumB += p and 0xFF
                            validCount++
                        }
                    }

                    if (validCount > 0) {
                        val avgR = sumR / validCount
                        val avgG = sumG / validCount
                        val avgB = sumB / validCount
                        val origAlpha = (srcPixels[idx] shr 24) and 0xFF
                        outPixels[idx] = (origAlpha shl 24) or (avgR shl 16) or (avgG shl 8) or avgB
                    }
                }
            }
        }

        out.setPixels(outPixels, 0, width, 0, 0, width, height)
        out
    }

    // ==========================================
    // 6. PRO STUDIO LIGHTING, DSLR BOKEH & ONE-TAP ENHANCE
    // ==========================================

    /**
     * Pro Studio Relighting Effects
     */
    suspend fun applyProStudioLighting(src: Bitmap, preset: String): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        val cx = width / 2f
        val cy = height / 2f

        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x
                val p = pixels[idx]

                val a = (p shr 24) and 0xFF
                var r = (p shr 16) and 0xFF
                var g = (p shr 8) and 0xFF
                var b = p and 0xFF

                when (preset) {
                    "Golden Sun" -> {
                        // Sunlight beam coming from top-right corner
                        val distToCorner = sqrt(((x - width) * (x - width) + y * y).toDouble()).toFloat()
                        val maxD = sqrt((width * width + height * height).toDouble()).toFloat()
                        val rayFactor = (1.0f - (distToCorner / maxD)).coerceIn(0f, 1f)

                        r = (r + 45f * rayFactor).toInt().coerceIn(0, 255)
                        g = (g + 28f * rayFactor).toInt().coerceIn(0, 255)
                        b = (b + 5f * rayFactor).toInt().coerceIn(0, 255)
                    }
                    "Cyber Neon" -> {
                        // Cyan on left, Magenta on right rim
                        val leftFactor = (1.0f - (x.toFloat() / width)).coerceIn(0f, 1f)
                        val rightFactor = (x.toFloat() / width).coerceIn(0f, 1f)

                        // Cyan glow left
                        g = (g + 30f * leftFactor).toInt().coerceIn(0, 255)
                        b = (b + 45f * leftFactor).toInt().coerceIn(0, 255)

                        // Magenta glow right
                        r = (r + 45f * rightFactor).toInt().coerceIn(0, 255)
                        b = (b + 35f * rightFactor).toInt().coerceIn(0, 255)
                    }
                    "Studio Softbox" -> {
                        // Center-focused softbox light gradient
                        val distToCenter = sqrt(((x - cx) * (x - cx) + (y - cy) * (y - cy)).toDouble()).toFloat()
                        val maxRadius = min(width, height) * 0.7f
                        val softFactor = (1.0f - (distToCenter / maxRadius)).coerceIn(0f, 1f)

                        r = (r * (1.0f + 0.22f * softFactor) + 12f * softFactor).toInt().coerceIn(0, 255)
                        g = (g * (1.0f + 0.20f * softFactor) + 10f * softFactor).toInt().coerceIn(0, 255)
                        b = (b * (1.0f + 0.18f * softFactor) + 8f * softFactor).toInt().coerceIn(0, 255)
                    }
                    "Spotlight" -> {
                        // Dark vignette edges with bright central spotlight
                        val distToCenter = sqrt(((x - cx) * (x - cx) + (y - cy) * (y - cy)).toDouble()).toFloat()
                        val spotRadius = min(width, height) * 0.42f

                        if (distToCenter > spotRadius) {
                            val darkFactor = (1.0f - (distToCenter - spotRadius) / (spotRadius * 0.8f)).coerceIn(0.25f, 1.0f)
                            r = (r * darkFactor).toInt().coerceIn(0, 255)
                            g = (g * darkFactor).toInt().coerceIn(0, 255)
                            b = (b * darkFactor).toInt().coerceIn(0, 255)
                        } else {
                            val boost = (1.0f - distToCenter / spotRadius) * 0.25f
                            r = (r * (1.0f + boost) + 15f * boost).toInt().coerceIn(0, 255)
                            g = (g * (1.0f + boost) + 15f * boost).toInt().coerceIn(0, 255)
                            b = (b * (1.0f + boost) + 15f * boost).toInt().coerceIn(0, 255)
                        }
                    }
                }

                pixels[idx] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
        }

        out.setPixels(pixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * Pro DSLR Bokeh Depth Blur
     */
    suspend fun applyProBokehDepthBlur(src: Bitmap, blurRadius: Int = 12): Bitmap = withContext(Dispatchers.Default) {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val srcPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        src.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val cx = width / 2f
        val cy = height / 2f
        val rx = width * 0.38f
        val ry = height * 0.42f

        val step = blurRadius.coerceIn(4, 20)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x
                val dx = (x - cx) / rx
                val dy = (y - cy) / ry
                val dist = dx * dx + dy * dy

                // In focal subject zone -> keep sharp
                if (dist < 0.65f) {
                    outPixels[idx] = srcPixels[idx]
                } else {
                    // Outer background -> apply box blur for DSLR Bokeh effect
                    var sumR = 0
                    var sumG = 0
                    var sumB = 0
                    var count = 0

                    for (ky in -step..step step 4) {
                        for (kx in -step..step step 4) {
                            val sx = (x + kx).coerceIn(0, width - 1)
                            val sy = (y + ky).coerceIn(0, height - 1)
                            val p = srcPixels[sy * width + sx]
                            sumR += (p shr 16) and 0xFF
                            sumG += (p shr 8) and 0xFF
                            sumB += p and 0xFF
                            count++
                        }
                    }

                    val origP = srcPixels[idx]
                    val origA = (origP shr 24) and 0xFF

                    val blurR = sumR / count
                    val blurG = sumG / count
                    val blurB = sumB / count

                    // Smooth transition from subject edge to blur background
                    val blurWeight = ((dist - 0.65f) / 0.35f).coerceIn(0f, 1f)

                    val origR = (origP shr 16) and 0xFF
                    val origG = (origP shr 8) and 0xFF
                    val origB = origP and 0xFF

                    val finalR = (origR * (1f - blurWeight) + blurR * blurWeight).toInt().coerceIn(0, 255)
                    val finalG = (origG * (1f - blurWeight) + blurG * blurWeight).toInt().coerceIn(0, 255)
                    val finalB = (origB * (1f - blurWeight) + blurB * blurWeight).toInt().coerceIn(0, 255)

                    outPixels[idx] = (origA shl 24) or (finalR shl 16) or (finalG shl 8) or finalB
                }
            }
        }

        out.setPixels(outPixels, 0, width, 0, 0, width, height)
        out
    }

    /**
     * Pro One-Tap Auto Enhance AI
     */
    suspend fun applyProOneTapAutoEnhance(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val sharpened = applyConvolutionSharpen(src, sharpness = 0.85f)
        val faceGlowing = portraitFaceGlow(sharpened)
        val hdResult = ultraHdrMax(faceGlowing)
        hdResult
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private fun getGrayscale(color: Int): Int {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        return (r * 0.299f + g * 0.587f + b * 0.114f).toInt()
    }

    private fun applyConvolutionSharpen(src: Bitmap, sharpness: Float): Bitmap {
        val width = src.width
        val height = src.height
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val inPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)

        src.getPixels(inPixels, 0, width, 0, 0, width, height)

        val centerWeight = 1.0f + (4f * sharpness)
        val edgeWeight = -sharpness

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val idx = y * width + x

                val pC = inPixels[idx]
                val pT = inPixels[(y - 1) * width + x]
                val pB = inPixels[(y + 1) * width + x]
                val pL = inPixels[y * width + (x - 1)]
                val pR = inPixels[y * width + (x + 1)]

                val a = (pC shr 24) and 0xFF

                val r = (((pC shr 16) and 0xFF) * centerWeight +
                        (((pT shr 16) and 0xFF) + ((pB shr 16) and 0xFF) +
                                ((pL shr 16) and 0xFF) + ((pR shr 16) and 0xFF)) * edgeWeight)
                    .toInt().coerceIn(0, 255)

                val g = (((pC shr 8) and 0xFF) * centerWeight +
                        (((pT shr 8) and 0xFF) + ((pB shr 8) and 0xFF) +
                                ((pL shr 8) and 0xFF) + ((pR shr 8) and 0xFF)) * edgeWeight)
                    .toInt().coerceIn(0, 255)

                val b = ((pC and 0xFF) * centerWeight +
                        ((pT and 0xFF) + (pB and 0xFF) + (pL and 0xFF) + (pR and 0xFF)) * edgeWeight)
                    .toInt().coerceIn(0, 255)

                outPixels[idx] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
        }

        out.setPixels(outPixels, 0, width, 0, 0, width, height)
        return out
    }

    private fun applySCurve(c: Int, contrastFactor: Float, gamma: Float): Int {
        val norm = c / 255.0f
        val curve = if (norm < 0.5f) {
            0.5f * Math.pow((norm * 2f).toDouble(), contrastFactor.toDouble()).toFloat()
        } else {
            1.0f - 0.5f * Math.pow(((1.0f - norm) * 2f).toDouble(), contrastFactor.toDouble()).toFloat()
        }
        val corrected = Math.pow(curve.toDouble(), (1.0f / gamma).toDouble()).toFloat()
        return (corrected * 255f).toInt().coerceIn(0, 255)
    }

    private fun hdrToneMap(c: Int): Int {
        val norm = c / 255.0f
        val lifted = if (norm < 0.4f) {
            norm + (0.4f - norm) * 0.35f
        } else if (norm > 0.8f) {
            norm - (norm - 0.8f) * 0.15f
        } else {
            norm
        }
        return (lifted * 255f).toInt().coerceIn(0, 255)
    }
}
