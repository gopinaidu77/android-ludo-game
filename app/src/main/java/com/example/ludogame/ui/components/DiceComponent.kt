package com.example.ludogame.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke  // ✅ ADD THIS IMPORT
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
fun DiceComponent(
    diceValue: Int,
    onRoll: (Int) -> Unit,
    enabled: Boolean = true
) {
    var isRolling by remember { mutableStateOf(false) }
    var displayValue by remember { mutableStateOf(diceValue) }
    var shouldStartRolling by remember { mutableStateOf(false) }

    // 3D rotation animations
    val rotationX by animateFloatAsState(
        targetValue = if (isRolling) 720f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = EaseOutBounce
        ),
        finishedListener = {
            isRolling = false
        }
    )

    val rotationY by animateFloatAsState(
        targetValue = if (isRolling) 540f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = EaseOutCubic
        )
    )

    val rotationZ by animateFloatAsState(
        targetValue = if (isRolling) 360f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = EaseInOutQuart
        )
    )

    // Scale animation for bounce effect
    val scale by animateFloatAsState(
        targetValue = if (isRolling) 1.2f else 1f,
        animationSpec = tween(
            durationMillis = 750,
            easing = EaseOutBounce
        )
    )

    // LaunchedEffect for rolling animation
    LaunchedEffect(shouldStartRolling) {
        if (shouldStartRolling) {
            isRolling = true
            shouldStartRolling = false

            // Simulate random values during rolling
            val rollDuration = 1500L
            val intervalDuration = 100L
            var elapsed = 0L

            // Generate final value
            val finalValue = Random.nextInt(1, 7)

            // Update display value during animation
            while (elapsed < rollDuration) {
                displayValue = Random.nextInt(1, 7)
                delay(intervalDuration)
                elapsed += intervalDuration
            }

            displayValue = finalValue
            onRoll(finalValue)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 3D Dice
        Box(
            modifier = Modifier
                .size(100.dp)
                .clickable(enabled = enabled && !isRolling) {
                    shouldStartRolling = true
                },
            contentAlignment = Alignment.Center
        ) {
            ThreeDDice(
                value = displayValue,
                rotationX = rotationX,
                rotationY = rotationY,
                rotationZ = rotationZ,
                scale = scale,
                isRolling = isRolling
            )
        }

        // Roll Button
        Button(
            onClick = {
                if (!isRolling) {
                    shouldStartRolling = true
                }
            },
            enabled = enabled && !isRolling,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray
            )
        ) {
            Text(
                text = if (isRolling) "Rolling..." else "Roll Dice",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ThreeDDice(
    value: Int,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    scale: Float,
    isRolling: Boolean
) {
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val diceSize = size.minDimension * 0.8f * scale

        // Apply 3D transformations
        rotate(rotationZ, center) {
            drawDice3D(
                center = center,
                size = diceSize,
                value = value,
                rotationX = rotationX,
                rotationY = rotationY,
                isRolling = isRolling
            )
        }
    }
}

private fun DrawScope.drawDice3D(
    center: Offset,
    size: Float,
    value: Int,
    rotationX: Float,
    rotationY: Float,
    isRolling: Boolean
) {
    val halfSize = size / 2f

    // Create 3D effect with multiple faces
    val faces = listOf(
        // Top face (lighter)
        DiceFace(
            topLeft = Offset(center.x - halfSize, center.y - halfSize),
            topRight = Offset(center.x + halfSize, center.y - halfSize),
            bottomLeft = Offset(center.x - halfSize * 0.8f, center.y - halfSize * 0.8f),
            bottomRight = Offset(center.x + halfSize * 0.8f, center.y - halfSize * 0.8f),
            color = Color.White,
            shadowOffset = -rotationY / 10f
        ),
        // Front face (main)
        DiceFace(
            topLeft = Offset(center.x - halfSize, center.y - halfSize),
            topRight = Offset(center.x + halfSize, center.y - halfSize),
            bottomLeft = Offset(center.x - halfSize, center.y + halfSize),
            bottomRight = Offset(center.x + halfSize, center.y + halfSize),
            color = Color(0xFFF8F8F8),
            shadowOffset = 0f
        ),
        // Right face (darker)
        DiceFace(
            topLeft = Offset(center.x + halfSize, center.y - halfSize),
            topRight = Offset(center.x + halfSize * 0.8f, center.y - halfSize * 0.8f),
            bottomLeft = Offset(center.x + halfSize, center.y + halfSize),
            bottomRight = Offset(center.x + halfSize * 0.8f, center.y + halfSize * 0.8f),
            color = Color(0xFFE0E0E0),
            shadowOffset = rotationY / 10f
        )
    )

    // Draw dice faces
    faces.forEach { face ->
        drawDiceFace(face, value, isRolling)
    }

    // Add glow effect when rolling
    if (isRolling) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                radius = size * 0.8f
            ),
            radius = size * 0.6f,
            center = center
        )
    }
}

