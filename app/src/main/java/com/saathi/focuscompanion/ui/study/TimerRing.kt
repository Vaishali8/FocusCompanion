package com.saathi.focuscompanion.ui.study

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saathi.focuscompanion.ui.theme.ChaiOrange
import com.saathi.focuscompanion.ui.theme.WarmBrown

@Composable
fun TimerRing(
    timeRemainingSeconds: Int,
    totalTimeSeconds: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalTimeSeconds > 0) {
        timeRemainingSeconds.toFloat() / totalTimeSeconds.toFloat()
    } else 0f

    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(160.dp)
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val strokeWidth = 12.dp.toPx()
            val arcSize = size.width - strokeWidth
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            // Background ring
            drawArc(
                color = ChaiOrange.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress ring
            drawArc(
                color = ChaiOrange,
                startAngle = 270f,
                sweepAngle = progress * 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = timeText,
            color = WarmBrown,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displayMedium
        )
    }
}
