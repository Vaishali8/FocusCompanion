package com.saathi.focuscompanion.ui.room

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.saathi.focuscompanion.ui.theme.ChaiOrange
import com.saathi.focuscompanion.ui.theme.SoftGold
import com.saathi.focuscompanion.ui.theme.WarmBrown

fun DrawScope.drawPencilBox(center: Offset, scale: Float) {
    val w = 30 * scale
    val h = 15 * scale
    // Box
    drawRoundRect(
        color = Color(0xFFFDD835),
        topLeft = Offset(center.x - w / 2, center.y - h / 2),
        size = Size(w, h),
        cornerRadius = CornerRadius(3 * scale)
    )
    // Pencil tops peeking out
    val colors = listOf(Color.Red, Color.Blue, Color.Green)
    colors.forEachIndexed { i, c ->
        drawCircle(
            color = c,
            radius = 2.5f * scale,
            center = Offset(center.x - 8 * scale + i * 8 * scale, center.y - h / 2 - 1 * scale)
        )
    }
}

fun DrawScope.drawCactus(center: Offset, scale: Float) {
    // Pot
    drawRoundRect(
        color = Color(0xFFD4764E),
        topLeft = Offset(center.x - 8 * scale, center.y + 5 * scale),
        size = Size(16 * scale, 12 * scale),
        cornerRadius = CornerRadius(2 * scale)
    )
    // Body
    drawOval(
        color = Color(0xFF4CAF50),
        topLeft = Offset(center.x - 6 * scale, center.y - 15 * scale),
        size = Size(12 * scale, 22 * scale)
    )
    // Arms
    drawOval(
        color = Color(0xFF4CAF50),
        topLeft = Offset(center.x - 14 * scale, center.y - 8 * scale),
        size = Size(8 * scale, 12 * scale)
    )
    drawOval(
        color = Color(0xFF4CAF50),
        topLeft = Offset(center.x + 6 * scale, center.y - 5 * scale),
        size = Size(8 * scale, 10 * scale)
    )
}

fun DrawScope.drawStickyNote(center: Offset, scale: Float) {
    val size = 20 * scale
    // Note with folded corner
    val path = Path().apply {
        moveTo(center.x - size / 2, center.y - size / 2)
        lineTo(center.x + size / 2 - 5 * scale, center.y - size / 2)
        lineTo(center.x + size / 2, center.y - size / 2 + 5 * scale)
        lineTo(center.x + size / 2, center.y + size / 2)
        lineTo(center.x - size / 2, center.y + size / 2)
        close()
    }
    drawPath(path, color = Color(0xFFFFF176))
    // Fold
    val foldPath = Path().apply {
        moveTo(center.x + size / 2 - 5 * scale, center.y - size / 2)
        lineTo(center.x + size / 2 - 5 * scale, center.y - size / 2 + 5 * scale)
        lineTo(center.x + size / 2, center.y - size / 2 + 5 * scale)
        close()
    }
    drawPath(foldPath, color = Color(0xFFFBC02D))
    // Lines
    for (i in 0..2) {
        val lineY = center.y - 4 * scale + i * 5 * scale
        drawLine(
            color = Color(0x30000000),
            start = Offset(center.x - 7 * scale, lineY),
            end = Offset(center.x + 7 * scale, lineY),
            strokeWidth = 0.5f * scale
        )
    }
}

fun DrawScope.drawLamp(center: Offset, scale: Float) {
    // Glow
    drawCircle(
        color = SoftGold.copy(alpha = 0.3f),
        radius = 25 * scale,
        center = Offset(center.x, center.y - 10 * scale)
    )
    // Shade (trapezoid)
    val shadePath = Path().apply {
        moveTo(center.x - 10 * scale, center.y - 15 * scale)
        lineTo(center.x + 10 * scale, center.y - 15 * scale)
        lineTo(center.x + 15 * scale, center.y)
        lineTo(center.x - 15 * scale, center.y)
        close()
    }
    drawPath(shadePath, color = ChaiOrange)
    // Stem
    drawRoundRect(
        color = WarmBrown,
        topLeft = Offset(center.x - 2 * scale, center.y),
        size = Size(4 * scale, 20 * scale),
        cornerRadius = CornerRadius(1 * scale)
    )
    // Base
    drawOval(
        color = WarmBrown,
        topLeft = Offset(center.x - 10 * scale, center.y + 18 * scale),
        size = Size(20 * scale, 6 * scale)
    )
}

fun DrawScope.drawTrophy(center: Offset, scale: Float) {
    // Cup body
    drawRoundRect(
        color = SoftGold,
        topLeft = Offset(center.x - 10 * scale, center.y - 15 * scale),
        size = Size(20 * scale, 20 * scale),
        cornerRadius = CornerRadius(4 * scale)
    )
    // Handles
    drawArc(
        color = SoftGold,
        startAngle = -90f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(center.x - 18 * scale, center.y - 12 * scale),
        size = Size(12 * scale, 14 * scale),
        style = Stroke(width = 2.5f * scale)
    )
    drawArc(
        color = SoftGold,
        startAngle = 90f,
        sweepAngle = -180f,
        useCenter = false,
        topLeft = Offset(center.x + 6 * scale, center.y - 12 * scale),
        size = Size(12 * scale, 14 * scale),
        style = Stroke(width = 2.5f * scale)
    )
    // Stem
    drawRoundRect(
        color = SoftGold,
        topLeft = Offset(center.x - 3 * scale, center.y + 5 * scale),
        size = Size(6 * scale, 8 * scale),
        cornerRadius = CornerRadius(1 * scale)
    )
    // Base
    drawRoundRect(
        color = SoftGold,
        topLeft = Offset(center.x - 8 * scale, center.y + 12 * scale),
        size = Size(16 * scale, 4 * scale),
        cornerRadius = CornerRadius(2 * scale)
    )
}

fun DrawScope.drawChaiMugCollectible(center: Offset, scale: Float, color: Color = WarmBrown) {
    // Mug body
    drawRoundRect(
        color = color,
        topLeft = Offset(center.x - 8 * scale, center.y - 8 * scale),
        size = Size(16 * scale, 16 * scale),
        cornerRadius = CornerRadius(2 * scale)
    )
    // Handle
    drawArc(
        color = color,
        startAngle = -60f,
        sweepAngle = 120f,
        useCenter = false,
        topLeft = Offset(center.x + 6 * scale, center.y - 4 * scale),
        size = Size(8 * scale, 10 * scale),
        style = Stroke(width = 2 * scale)
    )
    // Chai inside
    drawRoundRect(
        color = ChaiOrange.copy(alpha = 0.6f),
        topLeft = Offset(center.x - 5 * scale, center.y - 3 * scale),
        size = Size(10 * scale, 8 * scale),
        cornerRadius = CornerRadius(1 * scale)
    )
}
