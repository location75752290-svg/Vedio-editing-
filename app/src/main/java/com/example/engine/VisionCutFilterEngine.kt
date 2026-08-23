package com.example.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import java.io.File
import java.io.FileOutputStream

/**
 * Filter Specification for VisionCut AI
 * Shared between Video Editor and Photo Editor for 100% identical color science.
 */
data class VisionCutFilterSpec(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val previewGradient: List<Color>,
    val baseMatrix: FloatArray
)

data class PhotoAdjustments(
    val brightness: Float = 0f,    // -100 to +100
    val contrast: Float = 1f,       // 0.5 to 2.0
    val saturation: Float = 1f,     // 0.0 to 2.0
    val warmth: Float = 0f,         // -50 to +50
    val vignette: Float = 0f,       // 0 to 100
    val sharpness: Float = 0f       // 0 to 100
)

/**
 * Unified Filter & GPU Color Engine for VisionCut AI
 * Provides identical ColorMatrix and LUT pipelines for both Video and Photo processing.
 */
object VisionCutFilterEngine {

    // Identity 4x5 Color Matrix
    val IDENTITY_MATRIX = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    // The 50 CapCut-Grade Cinema & 4K UHD Filters
    val ALL_15_FILTERS: List<VisionCutFilterSpec> = listOf(
        // === 20 FULL HD 4K FILTERS (Category: "4K UHD Elite") ===
        VisionCutFilterSpec(
            id = "4k_cinema_gold",
            name = "4K Cinema Gold",
            category = "4K UHD Elite",
            description = "Premium 4K ultra-warm golden hour cinema look",
            previewGradient = listOf(Color(0xFFFFD700), Color(0xFFFF8C00)),
            baseMatrix = floatArrayOf(
                1.35f, 0.05f, -0.05f, 0f, 15f,
                0.00f, 1.15f, 0.05f, 0f, 8f,
                -0.10f, 0.05f, 0.90f, 0f, -5f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_hdr_bloom",
            name = "4K HDR Bloom",
            category = "4K UHD Elite",
            description = "High dynamic range with luminous velvet highlights",
            previewGradient = listOf(Color(0xFF00FFFF), Color(0xFFFF00FF)),
            baseMatrix = floatArrayOf(
                1.25f, 0.10f, 0.10f, 0f, 20f,
                0.10f, 1.25f, 0.10f, 0f, 20f,
                0.10f, 0.10f, 1.25f, 0f, 20f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_studio_port",
            name = "4K Studio Portrait",
            category = "4K UHD Elite",
            description = "Flattering high-definition soft skin tones and detail protection",
            previewGradient = listOf(Color(0xFFFFE4E1), Color(0xFFFFB6C1)),
            baseMatrix = floatArrayOf(
                1.12f, 0.02f, 0.02f, 0f, 10f,
                0.02f, 1.10f, 0.02f, 0f, 8f,
                0.02f, 0.02f, 1.08f, 0f, 6f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_sunset_glow",
            name = "4K Sunset Glow",
            category = "4K UHD Elite",
            description = "Dazzling 4K sunset amber glow with high saturation",
            previewGradient = listOf(Color(0xFFFF4500), Color(0xFFFF8C00)),
            baseMatrix = floatArrayOf(
                1.40f, 0.00f, 0.00f, 0f, 25f,
                0.00f, 1.15f, 0.00f, 0f, 12f,
                -0.15f, -0.15f, 0.75f, 0f, -20f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_vintage_chrome",
            name = "4K Vintage Chrome",
            category = "4K UHD Elite",
            description = "Classic chrome film vintage look with raised black point",
            previewGradient = listOf(Color(0xFF8B4513), Color(0xFFCD853F)),
            baseMatrix = floatArrayOf(
                1.10f, 0.08f, 0.04f, 0f, 18f,
                0.04f, 1.02f, 0.04f, 0f, 12f,
                -0.08f, 0.00f, 0.80f, 0f, 30f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_cyber_neon",
            name = "4K Cyber Neon",
            category = "4K UHD Elite",
            description = "Stunning neon Tokyo look with high contrast and glow",
            previewGradient = listOf(Color(0xFF00FF7F), Color(0xFFFF007F)),
            baseMatrix = floatArrayOf(
                1.45f, -0.15f, 0.05f, 0f, 18f,
                -0.10f, 1.25f, 0.30f, 0f, -5f,
                0.20f, 0.05f, 1.55f, 0f, 28f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_glacial_cold",
            name = "4K Glacial Cold",
            category = "4K UHD Elite",
            description = "Pristine arctic blue highlights with cold clear whites",
            previewGradient = listOf(Color(0xFFAFEEEE), Color(0xFF4682B4)),
            baseMatrix = floatArrayOf(
                0.85f, 0.00f, 0.00f, 0f, -8f,
                0.00f, 1.04f, 0.04f, 0f, 3f,
                0.04f, 0.08f, 1.35f, 0f, 22f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_forest_mist",
            name = "4K Forest Mist",
            category = "4K UHD Elite",
            description = "Organic cinematic deep forest green tones",
            previewGradient = listOf(Color(0xFF2E8B57), Color(0xFF006400)),
            baseMatrix = floatArrayOf(
                0.90f, 0.05f, 0.00f, 0f, -5f,
                0.05f, 1.25f, 0.05f, 0f, 15f,
                0.00f, 0.05f, 0.95f, 0f, -2f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_matte_noir",
            name = "4K Matte Noir",
            category = "4K UHD Elite",
            description = "Rich, high-contrast matte monochrome with faded shadows",
            previewGradient = listOf(Color(0xFF708090), Color(0xFF1C1C1C)),
            baseMatrix = floatArrayOf(
                0.35f, 0.65f, 0.15f, 0f, 10f,
                0.35f, 0.65f, 0.15f, 0f, 10f,
                0.35f, 0.65f, 0.15f, 0f, 10f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_rose_dream",
            name = "4K Rose Dream",
            category = "4K UHD Elite",
            description = "Dreamy pastel rose romance and pink undertones",
            previewGradient = listOf(Color(0xFFFFC0CB), Color(0xFFFF69B4)),
            baseMatrix = floatArrayOf(
                1.30f, 0.04f, 0.08f, 0f, 22f,
                0.04f, 0.98f, 0.04f, 0f, -2f,
                0.12f, 0.04f, 1.15f, 0f, 16f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_emerald_film",
            name = "4K Emerald Film",
            category = "4K UHD Elite",
            description = "Classic film look with lush emerald green details",
            previewGradient = listOf(Color(0xFF3CB371), Color(0xFF2E8B57)),
            baseMatrix = floatArrayOf(
                1.12f, 0.04f, -0.04f, 0f, -2f,
                0.00f, 1.10f, 0.04f, 0f, -1f,
                -0.04f, 0.04f, 0.92f, 0f, 6f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_classic_lomo",
            name = "4K Classic Lomo",
            category = "4K UHD Elite",
            description = "Lomo-style rich color saturation with dark borders",
            previewGradient = listOf(Color(0xFFFF0000), Color(0xFF8B0000)),
            baseMatrix = floatArrayOf(
                1.25f, 0.00f, 0.00f, 0f, 5f,
                0.00f, 1.20f, 0.00f, 0f, 10f,
                0.00f, 0.00f, 1.30f, 0f, -15f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_moody_teal",
            name = "4K Moody Teal",
            category = "4K UHD Elite",
            description = "Dark moody teal shadows paired with warm skin tones",
            previewGradient = listOf(Color(0xFF008080), Color(0xFF004D40)),
            baseMatrix = floatArrayOf(
                1.20f, -0.05f, -0.10f, 0f, 12f,
                -0.05f, 1.05f, 0.05f, 0f, -3f,
                -0.15f, 0.10f, 1.25f, 0f, -8f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_pastel_breeze",
            name = "4K Pastel Breeze",
            category = "4K UHD Elite",
            description = "Light and airy pastel breeze with soft focus effect",
            previewGradient = listOf(Color(0xFFF0FFF0), Color(0xFFE6E6FA)),
            baseMatrix = floatArrayOf(
                1.10f, 0.05f, 0.05f, 0f, 15f,
                0.05f, 1.10f, 0.05f, 0f, 15f,
                0.05f, 0.05f, 1.10f, 0f, 15f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_hollywood_orange",
            name = "4K Hollywood Orange",
            category = "4K UHD Elite",
            description = "Legendary Hollywood orange & teal color pop contrast",
            previewGradient = listOf(Color(0xFFFF7F50), Color(0xFF20B2AA)),
            baseMatrix = floatArrayOf(
                1.30f, -0.05f, -0.10f, 0f, 10f,
                -0.05f, 1.12f, 0.08f, 0f, -2f,
                -0.18f, 0.12f, 1.28f, 0f, -8f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_royal_velvet",
            name = "4K Royal Velvet",
            category = "4K UHD Elite",
            description = "Rich, dark royal velvet tone with deep purple vibes",
            previewGradient = listOf(Color(0xFF4B0082), Color(0xFF800080)),
            baseMatrix = floatArrayOf(
                1.15f, 0.05f, 0.12f, 0f, 8f,
                0.05f, 1.05f, 0.05f, 0f, 2f,
                0.15f, 0.05f, 1.30f, 0f, 12f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_soft_velvet",
            name = "4K Soft Velvet",
            category = "4K UHD Elite",
            description = "Ultra soft, cinema-quality velvet soft lens look",
            previewGradient = listOf(Color(0xFFBC8F8F), Color(0xFFF4A460)),
            baseMatrix = floatArrayOf(
                1.15f, 0.04f, 0.04f, 0f, 12f,
                0.04f, 1.12f, 0.04f, 0f, 10f,
                0.04f, 0.04f, 1.10f, 0f, 8f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_cozy_cabin",
            name = "4K Cozy Cabin",
            category = "4K UHD Elite",
            description = "Warm woody cabin aesthetic with cozy timber brown tones",
            previewGradient = listOf(Color(0xFF8A4F35), Color(0xFF5C3317)),
            baseMatrix = floatArrayOf(
                1.25f, 0.06f, 0.02f, 0f, 14f,
                0.04f, 1.12f, 0.02f, 0f, 8f,
                -0.08f, -0.04f, 0.88f, 0f, -10f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_arctic_ice",
            name = "4K Arctic Ice",
            category = "4K UHD Elite",
            description = "Ultra bright, clean arctic snow grading with silver tones",
            previewGradient = listOf(Color(0xFFE0FFFF), Color(0xFFB0C4DE)),
            baseMatrix = floatArrayOf(
                1.18f, 0.00f, 0.00f, 0f, 20f,
                0.00f, 1.18f, 0.00f, 0f, 20f,
                0.00f, 0.00f, 1.25f, 0f, 24f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "4k_dynamic_vivid",
            name = "4K Dynamic Vivid",
            category = "4K UHD Elite",
            description = "Super wide dynamic range with eye-popping vivid colors",
            previewGradient = listOf(Color(0xFFFF00FF), Color(0xFF00FF00)),
            baseMatrix = floatArrayOf(
                1.30f, -0.05f, -0.05f, 0f, 10f,
                -0.05f, 1.30f, -0.05f, 0f, 10f,
                -0.05f, -0.05f, 1.30f, 0f, 10f,
                0f, 0f, 0f, 1f, 0f
            )
        ),

        // === 30 CLASSIC & RETRO FILTERS ===
        VisionCutFilterSpec(
            id = "normal",
            name = "Normal",
            category = "Original",
            description = "Natural raw camera look without color shift",
            previewGradient = listOf(Color(0xFF4A5568), Color(0xFF2D3748)),
            baseMatrix = IDENTITY_MATRIX
        ),
        VisionCutFilterSpec(
            id = "film",
            name = "Film",
            category = "Cinema",
            description = "Classic 35mm motion picture grain with soft emerald greens",
            previewGradient = listOf(Color(0xFF2C3E50), Color(0xFF27AE60)),
            baseMatrix = floatArrayOf(
                1.15f, 0.05f, -0.05f, 0f, -4f,
                0.00f, 1.08f, 0.04f, 0f, -3f,
                -0.05f, 0.05f, 0.88f, 0f, 8f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "blockbuster",
            name = "Blockbuster",
            category = "Cinema",
            description = "Hollywood Teal & Orange grading with punchy contrast",
            previewGradient = listOf(Color(0xFF0083B0), Color(0xFF00B4DB)),
            baseMatrix = floatArrayOf(
                1.35f, -0.05f, -0.15f, 0f, 14f,
                -0.05f, 1.10f, 0.10f, 0f, -4f,
                -0.20f, 0.15f, 1.30f, 0f, -10f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "warm",
            name = "Warm",
            category = "Nature",
            description = "Golden hour sunset glow with rich amber tones",
            previewGradient = listOf(Color(0xFFF59E0B), Color(0xFFDC2626)),
            baseMatrix = floatArrayOf(
                1.30f, 0.05f, 0.00f, 0f, 22f,
                0.00f, 1.10f, 0.00f, 0f, 10f,
                -0.10f, -0.10f, 0.80f, 0f, -16f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "cold",
            name = "Cold",
            category = "Mood",
            description = "Nordic glacial blue with crisp clear highlights",
            previewGradient = listOf(Color(0xFF38BDF8), Color(0xFF2563EB)),
            baseMatrix = floatArrayOf(
                0.82f, 0.00f, 0.00f, 0f, -10f,
                0.00f, 1.02f, 0.05f, 0f, 4f,
                0.05f, 0.10f, 1.40f, 0f, 26f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "vintage",
            name = "Vintage",
            category = "Retro",
            description = "Warm faded polaroid nostalgia with raised blacks",
            previewGradient = listOf(Color(0xFFD97706), Color(0xFF78350F)),
            baseMatrix = floatArrayOf(
                1.05f, 0.10f, 0.05f, 0f, 24f,
                0.05f, 0.95f, 0.05f, 0f, 18f,
                -0.10f, 0.00f, 0.75f, 0f, 36f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "glow",
            name = "Glow",
            category = "Dreamy",
            description = "Luminous highlight bloom with velvety soft diffusion",
            previewGradient = listOf(Color(0xFFFFB199), Color(0xFFFF0844)),
            baseMatrix = floatArrayOf(
                1.22f, 0.08f, 0.08f, 0f, 26f,
                0.08f, 1.20f, 0.08f, 0f, 24f,
                0.08f, 0.08f, 1.18f, 0f, 22f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "smooth",
            name = "Smooth",
            category = "Portrait",
            description = "Flattering skin tone softening with delicate highlights",
            previewGradient = listOf(Color(0xFFFFA07A), Color(0xFFFF7F50)),
            baseMatrix = floatArrayOf(
                1.08f, 0.04f, 0.04f, 0f, 16f,
                0.04f, 1.06f, 0.04f, 0f, 12f,
                0.04f, 0.04f, 1.04f, 0f, 10f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "white",
            name = "White",
            category = "Minimal",
            description = "High-key pristine editorial clean white aesthetic",
            previewGradient = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8)),
            baseMatrix = floatArrayOf(
                1.24f, 0.00f, 0.00f, 0f, 28f,
                0.00f, 1.24f, 0.00f, 0f, 28f,
                0.00f, 0.00f, 1.24f, 0f, 28f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "pink",
            name = "Pink",
            category = "Stylized",
            description = "Pastel blush romance with magenta & rose undertones",
            previewGradient = listOf(Color(0xFFFF758C), Color(0xFFFF7EB3)),
            baseMatrix = floatArrayOf(
                1.35f, 0.05f, 0.10f, 0f, 26f,
                0.05f, 0.95f, 0.05f, 0f, -4f,
                0.15f, 0.05f, 1.20f, 0f, 20f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "dream",
            name = "Dream",
            category = "Dreamy",
            description = "Ethereal fantasy aura with lavender twilight tint",
            previewGradient = listOf(Color(0xFFC471ED), Color(0xFFF64F59)),
            baseMatrix = floatArrayOf(
                1.20f, 0.10f, 0.15f, 0f, 20f,
                0.05f, 1.05f, 0.10f, 0f, 10f,
                0.20f, 0.10f, 1.35f, 0f, 30f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "cyberpunk",
            name = "Cyberpunk",
            category = "Urban",
            description = "Futuristic neon Tokyo with hot pink & electric cyan",
            previewGradient = listOf(Color(0xFFFF007F), Color(0xFF00F0FF)),
            baseMatrix = floatArrayOf(
                1.50f, -0.20f, 0.10f, 0f, 22f,
                -0.15f, 1.20f, 0.35f, 0f, -8f,
                0.25f, 0.10f, 1.60f, 0f, 32f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "neon",
            name = "Neon",
            category = "Urban",
            description = "Ultra high vibrancy and electric night club glow",
            previewGradient = listOf(Color(0xFF00FF87), Color(0xFF60EFFF)),
            baseMatrix = floatArrayOf(
                1.40f, -0.10f, -0.10f, 0f, 12f,
                -0.10f, 1.40f, -0.10f, 0f, 16f,
                -0.10f, -0.10f, 1.50f, 0f, 22f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "retro",
            name = "Retro",
            category = "Retro",
            description = "80s VHS mixtape aesthetic with warm saturation",
            previewGradient = listOf(Color(0xFFF7971E), Color(0xFFFFD200)),
            baseMatrix = floatArrayOf(
                1.18f, 0.15f, -0.10f, 0f, 22f,
                0.05f, 1.10f, 0.05f, 0f, 16f,
                -0.20f, 0.10f, 0.85f, 0f, 42f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "bw",
            name = "B&W",
            category = "Monochrome",
            description = "High-contrast dramatic black and white monochrome",
            previewGradient = listOf(Color(0xFFCBD5E1), Color(0xFF334155)),
            baseMatrix = floatArrayOf(
                0.3887f, 0.7631f, 0.1482f, 0f, -14f,
                0.3887f, 0.7631f, 0.1482f, 0f, -14f,
                0.3887f, 0.7631f, 0.1482f, 0f, -14f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "clarity",
            name = "Clarity",
            category = "Enhance",
            description = "Punchy micro-contrast with crisp HDR dynamic range",
            previewGradient = listOf(Color(0xFF667EEA), Color(0xFF764BA2)),
            baseMatrix = floatArrayOf(
                1.32f, -0.10f, -0.10f, 0f, -8f,
                -0.10f, 1.32f, -0.10f, 0f, -8f,
                -0.10f, -0.10f, 1.32f, 0f, -8f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "faded_glory",
            name = "Faded Glory",
            category = "Retro",
            description = "Retro faded print look with low contrast shadows",
            previewGradient = listOf(Color(0xFFE2D1C3), Color(0xFFFDFCFB)),
            baseMatrix = floatArrayOf(
                0.95f, 0.05f, 0.05f, 0f, 10f,
                0.05f, 0.95f, 0.05f, 0f, 10f,
                0.05f, 0.05f, 0.95f, 0f, 10f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "cozy_warmth",
            name = "Cozy Warmth",
            category = "Portrait",
            description = "Gentle amber portrait warmth with smooth skin glow",
            previewGradient = listOf(Color(0xFFFAD961), Color(0xFFF76B1C)),
            baseMatrix = floatArrayOf(
                1.20f, 0.02f, 0.00f, 0f, 15f,
                0.00f, 1.08f, 0.00f, 0f, 8f,
                -0.05f, -0.05f, 0.92f, 0f, -10f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "ocean_breeze",
            name = "Ocean Breeze",
            category = "Mood",
            description = "Fresh ocean teal breeze with beautiful shadows",
            previewGradient = listOf(Color(0xFF4FACFE), Color(0xFF00F2FE)),
            baseMatrix = floatArrayOf(
                0.92f, 0.04f, 0.04f, 0f, -5f,
                0.04f, 1.15f, 0.04f, 0f, 8f,
                0.04f, 0.04f, 1.22f, 0f, 18f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "sepia_vintage",
            name = "Sepia Vintage",
            category = "Retro",
            description = "Deep antique bronze sepia style for vintage feel",
            previewGradient = listOf(Color(0xFF3E2723), Color(0xFFD7CCC8)),
            baseMatrix = floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 25f,
                0.349f, 0.686f, 0.168f, 0f, 15f,
                0.272f, 0.534f, 0.131f, 0f, -5f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "crimson_vibe",
            name = "Crimson Vibe",
            category = "Stylized",
            description = "Vivid crimson red tone highlighting warm elements",
            previewGradient = listOf(Color(0xFFFF0844), Color(0xFFFFB199)),
            baseMatrix = floatArrayOf(
                1.40f, 0.05f, 0.05f, 0f, 20f,
                0.05f, 0.95f, 0.05f, 0f, -10f,
                0.05f, 0.05f, 0.95f, 0f, -10f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "polaroid_1984",
            name = "Polaroid 1984",
            category = "Retro",
            description = "Authentic nostalgic Polaroid photo colors",
            previewGradient = listOf(Color(0xFFF1F2B5), Color(0xFF135058)),
            baseMatrix = floatArrayOf(
                1.02f, 0.10f, 0.08f, 0f, 15f,
                0.05f, 0.98f, 0.05f, 0f, 10f,
                -0.12f, 0.02f, 0.82f, 0f, 25f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "noir_classic",
            name = "Noir Classic",
            category = "Monochrome",
            description = "Intense deep shadows and high contrast monochrome",
            previewGradient = listOf(Color(0xFF232526), Color(0xFF414345)),
            baseMatrix = floatArrayOf(
                0.30f, 0.59f, 0.11f, 0f, -5f,
                0.30f, 0.59f, 0.11f, 0f, -5f,
                0.30f, 0.59f, 0.11f, 0f, -5f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "candy_pop",
            name = "Candy Pop",
            category = "Stylized",
            description = "Explosive bubblegum saturated color burst",
            previewGradient = listOf(Color(0xFFFECFEF), Color(0xFFFC6767)),
            baseMatrix = floatArrayOf(
                1.35f, -0.05f, -0.05f, 0f, 5f,
                -0.05f, 1.35f, -0.05f, 0f, 5f,
                -0.05f, -0.05f, 1.35f, 0f, 5f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "autumn_leaves",
            name = "Autumn Leaves",
            category = "Nature",
            description = "Warm rust reds, golden ambers and deep oranges",
            previewGradient = listOf(Color(0xFFE65C00), Color(0xFFF9D423)),
            baseMatrix = floatArrayOf(
                1.32f, 0.08f, 0.00f, 0f, 18f,
                0.02f, 1.12f, 0.00f, 0f, 6f,
                -0.15f, -0.15f, 0.80f, 0f, -22f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "urban_concrete",
            name = "Urban Concrete",
            category = "Urban",
            description = "Muted industrial greys and cool urban desaturated tones",
            previewGradient = listOf(Color(0xFFBDC3C7), Color(0xFF2C3E50)),
            baseMatrix = floatArrayOf(
                0.90f, 0.05f, 0.05f, 0f, -4f,
                0.05f, 0.90f, 0.05f, 0f, -4f,
                0.05f, 0.05f, 0.95f, 0f, 4f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "misty_morning",
            name = "Misty Morning",
            category = "Landscape",
            description = "Dreamy morning haze with beautiful soft blue tint",
            previewGradient = listOf(Color(0xFFECE9E6), Color(0xFFFFFFFF)),
            baseMatrix = floatArrayOf(
                0.95f, 0.02f, 0.02f, 0f, 8f,
                0.02f, 1.02f, 0.02f, 0f, 10f,
                0.02f, 0.02f, 1.15f, 0f, 18f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "desert_sand",
            name = "Desert Sand",
            category = "Nature",
            description = "Warm dry terracotta tones reminiscent of desert lands",
            previewGradient = listOf(Color(0xFFE1B382), Color(0xFF12232E)),
            baseMatrix = floatArrayOf(
                1.24f, 0.05f, 0.02f, 0f, 15f,
                0.05f, 1.12f, 0.02f, 0f, 8f,
                -0.10f, -0.10f, 0.88f, 0f, -12f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "midnight_blue",
            name = "Midnight Blue",
            category = "Mood",
            description = "Deep royal blue shadows and high contrast moonlight theme",
            previewGradient = listOf(Color(0xFF0F2027), Color(0xFF203A43)),
            baseMatrix = floatArrayOf(
                0.80f, 0.00f, 0.00f, 0f, -12f,
                0.00f, 0.95f, 0.05f, 0f, -5f,
                0.05f, 0.10f, 1.35f, 0f, 24f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        VisionCutFilterSpec(
            id = "neon_sunset",
            name = "Neon Sunset",
            category = "Urban",
            description = "High intensity synthwave orange and magenta look",
            previewGradient = listOf(Color(0xFFF000FF), Color(0xFFFF1200)),
            baseMatrix = floatArrayOf(
                1.45f, -0.10f, 0.10f, 0f, 20f,
                -0.05f, 1.15f, 0.20f, 0f, -2f,
                0.15f, 0.05f, 1.45f, 0f, 25f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    )

    fun findFilter(filterId: String): VisionCutFilterSpec {
        return ALL_15_FILTERS.firstOrNull { it.id.equals(filterId, ignoreCase = true) || it.name.equals(filterId, ignoreCase = true) }
            ?: ALL_15_FILTERS[0]
    }

    /**
     * Computes the blended 4x5 FloatArray for Compose and Canvas ColorMatrix.
     * Intensity ranges from 0.0 (Identity) to 1.0 (Full Filter Matrix).
     */
    fun getBlendedMatrix(
        filterId: String,
        intensity: Float = 1.0f,
        adjustments: PhotoAdjustments = PhotoAdjustments()
    ): FloatArray {
        val filter = findFilter(filterId)
        val t = intensity.coerceIn(0f, 1f)
        val target = filter.baseMatrix

        // 1. Interpolate Filter with Identity based on intensity
        val blended = FloatArray(20)
        for (i in 0 until 20) {
            blended[i] = IDENTITY_MATRIX[i] + t * (target[i] - IDENTITY_MATRIX[i])
        }

        // 2. Combine with Manual Adjustments (Brightness, Contrast, Saturation, Warmth)
        val cm = ColorMatrix(blended)

        // Saturation adjustment
        if (adjustments.saturation != 1.0f) {
            val satMatrix = ColorMatrix().apply { setSaturation(adjustments.saturation) }
            cm.postConcat(satMatrix)
        }

        // Contrast adjustment
        if (adjustments.contrast != 1.0f) {
            val c = adjustments.contrast
            val offset = (1f - c) * 128f
            val contrastMatrix = ColorMatrix(
                floatArrayOf(
                    c, 0f, 0f, 0f, offset,
                    0f, c, 0f, 0f, offset,
                    0f, 0f, c, 0f, offset,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            cm.postConcat(contrastMatrix)
        }

        // Brightness adjustment (-100 to +100)
        if (adjustments.brightness != 0f) {
            val b = adjustments.brightness
            val brightnessMatrix = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, b,
                    0f, 1f, 0f, 0f, b,
                    0f, 0f, 1f, 0f, b,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            cm.postConcat(brightnessMatrix)
        }

        // Warmth adjustment (-50 to +50)
        if (adjustments.warmth != 0f) {
            val w = adjustments.warmth
            val warmthMatrix = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, w * 0.8f,
                    0f, 1f, 0f, 0f, w * 0.3f,
                    0f, 0f, 1f, 0f, -w * 0.8f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            cm.postConcat(warmthMatrix)
        }

        return cm.array
    }

    /**
     * Applies filter and adjustments to a Bitmap and returns a new filtered Bitmap.
     */
    fun applyFilterToBitmap(
        sourceBitmap: Bitmap,
        filterId: String,
        intensity: Float = 1.0f,
        adjustments: PhotoAdjustments = PhotoAdjustments()
    ): Bitmap {
        val width = sourceBitmap.width
        val height = sourceBitmap.height
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(outputBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val matrixArray = getBlendedMatrix(filterId, intensity, adjustments)
        val colorMatrix = ColorMatrix(matrixArray)
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        canvas.drawBitmap(sourceBitmap, 0f, 0f, paint)

        // Apply optional Vignette in post-processing
        if (adjustments.vignette > 0f) {
            val vignettePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            val radius = Math.hypot(width / 2.0, height / 2.0).toFloat()
            val alphaPercent = (adjustments.vignette / 100f).coerceIn(0f, 1f)
            val colors = intArrayOf(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.argb((alphaPercent * 210).toInt(), 0, 0, 0)
            )
            val stops = floatArrayOf(0.45f, 1.0f)
            vignettePaint.shader = android.graphics.RadialGradient(
                width / 2f,
                height / 2f,
                radius,
                colors,
                stops,
                android.graphics.Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), vignettePaint)
        }

        return outputBitmap
    }

    /**
     * Saves the processed bitmap to a JPEG file in cache or destination.
     */
    fun saveBitmapToFile(bitmap: Bitmap, targetFile: File, quality: Int = 95): Boolean {
        return try {
            FileOutputStream(targetFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
