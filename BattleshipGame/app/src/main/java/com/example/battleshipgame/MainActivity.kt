package com.example.battleshipgame

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

class MainActivity : ComponentActivity() {
    private val battleshipViewModel by viewModels<BattleshipViewModel>() // ViewModel created once

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Log.d("MainActivity", "Main Activity starts BattleshipApp")  // Log to indicate start
            BattleshipApp(battleshipViewModel)
        }
    }

}

