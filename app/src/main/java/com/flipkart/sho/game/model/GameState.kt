package com.flipkart.sho.game.model

data class GameState(
    val board: Array<Array<ChessPiece?>> = createInitialBoard(),
    val currentTurn: PieceColor = PieceColor.WHITE,
    val selectedPosition: Position? = null,
    val validMoves: List<Position> = emptyList(),
    val capturedByWhite: List<ChessPiece> = emptyList(), // pieces white captured (black pieces)
    val capturedByBlack: List<ChessPiece> = emptyList(), // pieces black captured (white pieces)
    val isCheck: Boolean = false,
    val isCheckmate: Boolean = false,
    val isStalemate: Boolean = false,
    val isGameOver: Boolean = false,
    val winner: PieceColor? = null,
    val isDraw: Boolean = false,
    val lastMove: Move? = null,
    val whiteRequestedDraw: Boolean = false,
    val blackRequestedDraw: Boolean = false,
    val moveHistory: List<Move> = emptyList(),
    val promotionPending: Position? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameState) return false
        if (!board.contentDeepEquals(other.board)) return false
        return currentTurn == other.currentTurn &&
                selectedPosition == other.selectedPosition &&
                validMoves == other.validMoves &&
                capturedByWhite == other.capturedByWhite &&
                capturedByBlack == other.capturedByBlack &&
                isCheck == other.isCheck &&
                isCheckmate == other.isCheckmate &&
                isStalemate == other.isStalemate &&
                isGameOver == other.isGameOver &&
                winner == other.winner &&
                lastMove == other.lastMove &&
                whiteRequestedDraw == other.whiteRequestedDraw &&
                blackRequestedDraw == other.blackRequestedDraw &&
                promotionPending == other.promotionPending
    }

    override fun hashCode(): Int = board.contentDeepHashCode()
}

/**
 * Board layout: row 0 = top of screen (white pieces), row 7 = bottom (black pieces)
 * White at top, Black at bottom as per user requirement.
 */
fun createInitialBoard(): Array<Array<ChessPiece?>> {
    val board = Array(8) { arrayOfNulls<ChessPiece>(8) }

    // White pieces at top (rows 0-1)
    val whiteBackRow = arrayOf(
        PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
        PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
    )
    for (col in 0..7) {
        board[0][col] = ChessPiece(whiteBackRow[col], PieceColor.WHITE)
        board[1][col] = ChessPiece(PieceType.PAWN, PieceColor.WHITE)
    }

    // Black pieces at bottom (rows 6-7)
    val blackBackRow = arrayOf(
        PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
        PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
    )
    for (col in 0..7) {
        board[6][col] = ChessPiece(PieceType.PAWN, PieceColor.BLACK)
        board[7][col] = ChessPiece(blackBackRow[col], PieceColor.BLACK)
    }

    return board
}
