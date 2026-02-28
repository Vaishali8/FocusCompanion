package com.saathi.focuscompanion.ui.room

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.saathi.focuscompanion.ui.theme.ChaiOrange
import com.saathi.focuscompanion.ui.theme.CreamWhite
import com.saathi.focuscompanion.ui.theme.NightBlue
import com.saathi.focuscompanion.ui.theme.WarmBrown
import java.time.LocalDate
import java.time.LocalTime

// Coastal/rainy cities
private val rainyCities = setOf(
    "Mumbai", "Chennai", "Kolkata", "Kochi", "Thiruvananthapuram",
    "Kozhikode", "Mangalore", "Goa", "Visakhapatnam", "Puducherry"
)

@Composable
fun RoomBackground(
    city: String = "",
    modifier: Modifier = Modifier
) {
    val hour = remember { LocalTime.now().hour }
    val month = remember { LocalDate.now().monthValue }
    val isRainy = remember(city) { rainyCities.any { it.equals(city, ignoreCase = true) } }
    val isDelhiWinter = remember(city, month) {
        city.equals("Delhi", ignoreCase = true) && month in listOf(1, 2, 11, 12)
    }
    val textMeasurer = rememberTextMeasurer()

    // Rain animation
    val transition = rememberInfiniteTransition(label = "room_bg")
    val rainOffsets = if (isRainy) {
        (0..7).map { i ->
            transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800 + i * 100, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = StartOffset(i * 120)
                ),
                label = "rain_$i"
            )
        }
    } else emptyList()

    // Clock hand animation
    val clockSecond by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "clock"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val floorTop = h * 0.7f

        // Wall
        drawRect(color = CreamWhite, topLeft = Offset.Zero, size = Size(w, floorTop))

        // Floor
        drawRect(
            color = Color(0xFF4A2C0F),
            topLeft = Offset(0f, floorTop),
            size = Size(w, h - floorTop)
        )

        // Wall-floor join line
        drawLine(
            color = ChaiOrange,
            start = Offset(0f, floorTop),
            end = Offset(w, floorTop),
            strokeWidth = 2f
        )

        // Window
        drawWindow(w, h, hour, isRainy, isDelhiWinter, rainOffsets.map { it.value })

        // Bookshelf (right side)
        drawBookshelf(w, floorTop)

        // Clock (upper left)
        drawClock(clockSecond)

        // Poster with text
        drawPoster(w, textMeasurer)
    }
}