private data class DiceFace(
    val topLeft: Offset,
    val topRight: Offset,
    val bottomLeft: Offset,
    val bottomRight: Offset,
    val color: Color,
    val shadowOffset: Float
)

private fun DrawScope.drawDiceFace(
    face: DiceFace,
    value: Int,
    isRolling: Boolean
) {
    // Create face path
    val path = Path().apply {
        moveTo(face.topLeft.x, face.topLeft.y)
        lineTo(face.topRight.x, face.topRight.y)
        lineTo(face.bottomRight.x, face.bottomRight.y)
        lineTo(face.bottomLeft.x, face.bottomLeft.y)
        close()
    }

    // Draw face background
    drawPath(
        path = path,
        color = face.color
    )

    // Draw face border - THIS IS LINE 269 WHERE STROKE IS USED
    drawPath(
        path = path,
        color = Color.Black.copy(alpha = 0.3f),
        style = Stroke(width = 2.dp.toPx())  // ✅ NOW STROKE IS PROPERLY IMPORTED
    )

    // Draw dots on main face only
    if (face.shadowOffset == 0f) {
        val faceCenter = Offset(
            (face.topLeft.x + face.bottomRight.x) / 2f,
            (face.topLeft.y + face.bottomRight.y) / 2f
        )
        val faceWidth = face.bottomRight.x - face.topLeft.x
        val faceHeight = face.bottomRight.y - face.topLeft.y

        drawDiceDots(
            center = faceCenter,
            width = faceWidth * 0.7f,
            height = faceHeight * 0.7f,
            value = value,
            isRolling = isRolling
        )
    }
}

private fun DrawScope.drawDiceDots(
    center: Offset,
    width: Float,
    height: Float,
    value: Int,
    isRolling: Boolean
) {
    val dotRadius = minOf(width, height) / 12f
    val spacing = minOf(width, height) / 4f

    // Add flicker effect when rolling
    val dotColor = if (isRolling) {
        Color.Black.copy(alpha = 0.7f + 0.3f * sin(System.currentTimeMillis() / 100.0).toFloat())
    } else {
        Color.Black
    }

    val dotOffsets = when (value) {
        1 -> listOf(Offset(0f, 0f))
        2 -> listOf(
            Offset(-spacing, -spacing),
            Offset(spacing, spacing)
        )
        3 -> listOf(
            Offset(-spacing, -spacing),
            Offset(0f, 0f),
            Offset(spacing, spacing)
        )
        4 -> listOf(
            Offset(-spacing, -spacing),
            Offset(spacing, -spacing),
            Offset(-spacing, spacing),
            Offset(spacing, spacing)
        )
        5 -> listOf(
            Offset(-spacing, -spacing),
            Offset(spacing, -spacing),
            Offset(0f, 0f),
            Offset(-spacing, spacing),
            Offset(spacing, spacing)
        )
        6 -> listOf(
            Offset(-spacing, -spacing),
            Offset(0f, -spacing),
            Offset(spacing, -spacing),
            Offset(-spacing, spacing),
            Offset(0f, spacing),
            Offset(spacing, spacing)
        )
        else -> emptyList()
    }

    dotOffsets.forEach { offset ->
        drawCircle(
            color = dotColor,
            radius = dotRadius,
            center = center + offset
        )
    }
}
