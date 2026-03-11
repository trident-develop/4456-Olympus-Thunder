package com.flipkart.sho.navigation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flipkart.sho.LoadingActivity
import com.flipkart.sho.MainActivity
import com.flipkart.sho.ui.screens.connect.ConnectScreen
import com.flipkart.sho.ui.screens.connect.isFruitConnected
import com.flipkart.sho.ui.screens.loading.LoadingScreen
import kotlinx.coroutines.delay

@SuppressLint("ContextCastToActivity")
@Composable
fun LoadingGraph() {

    val navController = rememberNavController()
    val context = LocalContext.current as LoadingActivity

    NavHost(
        navController = navController,
        startDestination = if (context.isFruitConnected()) Routes.LOADING else Routes.CONNECT
    ) {
        composable(Routes.LOADING) {

            LaunchedEffect(Unit) {
                delay(2000)
                context.startActivity(Intent(context, MainActivity::class.java))
                context.finish()
            }

            LoadingScreen()
        }

        composable(Routes.CONNECT) {
            ConnectScreen(navController)
        }
    }
}