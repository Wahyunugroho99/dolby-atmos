package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AudioOutputDevice
import com.example.ui.theme.DolbyActiveGreen
import com.example.ui.theme.DolbyAmberAccent
import com.example.ui.theme.DolbyCyanPrimary
import com.example.ui.theme.DolbyDarkCardBorder
import com.example.ui.theme.DolbyDarkSurfaceElevated
import com.example.ui.theme.DolbyDarkSurfaceVariant
import com.example.ui.theme.DolbyGlowCyan
import com.example.ui.theme.DolbyInactiveGray
import com.example.ui.theme.DolbyTextPrimary
import com.example.ui.theme.DolbyTextSecondary

@Composable
fun DolbyHeader(
    isEnabled: Boolean,
    onTogglePower: (Boolean) -> Unit,
    currentDevice: AudioOutputDevice,
    onDeviceClick: () -> Unit,
    onSessionLinkClick: () -> Unit,
    onAboutClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glowAlpha by animateFloatAsState(
        targetValue = if (isEnabled) 0.8f else 0.1f,
        animationSpec = tween(400),
        label = "glowAlpha"
    )

    val activeColor by animateColorAsState(
        targetValue = if (isEnabled) DolbyCyanPrimary else DolbyInactiveGray,
        animationSpec = tween(300),
        label = "activeColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DolbyDarkSurfaceElevated,
                        DolbyDarkSurfaceVariant
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        if (isEnabled) DolbyCyanPrimary.copy(alpha = 0.5f) else DolbyDarkCardBorder,
                        if (isEnabled) DolbyAmberAccent.copy(alpha = 0.3f) else DolbyDarkCardBorder
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dolby Logo & Brand Title
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Double-D Dolby stylized emblem
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(
                                elevation = if (isEnabled) 12.dp else 0.dp,
                                shape = RoundedCornerShape(12.dp),
                                ambientColor = DolbyGlowCyan,
                                spotColor = DolbyCyanPrimary
                            )
                            .background(
                                color = if (isEnabled) DolbyDarkSurfaceElevated else DolbyDarkSurfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.5.dp,
                                color = activeColor,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "D",
                                color = activeColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = "D",
                                color = activeColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.dolby_atmos_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp,
                                color = DolbyTextPrimary
                            )
                        )
                        Text(
                            text = stringResource(R.string.dolby_magic_revision),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isEnabled) DolbyCyanPrimary else DolbyTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Power Toggle Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = if (isEnabled) DolbyActiveGreen else DolbyInactiveGray,
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = onTogglePower,
                        modifier = Modifier.testTag("power_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DolbyCyanPrimary,
                            checkedTrackColor = DolbyCyanPrimary.copy(alpha = 0.3f),
                            uncheckedThumbColor = DolbyInactiveGray,
                            uncheckedTrackColor = DolbyDarkCardBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-row: Output device pill & Quick Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Device badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DolbyDarkCardBorder.copy(alpha = 0.5f))
                        .border(
                            width = 1.dp,
                            color = DolbyDarkCardBorder,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(onClick = onDeviceClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("output_device_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = currentDevice.icon,
                            contentDescription = stringResource(currentDevice.titleRes),
                            tint = activeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(currentDevice.titleRes),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = DolbyTextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onSessionLinkClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("session_link_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = stringResource(R.string.session_control),
                            tint = DolbyCyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onResetClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.reset_defaults),
                            tint = DolbyTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onAboutClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("about_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.about_rootless_dolby),
                            tint = DolbyTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
