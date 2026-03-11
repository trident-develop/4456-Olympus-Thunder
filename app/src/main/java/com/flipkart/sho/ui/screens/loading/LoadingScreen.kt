package com.flipkart.sho.ui.screens.loading

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipkart.sho.ui.components.ChessBackground
import com.flipkart.sho.ui.theme.GameFont
import com.flipkart.sho.ui.theme.GoldAccent
import com.flipkart.sho.ui.theme.LightGold

@Composable
fun LoadingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    BackHandler(enabled = true) {}
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val diagonalSweep by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    val pieceAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2000), RepeatMode.Reverse
        ), label = "alpha1"
    )
    val pieceAlpha2 by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(2500), RepeatMode.Reverse
        ), label = "alpha2"
    )

    ChessBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Animated mini chess board with diagonal glow
            Canvas(
                modifier = Modifier
                    .size(270.dp)
                    .offset(y = (-40).dp)
            ) {
                val cellSize = size.width / 4f
                for (row in 0..3) {
                    for (col in 0..3) {
                        val isLight = (row + col) % 2 == 0
                        val distFromSweep = kotlin.math.abs((row + col) - diagonalSweep)
                        val glow = (1f - (distFromSweep / 2f).coerceIn(0f, 1f)) * 0.4f

                        val baseColor = if (isLight) Color(0xFFF0D9B5) else Color(0xFFB58863)
                        val glowColor = Color(
                            red = (baseColor.red + glow).coerceAtMost(1f),
                            green = (baseColor.green + glow * 0.8f).coerceAtMost(1f),
                            blue = (baseColor.blue + glow * 0.3f).coerceAtMost(1f),
                            alpha = 0.85f
                        )

                        drawRect(
                            color = glowColor,
                            topLeft = Offset(col * cellSize, row * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                }
            }

            // Floating chess pieces text
            Text(
                text = "♔",
                fontSize = 48.sp,
                color = GoldAccent.copy(alpha = pieceAlpha1 * pulse),
                modifier = Modifier.offset(x = (-60).dp, y = (-120).dp)
            )
            Text(
                text = "♛",
                fontSize = 44.sp,
                color = Color.White.copy(alpha = pieceAlpha2 * pulse),
                modifier = Modifier.offset(x = 60.dp, y = (-110).dp)
            )
            Text(
                text = "♞",
                fontSize = 40.sp,
                color = GoldAccent.copy(alpha = pieceAlpha2),
                modifier = Modifier.offset(x = (-50).dp, y = 100.dp)
            )

            // Spinner
            Canvas(
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = 140.dp)
            ) {
                rotate(rotation) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(Color.Transparent, GoldAccent, LightGold)
                        ),
                        startAngle = 0f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6.dp.toPx())
                    )
                }
            }

            // Title
            Text(
                text = "Olympus Thunder",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = GameFont,
                color = GoldAccent,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = (-240).dp)
            )

            Text(
                text = "Loading...",
                fontSize = 26.sp,
                fontFamily = GameFont,
                color = GoldAccent.copy(alpha = pulse),
                modifier = Modifier.offset(y = 195.dp)
            )
        }
    }
}