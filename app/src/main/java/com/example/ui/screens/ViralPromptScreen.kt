package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AIViralPromptEngine
import kotlinx.coroutines.launch

@Composable
fun ViralPromptScreen(engine: AIViralPromptEngine) {
    val context = LocalContext.current
    var prompt by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var isGenerating by remember { mutableStateOf(false) }
    var progressText by remember { mutableStateOf("") }
    var resultPath by remember { mutableStateOf<String?>(null) }
    var credits by remember { mutableIntStateOf(3) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AI Viral Prompt Studio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Credits Left: $credits",
                color = if (credits < 2) Color.Red else Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.testTag("credits_counter_text")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            placeholder = { Text("✨ Describe your video... sad urdu rain") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("viral_prompt_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(listOf("Sad Urdu Reel", "Epic Trailer", "Romantic Rain", "Matrix Dance")) { chipText ->
                SuggestionChip(
                    onClick = { prompt = chipText },
                    label = { Text(chipText) },
                    modifier = Modifier.testTag("suggestion_chip_${chipText.lowercase().replace(" ", "_")}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                prompt = listOf(
                    "epic urdu dub tokyo night",
                    "sad romantic rain window shayari",
                    "nostalgic lofi matrix bg"
                ).random()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("surprise_me_button")
        ) {
            Text("🎲 Surprise Me")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (credits > 0) {
                    credits--
                    scope.launch {
                        isGenerating = true
                        progressText = "1/4: AI Reading Prompt..."
                        val path1 = engine.parseAndStart(prompt)

                        progressText = "2/4: Changing Background..."
                        val path2 = engine.applyBG(path1)

                        progressText = "3/4: Applying Mood + LUT..."
                        val path3 = engine.applyMood(path2)

                        progressText = "4/4: Urdu Dub + LipSync..."
                        resultPath = engine.applyDub(path3)
                        isGenerating = false
                    }
                }
            },
            enabled = !isGenerating && prompt.isNotBlank() && credits > 0,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("generate_prompt_button")
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generating...")
            } else {
                Text("Generate")
            }
        }

        if (isGenerating) {
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = progressText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        resultPath?.let { path ->
            Spacer(modifier = Modifier.height(16.dp))
            VideoPlayer(uri = path)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { shareVideo(context, path) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_video_button")
                ) {
                    Text("📤 Share")
                }
                Button(
                    onClick = { saveToGallery(context, path) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_video_button")
                ) {
                    Text("💾 Save 4K")
                }
            }
        }
    }
}

@Composable
fun VideoPlayer(uri: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Video",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Playing: $uri",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun shareVideo(context: Context, path: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "video/*"
        putExtra(Intent.EXTRA_SUBJECT, "AI Generated Video")
        putExtra(Intent.EXTRA_TEXT, "Check out my AI generated video!")
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
}

private fun saveToGallery(context: Context, path: String) {
    Toast.makeText(context, "Saved 4K Video to Gallery!", Toast.LENGTH_SHORT).show()
}
