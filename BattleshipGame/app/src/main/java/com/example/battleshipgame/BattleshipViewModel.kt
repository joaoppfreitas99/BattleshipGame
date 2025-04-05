package com.example.battleshipgame

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BattleshipViewModel : ViewModel() {

    private val _playerBoard = mutableStateOf(generateBoard())
    val playerBoard: State<List<List<Cell>>> get() = _playerBoard

    private val _aiBoard = mutableStateOf(generateBoard())
    val aiBoard: State<List<List<Cell>>> get() = _aiBoard

    private val _playerTurn = mutableStateOf(true)

    private val _statusText = mutableStateOf("Player's Turn")
    val statusText: State<String> get() = _statusText

    private var huntModeCells = mutableListOf<Pair<Int, Int>>()

    private var lastHit : Pair<Int, Int>?= null

    init {
        Log.d("BattleshipViewModel", "ViewModel Initialized")
        resetGame()
    }

    private fun resetGame() {
        _playerBoard.value = generateBoard() // Set the player board
        _aiBoard.value = generateBoard() // Set the AI board
        placeShips(_playerBoard.value)
        placeShips(_aiBoard.value)
        _playerTurn.value = true
        updateStatus("Player's Turn") // Reset status
    }

    private fun switchTurn() {
        _playerTurn.value = _playerTurn.value.not()
    }

    private fun updateStatus(newStatus: String) {
        _statusText.value = newStatus
    }

    private fun generateBoard(): List<List<Cell>> {
        val board = List(10) { row -> List(10) { col -> Cell(row, col) } }
        return placeShips(board)
    }

    private fun placeShips(board: List<List<Cell>>) : List<List<Cell>>{
        var shipIdCounter = 1
        val ships = listOf(5, 4, 3, 3, 2) // Sizes of ships
        var newBoard = board

        for (ship in ships){
            var placed = false
            while (placed.not()){
                val row = (0..9).random()
                val col = (0..9).random()
                val horizontal = listOf(true,false).random()
                if(canPlaceShip(newBoard, row, col, ship, horizontal)){
                    newBoard = markShipOnBoard(newBoard, row, col, ship, horizontal, shipIdCounter)
                    placed = true
                    shipIdCounter++
                }
            }
        }
        return newBoard
    }

    private fun canPlaceShip(board: List<List<Cell>>, row: Int, col: Int, shipSize: Int, horizontal: Boolean) : Boolean{
        if (horizontal){
            if (col + shipSize > boardColumnSize) return false
            return (0 until shipSize).all { board[row][col + it].hasShip.not() }
        }
        // Vertical
        if(row + shipSize > boardRowSize) return false
        return (0 until shipSize).all { board[row + it][col].hasShip.not() }
    }

    private fun markShipOnBoard(board: List<List<Cell>>, row: Int, col: Int, shipSize: Int, horizontal: Boolean, shipId: Int) : List<List<Cell>>{
        return board.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                if (horizontal && r == row && c in col until col + shipSize) cell.copy(hasShip = true, shipId = shipId)
                else if(!horizontal && c == col && r in row until row + shipSize) cell.copy(hasShip = true,  shipId = shipId)
                else cell // change nothing, keep cell
            }
        }
    }

    fun onPlayerAttack(
        row: Int,
        col: Int,
    ){
        if(_playerTurn.value.not() || isCellHit(playerBoard.value, row, col)) return // Prevent player from clicking during AI's turn
        applyHit(BoardType.PLAYER, row, col) // Make hit in available cell
        updateHitStatus(BoardType.PLAYER, row, col) // Hit or Miss
        checkWinGameState(BoardType.PLAYER)
        switchTurn()
        aiMove()
    }

    private fun performAiMove() {
        if (statusText.value.contains("Wins")) return // 🛑 Stop AI if the game is over

        val availableCells = aiBoard.value
            .flatten()
            .filter { it.isHit.not() } // Only Cells which haven't been hit
            .map { it.row to it.col } // Make Pair

        huntModeCells = huntModeCells.filter { (r, c) -> !aiBoard.value[r][c].isHit }.toMutableList()

        val target = if(huntModeCells.isNotEmpty()){
            val randomPick = (0..<huntModeCells.size).random()
            huntModeCells.removeAt(randomPick)
        } else if (availableCells.isNotEmpty()){
            availableCells.random()
        } else {
            return
        }

        val (row, col) = target
        applyHit(BoardType.AI, row, col) // Make hit in available cell
        updateHitStatus(BoardType.AI, row, col) // Hit or Miss

        if (aiBoard.value[row][col].hasShip) {
            if(lastHit == null){
                huntModeCells.addAll(getAdjacentCells(row, col)) // First Hit
            } else {
                val lastRow = lastHit!!.first
                val lastCol = lastHit!!.second

                if (row == lastRow){
                    val right = row to col + 1
                    val left = row to col - 1
                    
                    // Clear huntModeCells since we are committing to a direction
                    huntModeCells.clear()

                    // Continue in same direction first
                    if(col + 1 < boardColumnSize && aiBoard.value[row][col+1].isHit.not()) {
                        huntModeCells.add(right)
                    } else if(aiBoard.value[row][col-1].isHit.not()){
                        huntModeCells.add(left)
                    }
                } else if (col == lastCol){
                    val up = row + 1 to col
                    val down = row -1 to col

                    // Clear huntModeCells since we are committing to a direction
                    huntModeCells.clear()

                    // Continue in same direction first
                    if(row > lastRow && aiBoard.value[row+1][col+1].isHit.not()){
                        huntModeCells.add(up)
                    } else if(aiBoard.value[row-1][col].isHit.not()){
                        huntModeCells.add(down)
                    }
                } else {
                    Log.e("performAiMove", "Unexpected hit pattern detected at ($row, $col)")
                }
            }
            lastHit = row to col

            if(isShipSunk(row, col)){
                huntModeCells.clear()
                lastHit = null
            }
        }

        checkWinGameState(BoardType.AI)

        if (!statusText.value.contains("Wins")) { // 🛑 Only switch turn if no one won
            switchTurn()
        }
    }

    private fun getAdjacentCells(row: Int, col: Int) : List<Pair<Int, Int>> {
        val potentialCells = listOf(
            row - 1 to col, // Top
            row + 1 to col, // Bottom
            row to col - 1, // Left
            row to col + 1 // Right
        )
        return potentialCells.filter { (r,c) ->
            r in 0 until boardRowSize &&
            c in 0 until boardColumnSize &&
            aiBoard.value[r][c].isHit.not()
        }
    }

    private fun isShipSunk(row: Int, col: Int) : Boolean{
        val shipId = aiBoard.value[row][col].shipId ?: return false
        val shipCells = aiBoard.value.flatten().filter { it.shipId == shipId }
        return shipCells.all { it.isHit }
    }

    private fun aiMove(){
        viewModelScope.launch {
            delay(1000) // 1-second delay
            // Now AI moves
            performAiMove()
        }
    }

    private fun applyHit(boardType: BoardType, row: Int, col: Int) {
        val currentBoard = if (boardType == BoardType.PLAYER) playerBoard.value else aiBoard.value

        if(isCellHit(currentBoard, row, col)) return // 🛑 Prevent hitting the same spot

        val newBoard = currentBoard.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                if (r == row && c == col) {
                    cell.copy(isHit = true) // Change Cell to isHit = true
                } else {
                    cell // Change nothing
                }
            }
        }

        updateBoard(boardType, newBoard)
    }

    private fun updateHitStatus(boardType: BoardType, row: Int, col: Int) {
        val currentBoard = if (boardType == BoardType.PLAYER) playerBoard.value else aiBoard.value
        val player = if (boardType == BoardType.PLAYER) "Player " else "AI "
        if (currentBoard[row][col].hasShip) {
            updateStatus(newStatus = player + "Hit!")
        } else {
            updateStatus(newStatus = player + "Miss!")
        }
    }

    private fun updateBoard(boardType: BoardType, newBoard: List<List<Cell>>) {
        if (boardType == BoardType.PLAYER) {
            _playerBoard.value = newBoard
        } else if (boardType == BoardType.AI) {
            _aiBoard.value = newBoard
        }
    }

    private fun checkWinGameState(boardType: BoardType) {
        val currentBoard = if (boardType == BoardType.PLAYER) playerBoard.value else aiBoard.value
        if (currentBoard.flatten().none { cell -> cell.hasShip && cell.isHit.not() }) {
            if (_playerTurn.value) {
                updateStatus("Player Wins!") // 🏆 Player Victory
            } else {
                updateStatus("AI Wins!") // 🏆 AI Victory
            }
        }
    }

    private fun isCellHit(currentBoard: List<List<Cell>>, row: Int, col: Int): Boolean {
        return currentBoard[row][col].isHit
    }

}

private const val boardRowSize = 10
private const val boardColumnSize = 10

enum class BoardType {
    PLAYER, AI
}