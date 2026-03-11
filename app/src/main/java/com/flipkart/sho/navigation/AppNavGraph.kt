package com.flipkart.sho.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipkart.sho.ui.screens.game.GameScreen
import com.flipkart.sho.ui.screens.howtoplay.HowToPlayScreen
import com.flipkart.sho.ui.screens.leaderboard.LeaderboardScreen
import com.flipkart.sho.ui.screens.menu.MenuScreen
import com.flipkart.sho.ui.screens.privacy.PrivacyPolicyScreen
import com.flipkart.sho.ui.screens.settings.SettingsScreen
import com.flipkart.sho.ui.screens.setup.PlayerSetupScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.MENU) {
        composable(Routes.MENU) {
            MenuScreen(
                onPlay = { navController.navigate(Routes.PLAYER_SETUP) },
                onLeaderboard = { navController.navigate(Routes.LEADERBOARD) },
                onSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onHowToPlay = { navController.navigate(Routes.HOW_TO_PLAY) },
                onPrivacyPolicy = { navController.navigate(Routes.PRIVACY_POLICY) }
            )
        }

        composable(Routes.PLAYER_SETUP) {
            PlayerSetupScreen(
                onBack = { navController.popBackStack() },
                onStartGame = { p1, p2, color ->
                    navController.navigate(Routes.game(p1, p2, color))
                }
            )
        }

        composable(
            route = Routes.GAME,
            arguments = listOf(
                navArgument("player1Name") { type = NavType.StringType },
                navArgument("player2Name") { type = NavType.StringType },
                navArgument("player1Color") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val p1 = backStackEntry.arguments?.getString("player1Name") ?: "Player 1"
            val p2 = backStackEntry.arguments?.getString("player2Name") ?: "Player 2"
            val color = backStackEntry.arguments?.getString("player1Color") ?: "white"
            GameScreen(
                player1Name = p1,
                player2Name = p2,
                player1Color = color,
                onBack = { navController.popBackStack() },
                onHome = {
                    navController.popBackStack(Routes.MENU, inclusive = false)
                },
                onReplay = {
                    navController.popBackStack()
                    navController.navigate(Routes.game(p1, p2, color))
                }
            )
        }

        composable(Routes.HOW_TO_PLAY) {
            HowToPlayScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PRIVACY_POLICY) {
            PrivacyPolicyScreen(onBack = { navController.popBackStack() })
        }
    }
}
