package com.example.ludogame.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ludogame.model.*
import kotlin.math.roundToInt

@Composable
fun LudoBoard(
    modifier: Modifier = Modifier,
    gameState: GameState,
    onPieceClick: (Piece) -> Unit
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .background(Color.White)
    ) {
        val boardSize = maxWidth
        val cellSize = boardSize / 15f
        val cellSizePx = with(density) { cellSize.toPx() }

        // Draw the board
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLudoBoard(cellSizePx)
        }

        // Draw pieces
        gameState.players.forEach { (_, player) ->
            player.pieces.forEach { piece ->
                if (piece.state != PieceState.FINISHED) {
                    PieceComponent(
                        piece = piece,
                        gameState = gameState,
                        cellSize = cellSize,
                        onPieceClick = onPieceClick
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawLudoBoard(cellSizePx: Float) {
    // Draw the 15x15 grid
    for (row in 0 until 15) {
        for (col in 0 until 15) {
            val topLeft = Offset(col * cellSizePx, row * cellSizePx)
            val cellSize = Size(cellSizePx, cellSizePx)

            // Determine cell color
            val color = getCellColor(row, col)

            // Draw cell background
            drawRect(
                color = color,
                topLeft = topLeft,
                size = cellSize
            )

            // Draw cell border
            drawRect(
                color = Color.Black,
                topLeft = topLeft,
                size = cellSize,
                style = Stroke(width = 1.dp.toPx())
            )

            // Draw star for safe spots
            if (isSafeSpot(row, col)) {
                drawStar(topLeft + Offset(cellSizePx/2, cellSizePx/2), cellSizePx * 0.3f)
            }
        }
    }

    // Draw center home triangle
    drawCenterTriangles(cellSizePx)
}

private fun getCellColor(row: Int, col: Int): Color {
    return when {
        // Player home areas (6x6 each)
        row in 0..5 && col in 0..5 -> PlayerColor.GREEN.toColor()
        row in 0..5 && col in 9..14 -> PlayerColor.YELLOW.toColor()
        row in 9..14 && col in 0..5 -> PlayerColor.RED.toColor()
        row in 9..14 && col in 9..14 -> PlayerColor.BLUE.toColor()

        // Start positions
        row == 6 && col == 1 -> PlayerColor.GREEN.toColor()   // Green start (left side)
        row == 1 && col == 8 -> PlayerColor.YELLOW.toColor()  // Yellow start (top side)
        row == 8 && col == 13 -> PlayerColor.BLUE.toColor()   // Blue start (right side)
        row == 13 && col == 6 -> PlayerColor.RED.toColor()    // Red start (bottom side)

        // Home paths (colored paths leading to center)
        row == 7 && col in 1..5 -> PlayerColor.GREEN.toColor()   // Green home path
        row in 1..5 && col == 7 -> PlayerColor.YELLOW.toColor()  // Yellow home path
        row == 7 && col in 9..13 -> PlayerColor.BLUE.toColor()   // Blue home path
        row in 9..13 && col == 7 -> PlayerColor.RED.toColor()    // Red home path

        // Safe spots (star background) - corrected positions
        isSafeSpot(row, col) -> Color(0xFFF0E68C)

        // Regular path cells
        else -> Color(0xFFE2E8F0)
    }
}

private fun isSafeSpot(row: Int, col: Int): Boolean {
    // 4 safe spots positioned correctly - Green and Blue stars moved to opposite sides
    return (row == 2 && col == 6) ||    // Top safe spot (near Yellow area) - CORRECT
            (row == 6 && col == 12) ||   // Right safe spot (near Green area) - MOVED
            (row == 12 && col == 8) ||   // Bottom safe spot (near Red area) - CORRECT
            (row == 8 && col == 2)       // Left safe spot (near Blue area) - MOVED
}

private fun DrawScope.drawStar(center: Offset, radius: Float) {
    val path = Path()
    val innerRadius = radius * 0.4f

    for (i in 0 until 10) {
        val angle = (i * 36.0 - 90.0) * Math.PI / 180.0
        val r = if (i % 2 == 0) radius else innerRadius
        val x = center.x + (r * kotlin.math.cos(angle)).toFloat()
        val y = center.y + (r * kotlin.math.sin(angle)).toFloat()

        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()

    drawPath(path, Color.White)
    drawPath(path, Color.Black, style = Stroke(width = 2.dp.toPx()))
}

private fun DrawScope.drawCenterTriangles(cellSizePx: Float) {
    val centerX = 7.5f * cellSizePx
    val centerY = 7.5f * cellSizePx
    val radius = cellSizePx * 1.5f  // Bigger size

    val triangles = listOf(
        // Top (→ yellow)
        Triple(PlayerColor.YELLOW.toColor(), Offset(centerX, centerY), listOf(
            Offset(centerX - radius, centerY - radius),
            Offset(centerX + radius, centerY - radius)
        )),
        // Right (→ blue)
        Triple(PlayerColor.BLUE.toColor(), Offset(centerX, centerY), listOf(
            Offset(centerX + radius, centerY - radius),
            Offset(centerX + radius, centerY + radius)
        )),
        // Bottom (→ red)
        Triple(PlayerColor.RED.toColor(), Offset(centerX, centerY), listOf(
            Offset(centerX + radius, centerY + radius),
            Offset(centerX - radius, centerY + radius)
        )),
        // Left (→ green)
        Triple(PlayerColor.GREEN.toColor(), Offset(centerX, centerY), listOf(
            Offset(centerX - radius, centerY + radius),
            Offset(centerX - radius, centerY - radius)
        ))
    )

    triangles.forEach { (color, pivot, points) ->
        val path = Path().apply {
            moveTo(pivot.x, pivot.y)
            lineTo(points[0].x, points[0].y)
            lineTo(points[1].x, points[1].y)
            close()
        }
        drawPath(path, color.copy(alpha = 0.999f))
    }
}

@Composable
fun PieceComponent(
    piece: Piece,
    gameState: GameState,
    cellSize: androidx.compose.ui.unit.Dp,
    onPieceClick: (Piece) -> Unit
) {
    val density = LocalDensity.current

    // Calculate position based on piece state and position
    val (gridRow, gridCol) = when {
        piece.state == PieceState.HOME -> getHomePosition(piece)
        piece.state == PieceState.FINISHED -> (7 to 7) // Center
        else -> getGridPosition(piece.position)
    }

    // Animation for piece movement
    val animatedRow by animateFloatAsState(
        targetValue = gridRow.toFloat(),
        animationSpec = tween(400, easing = EaseOutQuint)
    )
    val animatedCol by animateFloatAsState(
        targetValue = gridCol.toFloat(),
        animationSpec = tween(400, easing = EaseOutQuint)
    )

    val isCurrentPlayerPiece = gameState.currentColor() == piece.color
    val isMovable = gameState.isDiceRolled && piece.isMovable(gameState.diceValue)
    val shouldHighlight = isCurrentPlayerPiece && isMovable

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (animatedCol * with(density) { cellSize.toPx() }).roundToInt(),
                    (animatedRow * with(density) { cellSize.toPx() }).roundToInt()
                )
            }
            .size(cellSize)
            .padding(6.dp)
            .clip(CircleShape)
            .background(
                if (shouldHighlight) {
                    Color.White.copy(alpha = 0.9f)
                } else {
                    piece.color.toColor()
                }
            )
            .clickable(
                enabled = isCurrentPlayerPiece && isMovable
            ) {
                onPieceClick(piece)
            },
        contentAlignment = Alignment.Center
    ) {
        // Inner piece with white border
        Box(
            modifier = Modifier
                .size(cellSize * 0.8f)
                .clip(CircleShape)
                .background(Color.White)
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(piece.color.toColor())
            )
        }

        // Highlight ring for movable pieces
        if (shouldHighlight) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }
    }
}

