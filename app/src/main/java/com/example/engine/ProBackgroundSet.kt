package com.example.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ProBgPreset(
    val id: Int,
    val name: String,
    val category: String,
    val badge: String,
    val previewHex: String
)

object ProBackgroundSet {

    val ALL_50_BACKGROUNDS = listOf(
        // Category 1: Studio & Glow (1-10)
        ProBgPreset(1, "Studio Neon", "Studio", "NEON", "#00F2FE"),
        ProBgPreset(2, "Sunset Gold", "Studio", "GOLD", "#FF512F"),
        ProBgPreset(3, "Pure White", "Studio", "SHOP", "#FFFFFF"),
        ProBgPreset(4, "Cyber Matrix", "Studio", "GRID", "#00FF66"),
        ProBgPreset(5, "Royal Velvet", "Studio", "LUX", "#8E2DE2"),
        ProBgPreset(6, "Emerald Glow", "Studio", "GLOW", "#11998E"),
        ProBgPreset(7, "Dark Charcoal", "Studio", "PRO", "#232526"),
        ProBgPreset(8, "Soft Pastel", "Studio", "SOFT", "#FFB88C"),
        ProBgPreset(9, "Crimson Flare", "Studio", "HOT", "#ED213A"),
        ProBgPreset(10, "Ocean Aqua", "Studio", "AQUA", "#00C6FF"),

        // Category 2: Nature & Outdoor (11-20)
        ProBgPreset(11, "Tropical Beach", "Nature", "BEACH", "#FF7E5F"),
        ProBgPreset(12, "Autumn Forest", "Nature", "TREE", "#D38312"),
        ProBgPreset(13, "Misty Peaks", "Nature", "SNOW", "#4B6CB7"),
        ProBgPreset(14, "Cherry Sakura", "Nature", "PINK", "#FF758C"),
        ProBgPreset(15, "Emerald Jungle", "Nature", "LEAF", "#0052D4"),
        ProBgPreset(16, "Sahara Dunes", "Nature", "SAND", "#E65C00"),
        ProBgPreset(17, "Aurora Borealis", "Nature", "SKY", "#00C9FF"),
        ProBgPreset(18, "Cosmic Galaxy", "Nature", "STAR", "#200122"),
        ProBgPreset(19, "Lavender Field", "Nature", "FLOW", "#654EA3"),
        ProBgPreset(20, "Waterfall Splash", "Nature", "WATER", "#3A7BD5"),

        // Category 3: Urban & Modern (21-30)
        ProBgPreset(21, "Tokyo Neon", "Urban", "CITY", "#FF007F"),
        ProBgPreset(22, "Loft Brick", "Urban", "LOFT", "#800020"),
        ProBgPreset(23, "Glass Tower", "Urban", "VIEW", "#3A6073"),
        ProBgPreset(24, "Vintage Cafe", "Urban", "COZY", "#3E2723"),
        ProBgPreset(25, "Window Sunbeam", "Urban", "LIGHT", "#FCEABB"),
        ProBgPreset(26, "Neon Tunnel", "Urban", "FAST", "#833AB4"),
        ProBgPreset(27, "Art Gallery", "Urban", "MINI", "#E0E0E0"),
        ProBgPreset(28, "Fireplace Glow", "Urban", "WARM", "#FF4E50"),
        ProBgPreset(29, "Architect Arc", "Urban", "ARCH", "#616161"),
        ProBgPreset(30, "Concrete Industrial", "Urban", "RAW", "#424242"),

        // Category 4: Abstract & Art (31-40)
        ProBgPreset(31, "Golden Bokeh", "Abstract", "BOKEH", "#FFE000"),
        ProBgPreset(32, "Fluid Marble", "Abstract", "ART", "#70A1FF"),
        ProBgPreset(33, "Pastel Triangles", "Abstract", "GEO", "#A8EDEA"),
        ProBgPreset(34, "Hexagon Hive", "Abstract", "HIVE", "#F857A6"),
        ProBgPreset(35, "Holographic Foil", "Abstract", "HOLO", "#A18CD1"),
        ProBgPreset(36, "Gradient Wave", "Abstract", "WAVE", "#00C6FF"),
        ProBgPreset(37, "Cosmic Nebula", "Abstract", "DUST", "#CC2EFA"),
        ProBgPreset(38, "Polygon Mesh", "Abstract", "POLY", "#2193B0"),
        ProBgPreset(39, "Watercolor Splash", "Abstract", "PAINT", "#FF9A9E"),
        ProBgPreset(40, "Carbon Fiber", "Abstract", "TECH", "#121212"),

        // Category 5: Texture & Pattern (41-50)
        ProBgPreset(41, "Calacatta Marble", "Texture", "STONE", "#F5F5F5"),
        ProBgPreset(42, "Vintage Wood", "Texture", "WOOD", "#4E342E"),
        ProBgPreset(43, "Red Silk Curtain", "Texture", "SILK", "#B71C1C"),
        ProBgPreset(44, "Glitter Dust", "Texture", "SHINE", "#FFD700"),
        ProBgPreset(45, "Leather Grain", "Texture", "PRO", "#212121"),
        ProBgPreset(46, "Chalkboard", "Texture", "DRAW", "#263238"),
        ProBgPreset(47, "Memphis 80s", "Texture", "POP", "#FF4081"),
        ProBgPreset(48, "Vaporwave Sun", "Texture", "RETRO", "#FF007F"),
        ProBgPreset(49, "Comic Halftone", "Texture", "DOTS", "#FFEB3B"),
        ProBgPreset(50, "Paper Linen", "Texture", "PAPER", "#EFEBE9")
    )

