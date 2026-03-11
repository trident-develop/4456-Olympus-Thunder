package com.flipkart.sho.navigation

object Routes {
    const val MENU = "menu"
    const val LEADERBOARD = "leaderboard"
    const val SETTINGS = "settings"
    const val PLAYER_SETUP = "player_setup"
    const val GAME = "game/{player1Name}/{player2Name}/{player1Color}"
    const val HOW_TO_PLAY = "how_to_play"
    const val PRIVACY_POLICY = "privacy_policy"
    const val CONNECT = "connect"
    const val LOADING = "loading"

    fun game(player1Name: String, player2Name: String, player1Color: String): String {
        return "game/$player1Name/$player2Name/$player1Color"
    }
}
