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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.DolbyCyanPrimary
import com.example.ui.theme.DolbyDarkCardBorder
import com.example.ui.theme.DolbyDarkSurface
import com.example.ui.theme.DolbyDarkSurfaceElevated
import com.example.ui.theme.DolbyDarkSurfaceVariant
import com.example.ui.theme.DolbyTextPrimary
import com.example.ui.theme.DolbyTextSecondary

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Emblem
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DolbyDarkSurfaceElevated)
                        .border(1.5.dp, DolbyCyanPrimary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "D",
                            color = DolbyCyanPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = "D",
                            color = DolbyCyanPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.about_rootless_dolby),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DolbyTextPrimary
                    )
                )

                Text(
                    text = "MagicRevision DSP Engine v2.5",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = DolbyCyanPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DolbyDarkSurface)
                        .border(1.dp, DolbyDarkCardBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.about_dialog_content),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DolbyTextSecondary,
                            lineHeight = 18.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

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
