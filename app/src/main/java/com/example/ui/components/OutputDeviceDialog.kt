package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.AudioOutputDevice
import com.example.ui.theme.DolbyCyanPrimary
import com.example.ui.theme.DolbyDarkCardBorder
import com.example.ui.theme.DolbyDarkSurface
import com.example.ui.theme.DolbyDarkSurfaceElevated
import com.example.ui.theme.DolbyDarkSurfaceVariant
import com.example.ui.theme.DolbyInactiveGray
import com.example.ui.theme.DolbyTextPrimary
import com.example.ui.theme.DolbyTextSecondary

@Composable
fun OutputDeviceDialog(
    currentDevice: AudioOutputDevice,
    autoRouting: Boolean,
    onDeviceSelected: (AudioOutputDevice) -> Unit,
    onAutoRoutingToggled: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, DolbyCyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(22.dp)),
            colors = CardDefaults.cardColors(containerColor = DolbyDarkSurfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.output_device),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DolbyTextPrimary
                        )
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = DolbyTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Auto Routing switch
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DolbyDarkSurface)
                        .border(1.dp, DolbyDarkCardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.auto_detect_routing),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DolbyTextPrimary
                                )
                            )
                            Text(
                                text = stringResource(R.string.auto_detect_routing_desc),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DolbyTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Switch(
                            checked = autoRouting,
                            onCheckedChange = onAutoRoutingToggled,
                            modifier = Modifier.testTag("auto_routing_switch"),
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

                Text(
                    text = "Acoustic Target Device",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DolbyTextPrimary
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // List of Devices
                for (device in AudioOutputDevice.values()) {
                    val isSelected = device == currentDevice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) DolbyCyanPrimary.copy(alpha = 0.15f) else DolbyDarkSurface
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) DolbyCyanPrimary else DolbyDarkCardBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onDeviceSelected(device) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = device.icon,
                                contentDescription = null,
                                tint = if (isSelected) DolbyCyanPrimary else DolbyTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(device.titleRes),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) DolbyCyanPrimary else DolbyTextPrimary
                                )
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = DolbyCyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DolbyCyanPrimary,
                        contentColor = Color.Black
                    )
                ) {
                    Text(stringResource(R.string.close), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
