package com.example.domain.model

import java.util.UUID

/**
 * AI Tool Categories & Execution Config for VisionCut AI
 */
enum class AiToolCategory {
    GENERATION,
    ENHANCEMENT,
    AUDIO_VOICE,
    SMART_EDITING,
    EFFECTS_GRADES
}

data class AiToolSpec(
    val id: String,
    val name: String,
    val description: String,
    val category: AiToolCategory,
    val iconName: String,
    val isPremium: Boolean = false,
    val isNew: Boolean = false
)

/**
 * Specifications of all VisionCut AI Features
 */
object VisionCutAiFeatures {
    val ALL_AI_TOOLS = listOf(
        AiToolSpec("text_to_video", "AI Text to Video", "Generate cinematic scenes from prompt text", AiToolCategory.GENERATION, "AutoAwesome", isPremium = true),
        AiToolSpec("image_to_video", "AI Image to Video", "Animate static photos with motion physics", AiToolCategory.GENERATION, "Image"),
        AiToolSpec("ai_video_generator", "AI Video Generator", "Full script to storyboard video synthesis", AiToolCategory.GENERATION, "VideoCall", isPremium = true),
        AiToolSpec("enhancement_4k_8k", "AI Video Enhancement (4K/8K)", "Upscale and denoise raw footage", AiToolCategory.ENHANCEMENT, "FlashOn", isPremium = true),
        AiToolSpec("ai_upscaling", "AI Super Resolution", "Detail restoration and frame interpolation", AiToolCategory.ENHANCEMENT, "MovieFilter"),
        AiToolSpec("bg_remover", "AI Background Remover", "1-click portrait and object matting", AiToolCategory.SMART_EDITING, "ContentCut"),
        AiToolSpec("auto_cut", "AI Auto Cut", "Intelligent scene detection & silence trimming", AiToolCategory.SMART_EDITING, "ContentCut"),
        AiToolSpec("auto_captions", "AI Auto Captions", "Multi-language speech-to-text subtitling", AiToolCategory.AUDIO_VOICE, "Subtitles"),
        AiToolSpec("voice_generator", "AI Voice Generator", "Studio-grade neural text-to-speech", AiToolCategory.AUDIO_VOICE, "Mic"),
        AiToolSpec("voice_cloning", "AI Voice Cloning", "Custom zero-shot voice replication", AiToolCategory.AUDIO_VOICE, "RecordVoiceOver", isPremium = true),
        AiToolSpec("music_generator", "AI Music Generator", "Royalty-free background music composition", AiToolCategory.AUDIO_VOICE, "MusicNote"),
        AiToolSpec("ai_effects", "AI FX & Shaders", "Neural style transfer and visual FX", AiToolCategory.EFFECTS_GRADES, "AutoFixHigh"),
        AiToolSpec("object_tracking", "AI Object Tracking", "Bounding-box pin and blur follower", AiToolCategory.SMART_EDITING, "CenterFocusWeak"),
        AiToolSpec("motion_tracking", "AI Motion Tracking", "Optical flow camera path stabilization", AiToolCategory.SMART_EDITING, "TrackChanges"),
        AiToolSpec("color_grading", "AI Color Grading", "Cinematic LUT matching & HDR tone curve", AiToolCategory.EFFECTS_GRADES, "Palette"),
        AiToolSpec("face_enhancement", "AI Face Retouch", "Portrait beauty and skin smoothing", AiToolCategory.ENHANCEMENT, "Face"),
        AiToolSpec("stabilization", "AI Stabilization", "Gyro-less sub-pixel video steadying", AiToolCategory.ENHANCEMENT, "Camera"),
        AiToolSpec("noise_removal", "AI Audio Noise Removal", "Deep learning vocal isolation & denoise", AiToolCategory.AUDIO_VOICE, "GraphicEq"),
        AiToolSpec("smart_templates", "AI Smart Templates", "Auto-sync editing to music beats", AiToolCategory.SMART_EDITING, "ViewGrid"),
        AiToolSpec("script_generator", "AI Script Generator", "LLM powered viral story scriptwriter", AiToolCategory.GENERATION, "EditNote")
    )

