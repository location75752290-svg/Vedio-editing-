# Add project specific ProGuard rules here.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep Room Database entities and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Keep Moshi and JSON models
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

# Keep Gson models
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName *;
}
-keep class com.google.gson.** { *; }

# Keep ML Kit Selfie Segmentation
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# Keep Media3 ExoPlayer and Transformer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Keep Kotlinx Coroutines and Serialization
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

