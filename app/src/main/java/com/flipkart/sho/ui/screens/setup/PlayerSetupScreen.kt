package com.flipkart.sho.ui.screens.setup

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipkart.sho.R
import com.flipkart.sho.ui.components.ChessBackground
import com.flipkart.sho.ui.components.MenuButton
import com.flipkart.sho.ui.components.SquareButton
import com.flipkart.sho.ui.theme.DarkSurface
import com.flipkart.sho.ui.theme.GameFont
import com.flipkart.sho.ui.theme.GoldAccent

@Composable
fun PlayerSetupScreen(
    onBack: () -> Unit,
    onStartGame: (player1Name: String, player2Name: String, player1Color: String) -> Unit
) {
    var player1Name by remember { mutableStateOf("") }
    var player2Name by remember { mutableStateOf("") }
    var player1IsWhite by remember { mutableStateOf(true) }

    ChessBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                SquareButton(
                    btnRes = R.drawable.back_button,
                    btnMaxWidth = 0.12f,
                    btnClickable = onBack
                )
            }

            Image(
                painter = painterResource(R.drawable.player_setup_tittle),
                contentDescription = "Player Setup",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(18.dp))

            // Player 1
            PlayerInput(
                label = "Player 1",
                value = player1Name,
                onValueChange = { player1Name = it },
                colorLabel = if (player1IsWhite) "White ♔" else "Black ♚"
            )

            Spacer(Modifier.height(8.dp))

            // Color swap button
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.Center
            ) {
                MenuButton(
                    text = "Swap Colors",
                    buttonRes = R.drawable.button_blue,
                    maxWidth = 0.7f,
                    cooldown = 0L,
                    fontSize = 23.sp
                ) {
                    player1IsWhite = !player1IsWhite
                }
            }

            Spacer(Modifier.height(8.dp))

            // Player 2
            PlayerInput(
                label = "Player 2",
                value = player2Name,
                onValueChange = { player2Name = it },
                colorLabel = if (player1IsWhite) "Black ♚" else "White ♔"
            )

            Spacer(Modifier.height(4.dp))

            MenuButton(text = "Start", buttonRes = R.drawable.button_green) {
                val p1 = player1Name.ifBlank { "Player 1" }
                val p2 = player2Name.ifBlank { "Player 2" }
                val color = if (player1IsWhite) "white" else "black"
                onStartGame(p1, p2, color)
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun PlayerInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    colorLabel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(0.85f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = GameFont
                )
                Text(
                    text = colorLabel,
                    color = GoldAccent,
                    fontSize = 16.sp,
                    fontFamily = GameFont
                )
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = value,
                onValueChange = { if (it.length <= 10) onValueChange(it) },
                placeholder = {
                    Text(label, color = Color.White.copy(alpha = 0.4f), fontFamily = GameFont)
                },
                textStyle = TextStyle(color = Color.White, fontFamily = GameFont, fontSize = 18.sp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = GoldAccent
                )
            )
        }
    }
}