package com.example.ludogame.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ludogame.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen() {
    var gameState by remember { mutableStateOf(GameState()) }
    val gameStateRef = remember { mutableStateOf(gameState) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(gameState) {
        gameStateRef.value = gameState
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Game Status
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = gameState.currentColor().toColor().copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (gameState.winnerRank.size >= 3) {
                        "Game Over!"
                    } else {
                        "${gameState.currentColor().name}'s Turn"
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = gameState.currentColor().toColor()
                )

                if (gameState.winnerRank.isNotEmpty()) {
                    Text(
                        text = "Winners: ${gameState.winnerRank.joinToString(", ") { it.name }}",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        // Game Board
        LudoBoard(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(bottom = 16.dp),
            gameState = gameState,
            onPieceClick = { piece ->
                if (piece.isMovable(gameState.diceValue)) {
                    piece.move(gameState.diceValue, gameStateRef)
                    gameState = gameStateRef.value
                }
            }
        )

        // Game Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dice
            DiceComponent(
                diceValue = gameState.diceValue,
                onRoll = { value ->
                    gameState = gameState.copy(
                        diceValue = value,
                        isDiceRolled = true
                    )

                    // Check for auto-move
                    val currentPlayer = gameState.players[gameState.currentColor()]!!
                    val movablePieces = currentPlayer.pieces.filter { it.isMovable(value) }

                    if (movablePieces.isEmpty()) {
                        // No moves available, switch turn after delay
                        scope.launch {
                            delay(1000)
                            if (value != 6) {
                                switchTurn(gameStateRef)
                                gameState = gameStateRef.value
                            } else {
                                gameState = gameState.copy(isDiceRolled = false)
                            }
                        }
                    } else if (movablePieces.size == 1) {
                        // Auto-move single piece
                        scope.launch {
                            delay(500)
                            movablePieces[0].move(value, gameStateRef)
                            gameState = gameStateRef.value
                        }
                    }
                },
                enabled = !gameState.isDiceRolled && gameState.winnerRank.size < 3
            )
        }
    }
}
