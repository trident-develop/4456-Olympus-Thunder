package com.flipkart.sho.ui.screens.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.flipkart.sho.R
import com.flipkart.sho.data.LeaderboardManager
import com.flipkart.sho.data.SettingsManager
import com.flipkart.sho.game.logic.ChessEngine
import com.flipkart.sho.game.model.ChessPiece
import com.flipkart.sho.game.model.GameState
import com.flipkart.sho.game.model.MatchResult
import com.flipkart.sho.game.model.PieceColor
import com.flipkart.sho.game.model.PieceType
import com.flipkart.sho.game.model.Position
import com.flipkart.sho.ui.components.ChessBackground
import com.flipkart.sho.ui.components.MenuButton
import com.flipkart.sho.ui.components.SquareButton
import com.flipkart.sho.ui.theme.BoardDark
import com.flipkart.sho.ui.theme.BoardLight
import com.flipkart.sho.ui.theme.CaptureCell
import com.flipkart.sho.ui.theme.CheckCell
import com.flipkart.sho.ui.theme.DarkBackground
import com.flipkart.sho.ui.theme.DarkSurface
import com.flipkart.sho.ui.theme.GameFont
import com.flipkart.sho.ui.theme.GoldAccent
import com.flipkart.sho.ui.theme.LastMoveCell
import com.flipkart.sho.ui.theme.SelectedCell
import com.flipkart.sho.ui.theme.ValidMoveCell
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GameScreen(
    player1Name: String,
    player2Name: String,
    player1Color: String,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onReplay: () -> Unit
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val leaderboardManager = remember { LeaderboardManager(context) }
    val engine = remember { ChessEngine() }
    var gameState by remember { mutableStateOf(engine.state) }
    var isPaused by remember { mutableStateOf(false) }
    var showGameOverDialog by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableIntStateOf(0) }
    var isHelpEnabled by remember { mutableStateOf(settingsManager.isHelpEnabled) }
    var resultSaved by remember { mutableStateOf(false) }

    // Lightning animation state
    var lightningCell by remember { mutableStateOf<Position?>(null) }

    val p1IsWhite = player1Color == "white"
    val whiteName = if (p1IsWhite) player1Name else player2Name
    val blackName = if (p1IsWhite) player2Name else player1Name

    fun currentTurnName(): String {
        return if (gameState.currentTurn == PieceColor.WHITE) "$whiteName (White)" else "$blackName (Black)"
    }
    var isExitingScreen by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        isExitingScreen = true
        onBack()
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, engine) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE && !gameState.isGameOver && !isExitingScreen)
                isPaused = true
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    // Timer
    LaunchedEffect(gameState.isGameOver, isPaused) {
        while (!gameState.isGameOver && !isPaused) {
            delay(1000)
            timerSeconds++
        }
    }

    // Show game over dialog
    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) {
            showGameOverDialog = true
            if (!resultSaved) {
                resultSaved = true
                val winnerName = gameState.winner?.let { if (it == PieceColor.WHITE) whiteName else blackName }
                leaderboardManager.saveMatch(
                    MatchResult(
                        player1Name = player1Name,
                        player2Name = player2Name,
                        winnerName = winnerName,
                        isDraw = gameState.isDraw || gameState.isStalemate,
                        durationSeconds = timerSeconds,
                        dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    )
                )
            }
        }
    }

    // Lightning effect
    LaunchedEffect(lightningCell) {
        if (lightningCell != null) {
            delay(400)
            lightningCell = null
        }
    }

    ChessBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(top = 40.dp)
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top control bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SquareButton(btnRes = R.drawable.back_button, btnMaxWidth = 0.12f) {
                    isExitingScreen = true
                    onBack()
                }

                // Timer
                val mins = timerSeconds / 60
                val secs = timerSeconds % 60
                Text(
                    text = "%02d:%02d".format(mins, secs),
                    color = GoldAccent,
                    fontSize = 30.sp,
                    fontFamily = GameFont,
                    fontWeight = FontWeight.Bold
                )

                SquareButton(btnRes = R.drawable.pause_button, btnMaxWidth = 0.19f) { isPaused = true }
            }

            Spacer(Modifier.height(8.dp))

            // Turn indicator
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.85f))
            ) {
                val turnText = when {
                    gameState.isCheckmate -> "Checkmate! ${if (gameState.winner == PieceColor.WHITE) whiteName else blackName} wins!"
                    gameState.isStalemate -> "Stalemate! Draw!"
                    gameState.isDraw -> "Draw by agreement!"
                    gameState.isCheck -> "Check! Turn: ${currentTurnName()}"
                    else -> "Turn: ${currentTurnName()}"
                }
                Text(
                    text = turnText,
                    color = if (gameState.isCheck) Color.Red else Color.White,
                    fontSize = 16.sp,
                    fontFamily = GameFont,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            // White captured pieces (pieces that white lost = captured by black)
            CapturedPiecesRow(
                pieces = gameState.capturedByBlack,
                label = "$whiteName lost:"
            )

            // Chess Board
            ChessBoard(
                gameState = gameState,
                isHelpEnabled = isHelpEnabled,
                lightningCell = lightningCell,
                onCellClick = { row, col ->
                    if (!isPaused && !gameState.isGameOver) {
                        val targetPiece = gameState.board[row][col]
                        val selectedPos = gameState.selectedPosition
                        // Check if this move will capture
                        if (selectedPos != null && Position(row, col) in gameState.validMoves && targetPiece != null) {
                            lightningCell = Position(row, col)
                        }
                        gameState = engine.onCellClick(row, col)
                    }
                }
            )

            // Black captured pieces (pieces that black lost = captured by white)
            CapturedPiecesRow(
                pieces = gameState.capturedByWhite,
                label = "$blackName lost:"
            )

            Spacer(Modifier.height(4.dp))

            // Draw buttons and timer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player 1 draw button
                DrawButton(
                    playerName = if (p1IsWhite) whiteName else blackName,
                    isRequested = if (p1IsWhite) gameState.whiteRequestedDraw else gameState.blackRequestedDraw,
                    enabled = !gameState.isGameOver,
                    onClick = {
                        val color = if (p1IsWhite) PieceColor.WHITE else PieceColor.BLACK
                        gameState = engine.requestDraw(color)
                    },
                    modifier = Modifier.fillMaxWidth(0.31f)
                )

                // Player 2 draw button
                DrawButton(
                    playerName = if (p1IsWhite) blackName else whiteName,
                    isRequested = if (p1IsWhite) gameState.blackRequestedDraw else gameState.whiteRequestedDraw,
                    enabled = !gameState.isGameOver,
                    onClick = {
                        val color = if (p1IsWhite) PieceColor.BLACK else PieceColor.WHITE
                        gameState = engine.requestDraw(color)
                    },
                    modifier = Modifier.fillMaxWidth(0.45f)
                )
            }

            // Promotion dialog
            if (gameState.promotionPending != null) {
                PromotionDialog(
                    onSelect = { type ->
                        gameState = engine.promotePawn(type)
                    }
                )
            }
        }

        // Pause overlay
        if (isPaused) {
            PauseOverlay(
                onResume = { isPaused = false },
                onReplay = {
                    isExitingScreen = true
                    isPaused = false
                    engine.reset()
                    gameState = engine.state
                    timerSeconds = 0
                    resultSaved = false
                },
                onHome = {
                    isExitingScreen = true
                    onHome()
                }
            )
        }

        // Game over overlay
        if (showGameOverDialog && gameState.isGameOver) {
            GameOverOverlay(
                gameState = gameState,
                whiteName = whiteName,
                blackName = blackName,
                onReplay = {
                    isExitingScreen = true
                    showGameOverDialog = false
                    engine.reset()
                    gameState = engine.state
                    timerSeconds = 0
                    resultSaved = false
                },
                onHome = {
                    isExitingScreen = true
                    onHome()
                }
            )
        }
    }
}

