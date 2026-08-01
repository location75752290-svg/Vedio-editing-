package com.example.domain.model

enum class SharePlatform(
    val id: String,
    val title: String,
    val iconResName: String,
    val defaultAspectRatio: String,
    val supportedResolutions: List<String>,
    val supportedFramerates: List<String>,
    val packageName: String?
) {
    TIKTOK("tiktok", "TikTok", "ic_tiktok", "9:16", listOf("720p HD", "1080p Full HD"), listOf("30 fps", "60 fps"), "com.zhiliaoapp.musically"),
    YOUTUBE("youtube", "YouTube", "ic_youtube", "16:9", listOf("1080p Full HD", "2K QHD", "4K UHD (2160p)", "8K Ultra HD"), listOf("30 fps", "60 fps"), "com.google.android.youtube"),
    YOUTUBE_SHORTS("youtube_shorts", "YouTube Shorts", "ic_youtube_shorts", "9:16", listOf("1080p Full HD", "2K QHD", "4K UHD (2160p)"), listOf("30 fps", "60 fps"), "com.google.android.youtube"),
    INSTAGRAM_REELS("instagram_reels", "Instagram Reels", "ic_instagram", "9:16", listOf("720p HD", "1080p Full HD"), listOf("30 fps", "60 fps"), "com.instagram.android"),
    INSTAGRAM_STORIES("instagram_stories", "Instagram Stories", "ic_instagram", "9:16", listOf("720p HD", "1080p Full HD"), listOf("30 fps", "60 fps"), "com.instagram.android"),
    FACEBOOK("facebook", "Facebook Feed", "ic_facebook", "16:9", listOf("720p HD", "1080p Full HD", "4K UHD (2160p)"), listOf("30 fps", "60 fps"), "com.facebook.katana"),
    FACEBOOK_REELS("facebook_reels", "Facebook Reels", "ic_facebook", "9:16", listOf("720p HD", "1080p Full HD"), listOf("30 fps", "60 fps"), "com.facebook.katana"),
    WHATSAPP("whatsapp", "WhatsApp Status", "ic_whatsapp", "9:16", listOf("720p HD", "1080p Full HD"), listOf("30 fps", "60 fps"), "com.whatsapp"),
    SNAPCHAT("snapchat", "Snapchat", "ic_snapchat", "9:16", listOf("720p HD", "1080p Full HD"), listOf("30 fps", "60 fps"), "com.snapchat.android"),
    TWITTER("twitter", "X (Twitter)", "ic_twitter", "16:9", listOf("720p HD", "1080p Full HD"), listOf("30 fps", "60 fps"), "com.twitter.android"),
    TELEGRAM("telegram", "Telegram", "ic_telegram", "16:9", listOf("720p HD", "1080p Full HD", "4K UHD (2160p)"), listOf("30 fps", "60 fps", "120 fps"), "org.telegram.messenger"),
    GALLERY("gallery", "Save to Gallery", "ic_download", "Original", listOf("720p HD", "1080p Full HD", "2K QHD", "4K UHD (2160p)", "8K Ultra HD"), listOf("30 fps", "60 fps", "120 fps"), null),
    FILES("files", "Save to Files", "ic_folder", "Original", listOf("720p HD", "1080p Full HD", "2K QHD", "4K UHD (2160p)", "8K Ultra HD"), listOf("30 fps", "60 fps", "120 fps"), null)
}

data class ExportRecord(
    val id: String,
    val timestampMs: Long,
    val title: String,
    val platform: SharePlatform,
    val resolution: String,
    val framerate: String,
    val durationSeconds: Float,
    val fileSizeMb: Float,
    val fileUri: String
)
