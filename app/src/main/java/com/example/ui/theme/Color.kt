package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Bento Grid Ultra-Dark Color Palette for VisionCut AI
val ObsidianBackground = Color(0xFF050505)
val CharcoalSurface = Color(0xFF121216)
val CharcoalSurfaceVariant = Color(0xFF1A1A20)
val GlassBorder = Color(0x1CFFFFFF)
val GlassBackground = Color(0x14FFFFFF)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA1A1AA)
val TextMuted = Color(0xFF71717A)

// Accent Colors
val NeonIndigo = Color(0xFF6366F1)
val DeepPurple = Color(0xFF8B5CF6)
val ElectricBlue = Color(0xFF3B82F6)
val RadiantPink = Color(0xFFEC4899)
val CyanGlow = Color(0xFF06B6D4)

// Status Colors
val ProBadgeStart = Color(0xFFF59E0B)
val ProBadgeEnd = Color(0xFFEC4899)
val SuccessGreen = Color(0xFF10B981)

// Gradients
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(ElectricBlue, NeonIndigo, DeepPurple)
)

val AccentGradient = Brush.horizontalGradient(
    colors = listOf(NeonIndigo, DeepPurple, RadiantPink)
)

val GlowGradient = Brush.radialGradient(
    colors = listOf(DeepPurple.copy(alpha = 0.35f), Color.Transparent)
)

val GlassCardGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x26FFFFFF),
        Color(0x0DFFFFFF)
    )
)
