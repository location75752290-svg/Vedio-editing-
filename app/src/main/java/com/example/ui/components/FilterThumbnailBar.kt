package com.example.ui.components

import android.graphics.Bitmap
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.VisionCutFilterEngine
import com.example.engine.VisionCutFilterSpec
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextMuted

/**
 * LRU Cache for downscaled filter preview thumbnails.
 */
object FilterThumbnailCache {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8
    val lruCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }
}

/**
 * FilterThumbnailBar Composable
 *
 * LazyRow of 15 thumbnails rendering the user's uploaded photo with ColorMatrix applied.
 */
@Composable
fun FilterThumbnailBar(
    sourceBitmap: Bitmap?,
    selectedFilterId: String,
    onFilterSelected: (VisionCutFilterSpec) -> Unit,
    modifier: Modifier = Modifier,
    thumbnailSize: Dp = 80.dp,
    activeBorderColor: Color = ElectricBlue,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
) {
    val filters = VisionCutFilterEngine.ALL_15_FILTERS

    // Cache or scale the base thumbnail
    val downscaledBaseBitmap = remember(sourceBitmap) {
        sourceBitmap?.let { bmp ->
            val cacheKey = "base_${bmp.hashCode()}_${bmp.width}x${bmp.height}"
            FilterThumbnailCache.lruCache.get(cacheKey) ?: run {
                try {
                    val maxDim = 180
                    val aspect = bmp.width.toFloat() / bmp.height.toFloat()
                    val (targetW, targetH) = if (aspect >= 1f) {
                        (maxDim * aspect).toInt().coerceAtMost(240) to maxDim
                    } else {
                        maxDim to (maxDim / aspect).toInt().coerceAtMost(240)
                    }
                    val scaled = Bitmap.createScaledBitmap(bmp, targetW.coerceAtLeast(1), targetH.coerceAtLeast(1), true)
                    FilterThumbnailCache.lruCache.put(cacheKey, scaled)
                    scaled
                } catch (e: Exception) {
                    bmp
                }
            }
        }
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filters, key = { it.id }) { filter ->
            val isSelected = selectedFilterId.equals(filter.id, ignoreCase = true) ||
                    selectedFilterId.equals(filter.name, ignoreCase = true)

            FilterThumbnailItem(
                filter = filter,
                baseBitmap = downscaledBaseBitmap,
                isSelected = isSelected,
                thumbnailSize = thumbnailSize,
                activeBorderColor = activeBorderColor,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
private fun FilterThumbnailItem(
    filter: VisionCutFilterSpec,
    baseBitmap: Bitmap?,
    isSelected: Boolean,
    thumbnailSize: Dp,
    activeBorderColor: Color,
    onClick: () -> Unit
) {
    val matrixArray = remember(filter.id) {
        VisionCutFilterEngine.getBlendedMatrix(filter.id, 1.0f)
    }
    val colorMatrix = remember(matrixArray) {
        ColorMatrix(matrixArray)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(thumbnailSize)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(thumbnailSize)
                .clip(RoundedCornerShape(14.dp))
                .background(CharcoalSurfaceVariant)
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) activeBorderColor else GlassBorder,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (baseBitmap != null) {
                Image(
                    bitmap = baseBitmap.asImageBitmap(),
                    contentDescription = filter.name,
                    colorFilter = ColorFilter.colorMatrix(colorMatrix),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF242730))
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(activeBorderColor.copy(alpha = 0.22f))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(activeBorderColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = if (activeBorderColor == Color(0xFFFFD700) || activeBorderColor == Color.White) Color.Black else Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = filter.name,
            color = if (isSelected) activeBorderColor else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