    /**
     * Generates a high quality synthetic background bitmap for any of the 50 presets
     */
    suspend fun generateBackgroundBitmap(presetId: Int, width: Int, height: Int): Bitmap = withContext(Dispatchers.Default) {
        val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        when (presetId) {
            1 -> { // Studio Neon
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#1A0B2E"), Color.parseColor("#09203F"), Color.parseColor("#00F2FE")),
                    null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#4FACFE"); alpha = 130 }
                canvas.drawCircle(width * 0.8f, height * 0.2f, width * 0.35f, glow)
            }
            2 -> { // Sunset Gold
                val shader = LinearGradient(0f, 0f, 0f, height.toFloat(),
                    intArrayOf(Color.parseColor("#FF512F"), Color.parseColor("#F09819"), Color.parseColor("#FFD200")),
                    null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            3 -> { // Pure White
                canvas.drawColor(Color.WHITE)
            }
            4 -> { // Cyber Matrix
                canvas.drawColor(Color.parseColor("#050814"))
                paint.color = Color.parseColor("#00FF66")
                paint.strokeWidth = 2f
                val step = width / 12f
                for (i in 0..12) {
                    val x = i * step
                    canvas.drawLine(x, 0f, x, height.toFloat(), paint)
                }
                for (j in 0..16) {
                    val y = j * (height / 16f)
                    canvas.drawLine(0f, y, width.toFloat(), y, paint)
                }
            }
            5 -> { // Royal Velvet
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#8E2DE2"), Color.parseColor("#4A00E0")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            6 -> { // Emerald Glow
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#11998E"), Color.parseColor("#38EF7D")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            7 -> { // Dark Charcoal
                val shader = RadialGradient(width / 2f, height / 2f, maxOf(width, height) * 0.7f,
                    intArrayOf(Color.parseColor("#414345"), Color.parseColor("#141414")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            8 -> { // Soft Pastel
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#FFB88C"), Color.parseColor("#DE6262")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            9 -> { // Crimson Flare
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#ED213A"), Color.parseColor("#931010")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            10 -> { // Ocean Aqua
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#00C6FF"), Color.parseColor("#0072FF")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }

            // Nature
            11 -> { // Tropical Beach
                val shader = LinearGradient(0f, 0f, 0f, height.toFloat(),
                    intArrayOf(Color.parseColor("#FF7E5F"), Color.parseColor("#FEB47B"), Color.parseColor("#00C6FF")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            12 -> { // Autumn Forest
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#D38312"), Color.parseColor("#A83279")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            13 -> { // Misty Peaks
                val shader = LinearGradient(0f, 0f, 0f, height.toFloat(),
                    intArrayOf(Color.parseColor("#4B6CB7"), Color.parseColor("#182848")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            14 -> { // Cherry Sakura
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#FF758C"), Color.parseColor("#FF7EB3")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            15 -> { // Emerald Jungle
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#134E5E"), Color.parseColor("#71B280")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            16 -> { // Sahara Dunes
                val shader = LinearGradient(0f, 0f, 0f, height.toFloat(),
                    intArrayOf(Color.parseColor("#E65C00"), Color.parseColor("#F9D423")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            17 -> { // Aurora Borealis
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#00C9FF"), Color.parseColor("#92FE9D")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            18 -> { // Cosmic Galaxy
                val shader = RadialGradient(width * 0.5f, height * 0.4f, width * 0.8f,
                    intArrayOf(Color.parseColor("#4A00E0"), Color.parseColor("#8E2DE2"), Color.parseColor("#050510")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; alpha = 200 }
                for (i in 0..40) {
                    canvas.drawCircle((i * 137.5f) % width, (i * 219.3f) % height, (i % 3 + 1).toFloat(), starPaint)
                }
            }
            19 -> { // Lavender Field
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#654EA3"), Color.parseColor("#EAAFC8")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            20 -> { // Waterfall Splash
                val shader = LinearGradient(0f, 0f, 0f, height.toFloat(),
                    intArrayOf(Color.parseColor("#3A7BD5"), Color.parseColor("#3A6073")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }

            // Urban
            21 -> { // Tokyo Neon
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#FF007F"), Color.parseColor("#7928CA"), Color.parseColor("#00DFD8")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            22 -> { // Loft Brick
                canvas.drawColor(Color.parseColor("#4A1521"))
                paint.color = Color.parseColor("#361017")
                paint.strokeWidth = 3f
                for (y in 0 until height step 40) {
                    canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), paint)
                }
            }
            23 -> { // Glass Tower
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#3A6073"), Color.parseColor("#3A7BD5")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            24 -> { // Vintage Cafe
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#3E2723"), Color.parseColor("#4E342E")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            25 -> { // Window Sunbeam
                val shader = RadialGradient(width.toFloat(), 0f, width * 1.2f,
                    intArrayOf(Color.parseColor("#FFF1EB"), Color.parseColor("#ACE0F9")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            26 -> { // Neon Tunnel
                val shader = RadialGradient(width / 2f, height / 2f, width * 0.6f,
                    intArrayOf(Color.parseColor("#00F2FE"), Color.parseColor("#4FACFE"), Color.parseColor("#000000")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            27 -> { // Art Gallery
                canvas.drawColor(Color.parseColor("#F5F5F7"))
            }
            28 -> { // Fireplace Glow
                val shader = RadialGradient(width / 2f, height.toFloat(), height * 0.8f,
                    intArrayOf(Color.parseColor("#FF4E50"), Color.parseColor("#F9D423"), Color.parseColor("#110000")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            29 -> { // Architectural Arc
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#757F9A"), Color.parseColor("#D7DDE8")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            30 -> { // Concrete Industrial
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#2C3E50"), Color.parseColor("#000000")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }

            // Abstract
            31 -> { // Golden Bokeh
                canvas.drawColor(Color.parseColor("#1A1000"))
                val bPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFD700"); alpha = 90 }
                for (i in 0..15) {
                    val bx = (i * 123.4f) % width
                    val by = (i * 234.5f) % height
                    val br = 20f + (i % 5) * 25f
                    canvas.drawCircle(bx, by, br, bPaint)
                }
            }
            32 -> { // Fluid Marble
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#70A1FF"), Color.parseColor("#5352ED"), Color.parseColor("#FF4757")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            33 -> { // Pastel Triangles
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#A8EDEA"), Color.parseColor("#FED6E3")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            34 -> { // Hexagon Hive
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#F857A6"), Color.parseColor("#FF5858")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            35 -> { // Holographic Foil
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#A18CD1"), Color.parseColor("#FBC2EB")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            36 -> { // Gradient Wave
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#00C6FF"), Color.parseColor("#0072FF")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            37 -> { // Cosmic Nebula
                val shader = RadialGradient(width / 2f, height / 2f, maxOf(width, height) * 0.7f,
                    intArrayOf(Color.parseColor("#CC2EFA"), Color.parseColor("#000000")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            38 -> { // Polygon Mesh
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#2193B0"), Color.parseColor("#6DD5ED")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            39 -> { // Watercolor Splash
                val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#FF9A9E"), Color.parseColor("#FECFEF")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            40 -> { // Carbon Fiber
                canvas.drawColor(Color.parseColor("#121212"))
                paint.color = Color.parseColor("#252525")
                paint.strokeWidth = 4f
                for (i in -width..width * 2 step 20) {
                    canvas.drawLine(i.toFloat(), 0f, (i + height).toFloat(), height.toFloat(), paint)
                }
            }

            // Texture
            41 -> { // Marble
                canvas.drawColor(Color.parseColor("#F8F9FA"))
                paint.color = Color.parseColor("#DEE2E6")
                paint.strokeWidth = 2f
                canvas.drawLine(0f, height * 0.3f, width.toFloat(), height * 0.7f, paint)
                canvas.drawLine(0f, height * 0.6f, width.toFloat(), height * 0.2f, paint)
            }
            42 -> { // Vintage Wood
                canvas.drawColor(Color.parseColor("#3E2723"))
                paint.color = Color.parseColor("#271510")
                paint.strokeWidth = 8f
                for (y in 0 until height step 30) {
                    canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), paint)
                }
            }
            43 -> { // Silk Curtain
                val shader = LinearGradient(0f, 0f, width.toFloat(), 0f,
                    intArrayOf(Color.parseColor("#B71C1C"), Color.parseColor("#FF1744"), Color.parseColor("#B71C1C")), null, Shader.TileMode.MIRROR)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            44 -> { // Glitter Dust
                canvas.drawColor(Color.parseColor("#110E00"))
                val gPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFD700"); alpha = 180 }
                for (i in 0..50) {
                    val gx = (i * 157.3f) % width
                    val gy = (i * 269.1f) % height
                    canvas.drawCircle(gx, gy, (i % 4 + 1).toFloat(), gPaint)
                }
            }
            45 -> { // Leather Grain
                canvas.drawColor(Color.parseColor("#1C1C1C"))
            }
            46 -> { // Chalkboard
                canvas.drawColor(Color.parseColor("#212B26"))
            }
            47 -> { // Memphis 80s
                canvas.drawColor(Color.parseColor("#FFF0F5"))
                val popP = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FF4081") }
                canvas.drawCircle(width * 0.2f, height * 0.2f, width * 0.1f, popP)
                popP.color = Color.parseColor("#00E5FF")
                canvas.drawCircle(width * 0.8f, height * 0.8f, width * 0.12f, popP)
            }
            48 -> { // Vaporwave Sun
                val shader = LinearGradient(0f, 0f, 0f, height.toFloat(),
                    intArrayOf(Color.parseColor("#000000"), Color.parseColor("#2A0845"), Color.parseColor("#6441A5")), null, Shader.TileMode.CLAMP)
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                val sunP = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FF007F") }
                canvas.drawCircle(width / 2f, height * 0.6f, width * 0.25f, sunP)
            }
            49 -> { // Comic Halftone
                canvas.drawColor(Color.parseColor("#FFEB3B"))
                val dotP = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FF1744") }
                for (y in 0 until height step 24) {
                    for (x in 0 until width step 24) {
                        canvas.drawCircle(x.toFloat(), y.toFloat(), 4f, dotP)
                    }
                }
            }
            else -> { // Paper Linen (50)
                canvas.drawColor(Color.parseColor("#EFEBE9"))
            }
        }

        out
    }
}
