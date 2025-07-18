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

    // Draw center triangles
    drawCenterTriangle(cellSizePx)
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
        row == 7 && col in 1..6 -> PlayerColor.GREEN.toColor()   // Green home path
        row in 1..6 && col == 7 -> PlayerColor.YELLOW.toColor()  // Yellow home path
        row == 7 && col in 8..13 -> PlayerColor.BLUE.toColor()   // Blue home path
        row in 8..13 && col == 7 -> PlayerColor.RED.toColor()    // Red home path

        // Unplayable center cross cells
        (row == 6 && col == 6) || (row == 6 && col == 8) ||
                (row == 8 && col == 8) || (row == 8 && col == 6) -> Color.Gray

        // Safe spots (star background)
        isSafeSpot(row, col) -> Color(0xFFF0E68C)

        // Regular path cells
        else -> Color(0xFFE2E8F0)
    }
}

private fun isSafeSpot(row: Int, col: Int): Boolean {
    // All safe spots where tokens cannot kill each other
    return (row == 13 && col == 6) ||   // Red start (13,6)
            (row == 6 && col == 1) ||    // Green start (6,1)
            (row == 1 && col == 8) ||    // Yellow start (1,8)
            (row == 8 && col == 13) ||   // Blue start (8,13)
            (row == 8 && col == 2) ||    // Left side safe spot (8,2)
            (row == 2 && col == 6) ||    // Top side safe spot (2,6)
            (row == 6 && col == 12) ||   // Right side safe spot (6,12)
            (row == 12 && col == 8)      // Bottom side safe spot (12,8)
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

private fun DrawScope.drawCenterTriangle(cellSizePx: Float) {
    // Coordinates for the 3×3 square (rows & cols 6-8 in a 0-based 15×15 grid)
    val left = 6f * cellSizePx
    val top = 6f * cellSizePx
    val right = 9f * cellSizePx          // 6 + 3 cells
    val bottom = 9f * cellSizePx
    val centerX = (left + right) / 2f    // center of the board
    val centerY = (top + bottom) / 2f

    // Top triangle (Yellow)
    val topTriangle = Path().apply {
        moveTo(centerX, centerY)
        lineTo(left, top)
        lineTo(right, top)
        close()
    }
    drawPath(topTriangle, PlayerColor.YELLOW.toColor().copy(alpha = 1.0f))

    // Right triangle (Blue)
    val rightTriangle = Path().apply {
        moveTo(centerX, centerY)
        lineTo(right, top)
        lineTo(right, bottom)
        close()
    }
    drawPath(rightTriangle, PlayerColor.BLUE.toColor().copy(alpha = 1.0f))

    // Bottom triangle (Red)
    val bottomTriangle = Path().apply {
        moveTo(centerX, centerY)
        lineTo(right, bottom)
        lineTo(left, bottom)
        close()
    }
    drawPath(bottomTriangle, PlayerColor.RED.toColor().copy(alpha = 1.0f))

    // Left triangle (Green)
    val leftTriangle = Path().apply {
        moveTo(centerX, centerY)
        lineTo(left, bottom)
        lineTo(left, top)
        close()
    }
    drawPath(leftTriangle, PlayerColor.GREEN.toColor().copy(alpha = 1.0f))
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
        piece.state == PieceState.FINISHED -> getFinishPosition(piece.color)
        else -> getGridPosition(piece.position)
    }

    // Animation for piece movement
    val animatedRow by animateFloatAsState(
        targetValue = gridRow.toFloat(),
        animationSpec = tween(400, easing = LinearOutSlowInEasing)
    )
    val animatedCol by animateFloatAsState(
        targetValue = gridCol.toFloat(),
        animationSpec = tween(400, easing = LinearOutSlowInEasing)
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

private fun getFinishPosition(color: PlayerColor): Pair<Int, Int> {
    return when (color) {
        PlayerColor.RED -> (8 to 7)      // Red finishes at (8,7)
        PlayerColor.BLUE -> (7 to 8)     // Blue finishes at (7,8)
        PlayerColor.YELLOW -> (6 to 7)   // Yellow finishes at (6,7)
        PlayerColor.GREEN -> (7 to 6)    // Green finishes at (7,6)
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

// RESTORED: Complete position mapping including all previously blocked cells
private val positionToGridMap = mapOf(
    // Red path starting from (13,6) going clockwise
    1 to (13 to 6),   // Red start
    2 to (12 to 6), 3 to (11 to 6), 4 to (10 to 6), 5 to (9 to 6),
    6 to (8 to 5), 7 to (8 to 4), 8 to (8 to 3), 9 to (8 to 2), 10 to (8 to 1), 11 to (8 to 0),
    12 to (7 to 0), 13 to (6 to 0),  // RESTORED: (6,0) is now playable

    // Green path starting from (6,1) continuing clockwise
    14 to (6 to 1),   // Green start
    15 to (6 to 2), 16 to (6 to 3), 17 to (6 to 4), 18 to (6 to 5),
    19 to (5 to 6), 20 to (4 to 6), 21 to (3 to 6), 22 to (2 to 6), 23 to (1 to 6), 24 to (0 to 6),
    25 to (0 to 7), 26 to (0 to 8),  // RESTORED: (0,8) is now playable

    // Yellow path starting from (1,8) continuing clockwise
    27 to (1 to 8),   // Yellow start
    28 to (2 to 8), 29 to (3 to 8), 30 to (4 to 8), 31 to (5 to 8),
    32 to (6 to 9), 33 to (6 to 10), 34 to (6 to 11), 35 to (6 to 12), 36 to (6 to 13), 37 to (6 to 14),
    38 to (7 to 14), 39 to (8 to 14),  // RESTORED: (8,14) is now playable

    // Blue path starting from (8,13) continuing clockwise
    40 to (8 to 13),  // Blue start
    41 to (8 to 12), 42 to (8 to 11), 43 to (8 to 10), 44 to (8 to 9),
    45 to (9 to 8), 46 to (10 to 8), 47 to (11 to 8), 48 to (12 to 8), 49 to (13 to 8), 50 to (14 to 8),
    51 to (14 to 7), 52 to (14 to 6)  // RESTORED: (14,6) is now playable
)

// Home paths that lead to center
private val homePathToGridMap = mapOf(
    // Red home path: enters from (14,7) and moves toward center
    "rf52" to (14 to 7), "rf53" to (13 to 7), "rf54" to (12 to 7), "rf55" to (11 to 7), "rf56" to (10 to 7),

    // Green home path: enters from (7,0) and moves toward center
    "gf13" to (7 to 0), "gf14" to (7 to 1), "gf15" to (7 to 2), "gf16" to (7 to 3), "gf17" to (7 to 4),

    // Yellow home path: enters from (1,7) and moves toward center
    "yf26" to (1 to 7), "yf27" to (2 to 7), "yf28" to (3 to 7), "yf29" to (4 to 7), "yf30" to (5 to 7),

    // Blue home path: enters from (7,13) and moves toward center
    "bf39" to (7 to 13), "bf40" to (7 to 12), "bf41" to (7 to 11), "bf42" to (7 to 10), "bf43" to (7 to 9)
)
