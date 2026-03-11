package com.flipkart.sho.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipkart.sho.R
import com.flipkart.sho.audio.MusicManager
import com.flipkart.sho.data.SettingsManager
import com.flipkart.sho.ui.components.ChessBackground
import com.flipkart.sho.ui.components.MenuButton
import com.flipkart.sho.ui.components.SquareButton
import com.flipkart.sho.ui.theme.DarkSurface
import com.flipkart.sho.ui.theme.GameFont
import com.flipkart.sho.ui.theme.GoldAccent

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onHowToPlay: () -> Unit,
    onPrivacyPolicy: () -> Unit
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    var musicEnabled by remember { mutableStateOf(settingsManager.isMusicEnabled) }
    var helpEnabled by remember { mutableStateOf(settingsManager.isHelpEnabled) }

    ChessBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(top = 40.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SquareButton(
                    btnRes = R.drawable.back_button,
                    btnMaxWidth = 0.12f,
                    btnClickable = onBack
                )
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.height(8.dp))

            Image(
                painter = painterResource(R.drawable.settings_tittle),
                contentDescription = "Settings",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth(0.5f)
            )

            Spacer(Modifier.height(32.dp))

            // Music toggle
            SettingsToggleRow(
                label = "Music",
                checked = musicEnabled,
                onToggle = {
                    musicEnabled = it
                    settingsManager.isMusicEnabled = it
                    MusicManager.setEnabled(context, it)
                }
            )

            Spacer(Modifier.height(16.dp))

            // Help toggle
            SettingsToggleRow(
                label = "Show Moves",
                checked = helpEnabled,
                onToggle = {
                    helpEnabled = it
                    settingsManager.isHelpEnabled = it
                }
            )

            Spacer(Modifier.height(40.dp))

            MenuButton(
                text = "How To Play",
                maxWidth = 0.7f,
                onClick = onHowToPlay,
                buttonRes = R.drawable.button_blue
            )
            MenuButton(
                text = "Privacy Policy",
                maxWidth = 0.7f,
                onClick = onPrivacyPolicy,
                buttonRes = R.drawable.button_blue
            )

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun SettingsToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(0.85f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 23.sp,
                fontFamily = GameFont
            )
            Switch(
                checked = checked,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = GoldAccent,
                    checkedTrackColor = GoldAccent.copy(alpha = 0.4f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }
    }
}