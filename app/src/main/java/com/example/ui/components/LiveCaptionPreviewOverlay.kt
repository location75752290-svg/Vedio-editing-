package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.CaptionAnimation
import com.example.engine.CaptionFont
import com.example.engine.CaptionItem
import com.example.engine.CaptionStyleConfig
import com.example.engine.WordTimestamp

@Composable
fun LiveCaptionPreviewOverlay(
    captions: List<CaptionItem>,
    currentPlayheadMs: Long,
    style: CaptionStyleConfig,
    showCaptions: Boolean,
    modifier: Modifier = Modifier
) {
    if (!showCaptions || captions.isEmpty()) return

    val activeCaption = captions.firstOrNull { currentPlayheadMs in it.startMs..it.endMs } ?: return

    val alignment = when (style.position) {
        "Top" -> Alignment.TopCenter
        "Center" -> Alignment.Center
        else -> Alignment.BottomCenter
    }

    val topPadding = if (style.position == "Top") 36.dp else 12.dp
    val bottomPadding = if (style.position == "Bottom") 44.dp else 12.dp

    val fontFamily = when (style.font) {
        CaptionFont.BOLD -> FontFamily.Default
        CaptionFont.MONTSERRAT -> FontFamily.SansSerif
        CaptionFont.POPPINS -> FontFamily.Serif
    }

    val fontSize = style.fontSizeSp.sp

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .padding(top = topPadding, bottom = bottomPadding),
        contentAlignment = alignment
    ) {
        // Animation calculations
        when (style.animation) {
            CaptionAnimation.WORD_HIGHLIGHT -> {
                // Karaoke Word-by-Word Highlight using AnnotatedString
                val words = if (activeCaption.words.isNotEmpty()) activeCaption.words else {
                    activeCaption.text.split(" ").map { WordTimestamp(it, activeCaption.startMs, activeCaption.endMs) }
                }

                val annotatedText = buildAnnotatedString {
                    words.forEachIndexed { index, wordItem ->
                        val isCurrent = currentPlayheadMs in wordItem.startMs..wordItem.endMs
                        val spanColor = if (isCurrent) style.highlightColor else style.textColor
                        val spanWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold

                        withStyle(
                            style = SpanStyle(
                                color = spanColor,
                                fontWeight = spanWeight,
                                fontSize = if (isCurrent) (style.fontSizeSp + 2f).sp else fontSize,
                                shadow = Shadow(color = Color.Black, blurRadius = 8f, offset = Offset(0f, 2f))
                            )
                        ) {
                            append(wordItem.word)
                        }

                        if (index < words.size - 1) {
                            append(" ")
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .then(
                            if (style.showBackground) {
                                Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(style.backgroundColor)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            } else Modifier
                        )
                ) {
                    Text(
                        text = annotatedText,
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        lineHeight = (style.fontSizeSp * 1.3f).sp
                    )
                }
            }

            CaptionAnimation.POP_IN -> {
                val enterElapsed = (currentPlayheadMs - activeCaption.startMs).coerceAtLeast(0)
                val scaleProgress = (enterElapsed / 220f).coerceIn(0f, 1f)
                val currentScale = 0.75f + (0.25f * scaleProgress)

                Box(
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = currentScale,
                            scaleY = currentScale
                        )
                        .then(
                            if (style.showBackground) {
                                Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(style.backgroundColor)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            } else Modifier
                        )
                ) {
                    Text(
                        text = activeCaption.text,
                        color = style.highlightColor,
                        fontSize = fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        lineHeight = (style.fontSizeSp * 1.3f).sp,
                        style = TextStyle(
                            shadow = Shadow(color = Color.Black, blurRadius = 10f, offset = Offset(0f, 3f))
                        )
                    )
                }
            }

            CaptionAnimation.TYPEWRITER -> {
                val totalDuration = (activeCaption.endMs - activeCaption.startMs).coerceAtLeast(1)
                val progress = ((currentPlayheadMs - activeCaption.startMs).toFloat() / totalDuration).coerceIn(0f, 1f)
                val visibleChars = (activeCaption.text.length * progress).toInt().coerceIn(1, activeCaption.text.length)
                val visibleString = activeCaption.text.substring(0, visibleChars)

                Box(
                    modifier = Modifier
                        .then(
                            if (style.showBackground) {
                                Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(style.backgroundColor)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            } else Modifier
                        )
                ) {
                    Text(
                        text = visibleString,
                        color = style.highlightColor,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        lineHeight = (style.fontSizeSp * 1.3f).sp,
                        style = TextStyle(
                            shadow = Shadow(color = Color.Black, blurRadius = 8f, offset = Offset(0f, 2f))
                        )
                    )
                }
            }

            CaptionAnimation.CLASSIC -> {
                Box(
                    modifier = Modifier
                        .then(
                            if (style.showBackground) {
                                Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(style.backgroundColor)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            } else Modifier
                        )
                ) {
                    Text(
                        text = activeCaption.text,
                        color = style.textColor,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        lineHeight = (style.fontSizeSp * 1.3f).sp,
                        style = TextStyle(
                            shadow = Shadow(color = Color.Black, blurRadius = 8f, offset = Offset(0f, 2f))
                        )
                    )
                }
            }
        }
    }
}
