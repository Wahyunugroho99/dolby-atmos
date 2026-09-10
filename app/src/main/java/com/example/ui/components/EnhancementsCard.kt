package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DolbyAmberAccent
import com.example.ui.theme.DolbyCyanPrimary
import com.example.ui.theme.DolbyDarkCardBorder
import com.example.ui.theme.DolbyDarkSurface
import com.example.ui.theme.DolbyDarkSurfaceVariant
import com.example.ui.theme.DolbyElectricBlue
import com.example.ui.theme.DolbyInactiveGray
import com.example.ui.theme.DolbyTextPrimary
import com.example.ui.theme.DolbyTextSecondary

@Composable
fun EnhancementsCard(
    surroundVirtualizer: Int,
    onSurroundChanged: (Int) -> Unit,
    dialogueEnhancer: Int,
    onDialogueChanged: (Int) -> Unit,
    bassEnhancer: Int,
    onBassChanged: (Int) -> Unit,
    volumeLeveler: Boolean,
    onVolumeLevelerToggled: (Boolean) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DolbyDarkSurfaceVariant)
            .border(1.dp, DolbyDarkCardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section title
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Equalizer,
                contentDescription = null,
                tint = DolbyCyanPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Acoustic Enhancements",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DolbyTextPrimary
                    )
                )
                Text(
                    text = "Dolby Spatializer, Voice Clarity & Dynamic Bass",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = DolbyTextSecondary
                    )
                )
            }
        }

        // 1. Surround Virtualizer
        EnhancementSliderItem(
            title = stringResource(R.string.surround_virtualizer),
            description = stringResource(R.string.surround_virtualizer_desc),
            icon = Icons.Default.Headphones,
            value = surroundVirtualizer,
            onValueChange = onSurroundChanged,
            isEnabled = isEnabled,
            tint = DolbyCyanPrimary,
            testTagPrefix = "surround"
        )

        // 2. Dialogue Enhancer
        EnhancementSliderItem(
            title = stringResource(R.string.dialogue_enhancer),
            description = stringResource(R.string.dialogue_enhancer_desc),
            icon = Icons.Default.RecordVoiceOver,
            value = dialogueEnhancer,
            onValueChange = onDialogueChanged,
            isEnabled = isEnabled,
            tint = DolbyElectricBlue,
            testTagPrefix = "dialogue"
        )

        // 3. Bass Enhancer
        EnhancementSliderItem(
            title = stringResource(R.string.bass_enhancer),
            description = stringResource(R.string.bass_enhancer_desc),
            icon = Icons.Default.Speaker,
            value = bassEnhancer,
            onValueChange = onBassChanged,
            isEnabled = isEnabled,
            tint = DolbyAmberAccent,
            testTagPrefix = "bass"
        )

        // 4. Volume Leveler Switch
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DolbyDarkSurface.copy(alpha = 0.8f))
                .border(1.dp, DolbyDarkCardBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DolbyCyanPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = if (isEnabled && volumeLeveler) DolbyCyanPrimary else DolbyTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.volume_leveler),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DolbyTextPrimary
                            )
                        )
                        Text(
                            text = stringResource(R.string.volume_leveler_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DolbyTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = volumeLeveler && isEnabled,
                    onCheckedChange = { if (isEnabled) onVolumeLevelerToggled(it) },
                    enabled = isEnabled,
                    modifier = Modifier.testTag("volume_leveler_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DolbyCyanPrimary,
                        checkedTrackColor = DolbyCyanPrimary.copy(alpha = 0.3f),
                        uncheckedThumbColor = DolbyInactiveGray,
                        uncheckedTrackColor = DolbyDarkCardBorder
                    )
                )
            }
        }
    }
}

@Composable
private fun EnhancementSliderItem(
    title: String,
    description: String,
    icon: ImageVector,
    value: Int,
    onValueChange: (Int) -> Unit,
    isEnabled: Boolean,
    tint: androidx.compose.ui.graphics.Color,
    testTagPrefix: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DolbyDarkSurface.copy(alpha = 0.8f))
            .border(1.dp, DolbyDarkCardBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isEnabled) tint else DolbyTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DolbyTextPrimary
                            )
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DolbyTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Percentage Value Badge
                Text(
                    text = "$value%",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isEnabled) tint else DolbyTextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = 0f..100f,
                enabled = isEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("${testTagPrefix}_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = tint,
                    activeTrackColor = tint,
                    inactiveTrackColor = DolbyDarkCardBorder
                )
            )
        }
    }
}
