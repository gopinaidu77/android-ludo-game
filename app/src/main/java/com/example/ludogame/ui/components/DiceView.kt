package com.example.ludogame.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
fun DiceView(
    value: Int,
    rolled: Boolean,
    onRollComplete: (Int) -> Unit,
    onStartRoll: () -> Unit
) {
    val size = 90.dp // Reduced from 120.dp by 25%
    var displayValue by remember { mutableStateOf(value) }
    var isAnimating by remember { mutableStateOf(false) }

    // Physics-based bouncing animation
    val bounceHeight by animateFloatAsState(
        targetValue = if (isAnimating) -45f else 0f, // Reduced from -60f
        animationSpec = tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),
        label = "bounce"
    )

    // Multiple rotation axes for realistic tumbling
    val rotationX by animateFloatAsState(
        targetValue = if (isAnimating) 1440f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = EaseOutBounce
        ),
        label = "rotationX"
    )

    val rotationY by animateFloatAsState(
        targetValue = if (isAnimating) 1800f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = EaseOutBounce
        ),
        label = "rotationY"
    )

    val rotationZ by animateFloatAsState(
        targetValue = if (isAnimating) 1080f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = EaseOutBounce
        ),
        label = "rotationZ"
    )

    // Scale animation for impact effect
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Glow intensity
    val glowIntensity by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0f,
        animationSpec = tween(1500),
        label = "glow"
    )

    LaunchedEffect(rolled) {
        if (rolled) {
            isAnimating = true
            onStartRoll()

            // Rapid value changes during animation
            val animationDuration = 1500L
            val changeInterval = 100L
            var elapsed = 0L

            while (elapsed < animationDuration) {
                displayValue = Random.nextInt(1, 7)
                delay(changeInterval)
                elapsed += changeInterval
            }

            val newValue = Random.nextInt(1, 7)
            displayValue = newValue
            isAnimating = false
            onRollComplete(newValue)
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .padding(6.dp), // Reduced from 8.dp
        contentAlignment = Alignment.Center
    ) {
        PhysicsBasedDice(
            value = displayValue,
            size = size,
            bounceHeight = bounceHeight,
            rotationX = rotationX,
            rotationY = rotationY,
            rotationZ = rotationZ,
            scale = scale,
            glowIntensity = glowIntensity,
            isAnimating = isAnimating
        )
    }
}

@Composable
private fun PhysicsBasedDice(
    value: Int,
    size: Dp,
    bounceHeight: Float,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    scale: Float,
    glowIntensity: Float,
    isAnimating: Boolean
) {
    Canvas(modifier = Modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f + bounceHeight)
        val diceSize = this.size.minDimension * 0.8f * scale

        // Multiple rotation transformations
        rotate(rotationZ, center) {
            rotate(rotationY, center) {
                rotate(rotationX, center) {
                    drawRealisticDice(
                        center = center,
                        size = diceSize,
                        value = value,
                        glowIntensity = glowIntensity,
                        isAnimating = isAnimating
                    )
                }
            }
        }

        // Add particle effects
        if (isAnimating) {
            drawParticleEffects(center, diceSize, glowIntensity)
        }
    }
}

private fun DrawScope.drawRealisticDice(
    center: Offset,
    size: Float,
    value: Int,
    glowIntensity: Float,
    isAnimating: Boolean
) {
    val halfSize = size / 2f

    // Enhanced glow effect
    if (glowIntensity > 0) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4CAF50).copy(alpha = 0.6f * glowIntensity),
                    Color(0xFF81C784).copy(alpha = 0.4f * glowIntensity),
                    Color.Transparent
                ),
                radius = size * 1.5f
            ),
            radius = size * 1.2f,
            center = center
        )
    }

    // Draw dice shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.3f),
        radius = halfSize * 0.8f,
        center = center + Offset(0f, halfSize * 0.1f)
    )

    // Main dice body with gradient
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                Color(0xFFF0F0F0),
                Color(0xFFE0E0E0)
            ),
            radius = halfSize
        ),
        radius = halfSize,
        center = center
    )

    // Border
    drawCircle(
        color = Color.Black.copy(alpha = 0.6f),
        radius = halfSize,
        center = center,
        style = Stroke(width = 2.25.dp.toPx()) // Reduced from 3.dp
    )

    // Draw dice dots with better spacing
    drawEnhancedDots(center, halfSize * 0.7f, value)
}

private fun DrawScope.drawEnhancedDots(
    center: Offset,
    radius: Float,
    value: Int
) {
    val dotRadius = radius / 8f
    val spacing = radius / 1.3f  // INCREASED from radius / 2f for better spacing

    val dotPositions = when (value) {
        1 -> listOf(Offset(0f, 0f))
        2 -> listOf(Offset(-spacing/2, -spacing/2), Offset(spacing/2, spacing/2))
        3 -> listOf(
            Offset(-spacing/2, -spacing/2),
            Offset(0f, 0f),
            Offset(spacing/2, spacing/2)
        )
        4 -> listOf(
            Offset(-spacing/2, -spacing/2), Offset(spacing/2, -spacing/2),
            Offset(-spacing/2, spacing/2), Offset(spacing/2, spacing/2)
        )
        5 -> listOf(
            Offset(-spacing/2, -spacing/2), Offset(spacing/2, -spacing/2),
            Offset(0f, 0f),
            Offset(-spacing/2, spacing/2), Offset(spacing/2, spacing/2)
        )
        6 -> listOf(
            Offset(-spacing/2, -spacing/2), Offset(0f, -spacing/2), Offset(spacing/2, -spacing/2),
            Offset(-spacing/2, spacing/2), Offset(0f, spacing/2), Offset(spacing/2, spacing/2)
        )
        else -> emptyList()
    }

    dotPositions.forEach { offset ->
        // Dot shadow
        drawCircle(
            color = Color.Black.copy(alpha = 0.3f),
            radius = dotRadius,
            center = center + offset + Offset(1f, 1f)
        )

        // Main dot
        drawCircle(
            color = Color.Black,
            radius = dotRadius,
            center = center + offset
        )
    }
}

private fun DrawScope.drawParticleEffects(
    center: Offset,
    size: Float,
    intensity: Float
) {
    val particleCount = 12
    val radius = size * 0.8f

    repeat(particleCount) { i ->
        val angle = (i * 360f / particleCount) * PI / 180f
        val distance = radius * (0.8f + 0.4f * sin(intensity * PI * 2).toFloat())

        val particlePos = Offset(
            center.x + cos(angle).toFloat() * distance,
            center.y + sin(angle).toFloat() * distance
        )

        drawCircle(
            color = Color(0xFF4CAF50).copy(alpha = 0.7f * intensity),
            radius = 1.5.dp.toPx(), // Reduced from 2.dp
            center = particlePos
        )
    }
}
