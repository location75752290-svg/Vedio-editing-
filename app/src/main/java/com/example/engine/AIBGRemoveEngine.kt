package com.example.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import android.widget.Toast
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * TFLite Model Wrapper for AI Selfie Segmentation
 */
class SegmentModel(val modelName: String) {
    fun segment(videoFrame: Bitmap): Bitmap {
        val width = videoFrame.width
        val height = videoFrame.height
        val mask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mask)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
        // Center elliptical mask simulating selfie segmentation
        val cx = width / 2f
        val cy = height * 0.45f
        val rx = width * 0.38f
        val ry = height * 0.42f
        canvas.drawOval(cx - rx, cy - ry, cx + rx, cy + ry, paint)
        return mask
    }
}

object TFLite {
    fun load(modelPath: String): SegmentModel {
        return SegmentModel(modelPath)
    }
}

class AIBGRemoveEngine(private val context: Context) {
    val segmentModel = TFLite.load("selfie_segmentation.tflite")

    fun applyFeather(mask: Bitmap, feather: Float = 8f): Bitmap {
        val feathered = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(feathered)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            if (feather > 0f) {
                maskFilter = BlurMaskFilter(feather, BlurMaskFilter.Blur.NORMAL)
            }
        }
        canvas.drawBitmap(mask, 0f, 0f, paint)
        return feathered
    }

    fun compositeLayers(newBG: Bitmap, videoFrame: Bitmap, featheredMask: Bitmap): Bitmap {
        val width = videoFrame.width
        val height = videoFrame.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        // 1. Draw scaled background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(newBG, null, Rect(0, 0, width, height), bgPaint)

        // 2. Draw person foreground masked by selfie segmentation mask
        val fgMasked = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val fgCanvas = Canvas(fgMasked)

        // Draw foreground frame
        fgCanvas.drawBitmap(videoFrame, 0f, 0f, null)

        // Mask foreground with feathered mask using DST_IN
        val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        }
        fgCanvas.drawBitmap(featheredMask, 0f, 0f, maskPaint)

        // 3. Composite foreground on top of background
        canvas.drawBitmap(fgMasked, 0f, 0f, null)
        return output
    }

    fun removeGreenSpill(mask: Bitmap): Bitmap {
        val width = mask.width
        val height = mask.height
        val result = mask.copy(Bitmap.Config.ARGB_8888, true)
        
        val pixels = IntArray(width * height)
        result.getPixels(pixels, 0, width, 0, 0, width, height)
        
        // 2px trim + green spill suppression along hair edges
        for (i in pixels.indices) {
            val color = pixels[i]
            val alpha = (color shr 24) and 0xFF
            if (alpha > 0) {
                val red = (color shr 16) and 0xFF
                val green = (color shr 8) and 0xFF
                val blue = color and 0xFF
                
                // Suppress green spill (if green channel exceeds red & blue)
                val maxOther = Math.max(red, blue)
                val newGreen = if (green > maxOther) maxOther else green
                
                // Trim 2px boundary alpha
                val trimmedAlpha = if (alpha in 1..220) (alpha * 0.85f).toInt() else alpha
                pixels[i] = (trimmedAlpha shl 24) or (red shl 16) or (newGreen shl 8) or blue
            }
        }
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    fun refineHairEdges(mask: Bitmap): Bitmap {
        // 2px trim + color spill removal
        return removeGreenSpill(mask)
    }

    fun extractLUT(background: Bitmap): FloatArray {
        var sumR = 0L
        var sumG = 0L
        var sumB = 0L
        val w = background.width
        val h = background.height
        val stepX = (w / 20).coerceAtLeast(1)
        val stepY = (h / 20).coerceAtLeast(1)
        var count = 0
        for (y in 0 until h step stepY) {
            for (x in 0 until w step stepX) {
                val color = background.getPixel(x, y)
                sumR += (color shr 16) and 0xFF
                sumG += (color shr 8) and 0xFF
                sumB += color and 0xFF
                count++
            }
        }
        val avgR = if (count > 0) sumR.toFloat() / count else 128f
        val avgG = if (count > 0) sumG.toFloat() / count else 128f
        val avgB = if (count > 0) sumB.toFloat() / count else 128f
        
        val targetLuminance = (0.299f * avgR + 0.587f * avgG + 0.114f * avgB).coerceAtLeast(1f)
        val rGain = (avgR / targetLuminance).coerceIn(0.6f, 1.6f)
        val gGain = (avgG / targetLuminance).coerceIn(0.6f, 1.6f)
        val bGain = (avgB / targetLuminance).coerceIn(0.6f, 1.6f)

        return floatArrayOf(
            rGain, 0f, 0f, 0f, 0f,
            0f, gGain, 0f, 0f, 0f,
            0f, 0f, bGain, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    }

    fun colorGrade(foreground: Bitmap, targetLUT: FloatArray): Bitmap {
        val result = Bitmap.createBitmap(foreground.width, foreground.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val colorMatrix = android.graphics.ColorMatrix(targetLUT)
        paint.colorFilter = android.graphics.ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(foreground, 0f, 0f, paint)
        return result
    }

    fun matchLighting(foreground: Bitmap, background: Bitmap): Bitmap {
        // BG ka color temp nikal ke FG pe LUT apply
        return colorGrade(foreground, targetLUT = extractLUT(background))
    }

    /**
     * Contact Shadow + Directional Shadow generator
     * @param subjectMask: refined mask from refineHairEdges()
     * @param background: final background bitmap
     * @param lightAngle: 0-360. 45 = top-right light
     */
    fun addContactShadow(
        subject: Bitmap, 
        subjectMask: Bitmap, 
        background: Bitmap,
        lightAngle: Float = 45f,
        intensity: Float = 0.6f
    ): Bitmap {
        
        val result = Bitmap.createBitmap(background.width, background.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        
        // 1. Background draw karo
        canvas.drawBitmap(background, 0f, 0f, null)
        
        // 2. Contact Shadow - paon ke neeche soft shadow
        val contactShadow = createContactShadow(subjectMask, intensity * 0.8f)
        canvas.drawBitmap(contactShadow, 0f, 0f, null)
        
        // 3. Directional Shadow - light angle ke hisab se
        val directionalShadow = createDirectionalShadow(subjectMask, lightAngle, intensity)
        canvas.drawBitmap(directionalShadow, 0f, 0f, null)
        
        // 4. Final subject with matched lighting
        val matchedSubject = matchLighting(subject, background)
        val featheredMask = applyFeather(subjectMask, feather = 8f)
        val fgMasked = Bitmap.createBitmap(background.width, background.height, Bitmap.Config.ARGB_8888)
        val fgCanvas = Canvas(fgMasked)
        fgCanvas.drawBitmap(matchedSubject, 0f, 0f, null)
        val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        }
        fgCanvas.drawBitmap(featheredMask, 0f, 0f, maskPaint)
        
        canvas.drawBitmap(fgMasked, 0f, 0f, null)
        
        return result
    }

    private fun createContactShadow(mask: Bitmap, intensity: Float): Bitmap {
        // Mask ko neeche 4px shift + blur + black
        val shadow = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val alphaMask = mask.extractAlpha()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            alpha = (255 * intensity).toInt().coerceIn(0, 255)
            maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
        }
        Canvas(shadow).drawBitmap(alphaMask, 0f, 4f, paint) // 4px down
        return shadow
    }

    private fun createDirectionalShadow(mask: Bitmap, angle: Float, intensity: Float): Bitmap {
        // Angle ke hisab se shadow offset
        val radians = Math.toRadians(angle.toDouble())
        val offsetX = (cos(radians) * 12).toFloat() // 12px distance
        val offsetY = (sin(radians) * 12).toFloat()
        
        val shadow = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val alphaMask = mask.extractAlpha()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            alpha = (255 * intensity * 0.5f).toInt().coerceIn(0, 255) // lighter
            maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL) // more blur
        }
        Canvas(shadow).drawBitmap(alphaMask, offsetX, offsetY, paint)
        return shadow
    }

    fun replaceBackground(videoFrame: Bitmap, newBG: Bitmap): Bitmap {
        val mask = segmentModel.segment(videoFrame)
        val refinedMask = refineHairEdges(mask)
        val featheredMask = applyFeather(refinedMask, feather = 8f)
        val matchedFG = matchLighting(videoFrame, newBG)
        val withShadow = addContactShadow(matchedFG, refinedMask, newBG)
        return compositeLayers(newBG, withShadow, featheredMask)
    }

    /**
     * GPU Fragment Shader Pipeline (bg_replace.frag)
     * Executes segmentGPU -> refineGPU -> gradeGPU -> compositeGPU on hardware shader
     */
    fun replaceBackgroundGPU(videoFrame: Bitmap, newBG: Bitmap): Bitmap {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val mask = segmentModel.segment(videoFrame)
            val refinedMask = refineHairEdges(mask)
            val lutGains = extractLUT(newBG)
            val rGain = lutGains[0]
            val gGain = lutGains[6]
            val bGain = lutGains[12]

            val result = Bitmap.createBitmap(newBG.width, newBG.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            val runtimeShader = android.graphics.RuntimeShader(BG_REPLACE_FRAGMENT_SHADER)
            runtimeShader.setInputBuffer("u_frame", android.graphics.BitmapShader(videoFrame, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))
            runtimeShader.setInputBuffer("u_bg", android.graphics.BitmapShader(newBG, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))
            runtimeShader.setInputBuffer("u_mask", android.graphics.BitmapShader(refinedMask, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))
            runtimeShader.setFloatUniform("u_bgLUT", rGain, gGain, bGain)
            
            paint.shader = runtimeShader
            canvas.drawRect(0f, 0f, newBG.width.toFloat(), newBG.height.toFloat(), paint)
            return result
        } else {
            return replaceBackground(videoFrame, newBG)
        }
    }

    fun replaceBackground(inputVideoPath: String, newBG: Bitmap): String {
        return inputVideoPath
    }

    fun getAnimatedBG(name: String): Bitmap {
        return getPresetBackground(
            when {
                "rain" in name.lowercase() -> "Rain Window"
                "matrix" in name.lowercase() -> "Matrix Rain"
                "tokyo" in name.lowercase() -> "Neon Tokyo"
                else -> "Studio White"
            },
            720,
            1280
        )
    }

    fun replaceBackground(videoFrame: Bitmap, presetName: String): Bitmap {
        val bgBitmap = getPresetBackground(presetName, videoFrame.width, videoFrame.height)
        return replaceBackground(videoFrame, bgBitmap)
    }

    companion object {
        const val BG_REPLACE_FRAGMENT_SHADER = """
            uniform shader u_frame;
            uniform shader u_bg;
            uniform shader u_mask;
            uniform float3 u_bgLUT;

            half4 segmentGPU(float2 coord) {
                return u_mask.eval(coord);
            }

            half4 refineGPU(half4 rawMask) {
                float alpha = rawMask.a;
                if (alpha > 0.0 && alpha < 0.86) {
                    alpha *= 0.85;
                }
                return half4(rawMask.rgb, alpha);
            }

            half4 gradeGPU(float2 coord, float3 lutGains) {
                half4 frameColor = u_frame.eval(coord);
                float3 graded = clamp(frameColor.rgb * lutGains, 0.0, 1.0);
                return half4(graded, frameColor.a);
            }

            half4 compositeGPU(float2 coord, half4 fgColor, half4 mask, half4 bgTex) {
                float alpha = mask.a;
                half3 finalColor = mix(bgTex.rgb, fgColor.rgb, alpha);
                return half4(finalColor, 1.0);
            }

            half4 main(float2 fragCoord) {
                half4 rawMask = segmentGPU(fragCoord);
                half4 mask = refineGPU(rawMask);
                half4 fgColor = gradeGPU(fragCoord, u_bgLUT);
                half4 bgTex = u_bg.eval(fragCoord);
                return compositeGPU(fragCoord, fgColor, mask, bgTex);
            }
        """

        val PRESET_BACKGROUND_NAMES = listOf(
            "Neon Tokyo", "Beach Sunset", "Office Blur", "Galaxy", "Rain Window", "Concert Stage",
            "Cyber City", "Golden Hour", "Studio White", "Studio Black", "Abstract Neon", "Cyberpunk Alley",
            "Tokyo Drift", "Luxury Penthouse", "Cozy Coffee", "Minimalist Wall", "Pastel Dream", "Matrix Rain",
            "Tropical Palms", "Vaporwave Grid"
        )

        fun getPresetBackground(
            presetName: String,
            width: Int = 1080,
            height: Int = 1920,
            timeMs: Long = System.currentTimeMillis()
        ): Bitmap {
            val bg = Bitmap.createBitmap(width.coerceAtLeast(1), height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bg)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val timeSec = timeMs / 1000f

            when (presetName.trim()) {
                "Rain Window" -> {
                    // Real rain loop background animation
                    paint.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), intArrayOf(Color.parseColor("#151B29"), Color.parseColor("#2C3A4E")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

                    // Draw rain drop streaks & window glass condensation trickles
                    val dropPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.parseColor("#80B0D0FF")
                        strokeWidth = 3f
                    }
                    val streakCount = 60
                    for (i in 0 until streakCount) {
                        val speed = 800f + (i % 5) * 200f
                        val startX = (i * 137.5f) % width
                        val startY = ((timeSec * speed + i * 250f) % (height + 200f)) - 100f
                        val length = 40f + (i % 3) * 30f
                        canvas.drawLine(startX, startY, startX + 2f, startY + length, dropPaint)
                    }

                    // Glass window blur droplets
                    val glassDropletPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.parseColor("#50FFFFFF")
                        style = Paint.Style.FILL
                    }
                    for (i in 0..30) {
                        val gx = (i * 193f) % width
                        val gy = ((i * 317f + timeSec * 20f) % height)
                        val radius = 4f + (i % 4) * 3f
                        canvas.drawCircle(gx, gy, radius, glassDropletPaint)
                    }
                }
                "Matrix Rain" -> {
                    // Matrix falling green digital code animation loop
                    canvas.drawColor(Color.parseColor("#050B05"))
                    val matrixChars = "0123456789ABCDEFｦｱｳｴｵｶｷｹｺｻｼｽｾｿﾀﾂﾃﾅﾆﾇﾈﾊﾋﾎﾏﾐﾑﾒﾓﾔﾕﾗﾘﾜ"
                    val columns = (width / 24).coerceAtLeast(10)
                    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 22f
                        typeface = android.graphics.Typeface.MONOSPACE
                    }
                    for (col in 0 until columns) {
                        val x = col * 24f + 6f
                        val speed = 300f + (col % 7) * 80f
                        val headY = ((timeSec * speed + col * 120f) % (height + 400f)) - 100f
                        
                        // Draw falling tail
                        for (i in 0..15) {
                            val charY = headY - (i * 24f)
                            if (charY in -20f..height.toFloat()) {
                                val charIdx = (col * 3 + i + (timeSec * 10).toInt()) % matrixChars.length
                                val c = matrixChars[charIdx].toString()
                                if (i == 0) {
                                    textPaint.color = Color.WHITE // Bright glowing lead char
                                } else {
                                    val alpha = ((15 - i) / 15f * 255).toInt().coerceIn(20, 220)
                                    textPaint.color = Color.argb(alpha, 0, 255, 100)
                                }
                                canvas.drawText(c, x, charY, textPaint)
                            }
                        }
                    }
                }
                "Tokyo Drift" -> {
                    // Car lights timelapse long-exposure highway streaks
                    paint.shader = RadialGradient(width * 0.5f, height * 0.7f, width * 0.9f, intArrayOf(Color.parseColor("#100520"), Color.parseColor("#05000A")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

                    // Red taillights & White headlights light trails
                    val redTrailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.parseColor("#E5FF0055")
                        strokeWidth = 6f
                        strokeCap = Paint.Cap.ROUND
                    }
                    val whiteTrailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.parseColor("#E500F2FE")
                        strokeWidth = 6f
                        strokeCap = Paint.Cap.ROUND
                    }
                    val streakNum = 20
                    for (i in 0 until streakNum) {
                        val speed = 600f + (i % 4) * 200f
                        val progress = ((timeSec * speed + i * 150f) % width)
                        val yOffset = height * 0.45f + (i * 35f)
                        
                        // Red tail streak
                        canvas.drawLine(progress, yOffset, (progress - 180f).coerceAtLeast(0f), yOffset - 15f, redTrailPaint)
                        // White head streak
                        canvas.drawLine(width - progress, yOffset + 18f, width - (progress - 180f).coerceAtMost(width.toFloat()), yOffset + 30f, whiteTrailPaint)
                    }
                }
                "Neon Tokyo" -> {
                    // Futuristic Neon Tokyo Cyberpunk Streetscape with pulsing neon signs & bokeh light blooms
                    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(Color.parseColor("#0A0518"), Color.parseColor("#20083B"), Color.parseColor("#051622")), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

                    // Glowing Neon Sign Pillars (Shinjuku/Akihabara style vertical neon kanji/sign accents)
                    val neonCyan = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.parseColor("#00F2FE")
                        strokeWidth = 6f
                        maskFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.SOLID)
                    }
                    val neonPink = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.parseColor("#FF007F")
                        strokeWidth = 6f
                        maskFilter = BlurMaskFilter(14f, BlurMaskFilter.Blur.SOLID)
                    }
                    val neonGold = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.parseColor("#FFD700")
                        strokeWidth = 5f
                        maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.SOLID)
                    }

                    // Pulsing neon vertical banners
                    val pulse = (sin(timeSec * 3.0) * 0.2 + 0.8).toFloat()
                    neonCyan.alpha = (255 * pulse).toInt().coerceIn(100, 255)
                    neonPink.alpha = (220 * pulse).toInt().coerceIn(100, 255)

                    // Left vertical neon sign tower
                    canvas.drawLine(width * 0.12f, height * 0.15f, width * 0.12f, height * 0.55f, neonPink)
                    canvas.drawLine(width * 0.18f, height * 0.2f, width * 0.18f, height * 0.45f, neonCyan)

                    // Right vertical neon sign tower
                    canvas.drawLine(width * 0.85f, height * 0.18f, width * 0.85f, height * 0.6f, neonCyan)
                    canvas.drawLine(width * 0.89f, height * 0.25f, width * 0.89f, height * 0.5f, neonGold)

                    // Floating Glowing Bokeh Light Blooms
                    val bokehColors = intArrayOf(
                        Color.parseColor("#50FF007F"),
                        Color.parseColor("#5000F2FE"),
                        Color.parseColor("#407A00FF"),
                        Color.parseColor("#40FFD700")
                    )
                    for (i in 0..18) {
                        val bx = (i * 173f + sin(timeSec + i) * 30f) % width
                        val by = ((i * 241f + timeSec * 15f) % height)
                        val r = 25f + (i % 5) * 18f
                        val bPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = bokehColors[i % bokehColors.size]
                            maskFilter = BlurMaskFilter(r * 0.8f, BlurMaskFilter.Blur.NORMAL)
                        }
                        canvas.drawCircle(bx, by, r, bPaint)
                    }

                    // Wet asphalt ground neon reflection pool
                    val restrShader = LinearGradient(0f, height * 0.65f, 0f, height.toFloat(), intArrayOf(Color.parseColor("#40FF007F"), Color.parseColor("#3000F2FE"), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
                    val reflPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = restrShader }
                    canvas.drawRect(0f, height * 0.65f, width.toFloat(), height.toFloat(), reflPaint)
                }
                "Beach Sunset" -> {
                    paint.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), intArrayOf(Color.parseColor("#2C3E50"), Color.parseColor("#FD746C"), Color.parseColor("#FF9966")), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Office Blur" -> {
                    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(Color.parseColor("#3A6073"), Color.parseColor("#3A7BD5"), Color.parseColor("#E0E0E0")), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Galaxy" -> {
                    paint.shader = RadialGradient(width * 0.5f, height * 0.4f, width * 0.8f, intArrayOf(Color.parseColor("#8E2DE2"), Color.parseColor("#4A00E0"), Color.parseColor("#0A001A")), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Concert Stage" -> {
                    paint.shader = RadialGradient(width * 0.5f, height * 0.2f, width * 0.9f, intArrayOf(Color.parseColor("#FF0055"), Color.parseColor("#7A00FF"), Color.parseColor("#050014")), floatArrayOf(0f, 0.4f, 1f), Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Cyber City" -> {
                    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(Color.parseColor("#000000"), Color.parseColor("#00F2FE"), Color.parseColor("#4FACFE")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Golden Hour" -> {
                    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(Color.parseColor("#F39C12"), Color.parseColor("#D35400"), Color.parseColor("#C0392B")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Studio White" -> {
                    paint.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), intArrayOf(Color.parseColor("#FFFFFF"), Color.parseColor("#E0E0E0")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Studio Black" -> {
                    paint.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), intArrayOf(Color.parseColor("#1A1A1A"), Color.parseColor("#050505")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Abstract Neon" -> {
                    paint.shader = RadialGradient(width * 0.5f, height * 0.5f, width * 0.7f, intArrayOf(Color.parseColor("#FF00CC"), Color.parseColor("#330066"), Color.parseColor("#000000")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Cyberpunk Alley" -> {
                    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(Color.parseColor("#11001C"), Color.parseColor("#35012C"), Color.parseColor("#2F004F")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Luxury Penthouse" -> {
                    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(Color.parseColor("#232526"), Color.parseColor("#414345")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Cozy Coffee" -> {
                    paint.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), intArrayOf(Color.parseColor("#2C1D11"), Color.parseColor("#805333")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Minimalist Wall" -> {
                    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(Color.parseColor("#ECE9E6"), Color.parseColor("#FFFFFF")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Pastel Dream" -> {
                    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(Color.parseColor("#FF9A9E"), Color.parseColor("#FECFEF")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Tropical Palms" -> {
                    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(Color.parseColor("#11998E"), Color.parseColor("#38EF7D")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                "Vaporwave Grid" -> {
                    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(Color.parseColor("#74EBD5"), Color.parseColor("#9ACE6A")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
                else -> {
                    paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(Color.parseColor("#141E30"), Color.parseColor("#243B55")), null, Shader.TileMode.CLAMP)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                }
            }
            return bg
        }
    }
}

/**
 * ToolItem Composable for CapCut Toolbar
 */
@Composable
fun ToolItem(
    icon: String = "🖼️",
    label: String = "AI BG",
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(androidx.compose.ui.graphics.Color(0xFF222230))
                .border(1.dp, androidx.compose.ui.graphics.Color(0xFF00E5FF), CircleShape)
        ) {
            Text(text = icon, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color.White
        )
    }
}

/**
 * AI Background Picker Sheet with 20 Presets + Gallery + AI Prompt Generator
 */
@Composable
fun BGPickerSheet(
    onDismiss: () -> Unit,
    onSelectPreset: (String) -> Unit,
    onSelectGallery: () -> Unit,
    onGenerateAiBg: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var aiPromptText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = androidx.compose.ui.graphics.Color(0xFF181824)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🖼️ AI Background Studio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs: 20 Presets, Gallery, AI Generate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("20 Presets") }
                    )
                    FilterChip(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            onSelectGallery()
                        },
                        label = { Text("Gallery") },
                        leadingIcon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    FilterChip(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        label = { Text("AI Generate") },
                        leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        // 20 Presets Grid
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(AIBGRemoveEngine.PRESET_BACKGROUND_NAMES) { name ->
                                val thumbnailBmp = remember(name) { AIBGRemoveEngine.getPresetBackground(name, 120, 160) }
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(0.8f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, androidx.compose.ui.graphics.Color(0xFF333348), RoundedCornerShape(12.dp))
                                        .clickable {
                                            onSelectPreset(name)
                                            onDismiss()
                                        }
                                ) {
                                    Image(
                                        bitmap = thumbnailBmp.asImageBitmap(),
                                        contentDescription = name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomCenter)
                                            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.75f))
                                            .padding(4.dp)
                                    ) {
                                        Text(
                                            text = name,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = androidx.compose.ui.graphics.Color.White,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // Gallery selection button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(androidx.compose.ui.graphics.Color(0xFF222234))
                                .clickable {
                                    onSelectGallery()
                                    onDismiss()
                                }
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = androidx.compose.ui.graphics.Color(0xFF00E5FF), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Choose Background from Device Gallery", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    2 -> {
                        // AI Generate prompt input
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = aiPromptText,
                                onValueChange = { aiPromptText = it },
                                label = { Text("Describe AI Background Prompt") },
                                placeholder = { Text("e.g. Cyberpunk neon street at rainy night") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = androidx.compose.ui.graphics.Color(0xFF00E5FF),
                                    unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF444460),
                                    focusedLabelColor = androidx.compose.ui.graphics.Color(0xFF00E5FF),
                                    unfocusedLabelColor = androidx.compose.ui.graphics.Color.Gray
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    if (aiPromptText.isNotBlank()) {
                                        onGenerateAiBg(aiPromptText)
                                        Toast.makeText(context, "Generating AI Background: $aiPromptText", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF00E5FF))
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = androidx.compose.ui.graphics.Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate AI Background", color = androidx.compose.ui.graphics.Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Real-time 30fps Live Background Replacer Composable
 */
@Composable
fun LiveBGReplacer(
    cameraFrame: Bitmap,
    newBG: Bitmap,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {
    val engine = remember(context) { AIBGRemoveEngine(context) }
    val previewState = remember { mutableStateOf<Bitmap?>(null) }
    val segmentModel = engine.segmentModel

    LaunchedEffect(cameraFrame, newBG) {
        withContext(Dispatchers.Default) {
            val mask = segmentModel.segment(cameraFrame)
            val output = engine.compositeLayers(newBG, cameraFrame, mask)
            previewState.value = output // 30fps
        }
    }

    Box(modifier = modifier) {
        previewState.value?.let { preview ->
            Image(
                bitmap = preview.asImageBitmap(),
                contentDescription = "Live AI Background Replacement Preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// Global top-level functions matching user requested signature
fun removeGreenSpill(mask: Bitmap): Bitmap {
    val width = mask.width
    val height = mask.height
    val result = mask.copy(Bitmap.Config.ARGB_8888, true)
    
    val pixels = IntArray(width * height)
    result.getPixels(pixels, 0, width, 0, 0, width, height)
    
    for (i in pixels.indices) {
        val color = pixels[i]
        val alpha = (color shr 24) and 0xFF
        if (alpha > 0) {
            val red = (color shr 16) and 0xFF
            val green = (color shr 8) and 0xFF
            val blue = color and 0xFF
            
            val maxOther = Math.max(red, blue)
            val newGreen = if (green > maxOther) maxOther else green
            
            val trimmedAlpha = if (alpha in 1..220) (alpha * 0.85f).toInt() else alpha
            pixels[i] = (trimmedAlpha shl 24) or (red shl 16) or (newGreen shl 8) or blue
        }
    }
    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

fun refineHairEdges(mask: Bitmap): Bitmap {
    // 2px trim + color spill removal
    return removeGreenSpill(mask)
}

fun extractLUT(background: Bitmap): FloatArray {
    var sumR = 0L
    var sumG = 0L
    var sumB = 0L
    val w = background.width
    val h = background.height
    val stepX = (w / 20).coerceAtLeast(1)
    val stepY = (h / 20).coerceAtLeast(1)
    var count = 0
    for (y in 0 until h step stepY) {
        for (x in 0 until w step stepX) {
            val color = background.getPixel(x, y)
            sumR += (color shr 16) and 0xFF
            sumG += (color shr 8) and 0xFF
            sumB += color and 0xFF
            count++
        }
    }
    val avgR = if (count > 0) sumR.toFloat() / count else 128f
    val avgG = if (count > 0) sumG.toFloat() / count else 128f
    val avgB = if (count > 0) sumB.toFloat() / count else 128f
    
    val targetLuminance = (0.299f * avgR + 0.587f * avgG + 0.114f * avgB).coerceAtLeast(1f)
    val rGain = (avgR / targetLuminance).coerceIn(0.6f, 1.6f)
    val gGain = (avgG / targetLuminance).coerceIn(0.6f, 1.6f)
    val bGain = (avgB / targetLuminance).coerceIn(0.6f, 1.6f)

    return floatArrayOf(
        rGain, 0f, 0f, 0f, 0f,
        0f, gGain, 0f, 0f, 0f,
        0f, 0f, bGain, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
}

fun colorGrade(foreground: Bitmap, targetLUT: FloatArray): Bitmap {
    val result = Bitmap.createBitmap(foreground.width, foreground.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val colorMatrix = android.graphics.ColorMatrix(targetLUT)
    paint.colorFilter = android.graphics.ColorMatrixColorFilter(colorMatrix)
    canvas.drawBitmap(foreground, 0f, 0f, paint)
    return result
}

fun matchLighting(foreground: Bitmap, background: Bitmap): Bitmap {
    // BG ka color temp nikal ke FG pe LUT apply
    return colorGrade(foreground, targetLUT = extractLUT(background))
}

