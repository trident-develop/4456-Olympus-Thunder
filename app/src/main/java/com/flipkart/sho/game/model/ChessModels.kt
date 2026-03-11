package com.flipkart.sho.game.model

enum class PieceType(val symbol: String, val value: Int) {
    PAWN("♟", 1),
    ROOK("♜", 5),
    KNIGHT("♞", 3),
    BISHOP("♝", 3),
    QUEEN("♛", 9),
    KING("♚", 0)
}

enum class PieceColor {
    WHITE, BLACK;

    fun opposite(): PieceColor = if (this == WHITE) BLACK else WHITE
}

data class ChessPiece(
    val type: PieceType,
    val color: PieceColor,
    val hasMoved: Boolean = false
) {
    fun symbol(): String {
        return when (color) {
            PieceColor.WHITE -> when (type) {
                PieceType.KING -> "♔"
                PieceType.QUEEN -> "♕"
                PieceType.ROOK -> "♖"
                PieceType.BISHOP -> "♗"
                PieceType.KNIGHT -> "♘"
                PieceType.PAWN -> "♙"
            }
            PieceColor.BLACK -> when (type) {
                PieceType.KING -> "♚"
                PieceType.QUEEN -> "♛"
                PieceType.ROOK -> "♜"
                PieceType.BISHOP -> "♝"
                PieceType.KNIGHT -> "♞"
                PieceType.PAWN -> "♟"
            }
        }
    }
}

data class Position(val row: Int, val col: Int) {
    fun isValid(): Boolean = row in 0..7 && col in 0..7
}

data class Move(
    val from: Position,
    val to: Position,
    val capturedPiece: ChessPiece? = null,
    val isPromotion: Boolean = false,
    val isCastling: Boolean = false
)

data class MatchResult(
    val id: Long = System.currentTimeMillis(),
    val player1Name: String,
    val player2Name: String,
    val winnerName: String? = null,
    val isDraw: Boolean = false,
    val durationSeconds: Int = 0,
    val dateTime: String = ""
)
