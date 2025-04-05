package com.example.battleshipgame

data class Cell(
    val row: Int,
    val col: Int,
    var isHit: Boolean = false,
    var hasShip: Boolean = false,
    val shipId: Int? = null
)