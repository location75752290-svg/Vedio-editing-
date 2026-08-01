package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.RadiantPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class VideoTemplateItem(
    val id: String,
    val title: String,
    val category: String,
    val duration: String,
    val ratio: String,
    val likes: String,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    onUseTemplate: (VideoTemplateItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Trending") }
    var selectedTemplateForPreview by remember { mutableStateOf<VideoTemplateItem?>(null) }

    val categories = listOf(
        "Trending", "YouTube", "TikTok", "Instagram",
        "Business", "Gaming", "Cinematic", "Wedding"
    )

    val allTemplates = listOf(
        VideoTemplateItem(
            id = "1",
            title = "Cyberpunk Neo Intro",
            category = "Cinematic",
            duration = "00:15",
            ratio = "9:16",
            likes = "142K",
            imageRes = R.drawable.thumb_cyberpunk_1785491147932
        ),
        VideoTemplateItem(
            id = "2",
            title = "Sunset Vlog Beat Sync",
            category = "Trending",
            duration = "00:28",
            ratio = "9:16",
            likes = "98K",
            imageRes = R.drawable.thumb_sunset_1785491163192
        ),
        VideoTemplateItem(
            id = "3",
            title = "Tech Product Unboxing",
            category = "YouTube",
            duration = "01:00",
            ratio = "16:9",
            likes = "65K",
            imageRes = R.drawable.thumb_cyberpunk_1785491147932
        ),
        VideoTemplateItem(
            id = "4",
            title = "Fast Cuts Reel Promo",
            category = "TikTok",
            duration = "00:12",
            ratio = "9:16",
            likes = "210K",
            imageRes = R.drawable.thumb_sunset_1785491163192
        ),
        VideoTemplateItem(
            id = "5",
            title = "Gaming Highlight Montage",
            category = "Gaming",
            duration = "00:45",
            ratio = "16:9",
            likes = "88K",
            imageRes = R.drawable.thumb_cyberpunk_1785491147932
        ),
        VideoTemplateItem(
            id = "6",
            title = "Wedding Romance Memories",
            category = "Wedding",
            duration = "01:30",
            ratio = "16:9",
            likes = "45K",
            imageRes = R.drawable.thumb_sunset_1785491163192
        )
    )

    val filteredTemplates = if (selectedCategory == "Trending") {
        allTemplates
    } else {
        allTemplates.filter { it.category.equals(selectedCategory, ignoreCase = true) }.ifEmpty { allTemplates }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
            .padding(bottom = 90.dp)
    ) {
        // Screen Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Video Templates",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "AI-crafted templates ready for instant export",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(CharcoalSurface)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Templates",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Category Pills Filter Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                Box(
                    modifier = Modifier
                        .testTag("template_cat_$category")
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(listOf(ElectricBlue, RadiantPink))
                            } else {
                                Brush.horizontalGradient(listOf(CharcoalSurface, CharcoalSurface))
                            }
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // 2-Column Templates Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredTemplates) { template ->
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedTemplateForPreview = template },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            Image(
                                painter = painterResource(id = template.imageRes),
                                contentDescription = template.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Aspect Ratio Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.75f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = template.ratio,
                                    color = ElectricBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Play Button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(CharcoalSurface.copy(alpha = 0.85f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Likes Pill
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.75f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = RadiantPink,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = " ${template.likes}",
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = template.title,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = template.category,
                                    color = NeonIndigo,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = template.duration,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet Preview for Template
    selectedTemplateForPreview?.let { template ->
        ModalBottomSheet(
            onDismissRequest = { selectedTemplateForPreview = null },
            sheetState = rememberModalBottomSheetState(),
            containerColor = CharcoalSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Image(
                        painter = painterResource(id = template.imageRes),
                        contentDescription = template.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = template.title,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${template.category} • ${template.duration} • ${template.ratio} • ${template.likes} Uses",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                GradientButton(
                    text = "Use This Template",
                    onClick = {
                        onUseTemplate(template)
                        selectedTemplateForPreview = null
                    },
                    modifier = Modifier
                        .testTag("template_use_button")
                        .fillMaxWidth(),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
