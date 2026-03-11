package com.flipkart.sho.game.logic

import com.flipkart.sho.game.model.*

object MoveValidator {

    fun getValidMoves(board: Array<Array<ChessPiece?>>, position: Position, checkKingSafety: Boolean = true): List<Position> {
        val piece = board[position.row][position.col] ?: return emptyList()
        val rawMoves = when (piece.type) {
            PieceType.PAWN -> getPawnMoves(board, position, piece)
            PieceType.ROOK -> getRookMoves(board, position, piece)
            PieceType.KNIGHT -> getKnightMoves(board, position, piece)
            PieceType.BISHOP -> getBishopMoves(board, position, piece)
            PieceType.QUEEN -> getQueenMoves(board, position, piece)
            PieceType.KING -> getKingMoves(board, position, piece, includeCastling = checkKingSafety)
        }
        if (!checkKingSafety) return rawMoves
        return rawMoves.filter { target ->
            !wouldBeInCheck(board, position, target, piece.color)
        }
    }

    private fun getPawnMoves(board: Array<Array<ChessPiece?>>, pos: Position, piece: ChessPiece): List<Position> {
        val moves = mutableListOf<Position>()
        // White at top (row 0), moves DOWN. Black at bottom (row 7), moves UP.
        val direction = if (piece.color == PieceColor.WHITE) 1 else -1
        val startRow = if (piece.color == PieceColor.WHITE) 1 else 6

        // Forward one
        val oneStep = Position(pos.row + direction, pos.col)
        if (oneStep.isValid() && board[oneStep.row][oneStep.col] == null) {
            moves.add(oneStep)
            // Forward two from start
            if (pos.row == startRow) {
                val twoStep = Position(pos.row + 2 * direction, pos.col)
                if (twoStep.isValid() && board[twoStep.row][twoStep.col] == null) {
                    moves.add(twoStep)
                }
            }
        }

        // Captures
        for (dc in listOf(-1, 1)) {
            val cap = Position(pos.row + direction, pos.col + dc)
            if (cap.isValid()) {
                val target = board[cap.row][cap.col]
                if (target != null && target.color != piece.color) {
                    moves.add(cap)
                }
            }
        }

        return moves
    }

    private fun getSlidingMoves(
        board: Array<Array<ChessPiece?>>, pos: Position, piece: ChessPiece,
        directions: List<Pair<Int, Int>>
    ): List<Position> {
        val moves = mutableListOf<Position>()
        for ((dr, dc) in directions) {
            var r = pos.row + dr
            var c = pos.col + dc
            while (r in 0..7 && c in 0..7) {
                val target = board[r][c]
                if (target == null) {
                    moves.add(Position(r, c))
                } else {
                    if (target.color != piece.color) moves.add(Position(r, c))
                    break
                }
                r += dr
                c += dc
            }
        }
        return moves
    }

    private fun getRookMoves(board: Array<Array<ChessPiece?>>, pos: Position, piece: ChessPiece): List<Position> {
        return getSlidingMoves(board, pos, piece, listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1)))
    }

    private fun getBishopMoves(board: Array<Array<ChessPiece?>>, pos: Position, piece: ChessPiece): List<Position> {
        return getSlidingMoves(board, pos, piece, listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1)))
    }

    private fun getQueenMoves(board: Array<Array<ChessPiece?>>, pos: Position, piece: ChessPiece): List<Position> {
        return getRookMoves(board, pos, piece) + getBishopMoves(board, pos, piece)
    }

    private fun getKnightMoves(board: Array<Array<ChessPiece?>>, pos: Position, piece: ChessPiece): List<Position> {
        val offsets = listOf(
            Pair(-2, -1), Pair(-2, 1), Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2), Pair(2, -1), Pair(2, 1)
        )
        return offsets.map { (dr, dc) -> Position(pos.row + dr, pos.col + dc) }
            .filter { it.isValid() }
            .filter { board[it.row][it.col]?.color != piece.color }
    }

    private fun getKingMoves(board: Array<Array<ChessPiece?>>, pos: Position, piece: ChessPiece, includeCastling: Boolean = true): List<Position> {
        val moves = mutableListOf<Position>()
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val target = Position(pos.row + dr, pos.col + dc)
                if (target.isValid() && board[target.row][target.col]?.color != piece.color) {
                    moves.add(target)
                }
            }
        }
        // Castling
        if (includeCastling && !piece.hasMoved && !isSquareAttacked(board, pos, piece.color.opposite())) {
            // King-side
            if (canCastle(board, pos, piece, kingside = true)) {
                moves.add(Position(pos.row, pos.col + 2))
            }
            // Queen-side
            if (canCastle(board, pos, piece, kingside = false)) {
                moves.add(Position(pos.row, pos.col - 2))
            }
        }
        return moves
    }

    private fun canCastle(board: Array<Array<ChessPiece?>>, kingPos: Position, king: ChessPiece, kingside: Boolean): Boolean {
        val rookCol = if (kingside) 7 else 0
        val rook = board[kingPos.row][rookCol]
        if (rook == null || rook.type != PieceType.ROOK || rook.color != king.color || rook.hasMoved) return false

        val step = if (kingside) 1 else -1
        val endCol = if (kingside) 6 else 1
        var col = kingPos.col + step
        val checkUntil = if (kingside) 6 else 2
        while (col != endCol + step) {
            if (board[kingPos.row][col] != null) return false
            col += step
        }
        // Check that king doesn't pass through attacked squares
        for (c in listOf(kingPos.col + step, kingPos.col + 2 * step)) {
            if (isSquareAttacked(board, Position(kingPos.row, c), king.color.opposite())) return false
        }
        return true
    }

    fun isSquareAttacked(board: Array<Array<ChessPiece?>>, pos: Position, byColor: PieceColor): Boolean {
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board[r][c]
                if (piece != null && piece.color == byColor) {
                    val moves = getValidMoves(board, Position(r, c), checkKingSafety = false)
                    if (pos in moves) return true
                }
            }
        }
        return false
    }

    fun isInCheck(board: Array<Array<ChessPiece?>>, color: PieceColor): Boolean {
        val kingPos = findKing(board, color) ?: return false
        return isSquareAttacked(board, kingPos, color.opposite())
    }

    fun isCheckmate(board: Array<Array<ChessPiece?>>, color: PieceColor): Boolean {
        if (!isInCheck(board, color)) return false
        return !hasAnyLegalMove(board, color)
    }

    fun isStalemate(board: Array<Array<ChessPiece?>>, color: PieceColor): Boolean {
        if (isInCheck(board, color)) return false
        return !hasAnyLegalMove(board, color)
    }

    private fun hasAnyLegalMove(board: Array<Array<ChessPiece?>>, color: PieceColor): Boolean {
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board[r][c]
                if (piece != null && piece.color == color) {
                    if (getValidMoves(board, Position(r, c), checkKingSafety = true).isNotEmpty()) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun findKing(board: Array<Array<ChessPiece?>>, color: PieceColor): Position? {
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board[r][c]
                if (p != null && p.type == PieceType.KING && p.color == color) {
                    return Position(r, c)
                }
            }
        }
        return null
    }

    private fun wouldBeInCheck(board: Array<Array<ChessPiece?>>, from: Position, to: Position, color: PieceColor): Boolean {
        val newBoard = board.map { it.copyOf() }.toTypedArray()
        val piece = newBoard[from.row][from.col]
        newBoard[to.row][to.col] = piece?.copy(hasMoved = true)
        newBoard[from.row][from.col] = null
        return isInCheck(newBoard, color)
    }
}
