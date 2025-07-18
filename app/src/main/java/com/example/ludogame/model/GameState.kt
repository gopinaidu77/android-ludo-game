package com.example.ludogame.model

data class GameState(
    val players: Map<PlayerColor, Player> = PlayerColor.values().associateWith { Player(it) },
    var currentPlayerIndex: Int = 0,
    val turnOrder: List<PlayerColor> = PlayerColor.values().toList(),
    var isDiceRolled: Boolean = false,
    var diceValue: Int = 1,
    val winnerRank: MutableList<PlayerColor> = mutableListOf()
) {
    fun currentPlayer(): Player = players[turnOrder[currentPlayerIndex]]!!
    fun currentColor(): PlayerColor = turnOrder[currentPlayerIndex]
}

data class Player(
    val color: PlayerColor,
    val pieces: List<Piece> = List(4) { Piece(id = it + 1, color = color) }
) {
    fun hasWon(): Boolean = pieces.all { it.state == PieceState.FINISHED }
}

data class Piece(
    val id: Int,
    val color: PlayerColor,
    var position: Any = -1,
    var state: PieceState = PieceState.HOME
)
