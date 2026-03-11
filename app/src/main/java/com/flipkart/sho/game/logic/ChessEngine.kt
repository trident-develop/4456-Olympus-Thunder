package com.flipkart.sho.game.logic

import com.flipkart.sho.game.model.*

class ChessEngine {

    var state: GameState = GameState()
        private set

    fun reset() {
        state = GameState()
    }

    fun onCellClick(row: Int, col: Int): GameState {
        if (state.isGameOver || state.promotionPending != null) return state

        val pos = Position(row, col)
        val clickedPiece = state.board[row][col]
        val selected = state.selectedPosition

        if (selected == null) {
            if (clickedPiece != null && clickedPiece.color == state.currentTurn) {
                val valid = MoveValidator.getValidMoves(state.board, pos)
                state = state.copy(selectedPosition = pos, validMoves = valid)
            }
        } else {
            if (clickedPiece != null && clickedPiece.color == state.currentTurn && pos != selected) {
                val valid = MoveValidator.getValidMoves(state.board, pos)
                state = state.copy(selectedPosition = pos, validMoves = valid)
            } else if (pos in state.validMoves) {
                executeMove(selected, pos)
            } else {
                state = state.copy(selectedPosition = null, validMoves = emptyList())
            }
        }
        return state
    }

    private fun executeMove(from: Position, to: Position) {
        val newBoard = state.board.map { it.copyOf() }.toTypedArray()
        val piece = newBoard[from.row][from.col] ?: return
        val captured = newBoard[to.row][to.col]
        val movedPiece = piece.copy(hasMoved = true)

        // Handle castling
        if (piece.type == PieceType.KING && kotlin.math.abs(to.col - from.col) == 2) {
            val isKingside = to.col > from.col
            val rookFromCol = if (isKingside) 7 else 0
            val rookToCol = if (isKingside) 5 else 3
            val rook = newBoard[from.row][rookFromCol]
            newBoard[from.row][rookFromCol] = null
            newBoard[from.row][rookToCol] = rook?.copy(hasMoved = true)
        }

        newBoard[to.row][to.col] = movedPiece
        newBoard[from.row][from.col] = null

        val promotionRow = if (piece.color == PieceColor.WHITE) 7 else 0
        val isPromotion = piece.type == PieceType.PAWN && to.row == promotionRow

        val newCapturedByWhite = if (captured != null && piece.color == PieceColor.WHITE)
            state.capturedByWhite + captured else state.capturedByWhite
        val newCapturedByBlack = if (captured != null && piece.color == PieceColor.BLACK)
            state.capturedByBlack + captured else state.capturedByBlack

        val move = Move(from, to, captured, isPromotion)
        val nextTurn = state.currentTurn.opposite()

        if (isPromotion) {
            state = state.copy(
                board = newBoard,
                selectedPosition = null,
                validMoves = emptyList(),
                capturedByWhite = newCapturedByWhite,
                capturedByBlack = newCapturedByBlack,
                lastMove = move,
                moveHistory = state.moveHistory + move,
                promotionPending = to,
                whiteRequestedDraw = false,
                blackRequestedDraw = false
            )
            return
        }

        val inCheck = MoveValidator.isInCheck(newBoard, nextTurn)
        val checkmate = MoveValidator.isCheckmate(newBoard, nextTurn)
        val stalemate = MoveValidator.isStalemate(newBoard, nextTurn)

        state = state.copy(
            board = newBoard,
            currentTurn = nextTurn,
            selectedPosition = null,
            validMoves = emptyList(),
            capturedByWhite = newCapturedByWhite,
            capturedByBlack = newCapturedByBlack,
            isCheck = inCheck,
            isCheckmate = checkmate,
            isStalemate = stalemate,
            isGameOver = checkmate || stalemate,
            isDraw = stalemate,
            winner = if (checkmate) state.currentTurn else null,
            lastMove = move,
            moveHistory = state.moveHistory + move,
            whiteRequestedDraw = false,
            blackRequestedDraw = false
        )
    }

    fun promotePawn(pieceType: PieceType): GameState {
        val pos = state.promotionPending ?: return state
        val newBoard = state.board.map { it.copyOf() }.toTypedArray()
        val oldPiece = newBoard[pos.row][pos.col] ?: return state
        newBoard[pos.row][pos.col] = ChessPiece(pieceType, oldPiece.color, hasMoved = true)

        val nextTurn = state.currentTurn.opposite()
        val inCheck = MoveValidator.isInCheck(newBoard, nextTurn)
        val checkmate = MoveValidator.isCheckmate(newBoard, nextTurn)
        val stalemate = MoveValidator.isStalemate(newBoard, nextTurn)

        state = state.copy(
            board = newBoard,
            currentTurn = nextTurn,
            promotionPending = null,
            isCheck = inCheck,
            isCheckmate = checkmate,
            isStalemate = stalemate,
            isGameOver = checkmate || stalemate,
            isDraw = stalemate,
            winner = if (checkmate) state.currentTurn else null
        )
        return state
    }

    fun requestDraw(color: PieceColor): GameState {
        state = if (color == PieceColor.WHITE) {
            if (state.blackRequestedDraw) {
                state.copy(isGameOver = true, isDraw = true, whiteRequestedDraw = true)
            } else {
                state.copy(whiteRequestedDraw = true)
            }
        } else {
            if (state.whiteRequestedDraw) {
                state.copy(isGameOver = true, isDraw = true, blackRequestedDraw = true)
            } else {
                state.copy(blackRequestedDraw = true)
            }
        }
        return state
    }
}