    val TIMELINE_CAPABILITIES = listOf(
        "Multi-track Timeline",
        "Video & Audio Layers",
        "Keyframe Animation Engine",
        "Professional Transitions",
        "Cinematic FX & Filters",
        "Animated Text & Captions",
        "Dynamic Speed Ramping",
        "AI Chroma Key & Green Screen",
        "Custom Masking & Cutouts",
        "Alpha Blend Modes",
        "Pro Audio Mixer & EQ",
        "4K & 8K Ultra-HD Export",
        "60 FPS & 120 FPS High-Frame Export",
        "HDR10 & HLG Wide Color Export"
    )
}

/**
 * Timeline Track & Keyframe Engine Models
 */
enum class TrackType { VIDEO, AUDIO, TEXT, EFFECT, OVERLAY }

enum class CurveType { LINEAR, EASE_IN, EASE_OUT, EASE_IN_OUT, BEZIER, CUSTOM_SPEED_RAMP }

data class Keyframe(
    val timeMs: Long,
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val opacity: Float = 1f,
    val curveType: CurveType = CurveType.EASE_IN_OUT
)

data class TimelineClip(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val startMs: Long,
    val durationMs: Long,
    val assetUri: String = "",
    val speed: Float = 1.0f,
    val isReversed: Boolean = false,
    val isFrozen: Boolean = false,
    val blendMode: String = "Normal", // Normal, Multiply, Screen, Overlay, Darken, Lighten, ColorDodge
    val maskType: String = "None", // None, Rectangle, Circle, Linear, Split, Film
    val isPip: Boolean = false, // Picture in Picture
    val chromaKeyEnabled: Boolean = false,
    val chromaKeyColorHex: String = "#00FF00", // Green screen default
    val motionBlurEnabled: Boolean = false,
    val motionBlurIntensity: Float = 0.5f,
    val stabilizationEnabled: Boolean = false,
    val stabilizationLevel: Float = 0.5f,
    val lutFilter: String = "None", // None, Teal & Orange, Cyberpunk Neon, Vintage 35mm, Monochrome Noir, Cinematic Warm
    val audioWaveform: List<Float> = listOf(0.2f, 0.5f, 0.8f, 0.3f, 0.9f, 0.6f, 0.4f, 0.7f, 0.2f, 0.8f, 0.5f, 0.9f, 0.3f, 0.6f, 0.8f, 0.4f),
    val audioVolume: Float = 1.0f,
    val isNoiseReduced: Boolean = false,
    val isVoiceEnhanced: Boolean = false,
    val audioBeatsMs: List<Long> = emptyList(), // Beat detection timestamp markers
    val transitionIn: String = "None", // None, Crossfade, Dissolve, Wipe Left, Zoom In, Spin Out, Glitch Shift
    val transitionOut: String = "None",
    val activeEffect: String = "None", // None, Glow, Vignette, Chromatic Aberration, VHS Scanline, Bokeh Light, Sharpen
    val isSelected: Boolean = false,
    val cropLeft: Float = 0f,
    val cropTop: Float = 0f,
    val cropRight: Float = 0f,
    val cropBottom: Float = 0f,
    val rotationDegrees: Float = 0f,
    val keyframes: List<Keyframe> = emptyList()
)

data class TimelineTrack(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: TrackType,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false,
    val isSolo: Boolean = false,
    val clips: List<TimelineClip> = emptyList()
)

/**
 * Export Config for 4K/8K 60/120FPS HDR Rendering
 */
data class ExportSettings(
    val resolution: String = "4K UHD (3840x2160)",
    val frameRate: Int = 60,
    val format: String = "MP4 (H.265 / HEVC)",
    val bitRateMbps: Int = 80,
    val isHdrEnabled: Boolean = true,
    val includeAiEnhancement: Boolean = true
)
