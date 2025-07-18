package com.example.ludogame.model

import androidx.compose.runtime.MutableState

fun Piece.isMovable(dice: Int): Boolean {
    if (state == PieceState.FINISHED) return false
    if (state == PieceState.HOME) return dice == 6

    val mainPath = GameConstants.MAIN_PATHS[color] ?: return false
    val homePath = GameConstants.HOME_PATHS[color] ?: return false

    return when (position) {
        is Int -> {
            val index = mainPath.indexOf(position)
            if (index == -1) false
            else {
                val effectiveDice = calculateEffectiveDiceForLanding(dice)
                val newIndex = index + effectiveDice
                if (newIndex < mainPath.size) {
                    true // Can move within main path
                } else {
                    val homeIndex = newIndex - mainPath.size
                    homeIndex <= homePath.size
                }
            }
        }
        is String -> {
            val index = homePath.indexOf(position)
            if (index == -1) false
            else (index + dice) <= homePath.size
        }
        else -> false
    }
}

fun Piece.calculateEffectiveDiceForLanding(dice: Int): Int {
    if (position !is Int) return dice

    val mainPath = GameConstants.MAIN_PATHS[color] ?: return dice
    val bonusPosition = GameConstants.COLOR_LANDING_BONUS_POSITIONS[color] ?: return dice
    val currentIndex = mainPath.indexOf(position)

    if (currentIndex == -1) return dice

    // Calculate where the token would land with the current dice value
    val targetIndex = currentIndex + dice
    if (targetIndex < mainPath.size) {
        val targetPosition = mainPath[targetIndex]
        // If the token would land on its bonus position, add +1 step
        if (targetPosition == bonusPosition) {
            return dice + 1
        }
    }

    return dice
}

fun Piece.move(dice: Int, gameState: MutableState<GameState>) {
    if (!isMovable(dice)) return

    val mainPath = GameConstants.MAIN_PATHS[color]!!
    val homePath = GameConstants.HOME_PATHS[color]!!
    var killed = false
    var extraTurn = false

    when (state) {
        PieceState.HOME -> {
            if (dice == 6) {
                state = PieceState.ACTIVE
                position = GameConstants.START_POSITIONS[color]!!
                extraTurn = true
            }
        }
        PieceState.ACTIVE -> {
            when (position) {
                is Int -> {
                    val index = mainPath.indexOf(position)
                    val effectiveDice = calculateEffectiveDiceForLanding(dice)
                    val newIndex = index + effectiveDice

                    if (newIndex < mainPath.size) {
                        position = mainPath[newIndex]
                    } else {
                        val homeIndex = newIndex - mainPath.size
                        if (homeIndex < homePath.size) {
                            position = homePath[homeIndex]
                        } else if (homeIndex == homePath.size) {
                            state = PieceState.FINISHED
                            extraTurn = true
                        }
                    }
                }
                is String -> {
                    val index = homePath.indexOf(position)
                    val newIndex = index + dice

                    if (newIndex < homePath.size) {
                        position = homePath[newIndex]
                    } else if (newIndex == homePath.size) {
                        state = PieceState.FINISHED
                        extraTurn = true
                    }
                }
            }
        }
        else -> {}
    }

    // Check for kills on main path (not safe spots)
    if (position is Int && !GameConstants.SAFE_SPOTS.contains(position)) {
        gameState.value.players.forEach { (pColor, player) ->
            if (pColor != color) {
                player.pieces.forEach { piece ->
                    if (piece.position == position && piece.state == PieceState.ACTIVE) {
                        piece.position = -1
                        piece.state = PieceState.HOME
                        killed = true
                        extraTurn = true
                    }
                }
            }
        }
    }

    // Check if player has won
    val player = gameState.value.players[color]!!
    if (player.hasWon() && !gameState.value.winnerRank.contains(color)) {
        gameState.value.winnerRank.add(color)
    }

    // Handle turn logic
    if (dice == 6 || killed || extraTurn) {
        gameState.value = gameState.value.copy(isDiceRolled = false)
    } else {
        switchTurn(gameState)
    }
}

fun switchTurn(gameState: MutableState<GameState>) {
    val state = gameState.value
    var newPlayerIndex = state.currentPlayerIndex

    do {
        newPlayerIndex = (newPlayerIndex + 1) % state.turnOrder.size
    } while (
        state.players[state.turnOrder[newPlayerIndex]]!!.hasWon() &&
        state.winnerRank.size < 4
    )

    gameState.value = state.copy(
        currentPlayerIndex = newPlayerIndex,
        isDiceRolled = false,
        diceValue = 1
    )
}