private fun DrawScope.drawWindow(
    w: Float, h: Float,
    hour: Int,
    isRainy: Boolean,
    isDelhiWinter: Boolean,
    rainOffsets: List<Float>
) {
    val winLeft = w * 0.3f
    val winTop = h * 0.08f
    val winWidth = w * 0.4f
    val winHeight = h * 0.28f

    // Window gradient based on time of day
    val gradient = when (hour) {
        in 6..11 -> Brush.verticalGradient(
            colors = listOf(Color(0xFFFDB347), Color(0xFFFF8C42)),
            startY = winTop,
            endY = winTop + winHeight
        )
        in 12..16 -> Brush.verticalGradient(
            colors = listOf(Color(0xFF87CEEB), Color(0xFFB0E0FF)),
            startY = winTop,
            endY = winTop + winHeight
        )
        in 17..19 -> Brush.verticalGradient(
            colors = listOf(Color(0xFFFF6B35), Color(0xFFFF9A76)),
            startY = winTop,
            endY = winTop + winHeight
        )
        else -> Brush.verticalGradient(
            colors = listOf(Color(0xFF0D1B2A), Color(0xFF1B2838)),
            startY = winTop,
            endY = winTop + winHeight
        )
    }

    // Window interior
    drawRoundRect(
        brush = gradient,
        topLeft = Offset(winLeft, winTop),
        size = Size(winWidth, winHeight),
        cornerRadius = CornerRadius(8f)
    )

    // Stars at night
    if (hour !in 6..19) {
        val starPositions = listOf(
            Offset(winLeft + winWidth * 0.2f, winTop + winHeight * 0.2f),
            Offset(winLeft + winWidth * 0.5f, winTop + winHeight * 0.15f),
            Offset(winLeft + winWidth * 0.8f, winTop + winHeight * 0.25f),
            Offset(winLeft + winWidth * 0.35f, winTop + winHeight * 0.45f),
            Offset(winLeft + winWidth * 0.7f, winTop + winHeight * 0.5f),
        )
        starPositions.forEach { pos ->
            drawCircle(Color.White.copy(alpha = 0.8f), radius = 2f, center = pos)
        }
    }

    // Rain overlay
    if (isRainy && rainOffsets.isNotEmpty()) {
        rainOffsets.forEachIndexed { i, progress ->
            val startX = winLeft + (winWidth / rainOffsets.size) * i + 10f
            val y = winTop + progress * winHeight
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(startX, y),
                end = Offset(startX - 3f, y + 12f),
                strokeWidth = 1.5f
            )
        }
    }

    // Delhi winter fog
    if (isDelhiWinter) {
        drawRoundRect(
            color = Color.White.copy(alpha = 0.4f),
            topLeft = Offset(winLeft, winTop + winHeight * 0.5f),
            size = Size(winWidth, winHeight * 0.5f),
            cornerRadius = CornerRadius(8f)
        )
    }

    // Window frame
    drawRoundRect(
        color = ChaiOrange,
        topLeft = Offset(winLeft, winTop),
        size = Size(winWidth, winHeight),
        cornerRadius = CornerRadius(8f),
        style = Stroke(width = 3f)
    )

    // Window panes (cross)
    drawLine(
        color = ChaiOrange,
        start = Offset(winLeft + winWidth / 2, winTop),
        end = Offset(winLeft + winWidth / 2, winTop + winHeight),
        strokeWidth = 2f
    )
    drawLine(
        color = ChaiOrange,
        start = Offset(winLeft, winTop + winHeight / 2),
        end = Offset(winLeft + winWidth, winTop + winHeight / 2),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawBookshelf(w: Float, floorTop: Float) {
    val shelfLeft = w * 0.82f
    val shelfTop = floorTop * 0.25f
    val shelfWidth = w * 0.14f
    val shelfHeight = floorTop * 0.65f

    // Main shelf body
    drawRoundRect(
        color = WarmBrown,
        topLeft = Offset(shelfLeft, shelfTop),
        size = Size(shelfWidth, shelfHeight),
        cornerRadius = CornerRadius(4f)
    )

    // Shelf dividers (at thirds)
    for (i in 1..2) {
        val divY = shelfTop + (shelfHeight / 3f) * i
        drawLine(
            color = WarmBrown.copy(alpha = 0.7f),
            start = Offset(shelfLeft + 2f, divY),
            end = Offset(shelfLeft + shelfWidth - 2f, divY),
            strokeWidth = 2f
        )
    }

    // Books on shelves
    val bookColors = listOf(
        Color(0xFFE74C3C), Color(0xFF3498DB), Color(0xFF2ECC71),
        Color(0xFFF39C12), Color(0xFF9B59B6), Color(0xFF1ABC9C),
        Color(0xFFE67E22), Color(0xFFE91E63), Color(0xFF00BCD4)
    )
    for (shelf in 0..2) {
        val shelfBaseY = shelfTop + (shelfHeight / 3f) * shelf + 5f
        val bookHeight = shelfHeight / 3f - 8f
        val numBooks = 3
        for (b in 0 until numBooks) {
            val bookWidth = shelfWidth / (numBooks + 1) - 2f
            val bookX = shelfLeft + 4f + b * (bookWidth + 3f)
            val colorIdx = (shelf * 3 + b) % bookColors.size
            drawRoundRect(
                color = bookColors[colorIdx],
                topLeft = Offset(bookX, shelfBaseY),
                size = Size(bookWidth, bookHeight),
                cornerRadius = CornerRadius(2f)
            )
            // Spine line
            drawLine(
                color = Color.White.copy(alpha = 0.3f),
                start = Offset(bookX + bookWidth / 2, shelfBaseY + 3f),
                end = Offset(bookX + bookWidth / 2, shelfBaseY + bookHeight - 3f),
                strokeWidth = 0.5f
            )
        }
    }
}

private fun DrawScope.drawClock(secondAngle: Float) {
    val clockCx = size.width * 0.12f
    val clockCy = size.height * 0.12f
    val clockRadius = minOf(size.width, size.height) * 0.05f

    // Clock face
    drawCircle(
        color = CreamWhite,
        radius = clockRadius,
        center = Offset(clockCx, clockCy)
    )
    drawCircle(
        color = WarmBrown,
        radius = clockRadius,
        center = Offset(clockCx, clockCy),
        style = Stroke(width = 2f)
    )

    // Hour marks
    for (i in 0..11) {
        val angle = Math.toRadians((i * 30).toDouble())
        val innerR = clockRadius * 0.8f
        val outerR = clockRadius * 0.95f
        drawLine(
            color = WarmBrown,
            start = Offset(
                clockCx + (innerR * kotlin.math.sin(angle)).toFloat(),
                clockCy - (innerR * kotlin.math.cos(angle)).toFloat()
            ),
            end = Offset(
                clockCx + (outerR * kotlin.math.sin(angle)).toFloat(),
                clockCy - (outerR * kotlin.math.cos(angle)).toFloat()
            ),
            strokeWidth = 1.5f
        )
    }

    // Hour hand (static based on current time)
    val currentHour = LocalTime.now().hour % 12
    val hourAngle = Math.toRadians((currentHour * 30).toDouble())
    drawLine(
        color = WarmBrown,
        start = Offset(clockCx, clockCy),
        end = Offset(
            clockCx + (clockRadius * 0.5f * kotlin.math.sin(hourAngle)).toFloat(),
            clockCy - (clockRadius * 0.5f * kotlin.math.cos(hourAngle)).toFloat()
        ),
        strokeWidth = 2.5f
    )

    // Minute hand (animated as second hand for visual effect)
    val minAngle = Math.toRadians(secondAngle.toDouble())
    drawLine(
        color = ChaiOrange,
        start = Offset(clockCx, clockCy),
        end = Offset(
            clockCx + (clockRadius * 0.7f * kotlin.math.sin(minAngle)).toFloat(),
            clockCy - (clockRadius * 0.7f * kotlin.math.cos(minAngle)).toFloat()
        ),
        strokeWidth = 1.5f
    )

    // Center dot
    drawCircle(color = WarmBrown, radius = 2f, center = Offset(clockCx, clockCy))
}

private fun DrawScope.drawPoster(w: Float, textMeasurer: TextMeasurer) {
    val posterLeft = w * 0.05f
    val posterTop = size.height * 0.25f
    val posterWidth = w * 0.15f
    val posterHeight = posterWidth * 1.3f

    // Poster background
    drawRoundRect(
        color = Color(0xFFFFF0DB),
        topLeft = Offset(posterLeft, posterTop),
        size = Size(posterWidth, posterHeight),
        cornerRadius = CornerRadius(4f)
    )
    drawRoundRect(
        color = ChaiOrange,
        topLeft = Offset(posterLeft, posterTop),
        size = Size(posterWidth, posterHeight),
        cornerRadius = CornerRadius(4f),
        style = Stroke(width = 1.5f)
    )

    // Draw text
    val textStyle = TextStyle(
        color = WarmBrown,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold
    )
    val result = textMeasurer.measure("पढ़ो,\nबढ़ो", style = textStyle)
    drawText(
        textLayoutResult = result,
        topLeft = Offset(
            posterLeft + (posterWidth - result.size.width) / 2,
            posterTop + (posterHeight - result.size.height) / 2
        )
    )
}
