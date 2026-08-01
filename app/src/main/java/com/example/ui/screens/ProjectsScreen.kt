package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
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

data class UserProjectItem(
    val id: String,
    val title: String,
    val duration: String,
    val resolution: String,
    val status: String,
    val lastEdited: String,
    val sizeMb: String,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    onOpenProject: (UserProjectItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var isGridView by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    var activeMenuProjectId by remember { mutableStateOf<String?>(null) }

    val filters = listOf("All", "Drafts", "Rendered", "Favorites")

    val projectsList = listOf(
        UserProjectItem(
            id = "p1",
            title = "Cyberpunk Neo City Vlog",
            duration = "02:45",
            resolution = "4K 60fps",
            status = "Rendered",
            lastEdited = "Edited 2 hours ago",
            sizeMb = "480 MB",
            imageRes = R.drawable.thumb_cyberpunk_1785491147932
        ),
        UserProjectItem(
            id = "p2",
            title = "Golden Hour Cinematic Reel",
            duration = "00:58",
            resolution = "4K HDR",
            status = "Draft",
            lastEdited = "Edited yesterday",
            sizeMb = "120 MB",
            imageRes = R.drawable.thumb_sunset_1785491163192
        ),
        UserProjectItem(
            id = "p3",
            title = "AI Voiceover Promo Video",
            duration = "01:12",
            resolution = "1080p",
            status = "Rendered",
            lastEdited = "Edited 3 days ago",
            sizeMb = "210 MB",
            imageRes = R.drawable.thumb_cyberpunk_1785491147932
        ),
        UserProjectItem(
            id = "p4",
            title = "Travel Memories Montage",
            duration = "03:10",
            resolution = "4K 60fps",
            status = "Favorites",
            lastEdited = "Edited 5 days ago",
            sizeMb = "850 MB",
            imageRes = R.drawable.thumb_sunset_1785491163192
        )
    )

    val filteredProjects = when (selectedFilter) {
        "Drafts" -> projectsList.filter { it.status == "Draft" }
        "Rendered" -> projectsList.filter { it.status == "Rendered" }
        "Favorites" -> projectsList.filter { it.status == "Favorites" }
        else -> projectsList
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
            .padding(bottom = 90.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "My Projects",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${projectsList.size} Saved Projects • Cloud Sync Enabled",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            // Grid / List Toggle Button
            Box(
                modifier = Modifier
                    .testTag("projects_view_toggle")
                    .clip(CircleShape)
                    .background(CharcoalSurface)
                    .clickable { isGridView = !isGridView }
                    .padding(10.dp)
            ) {
                Icon(
                    imageVector = if (isGridView) Icons.Default.List else Icons.Default.GridView,
                    contentDescription = "Toggle View",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Storage Usage Bar Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "  Cloud Storage",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "14.2 GB / 50 GB Used",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { 14.2f / 50f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ElectricBlue,
                    trackColor = CharcoalSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Pills Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .testTag("projects_filter_$filter")
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(listOf(NeonIndigo, DeepPurple))
                            } else {
                                Brush.horizontalGradient(listOf(CharcoalSurface, CharcoalSurface))
                            }
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Projects List or Grid
        if (isGridView) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProjects) { project ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenProject(project) },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = project.imageRes),
                                    contentDescription = project.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(CharcoalSurface.copy(alpha = 0.85f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = project.title,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = project.lastEdited,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProjects) { project ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenProject(project) },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 100.dp, height = 70.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = project.imageRes),
                                    contentDescription = project.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.8f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = project.duration,
                                        color = TextPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 14.dp)
                            ) {
                                Text(
                                    text = project.title,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NeonIndigo.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = project.resolution,
                                            color = ElectricBlue,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = " • ${project.sizeMb}",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = project.lastEdited,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            // Options Menu Button
                            Box {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = TextMuted,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { activeMenuProjectId = project.id }
                                        .padding(6.dp)
                                )

                                DropdownMenu(
                                    expanded = (activeMenuProjectId == project.id),
                                    onDismissRequest = { activeMenuProjectId = null },
                                    modifier = Modifier.background(CharcoalSurface)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Rename Project", color = TextPrimary) },
                                        leadingIcon = { Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null, tint = ElectricBlue) },
                                        onClick = { activeMenuProjectId = null }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Share Project", color = TextPrimary) },
                                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = RadiantPink) },
                                        onClick = { activeMenuProjectId = null }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Delete", color = RadiantPink) },
                                        leadingIcon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = RadiantPink) },
                                        onClick = { activeMenuProjectId = null }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
