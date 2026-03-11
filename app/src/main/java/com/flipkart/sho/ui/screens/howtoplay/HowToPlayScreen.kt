package com.flipkart.sho.ui.screens.howtoplay

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.flipkart.sho.R
import com.flipkart.sho.ui.components.ChessBackground
import com.flipkart.sho.ui.components.SquareButton
import com.flipkart.sho.ui.theme.DarkSurface
import com.flipkart.sho.ui.theme.GameFont
import com.flipkart.sho.ui.theme.GoldAccent
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun HowToPlayScreen(onBack: () -> Unit) {
    val isInPreview = LocalInspectionMode.current
    Box(modifier = Modifier.fillMaxSize()) {
        ChessBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(top = 40.dp, bottom = 16.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SquareButton(
                        btnRes = R.drawable.back_button,
                        btnMaxWidth = 0.12f,
                        btnClickable = onBack
                    )
                }

                Spacer(Modifier.height(8.dp))

                Image(
                    painter = painterResource(R.drawable.how_to_play_tittle),
                    contentDescription = "How To Play",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.85f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Section(
                            "Goal",
                            "Checkmate your opponent's King — put it under attack with no escape."
                        )
                        Section(
                            "♙ Pawn",
                            "Moves forward 1 square (2 from start). Captures diagonally. Promotes to any piece upon reaching the last rank."
                        )
                        Section("♖ Rook", "Moves any number of squares horizontally or vertically.")
                        Section(
                            "♘ Knight",
                            "Moves in an L-shape: 2 squares in one direction and 1 perpendicular. Jumps over pieces."
                        )
                        Section("♗ Bishop", "Moves any number of squares diagonally.")
                        Section(
                            "♕ Queen",
                            "Combines Rook and Bishop — moves any number of squares in any direction."
                        )
                        Section(
                            "♔ King",
                            "Moves 1 square in any direction. Can castle with a Rook if neither has moved."
                        )
                        Section(
                            "Check & Checkmate",
                            "When a King is under attack, it's in Check. If no legal move can escape Check, it's Checkmate — game over."
                        )
                        Section(
                            "Draw",
                            "A game is drawn by stalemate (no legal moves, not in check) or by mutual agreement using the Draw buttons."
                        )
                        Section(
                            "Help Mode",
                            "Enable Help in Settings to see highlighted valid moves when selecting a piece. Blue = move, Red = capture."
                        )
                        Spacer(modifier = Modifier.height(44.dp))
                    }
                }
            }

            if (!isInPreview) {
                AndroidView(
                    factory = {
                        val adView = AdView(it)
                        adView.setAdSize(AdSize.BANNER)
                        adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
                        adView.loadAd(AdRequest.Builder().build())
                        adView
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun Section(title: String, body: String) {
    Text(
        text = title,
        color = GoldAccent,
        fontSize = 18.sp,
        fontFamily = GameFont,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = body,
        color = Color.White.copy(alpha = 0.9f),
        fontSize = 14.sp,
        fontFamily = GameFont,
        lineHeight = 20.sp
    )
    Spacer(Modifier.height(16.dp))
}