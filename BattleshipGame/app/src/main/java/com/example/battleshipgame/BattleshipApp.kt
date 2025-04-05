package com.example.battleshipgame

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.battleshipgame.screens.GameScreen
import com.example.battleshipgame.screens.MenuScreen

@Composable
fun BattleshipApp(battleshipViewModel: BattleshipViewModel) {
    Log.d("BattleshipApp", "BattleshipApp starts Screens")  // Log to indicate start
    val navController = rememberNavController()

    NavHost(navController, startDestination = "menu"){
        composable(route = "menu") {
            MenuScreen(onStartGame = { navController.navigate(route = "game")})
        }
        composable(route = "game") {
            GameScreen(battleshipViewModel = battleshipViewModel, onBackToMenu = { navController.popBackStack() })
        }
    }

}