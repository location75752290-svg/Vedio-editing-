package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.AutoCaptionEngine
import com.example.engine.CaptionAnimation
import com.example.engine.CaptionFont
import com.example.engine.CaptionItem
import com.example.engine.CaptionStyleConfig
import com.example.engine.WordTimestamp
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AutoCaptionEngineTest {

    @Test
    fun testFormatToSrt() {
        val captions = listOf(
            CaptionItem(
                id = "1",
                text = "Welcome to VisionCut AI",
                startMs = 0L,
                endMs = 2500L,
                words = listOf(
                    WordTimestamp("Welcome", 0L, 500L),
                    WordTimestamp("to", 500L, 800L),
                    WordTimestamp("VisionCut", 800L, 1800L),
                    WordTimestamp("AI", 1800L, 2500L)
                )
            ),
            CaptionItem(
                id = "2",
                text = "Edit your videos seamlessly",
                startMs = 2600L,
                endMs = 5000L
            )
        )

        val srt = AutoCaptionEngine.formatToSrt(captions)
        assertTrue(srt.contains("1\n00:00:00,000 --> 00:00:02,500\nWelcome to VisionCut AI"))
        assertTrue(srt.contains("2\n00:00:02,600 --> 00:00:05,000\nEdit your videos seamlessly"))
    }

    @Test
    fun testGenerateCaptionsForLanguages() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val englishCaptions = AutoCaptionEngine.generateCaptions(
            context = context,
            videoUri = null,
            durationMs = 10000L,
            language = "English",
            onProgress = {}
        )
        assertTrue(englishCaptions.isNotEmpty())
        assertTrue(englishCaptions.all { it.text.isNotBlank() })

        val urduCaptions = AutoCaptionEngine.generateCaptions(
            context = context,
            videoUri = null,
            durationMs = 10000L,
            language = "Urdu Roman",
            onProgress = {}
        )
        assertTrue(urduCaptions.isNotEmpty())

        val pashtoCaptions = AutoCaptionEngine.generateCaptions(
            context = context,
            videoUri = null,
            durationMs = 10000L,
            language = "Pashto",
            onProgress = {}
        )
        assertTrue(pashtoCaptions.isNotEmpty())
    }

    @Test
    fun testRenderCaptionBitmap() {
        val captions = listOf(
            CaptionItem(
                id = "1",
                text = "Dynamic Caption",
                startMs = 0L,
                endMs = 3000L,
                words = listOf(
                    WordTimestamp("Dynamic", 0L, 1500L),
                    WordTimestamp("Caption", 1500L, 3000L)
                )
            )
        )
        val style = CaptionStyleConfig(
            font = CaptionFont.BOLD,
            animation = CaptionAnimation.WORD_HIGHLIGHT,
            fontSizeSp = 28f
        )

        val bitmap = AutoCaptionEngine.renderCaptionBitmap(
            captions = captions,
            currentMs = 1000L,
            style = style,
            width = 1080,
            height = 1920
        )
        assertNotNull(bitmap)
        assertEquals(1080, bitmap.width)
        assertEquals(1920, bitmap.height)
    }
}
