package com.flipkart.sho.ui.screens.leaderboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipkart.sho.R
import com.flipkart.sho.data.LeaderboardManager
import com.flipkart.sho.game.model.MatchResult
import com.flipkart.sho.ui.components.ChessBackground
import com.flipkart.sho.ui.components.SquareButton
import com.flipkart.sho.ui.theme.DarkSurface
import com.flipkart.sho.ui.theme.GameFont
import com.flipkart.sho.ui.theme.GoldAccent

@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    previewMatches: List<MatchResult>? = null
) {
    val context = LocalContext.current
    val manager = remember { LeaderboardManager(context) }
    val matches = previewMatches ?: remember { manager.getMatches() }

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

            Image(
                painter = painterResource(R.drawable.leaders_tittle),
                contentDescription = "Leaderboard",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth(0.6f)
            )

            Spacer(Modifier.height(16.dp))

            if (matches.isEmpty()) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = "No games played yet",
                    color = GoldAccent,
                    fontSize = 34.sp,
                    fontFamily = GameFont,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = (-100).dp)
                )
                Spacer(Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(matches) { match ->
                        MatchCard(match)
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchCard(match: MatchResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface.copy(alpha = 0.85f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${match.player1Name} vs ${match.player2Name}",
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = GameFont,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            val resultText = if (match.isDraw) {
                "Draw"
            } else {
                "Winner: ${match.winnerName}"
            }

            Text(
                text = resultText,
                color = if (match.isDraw) Color.LightGray else GoldAccent,
                fontSize = 15.sp,
                fontFamily = GameFont
            )

            if (match.durationSeconds > 0) {
                val mins = match.durationSeconds / 60
                val secs = match.durationSeconds % 60
                Text(
                    text = "Duration: ${"%02d:%02d".format(mins, secs)}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontFamily = GameFont
                )
            }
        }
    }
}