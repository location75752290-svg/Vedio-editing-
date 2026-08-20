package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurLinear
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.BgRemoverConfig
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BackgroundRemoverPanel(
    config: BgRemoverConfig,
    onConfigChange: (BgRemoverConfig) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val greenAccent = Color(0xFF00E676)
    val cyanAccent = Color(0xFF00E5FF)
    val purpleAccent = Color(0xFFB388FF)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onConfigChange(config.copy(mode = "replace_image", replaceBgUri = uri, enabled = true))
            Toast.makeText(context, "Background image selected", Toast.LENGTH_SHORT).show()
        }
    }

    val solidColorOptions = listOf(
        Pair("Green Screen", "#00FF00"),
        Pair("Red", "#FF334B"),
        Pair("Blue", "#2563EB"),
        Pair("Black", "#000000"),
        Pair("White", "#FFFFFF"),
        Pair("Gold", "#FFD700"),
        Pair("Purple", "#7928CA")
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = CharcoalSurfaceVariant,
        borderColor = greenAccent.copy(alpha = 0.5f),
        borderWidth = 1.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row with 🟢 Green Badge & Toggle Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(greenAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🟢", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI BG Remover & Green Screen",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "MLKit Selfie Segmentation (Offline)",
                            color = greenAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = config.enabled,
                        onCheckedChange = { isEnabled ->
                            onConfigChange(config.copy(enabled = isEnabled))
                            if (isEnabled) {
                                Toast.makeText(context, "Background Removed", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = greenAccent
                        ),
                        modifier = Modifier.testTag("bg_remover_toggle")
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = config.enabled) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {

                    // Processing Status Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CharcoalSurface)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = greenAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Removing BG... 100% Ready",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = if (config.isHighQuality) "High Quality" else "Fast 15fps",
                            color = greenAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quality Mode Selector (Fast 15fps vs High Quality)
                    Text(text = "Processing Mode", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!config.isHighQuality) greenAccent.copy(alpha = 0.25f) else CharcoalSurface)
                                .border(width = if (!config.isHighQuality) 2.dp else 1.dp, color = if (!config.isHighQuality) greenAccent else GlassBorder, shape = RoundedCornerShape(12.dp))
                                .clickable { onConfigChange(config.copy(isHighQuality = false)) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⚡ Low Quality Fast (15fps)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (config.isHighQuality) cyanAccent.copy(alpha = 0.25f) else CharcoalSurface)
                                .border(width = if (config.isHighQuality) 2.dp else 1.dp, color = if (config.isHighQuality) cyanAccent else GlassBorder, shape = RoundedCornerShape(12.dp))
                                .clickable { onConfigChange(config.copy(isHighQuality = true)) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✨ High Quality Slow", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Background Options (Options A - E)
                    Text(text = "Background Options", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    val bgModes = listOf(
                        Triple("green_screen", "🟢 Green Screen", Color(0xFF00FF00)),
                        Triple("solid_color", "🎨 Solid Color", purpleAccent),
                        Triple("blur", "💧 Blur BG", cyanAccent),
                        Triple("replace_image", "🖼️ Replace BG", Color(0xFFFF9100)),
                        Triple("transparent", "🏁 Transparent", Color.White)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bgModes.forEach { (modeKey, label, badgeColor) ->
                            val isSelected = config.mode == modeKey
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) badgeColor.copy(alpha = 0.25f) else CharcoalSurface)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) badgeColor else GlassBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        if (modeKey == "replace_image") {
                                            imagePickerLauncher.launch("image/*")
                                        } else {
                                            onConfigChange(config.copy(mode = modeKey))
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Solid Color Palette options if solid_color mode selected
                    if (config.mode == "solid_color" || config.mode == "green_screen") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            solidColorOptions.forEach { (colorName, hex) ->
                                val colorVal = try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { Color.Green }
                                val isColorSelected = config.colorHex.equals(hex, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(colorVal)
                                        .border(
                                            width = if (isColorSelected) 2.5.dp else 1.dp,
                                            color = if (isColorSelected) Color.White else GlassBorder,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onConfigChange(config.copy(mode = "solid_color", colorHex = hex))
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isColorSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (hex == "#FFFFFF") Color.Black else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Edge Feather Slider (0 - 100)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Edge Feather (${config.featherAmount})", color = TextPrimary, fontSize = 12.sp)
                        Text(text = "Smooth Edges", color = TextMuted, fontSize = 11.sp)
                    }
                    Slider(
                        value = config.featherAmount.toFloat(),
                        onValueChange = { onConfigChange(config.copy(featherAmount = it.toInt())) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = greenAccent,
                            activeTrackColor = greenAccent,
                            inactiveTrackColor = GlassBorder
                        )
                    )

                    // Blur Amount Slider (0 - 50) if Blur mode active
                    if (config.mode == "blur") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Background Blur (${config.blurAmount})", color = TextPrimary, fontSize = 12.sp)
                            Text(text = "Depth Effect", color = TextMuted, fontSize = 11.sp)
                        }
                        Slider(
                            value = config.blurAmount.toFloat(),
                            onValueChange = { onConfigChange(config.copy(blurAmount = it.toInt())) },
                            valueRange = 0f..50f,
                            colors = SliderDefaults.colors(
                                thumbColor = cyanAccent,
                                activeTrackColor = cyanAccent,
                                inactiveTrackColor = GlassBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Split Preview Toggle & Apply Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                onConfigChange(config.copy(showSplitPreview = !config.showSplitPreview))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Compare,
                                contentDescription = null,
                                tint = if (config.showSplitPreview) cyanAccent else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Split Preview",
                                color = if (config.showSplitPreview) cyanAccent else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "Background Removed", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = greenAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
