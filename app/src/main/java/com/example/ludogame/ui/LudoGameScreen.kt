package com.example.ludogame.ui

import android.content.Context
import com.example.ludogame.model.isMovable
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ludogame.R
import com.example.ludogame.model.*
import com.example.ludogame.ui.components.DiceView
import com.example.ludogame.ui.components.LudoBoard
import kotlinx.coroutines.delay

@Composable
fun LudoGameScreen() {
    val gameState = remember { mutableStateOf(GameState()) }
    var diceRolled by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val currentColor = gameState.value.currentColor()
    val currentPlayer = gameState.value.currentPlayer()

    val statusText = when {
        gameState.value.winnerRank.size >= 3 ->
            "Game Over! Ranks: ${gameState.value.winnerRank.joinToString()}"

        gameState.value.isDiceRolled.not() ->
            "${currentColor.name.lowercase().replaceFirstChar { it.uppercase() }}'s turn. Roll the dice!"

        currentPlayer.pieces.none { it.isMovable(gameState.value.diceValue) } ->
            "No movable pieces! Turn will skip..."

        currentPlayer.pieces.all { it.state == PieceState.HOME } && gameState.value.diceValue != 6 ->
            "You need a 6 to enter the board!"

        else -> "Select a piece to move"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAFC))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = statusText,
            color = playerColor(currentColor),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        DiceView(
            value = gameState.value.diceValue,
            rolled = diceRolled,
            onRollComplete = { newValue ->
                gameState.value.diceValue = newValue
                gameState.value.isDiceRolled = true
                diceRolled = false
            },
            onStartRoll = {
                if (!diceRolled) diceRolled = true
            }
        )

        // This effect will react to dice changes and handle auto-moving/skipping
        LaunchedEffect(gameState.value.diceValue) {
            if (gameState.value.isDiceRolled) {
                val movable = currentPlayer.pieces.filter { it.isMovable(gameState.value.diceValue) }
                when (movable.size) {
                    0 -> {
                        delay(700)
                        switchTurn(gameState, gameState.value.diceValue)
                    }
                    1 -> {
                        delay(500)
                        movable[0].move(gameState.value.diceValue, gameState, context)
                    }
                    else -> { /* Wait for user tap to move */ }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { diceRolled = true },
            enabled = !diceRolled,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C51BF))
        ) {
            Text("Roll Dice", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        LudoBoard(modifier = Modifier.fillMaxWidth(), gameState = gameState.value) {
            it.move(gameState.value.diceValue, gameState, context)
        }
    }
}

fun Piece.isMovable(dice: Int): Boolean {
    if (state == PieceState.FINISHED) return false
    if (state == PieceState.HOME) return dice == 6

    val mainPath = GameConstants.MAIN_PATHS[color] ?: return false
    val homePath = GameConstants.HOME_PATHS[color] ?: return false

    return when (position) {
        is Int -> {
            val index = mainPath.indexOf(position)
            if (index == -1) false
            else dice <= (mainPath.size - index - 1 + homePath.size)
        }
        is String -> {
            val index = homePath.indexOf(position)
            index != -1 && (index + dice) <= homePath.size
        }
        else -> false
    }
}

fun Piece.move(dice: Int, gameState: MutableState<GameState>, context: Context) {
    val mainPath = GameConstants.MAIN_PATHS[color]!!
    val homePath = GameConstants.HOME_PATHS[color]!!

    var killed = false

    when (state) {
        PieceState.HOME -> {
            if (dice == 6) {
                state = PieceState.ACTIVE
                position = GameConstants.START_POSITIONS[color]!!
            }
        }
        PieceState.ACTIVE -> {
            when (position) {
                is Int -> {
                    val index = mainPath.indexOf(position)
                    val newIndex = index + dice
                    position = if (newIndex < mainPath.size) {
                        mainPath[newIndex]
                    } else {
                        val homeIndex = newIndex - mainPath.size
                        if (homeIndex < homePath.size) homePath[homeIndex] else {
                            state = PieceState.FINISHED
                            return
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
                    }
                }
            }
        }
        else -> {}
    }

    if (position is Int && !GameConstants.SAFE_SPOTS.contains(position)) {
        gameState.value.players.forEach { (pColor, player) ->
            if (pColor != color) {
                player.pieces.forEach {
                    if (it.position == position && it.state == PieceState.ACTIVE) {
                        it.position = -1
                        it.state = PieceState.HOME
                        killed = true
                    }
                }
            }
        }
    }

   // MediaPlayer.create(context, R.raw.piece_move)?.start()

    val player = gameState.value.players[color]!!
    if (player.hasWon() && !gameState.value.winnerRank.contains(color)) {
        gameState.value.winnerRank.add(color)
    }

    if (dice == 6 || killed || state == PieceState.FINISHED) {
        gameState.value.isDiceRolled = false
        return
    }

    switchTurn(gameState, dice)
}

fun switchTurn(gameState: MutableState<GameState>, dice: Int) {
    val state = gameState.value
    do {
        state.currentPlayerIndex = (state.currentPlayerIndex + 1) % state.turnOrder.size
    } while (
        state.players[state.currentColor()]!!.hasWon() &&
        state.winnerRank.size < 4
    )
    state.isDiceRolled = false
}

fun playerColor(player: PlayerColor): Color = when (player) {
    PlayerColor.RED -> Color(0xFFD92D20)
    PlayerColor.GREEN -> Color(0xFF38A169)
    PlayerColor.YELLOW -> Color(0xFFD69E2E)
    PlayerColor.BLUE -> Color(0xFF3182CE)
}