// Helper functions for positioning
private fun getHomePosition(piece: Piece): Pair<Int, Int> {
    return when (piece.color) {
        PlayerColor.RED -> when (piece.id) {
            1 -> 11 to 2
            2 -> 11 to 4
            3 -> 13 to 2
            4 -> 13 to 4
            else -> 11 to 2
        }
        PlayerColor.GREEN -> when (piece.id) {
            1 -> 2 to 2
            2 -> 2 to 4
            3 -> 4 to 2
            4 -> 4 to 4
            else -> 2 to 2
        }
        PlayerColor.YELLOW -> when (piece.id) {
            1 -> 2 to 11
            2 -> 2 to 13
            3 -> 4 to 11
            4 -> 4 to 13
            else -> 2 to 11
        }
        PlayerColor.BLUE -> when (piece.id) {
            1 -> 11 to 11
            2 -> 11 to 13
            3 -> 13 to 11
            4 -> 13 to 13
            else -> 11 to 11
        }
    }
}

private fun getGridPosition(position: Any): Pair<Int, Int> {
    return when (position) {
        is Int -> positionToGridMap[position] ?: (7 to 7)
        is String -> homePathToGridMap[position] ?: (7 to 7)
        else -> (7 to 7)
    }
}

fun PlayerColor.toColor(): Color = when (this) {
    PlayerColor.RED -> Color(0xFFD92D20)
    PlayerColor.GREEN -> Color(0xFF38A169)
    PlayerColor.YELLOW -> Color(0xFFD69E2E)
    PlayerColor.BLUE -> Color(0xFF3182CE)
}

