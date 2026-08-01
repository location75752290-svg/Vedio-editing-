package com.example.ui.theme

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonIndigo,
    onPrimary = TextPrimary,
    primaryContainer = CharcoalSurfaceVariant,
    onPrimaryContainer = TextPrimary,
    secondary = DeepPurple,
    onSecondary = TextPrimary,
    tertiary = RadiantPink,
    onTertiary = TextPrimary,
    background = ObsidianBackground,
    onBackground = TextPrimary,
    surface = CharcoalSurface,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassBorder
)

@Composable
fun VisionCutTheme(
    darkTheme: Boolean = true, // Premium dark theme forced by default
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            var context = view.context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    val window = context.window
                    window.statusBarColor = ObsidianBackground.toArgb()
                    window.navigationBarColor = ObsidianBackground.toArgb()
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                    WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
                    break
                }
                context = context.baseContext
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
