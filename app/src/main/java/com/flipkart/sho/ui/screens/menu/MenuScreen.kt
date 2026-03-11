package com.flipkart.sho.ui.screens.menu

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipkart.sho.MainActivity
import com.flipkart.sho.R
import com.flipkart.sho.ui.components.ChessBackground
import com.flipkart.sho.ui.components.MenuButton
import com.flipkart.sho.ui.theme.GameFont
import com.flipkart.sho.ui.theme.GoldAccent

@SuppressLint("ContextCastToActivity")
@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onLeaderboard: () -> Unit,
    onSettings: () -> Unit
) {
    val activity = LocalContext.current as? MainActivity
    val infiniteTransition = rememberInfiniteTransition(label = "menu")

    val float1 by infiniteTransition.animateFloat(
        initialValue = -10f, targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float1"
    )
    val float2 by infiniteTransition.animateFloat(
        initialValue = 8f, targetValue = -8f,
        animationSpec = infiniteRepeatable(tween(3500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float2"
    )
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.15f, targetValue = 0.35f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "alpha"
    )
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 0.97f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "titleScale"
    )

    ChessBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            // Floating background pieces
            Text(
                text = "♔", fontSize = 80.sp,
                color = GoldAccent.copy(alpha = alphaAnim),
                modifier = Modifier
                    .offset(x = 30.dp, y = (120 + float1).dp)
            )
            Text(
                text = "♛", fontSize = 70.sp,
                color = GoldAccent.copy(alpha = alphaAnim * 0.8f),
                modifier = Modifier
                    .offset(x = 280.dp, y = (200 + float2).dp)
            )
            Text(
                text = "♞", fontSize = 60.sp,
                color = GoldAccent.copy(alpha = alphaAnim),
                modifier = Modifier
                    .offset(x = 50.dp, y = (550 + float2).dp)
            )
            Text(
                text = "♜", fontSize = 65.sp,
                color = GoldAccent.copy(alpha = alphaAnim * 0.7f),
                modifier = Modifier
                    .offset(x = 300.dp, y = (480 + float1).dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(0.3f))

                // Title
                Text(
                    text = "♚ Chess ♚",
                    fontSize = 54.sp,
                    fontFamily = GameFont,
                    color = GoldAccent,
                    modifier = Modifier.graphicsLayer {
                        scaleX = titleScale
                        scaleY = titleScale
                    }
                )

                Spacer(Modifier.height(60.dp))

                MenuButton(text = "Play", onClick = onPlay)
                MenuButton(text = "Leaderboard", onClick = onLeaderboard)
                MenuButton(text = "Settings", onClick = onSettings)
                MenuButton(text = "Exit", buttonRes = R.drawable.button_red, onClick = { activity?.finish() })

                Spacer(Modifier.weight(0.4f))
            }
        }
    }
}