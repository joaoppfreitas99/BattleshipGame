package com.example.battleshipgame.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleshipgame.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MenuScreen(onStartGame: () -> Unit) {
    Log.d("MenuScreen", "MenuScreen starts")  // Log to indicate start

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.ic_menu), // your background image
            contentDescription = "Menu Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Centered Start Game Button
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp), // Add padding to prevent clipping
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            BattleshipTitle()
            // Spacer to push buttons to the bottom
            Spacer(modifier = Modifier.weight(1f))
            MenuButton(
                text = "Start Game vs AI",
                onClick = onStartGame
            )
            Spacer(modifier = Modifier.height(24.dp))
            MenuButton(
                text = "Start Game vs Player",
                onClick = onStartGame
            )
        }
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(280.dp)
            .height(60.dp)
    ) {
        Text(text, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BattleshipTitle() {
    Text(
        text = "Battleship",
        fontSize = 64.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        fontFamily = FontFamily.Cursive, // Change this to a custom font if needed
        modifier = Modifier.shadow(8.dp) // Optional shadow for effect
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMenuScreen() {
    MenuScreen(onStartGame = {})
}