package com.example.data

data class StickerItem(
    val id: String,
    val emoji: String,
    val name: String,
    val category: String
)

object StickersRepository {
    val categories = listOf("All", "Trending", "Aesthetic", "Mood & Love", "Badges", "Cool & Swag")

    val stickers: List<StickerItem> = listOf(
        // Trending
        StickerItem("s1", "🔥", "Fire", "Trending"),
        StickerItem("s2", "⚡", "Lightning", "Trending"),
        StickerItem("s3", "✨", "Sparkles", "Trending"),
        StickerItem("s4", "💫", "Dizzy Star", "Trending"),
        StickerItem("s5", "🌟", "Glowing Star", "Trending"),
        StickerItem("s6", "💎", "Diamond", "Trending"),
        StickerItem("s7", "🚀", "Rocket", "Trending"),
        StickerItem("s8", "🎯", "Bullseye", "Trending"),

        // Aesthetic
        StickerItem("s9", "🦋", "Butterfly", "Aesthetic"),
        StickerItem("s10", "🌹", "Red Rose", "Aesthetic"),
        StickerItem("s11", "🌸", "Cherry Blossom", "Aesthetic"),
        StickerItem("s12", "🌙", "Crescent Moon", "Aesthetic"),
        StickerItem("s13", "🌺", "Hibiscus", "Aesthetic"),
        StickerItem("s14", "🕊️", "Dove", "Aesthetic"),
        StickerItem("s15", "🌿", "Herb Leaf", "Aesthetic"),
        StickerItem("s16", "🕯️", "Candle", "Aesthetic"),

        // Mood & Love
        StickerItem("s17", "❤️", "Red Heart", "Mood & Love"),
        StickerItem("s18", "🖤", "Black Heart", "Mood & Love"),
        StickerItem("s19", "💖", "Sparkling Heart", "Mood & Love"),
        StickerItem("s20", "💔", "Broken Heart", "Mood & Love"),
        StickerItem("s21", "🥺", "Pleading Eyes", "Mood & Love"),
        StickerItem("s22", "🥀", "Wilted Flower", "Mood & Love"),
        StickerItem("s23", "🕊️", "Peace", "Mood & Love"),
        StickerItem("s24", "💌", "Love Letter", "Mood & Love"),

        // Badges
        StickerItem("s25", "👑", "King Crown", "Badges"),
        StickerItem("s26", "🏆", "Trophy", "Badges"),
        StickerItem("s27", "💯", "100 Points", "Badges"),
        StickerItem("s28", "⭐", "Gold Star", "Badges"),
        StickerItem("s29", "🥇", "First Place", "Badges"),
        StickerItem("s30", "🛡️", "Shield", "Badges"),
        StickerItem("s31", "🎖️", "Medal", "Badges"),
        StickerItem("s32", "🎗️", "Ribbon", "Badges"),

        // Cool & Swag
        StickerItem("s33", "😎", "Sunglasses", "Cool & Swag"),
        StickerItem("s34", "🦁", "Lion King", "Cool & Swag"),
        StickerItem("s35", "🦅", "Eagle", "Cool & Swag"),
        StickerItem("s36", "💸", "Money Wings", "Cool & Swag"),
        StickerItem("s37", "🕶️", "Dark Shades", "Cool & Swag"),
        StickerItem("s38", "⚔️", "Crossed Swords", "Cool & Swag"),
        StickerItem("s39", "🐺", "Wolf Alpha", "Cool & Swag"),
        StickerItem("s40", "⛓️", "Chains", "Cool & Swag")
    )
}
