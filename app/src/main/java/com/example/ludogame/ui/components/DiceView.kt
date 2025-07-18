package com.example.ludogame.ui.components

import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ludogame.R
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
fun DiceView(
    value: Int,
    rolled: Boolean,
    onRollComplete: (Int) -> Unit,
    onStartRoll: () -> Unit
) {
    val size = 100.dp
    var displayValue by remember { mutableStateOf(value) }
    val context = LocalContext.current
    val diceSound = remember { MediaPlayer.create(context, R.raw.dice_roll) }

    LaunchedEffect(rolled) {
        if (rolled) {
            diceSound.start()
            onStartRoll()
            val newValue = Random.nextInt(1, 7)
            delay(700)
            displayValue = newValue
            onRollComplete(newValue)
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        DiceFace(value = displayValue, size = size)
    }
}

@Composable
fun DiceFace(value: Int, size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val sideLength = size.toPx()
        drawRoundRect(
            color = Color.White,
            size = androidx.compose.ui.geometry.Size(sideLength, sideLength),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
        )

        val center = sideLength / 2
        val spacing = sideLength / 4
        val dotRadius = sideLength / 10f

        val offsets = when (value) {
            1 -> listOf(Offset(center, center))
            2 -> listOf(Offset(center - spacing, center - spacing), Offset(center + spacing, center + spacing))
            3 -> listOf(Offset(center, center), Offset(center - spacing, center - spacing), Offset(center + spacing, center + spacing))
            4 -> listOf(
                Offset(center - spacing, center - spacing), Offset(center + spacing, center - spacing),
                Offset(center - spacing, center + spacing), Offset(center + spacing, center + spacing)
            )
            5 -> listOf(
                Offset(center, center),
                Offset(center - spacing, center - spacing), Offset(center + spacing, center - spacing),
                Offset(center - spacing, center + spacing), Offset(center + spacing, center + spacing)
            )
            6 -> listOf(
                Offset(center - spacing, center - spacing), Offset(center, center - spacing), Offset(center + spacing, center - spacing),
                Offset(center - spacing, center + spacing), Offset(center, center + spacing), Offset(center + spacing, center + spacing)
            )
            else -> emptyList()
        }

        offsets.forEach {
            drawCircle(color = Color.Black, radius = dotRadius, center = it)
        }
    }
}