// Updated position mapping with corrected safe spot positions
private val positionToGridMap = mapOf(
    // Path starting from each color's start position, going clockwise
    1 to (13 to 6),   // Red start (bottom side)
    2 to (12 to 6), 3 to (11 to 6), 4 to (10 to 6), 5 to (9 to 6), 6 to (8 to 6),
    7 to (8 to 5), 8 to (8 to 4), 9 to (8 to 3), 10 to (8 to 2), 11 to (8 to 1), 12 to (8 to 0),
    13 to (7 to 0),
    14 to (6 to 1),   // Green start (left side)
    15 to (5 to 1), 16 to (4 to 1), 17 to (3 to 1), 18 to (2 to 1), 19 to (1 to 1), 20 to (0 to 1),
    21 to (0 to 2), 22 to (0 to 3), 23 to (0 to 4), 24 to (0 to 5), 25 to (0 to 6), 26 to (0 to 7),
    27 to (1 to 8),   // Yellow start (top side)
    28 to (1 to 9), 29 to (1 to 10), 30 to (1 to 11), 31 to (1 to 12), 32 to (1 to 13), 33 to (1 to 14),
    34 to (2 to 14), 35 to (3 to 14), 36 to (4 to 14), 37 to (5 to 14), 38 to (6 to 14), 39 to (7 to 14),
    40 to (8 to 13),  // Blue start (right side)
    41 to (9 to 13), 42 to (10 to 13), 43 to (11 to 13), 44 to (12 to 13), 45 to (13 to 13), 46 to (14 to 13),
    47 to (14 to 12), 48 to (14 to 11), 49 to (14 to 10), 50 to (14 to 9), 51 to (14 to 8), 52 to (14 to 7)
)

private val homePathToGridMap = mapOf(
    // Home paths for each color leading to center
    "rf52" to (12 to 7), "rf53" to (11 to 7), "rf54" to (10 to 7), "rf55" to (9 to 7), "rf56" to (8 to 7),
    "gf13" to (7 to 2), "gf14" to (7 to 3), "gf15" to (7 to 4), "gf16" to (7 to 5), "gf17" to (7 to 6),
    "yf26" to (2 to 7), "yf27" to (3 to 7), "yf28" to (4 to 7), "yf29" to (5 to 7), "yf30" to (6 to 7),
    "bf39" to (7 to 12), "bf40" to (7 to 11), "bf41" to (7 to 10), "bf42" to (7 to 9), "bf43" to (7 to 8)
)