@Composable
private fun CapturedPiecesRow(pieces: List<ChessPiece>, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = DarkBackground,
            fontSize = 13.sp,
            fontFamily = GameFont
        )
        Spacer(Modifier.width(4.dp))
        LazyRow {
            items(pieces.sortedByDescending { it.type.value }) { piece ->
                Text(
                    text = piece.symbol(),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun DrawButton(
    modifier: Modifier = Modifier,
    playerName: String,
    isRequested: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(enabled = enabled && !isRequested) { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRequested) GoldAccent.copy(alpha = 0.6f) else DarkSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isRequested) "Draw ✓" else "Draw",
                color = if (isRequested) DarkBackground else Color.White,
                fontSize = 13.sp,
                fontFamily = GameFont
            )
            Text(
                text = playerName,
                color = if (isRequested) DarkBackground.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontFamily = GameFont
            )
        }
    }
}

@Composable
fun ChessBoard(
    gameState: GameState,
    isHelpEnabled: Boolean,
    lightningCell: Position?,
    onCellClick: (Int, Int) -> Unit
) {
    val selectedPulse = rememberInfiniteTransition(label = "sel")
    val selAlpha by selectedPulse.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "selAlpha"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        val boardSize = maxWidth
        val cellSize = boardSize / 8

        Column {
            for (row in 0..7) {
                Row {
                    for (col in 0..7) {
                        val pos = Position(row, col)
                        val isLight = (row + col) % 2 == 0
                        val isSelected = gameState.selectedPosition == pos
                        val isValidMove = isHelpEnabled && pos in gameState.validMoves
                        val isCapture = isValidMove && gameState.board[row][col] != null
                        val isLastMoveFrom = gameState.lastMove?.from == pos
                        val isLastMoveTo = gameState.lastMove?.to == pos
                        val isKingInCheck = gameState.isCheck &&
                                gameState.board[row][col]?.type == PieceType.KING &&
                                gameState.board[row][col]?.color == gameState.currentTurn
                        val isLightning = lightningCell == pos

                        val bgColor = when {
                            isKingInCheck -> CheckCell
                            isSelected -> SelectedCell.copy(alpha = selAlpha)
                            isCapture -> CaptureCell
                            isValidMove -> ValidMoveCell
                            isLastMoveFrom || isLastMoveTo -> LastMoveCell
                            isLight -> BoardLight
                            else -> BoardDark
                        }

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .background(if (isLight) BoardLight else BoardDark)
                                .background(bgColor)
                                .clickable { onCellClick(row, col) },
                            contentAlignment = Alignment.Center
                        ) {
                            val piece = gameState.board[row][col]
                            if (piece != null) {
                                Text(
                                    text = piece.symbol(),
                                    fontSize = (cellSize.value * 0.7).sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Valid move dot
                            if (isValidMove && !isCapture && piece == null) {
                                Canvas(modifier = Modifier.size(cellSize * 0.3f)) {
                                    drawCircle(
                                        color = Color(0x6600AAFF),
                                        radius = size.minDimension / 2
                                    )
                                }
                            }

                            // Lightning effect
                            if (isLightning) {
                                LightningEffect(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LightningEffect(modifier: Modifier = Modifier) {
    val alpha by rememberInfiniteTransition(label = "lightning").animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(200), RepeatMode.Reverse),
        label = "lightAlpha"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2
        val cy = size.height / 2
        // Draw radiating lines for lightning effect
        for (i in 0..7) {
            val angle = Math.toRadians((i * 45.0))
            val endX = cx + (size.width * 0.45f * kotlin.math.cos(angle)).toFloat()
            val endY = cy + (size.height * 0.45f * kotlin.math.sin(angle)).toFloat()
            drawLine(
                color = Color.Yellow.copy(alpha = alpha),
                start = Offset(cx, cy),
                end = Offset(endX, endY),
                strokeWidth = 3f
            )
        }
        drawCircle(
            color = Color.White.copy(alpha = alpha * 0.6f),
            radius = size.minDimension * 0.2f,
            center = Offset(cx, cy)
        )
    }
}

@Composable
fun PromotionDialog(onSelect: (PieceType) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Promote Pawn", color = GoldAccent, fontSize = 22.sp, fontFamily = GameFont)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    for (type in listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)) {
                        Card(
                            modifier = Modifier
                                .size(56.dp)
                                .clickable { onSelect(type) },
                            colors = CardDefaults.cardColors(containerColor = BoardLight)
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = ChessPiece(type, PieceColor.WHITE).symbol(),
                                    fontSize = 32.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PauseOverlay(onResume: () -> Unit, onReplay: () -> Unit, onHome: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .pointerInput(Unit) { detectTapGestures { /* consume */ } },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(0.65f),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(R.drawable.pop_up_1),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Paused", color = GoldAccent, fontSize = 34.sp, fontFamily = GameFont, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(24.dp))
                MenuButton(text = "Resume", maxWidth = 0.75f, onClick = onResume)
                MenuButton(text = "Replay", maxWidth = 0.75f,onClick = onReplay)
                MenuButton(text = "Home", maxWidth = 0.75f,onClick = onHome)
            }
        }
    }
}

@Composable
fun GameOverOverlay(
    gameState: GameState,
    whiteName: String,
    blackName: String,
    onReplay: () -> Unit,
    onHome: () -> Unit
) {
    val resultText = when {
        gameState.isDraw || gameState.isStalemate -> "Draw!"
        gameState.winner == PieceColor.WHITE -> "$whiteName Wins!"
        gameState.winner == PieceColor.BLACK -> "$blackName Wins!"
        else -> "Game Over"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .pointerInput(Unit) { detectTapGestures { /* consume */ } },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(0.65f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.pop_up_1),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("♚", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = resultText,
                    color = GoldAccent,
                    fontSize = 26.sp,
                    fontFamily = GameFont,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (gameState.isCheckmate) {
                    Text("Checkmate!", color = Color.White, fontSize = 16.sp, fontFamily = GameFont)
                }
                Spacer(Modifier.height(24.dp))
                MenuButton(text = "Replay", maxWidth = 0.75f,onClick = onReplay)
                MenuButton(text = "Home", maxWidth = 0.75f,onClick = onHome)
            }
        }
    }
}