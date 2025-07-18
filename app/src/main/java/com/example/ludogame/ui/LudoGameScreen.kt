package com.example.ludogame.ui

import android.content.Context
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
            "No movable pieces! skip..."

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
                gameState.value = gameState.value.copy(
                    diceValue = newValue,
                    isDiceRolled = true
                )
                diceRolled = false
            },
            onStartRoll = {
                if (!diceRolled) diceRolled = true
            }
        )

        // This effect will react to dice changes and handle auto-moving/skipping
        LaunchedEffect(gameState.value.diceValue, gameState.value.isDiceRolled) {
            if (gameState.value.isDiceRolled) {
                val movable = currentPlayer.pieces.filter { it.isMovable(gameState.value.diceValue) }
                when (movable.size) {
                    0 -> {
                        delay(700)
                        switchTurn(gameState)
                    }
                    1 -> {
                        delay(500)
                        movable[0].move(gameState.value.diceValue, gameState)
                    }
                    else -> { /* Wait for user tap to move */ }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { diceRolled = true },
            enabled = !diceRolled && !gameState.value.isDiceRolled,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C51BF))
        ) {
            Text("Roll Dice", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        LudoBoard(
            modifier = Modifier.fillMaxWidth(),
            gameState = gameState.value
        ) { piece ->
            piece.move(gameState.value.diceValue, gameState)
        }
    }
}

fun playerColor(player: PlayerColor): Color = when (player) {
    PlayerColor.RED -> Color(0xFFD92D20)
    PlayerColor.GREEN -> Color(0xFF38A169)
    PlayerColor.YELLOW -> Color(0xFFD69E2E)
    PlayerColor.BLUE -> Color(0xFF3182CE)
}
