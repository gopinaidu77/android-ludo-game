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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun DiceComponent(
    diceValue: Int,
    onRoll: (Int) -> Unit,
    enabled: Boolean = true
) {
    var isRolling by remember { mutableStateOf(false) }
    val rotationX by animateFloatAsState(
        targetValue = if (isRolling) 360f * 4 else 0f,
        animationSpec = tween(1000, easing = EaseOutBounce),
        finishedListener = {
            isRolling = false
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dice
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable(enabled = enabled) {
                    if (!isRolling) {
                        isRolling = true
                        val newValue = Random.nextInt(1, 7)
                        onRoll(newValue)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize().padding(8.dp)
            ) {
                drawDice(diceValue)
            }
        }

        // Roll Button
        Button(
            onClick = {
                if (!isRolling) {
                    isRolling = true
                    val newValue = Random.nextInt(1, 7)
                    onRoll(newValue)
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

private fun DrawScope.drawDice(value: Int) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = 8f

    // Draw dice background
    drawRect(
        color = Color.White,
        topLeft = Offset.Zero,
        size = size
    )

    // Draw dice border
    drawRect(
        color = Color.Black,
        topLeft = Offset.Zero,
        size = size,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
    )

    // Draw dots based on value
    when (value) {
        1 -> {
            drawCircle(Color.Black, radius, center)
        }
        2 -> {
            drawCircle(Color.Black, radius, Offset(center.x - size.width/4, center.y - size.height/4))
            drawCircle(Color.Black, radius, Offset(center.x + size.width/4, center.y + size.height/4))
        }
        3 -> {
            drawCircle(Color.Black, radius, Offset(center.x - size.width/4, center.y - size.height/4))
            drawCircle(Color.Black, radius, center)
            drawCircle(Color.Black, radius, Offset(center.x + size.width/4, center.y + size.height/4))
        }
        4 -> {
            drawCircle(Color.Black, radius, Offset(center.x - size.width/4, center.y - size.height/4))
            drawCircle(Color.Black, radius, Offset(center.x + size.width/4, center.y - size.height/4))
            drawCircle(Color.Black, radius, Offset(center.x - size.width/4, center.y + size.height/4))
            drawCircle(Color.Black, radius, Offset(center.x + size.width/4, center.y + size.height/4))
        }
        5 -> {
            drawCircle(Color.Black, radius, Offset(center.x - size.width/4, center.y - size.height/4))
            drawCircle(Color.Black, radius, Offset(center.x + size.width/4, center.y - size.height/4))
            drawCircle(Color.Black, radius, center)
            drawCircle(Color.Black, radius, Offset(center.x - size.width/4, center.y + size.height/4))
            drawCircle(Color.Black, radius, Offset(center.x + size.width/4, center.y + size.height/4))
        }
        6 -> {
            drawCircle(Color.Black, radius, Offset(center.x - size.width/4, center.y - size.height/3))
            drawCircle(Color.Black, radius, Offset(center.x + size.width/4, center.y - size.height/3))
            drawCircle(Color.Black, radius, Offset(center.x - size.width/4, center.y))
            drawCircle(Color.Black, radius, Offset(center.x + size.width/4, center.y))
            drawCircle(Color.Black, radius, Offset(center.x - size.width/4, center.y + size.height/3))
            drawCircle(Color.Black, radius, Offset(center.x + size.width/4, center.y + size.height/3))
        }
    }
}
