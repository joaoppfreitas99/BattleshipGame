package com.example.battleshipgame.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleshipgame.BattleshipViewModel
import com.example.battleshipgame.Cell
import com.example.battleshipgame.R

@Composable
fun GameScreen(battleshipViewModel: BattleshipViewModel, onBackToMenu: () -> Unit) {

    Log.d("GameScreen", "GameScreen starts")  // Log to indicate start

    val playerBoard = battleshipViewModel.playerBoard.value
    val aiBoard = battleshipViewModel.aiBoard.value
    val statusText = battleshipViewModel.statusText.value

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.ic_menu3), // your background image
            contentDescription = "Menu Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Battleship - Player vs AI", fontSize = 20.sp, fontWeight = FontWeight.Bold,  color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            // Game Grid
            BoardGrid(playerBoard, isPlayerBoard = true) { row, col ->
                battleshipViewModel.onPlayerAttack(row, col)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Text (Hit/Miss/Player's Turn)
            Text(text = statusText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            // AI's Board
            BoardGrid(aiBoard, isPlayerBoard = false) { _, _ -> }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { TODO()/* Back to Menu action */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Back to Menu", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(16.dp)) // Add space between buttons
                Button(
                    onClick = { TODO()/* Reset Game action */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Reset Game", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BoardGrid(board: List<List<Cell>>, isPlayerBoard: Boolean, onCellClick : (Int, Int) -> Unit) {
    Column {
        for (row in board) {
            Row {
                for (cell in row) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.dp, Color.Blue)
                            .background(getCellColor(cell, isPlayerBoard))
                            .clickable { onCellClick(cell.row, cell.col) }
                    )
                }
            }
        }
    }
}

// Function to determine the color of each cell
@Composable
fun getCellColor(cell: Cell, isPlayerBoard: Boolean): Color {
    return if(isPlayerBoard){
        when {
            cell.isHit && cell.hasShip -> Color(0xFFFF0000)   // Hit - Red (Ship)
            cell.isHit && !cell.hasShip -> Color(0xFF87CEFA)  // Miss - Light Blue (Water)
            else -> Color(0xFFFFFFFF)                         // Default - Dark Blue
        }
    } else {
        when {
            cell.isHit && cell.hasShip -> Color(0xFFFF0000)   // Hit - Red
            cell.isHit && !cell.hasShip -> Color(0xFF696969)  // Miss - Dark Grey
            !cell.isHit && cell.hasShip  -> Color(0xFF32CD32) // Ships - Lime Green
            else -> Color(0xFF87CEFA)                         // Default -  Light Blue (Water)
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewGameScreen() {
    val battleshipViewModel = BattleshipViewModel()
    GameScreen(battleshipViewModel, onBackToMenu = {})
}
