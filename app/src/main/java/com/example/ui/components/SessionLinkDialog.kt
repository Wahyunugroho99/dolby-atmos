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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.DolbyActiveGreen
import com.example.ui.theme.DolbyAmberAccent
import com.example.ui.theme.DolbyCyanPrimary
import com.example.ui.theme.DolbyDarkCardBorder
import com.example.ui.theme.DolbyDarkSurface
import com.example.ui.theme.DolbyDarkSurfaceElevated
import com.example.ui.theme.DolbyDarkSurfaceVariant
import com.example.ui.theme.DolbyElectricBlue
import com.example.ui.theme.DolbyTextPrimary
import com.example.ui.theme.DolbyTextSecondary

@Composable
fun SessionLinkDialog(
    activeSessionId: Int,
    connectedPackageName: String?,
    detectedSessions: List<Pair<Int, String>>,
    onAttachSession: (Int, String?) -> Unit,
    onDetachSession: () -> Unit,
    onDismiss: () -> Unit
) {
    var manualSessionInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, DolbyCyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(22.dp)),
            colors = CardDefaults.cardColors(containerColor = DolbyDarkSurfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DolbyCyanPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = DolbyCyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.session_control),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DolbyTextPrimary
                                )
                            )
                            Text(
                                text = "Rootless Audio Effect Session Link",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DolbyCyanPrimary
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = DolbyTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Explanatory note
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DolbyDarkSurface)
                        .border(1.dp, DolbyDarkCardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.session_control_desc),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DolbyTextSecondary,
                            lineHeight = 16.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Active Session Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DolbyDarkSurfaceElevated)
                        .border(
                            1.dp,
                            if (activeSessionId != 0) DolbyActiveGreen else DolbyDarkCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Current Status",
                            style = MaterialTheme.typography.labelSmall.copy(color = DolbyTextSecondary)
                        )
                        Text(
                            text = if (activeSessionId == 0) {
                                "Global Output (Session 0)"
                            } else {
                                "Session $activeSessionId (${connectedPackageName ?: "Active App"})"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (activeSessionId != 0) DolbyActiveGreen else DolbyCyanPrimary
                            )
                        )
                    }

                    if (activeSessionId != 0) {
                        OutlinedButton(
                            onClick = onDetachSession,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DolbyAmberAccent)
                        ) {
                            Text("Detach", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detected Sessions List
                if (detectedSessions.isNotEmpty()) {
                    Text(
                        text = "Detected Media Players",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DolbyTextPrimary
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    for (session in detectedSessions) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DolbyDarkSurface)
                                .clickable { onAttachSession(session.first, session.second) }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = DolbyCyanPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = session.second,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DolbyTextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Session ID: ${session.first}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = DolbyTextSecondary
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = { onAttachSession(session.first, session.second) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DolbyCyanPrimary)
                            ) {
                                Text("Attach", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Manual Session ID input
                Text(
                    text = "Manual Session ID",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DolbyTextPrimary
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = manualSessionInput,
                        onValueChange = { manualSessionInput = it },
                        placeholder = { Text("e.g. 1045", color = DolbyTextSecondary, fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("manual_session_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DolbyCyanPrimary,
                            unfocusedBorderColor = DolbyDarkCardBorder,
                            focusedTextColor = DolbyTextPrimary,
                            unfocusedTextColor = DolbyTextPrimary
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            val id = manualSessionInput.toIntOrNull()
                            if (id != null) {
                                onAttachSession(id, "Manual Player")
                            }
                        },
                        enabled = manualSessionInput.toIntOrNull() != null,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DolbyCyanPrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("manual_attach_button")
                    ) {
                        Text("Connect", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Global Session Button
                OutlinedButton(
                    onClick = {
                        onAttachSession(0, "Global Output")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DolbyCyanPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DolbyDarkCardBorder)
                ) {
                    Text(stringResource(R.string.global_session))
                }
            }
        }
    }
}
