package com.saathi.focuscompanion.ui.break_

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.saathi.focuscompanion.ui.theme.ChaiSteamGray

@Composable
fun ChaiSteam(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "chai_steam")

    val offsets = (0..2).map { i ->
        val offsetY by transition.animateFloat(
            initialValue = 0f,
            targetValue = -40f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(i * 400)
            ),
            label = "steam_y_$i"
        )
        val alpha by transition.animateFloat(
            initialValue = 0.7f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(i * 400)
            ),
            label = "steam_a_$i"
        )
        Pair(offsetY, alpha)
    }

    Canvas(modifier = modifier.size(60.dp, 50.dp)) {
        val cx = size.width / 2
        val baseY = size.height

        offsets.forEachIndexed { i, (offsetY, alpha) ->
            if (alpha <= 0f) return@forEachIndexed
            val x = cx + (i - 1) * 10f
            val path = Path().apply {
                moveTo(x, baseY + offsetY)
                cubicTo(
                    x + 6f, baseY + offsetY - 10f,
                    x - 6f, baseY + offsetY - 22f,
                    x + 3f, baseY + offsetY - 35f
                )
            }
            drawPath(
                path,
                color = ChaiSteamGray.copy(alpha = alpha),
                style = Stroke(width = 2f, cap = StrokeCap.Round)
            )
        }
    }
}
