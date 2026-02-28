package com.saathi.focuscompanion.ui.character

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saathi.focuscompanion.ui.theme.ChaiOrange
import com.saathi.focuscompanion.ui.theme.CreamWhite
import com.saathi.focuscompanion.ui.theme.SoftGold
import com.saathi.focuscompanion.ui.theme.WarmBrown

// Colors used in character drawing
private val SkinColor = Color(0xFFD4A574)
private val HairColor = Color(0xFF3D2314)
private val EyeColor = Color(0xFF2C1810)
private val PupilColor = Color(0xFF1A0E08)
private val ShirtColor = Color(0xFF4A90D9)
private val DeskColor = Color(0xFF8B5E3C)
private val NotebookColor = Color(0xFFFFF5E6)
private val PenColor = Color(0xFF1A1A2E)

@Composable
fun CharacterView(
    state: CharacterState,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    pipMode: Boolean = false
) {
    when (state) {
        CharacterState.IDLE_STUDYING -> IdleStudyingView(modifier, size)
        CharacterState.IDLE_READING -> IdleReadingView(modifier, size)
        CharacterState.CHAI_BREAK_SIP -> ChaiBreakSipView(modifier, size)
        CharacterState.PHONE_CAUGHT -> PhoneCaughtView(modifier, size)
        CharacterState.WAITING -> WaitingView(modifier, size)
        CharacterState.RETURNED -> ReturnedView(modifier, size, onComplete = {})
        CharacterState.SESSION_COMPLETE -> SessionCompleteView(modifier, size)
        CharacterState.PIP_JUDGING -> PipJudgingView(modifier, size)
        CharacterState.PIP_IMPATIENT -> PipImpatientView(modifier, size)
    }
}

@Composable
fun CharacterView(
    state: CharacterState,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    onReturnComplete: () -> Unit = {}
) {
    when (state) {
        CharacterState.RETURNED -> ReturnedView(modifier, size, onComplete = onReturnComplete)
        else -> CharacterView(state, modifier, size, pipMode = false)
    }
}

// ════════════════════════════════════════════
// IDLE STUDYING
// ════════════════════════════════════════════

@Composable
private fun IdleStudyingView(modifier: Modifier, size: Dp) {
    val anims = rememberIdleStudyingAnims()
    val armY by anims.armYOffset
    val headRot by anims.headRotation
    val penX by anims.penXOffset
    val blink by anims.blinkProgress

    Canvas(modifier = modifier.size(size, size * 1.4f)) {
        val scale = size.toPx() / 200f
        drawStudyingCharacter(scale, headRot, armY, penX, blink)
    }
}

private fun DrawScope.drawStudyingCharacter(
    scale: Float,
    headRotation: Float,
    armYOffset: Float,
    penXOffset: Float,
    blinkProgress: Float
) {
    val cx = size.width / 2f
    val baseY = size.height * 0.4f

    // Desk
    drawDesk(cx, size.height * 0.72f, scale)

    // Notebook on desk
    drawNotebook(cx - 10 * scale, size.height * 0.68f, scale, penXOffset)

    // Body
    drawBody(cx, baseY + 40 * scale, scale)

    // Left arm (writing arm) with pen
    translate(left = 0f, top = armYOffset * scale) {
        drawWritingArm(cx, baseY + 50 * scale, scale, penXOffset)
    }

    // Right arm (resting on desk)
    drawRestingArm(cx, baseY + 50 * scale, scale)

    // Head with rotation
    rotate(headRotation, pivot = Offset(cx, baseY)) {
        drawHead(cx, baseY, scale, blinkProgress)
    }
}

private fun DrawScope.drawDesk(cx: Float, y: Float, scale: Float) {
    drawRoundRect(
        color = DeskColor,
        topLeft = Offset(cx - 120 * scale, y),
        size = Size(240 * scale, 40 * scale),
        cornerRadius = CornerRadius(6 * scale)
    )
    // Desk front edge highlight
    drawRoundRect(
        color = DeskColor.copy(alpha = 0.7f),
        topLeft = Offset(cx - 120 * scale, y + 32 * scale),
        size = Size(240 * scale, 8 * scale),
        cornerRadius = CornerRadius(4 * scale)
    )
}

private fun DrawScope.drawNotebook(x: Float, y: Float, scale: Float, penOffset: Float) {
    // Notebook body
    drawRoundRect(
        color = NotebookColor,
        topLeft = Offset(x - 25 * scale, y - 8 * scale),
        size = Size(50 * scale, 32 * scale),
        cornerRadius = CornerRadius(3 * scale)
    )
    // Ruled lines on notebook
    for (i in 0..3) {
        val lineY = y - 2 * scale + i * 7 * scale
        drawLine(
            color = Color(0x30000000),
            start = Offset(x - 20 * scale, lineY),
            end = Offset(x + 20 * scale, lineY),
            strokeWidth = 0.5f * scale
        )
    }
    // Pen writing marks (animated)
    val markX = x + penOffset * scale
    for (i in 0..2) {
        val lineY = y + 2 * scale + i * 5 * scale
        drawLine(
            color = PenColor.copy(alpha = 0.4f),
            start = Offset(markX - 6 * scale, lineY),
            end = Offset(markX + 6 * scale, lineY),
            strokeWidth = 0.8f * scale
        )
    }
}

private fun DrawScope.drawBody(cx: Float, bodyTop: Float, scale: Float) {
    drawRoundRect(
        color = ShirtColor,
        topLeft = Offset(cx - 30 * scale, bodyTop),
        size = Size(60 * scale, 90 * scale),
        cornerRadius = CornerRadius(12 * scale)
    )
    // Collar detail
    val collarPath = Path().apply {
        moveTo(cx - 12 * scale, bodyTop)
        lineTo(cx, bodyTop + 15 * scale)
        lineTo(cx + 12 * scale, bodyTop)
    }
    drawPath(collarPath, color = ShirtColor.darken(0.15f))
}

private fun DrawScope.drawWritingArm(cx: Float, shoulderY: Float, scale: Float, penOffset: Float) {
    val armStartX = cx - 30 * scale
    val armEndX = cx - 15 * scale
    val armEndY = shoulderY + 55 * scale

    // Upper arm
    drawRoundRect(
        color = ShirtColor.darken(0.1f),
        topLeft = Offset(armStartX - 10 * scale, shoulderY),
        size = Size(20 * scale, 45 * scale),
        cornerRadius = CornerRadius(8 * scale)
    )

    // Hand
    drawCircle(
        color = SkinColor,
        radius = 6 * scale,
        center = Offset(armEndX, armEndY)
    )

    // Pen
    val penTipX = armEndX + penOffset * scale
    drawRoundRect(
        color = PenColor,
        topLeft = Offset(penTipX - 1.5f * scale, armEndY - 18 * scale),
        size = Size(3 * scale, 24 * scale),
        cornerRadius = CornerRadius(1.5f * scale)
    )
}

private fun DrawScope.drawRestingArm(cx: Float, shoulderY: Float, scale: Float) {
    val armStartX = cx + 30 * scale

    // Arm resting on desk
    drawRoundRect(
        color = ShirtColor.darken(0.1f),
        topLeft = Offset(armStartX - 10 * scale, shoulderY),
        size = Size(20 * scale, 50 * scale),
        cornerRadius = CornerRadius(8 * scale)
    )

    // Hand
    drawCircle(
        color = SkinColor,
        radius = 6 * scale,
        center = Offset(armStartX, shoulderY + 55 * scale)
    )
}

private fun DrawScope.drawHead(
    cx: Float,
    headCenterY: Float,
    scale: Float,
    blinkProgress: Float,
    eyeWidenFactor: Float = 0f,
    pupilXOffset: Float = 0f,
    eyebrowRaise: Float = 0f,
    rightEyebrowExtraRotation: Float = 0f,
    showSmile: Boolean = false,
    smileAngle: Float = 0f,
    happyEyes: Boolean = false,
    eyelidOverride: Float = -1f
) {
    val headRadius = 40 * scale

    // Hair (arc on top of head)
    drawHair(cx, headCenterY, headRadius, scale)

    // Head circle
    drawCircle(
        color = SkinColor,
        radius = headRadius,
        center = Offset(cx, headCenterY)
    )
    // Head outline
    drawCircle(
        color = ChaiOrange.copy(alpha = 0.4f),
        radius = headRadius,
        center = Offset(cx, headCenterY),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f * scale)
    )

    // Eyes
    val eyeY = headCenterY - 8 * scale
    val leftEyeX = cx - 12 * scale + pupilXOffset * scale
    val rightEyeX = cx + 12 * scale + pupilXOffset * scale
    val eyeRadius = (5f + eyeWidenFactor * 1.5f) * scale

    if (happyEyes) {
        // Happy crescents (^^ shape)
        drawHappyEye(leftEyeX, eyeY, eyeRadius, scale)
        drawHappyEye(rightEyeX, eyeY, eyeRadius, scale)
    } else {
        // Normal eyes
        drawEye(cx - 12 * scale, eyeY, eyeRadius, scale, blinkProgress, pupilXOffset, eyelidOverride)
        drawEye(cx + 12 * scale, eyeY, eyeRadius, scale, blinkProgress, pupilXOffset, eyelidOverride)
    }

    // Eyebrows
    drawEyebrow(
        cx - 12 * scale, eyeY - 10 * scale - eyebrowRaise * scale,
        scale, isRight = false, extraRotation = 0f
    )
    drawEyebrow(
        cx + 12 * scale, eyeY - 10 * scale - eyebrowRaise * scale,
        scale, isRight = true, extraRotation = rightEyebrowExtraRotation
    )

    // Nose — small dot
    drawCircle(
        color = SkinColor.darken(0.15f),
        radius = 2 * scale,
        center = Offset(cx, headCenterY + 5 * scale)
    )

    // Smile
    if (showSmile && smileAngle > 0f) {
        val smilePath = Path().apply {
            moveTo(cx - 8 * scale, headCenterY + 14 * scale)
            quadraticBezierTo(
                cx, headCenterY + 14 * scale + smileAngle * 0.3f * scale,
                cx + 8 * scale, headCenterY + 14 * scale
            )
        }
        drawPath(
            smilePath,
            color = Color(0xFF8B4513),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f * scale, cap = StrokeCap.Round)
        )
    }
}

private fun DrawScope.drawHair(cx: Float, headCenterY: Float, headRadius: Float, scale: Float) {
    val hairPath = Path().apply {
        // Arc across top of head, overlapping slightly
        moveTo(cx - headRadius - 3 * scale, headCenterY - 5 * scale)
        quadraticBezierTo(
            cx - headRadius * 0.5f, headCenterY - headRadius - 18 * scale,
            cx, headCenterY - headRadius - 10 * scale
        )
        quadraticBezierTo(
            cx + headRadius * 0.5f, headCenterY - headRadius - 18 * scale,
            cx + headRadius + 3 * scale, headCenterY - 5 * scale
        )
        // Close along the top of the head circle
        quadraticBezierTo(
            cx + headRadius * 0.7f, headCenterY - headRadius + 5 * scale,
            cx, headCenterY - headRadius + 2 * scale
        )
        quadraticBezierTo(
            cx - headRadius * 0.7f, headCenterY - headRadius + 5 * scale,
            cx - headRadius - 3 * scale, headCenterY - 5 * scale
        )
        close()
    }
    drawPath(hairPath, color = HairColor)
}

private fun DrawScope.drawEye(
    cx: Float,
    cy: Float,
    radius: Float,
    scale: Float,
    blinkProgress: Float,
    pupilXOffset: Float = 0f,
    eyelidOverride: Float = -1f
) {
    // Eye white
    drawCircle(color = Color.White, radius = radius, center = Offset(cx, cy))

    // Pupil
    val pupilOffset = pupilXOffset * scale * 0.3f
    drawCircle(
        color = EyeColor,
        radius = radius * 0.55f,
        center = Offset(cx + pupilOffset, cy)
    )
    // Pupil highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.7f),
        radius = radius * 0.2f,
        center = Offset(cx + pupilOffset + 1 * scale, cy - 1 * scale)
    )

    // Eyelid (blink or expression)
    val lidProgress = if (eyelidOverride >= 0f) eyelidOverride else blinkProgress
    if (lidProgress > 0f) {
        drawCircle(
            color = SkinColor,
            radius = radius,
            center = Offset(cx, cy - radius * (1f - lidProgress))
        )
    }
}

private fun DrawScope.drawHappyEye(cx: Float, cy: Float, radius: Float, scale: Float) {
    // Happy crescent — upward arc like ^^
    val path = Path().apply {
        moveTo(cx - radius, cy)
        quadraticBezierTo(cx, cy - radius * 1.2f, cx + radius, cy)
    }
    drawPath(
        path,
        color = EyeColor,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f * scale, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawEyebrow(
    cx: Float,
    cy: Float,
    scale: Float,
    isRight: Boolean,
    extraRotation: Float = 0f
) {
    val rotation = if (isRight) extraRotation else 0f
    withTransform({
        rotate(rotation, pivot = Offset(cx, cy))
    }) {
        drawRoundRect(
            color = HairColor,
            topLeft = Offset(cx - 8 * scale, cy),
            size = Size(16 * scale, 2.5f * scale),
            cornerRadius = CornerRadius(2 * scale)
        )
    }
}

// ════════════════════════════════════════════
// PHONE CAUGHT
// ════════════════════════════════════════════

@Composable
private fun PhoneCaughtView(modifier: Modifier, size: Dp) {
    val anims = rememberPhoneCaughtAnims()
    val headRot = anims.headRotation.value
    val armDrop = anims.armDrop.value
    val eyebrowUp = anims.eyebrowRaise.value
    val rightBrowRot = anims.rightEyebrowRotation.value
    val eyeWiden = anims.eyeWiden.value

    Canvas(modifier = modifier.size(size, size * 1.4f)) {
        val scale = size.toPx() / 200f
        val cx = this.size.width / 2f
        val baseY = this.size.height * 0.4f

        // Desk
        drawDesk(cx, this.size.height * 0.72f, scale)

        // Notebook (no active pen marks)
        drawNotebook(cx - 10 * scale, this.size.height * 0.68f, scale, 0f)

        // Body
        drawBody(cx, baseY + 40 * scale, scale)

        // Arms dropped/resting
        val armOffsetY = armDrop * 10 * scale
        drawRestingArm(cx, baseY + 50 * scale + armOffsetY, scale)
        drawRestingArm(
            cx - 60 * scale, // mirror to left side
            baseY + 50 * scale + armOffsetY,
            scale
        )

        // Head — looking straight up at user
        rotate(headRot, pivot = Offset(cx, baseY)) {
            drawHead(
                cx, baseY, scale,
                blinkProgress = 0f,
                eyeWidenFactor = eyeWiden,
                eyebrowRaise = eyebrowUp,
                rightEyebrowExtraRotation = rightBrowRot
            )
        }
    }
}

// ════════════════════════════════════════════
// PIP JUDGING
// ════════════════════════════════════════════

@Composable
fun PipJudgingView(modifier: Modifier = Modifier, size: Dp = 120.dp) {
    val anims = rememberPipJudgingAnims()
    val pupilX by anims.pupilXOffset
    val tilt by anims.headTilt

    Canvas(modifier = modifier.size(size)) {
        val scale = size.toPx() / 120f
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f

        // Just head and shoulders for PiP
        // Shoulders
        drawRoundRect(
            color = ShirtColor,
            topLeft = Offset(cx - 35 * scale, cy + 20 * scale),
            size = Size(70 * scale, 30 * scale),
            cornerRadius = CornerRadius(15 * scale)
        )

        rotate(tilt, pivot = Offset(cx, cy)) {
            drawHead(
                cx, cy, scale,
                blinkProgress = 0f,
                pupilXOffset = pupilX,
                eyebrowRaise = 4f
            )
        }
    }
}

// ════════════════════════════════════════════
// PIP IMPATIENT
// ════════════════════════════════════════════

@Composable
fun PipImpatientView(modifier: Modifier = Modifier, size: Dp = 120.dp) {
    val anims = rememberPipImpatientAnims()
    val headNod by anims.headNod

    Canvas(modifier = modifier.size(size)) {
        val scale = size.toPx() / 120f
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f

        // Shoulders
        drawRoundRect(
            color = ShirtColor,
            topLeft = Offset(cx - 35 * scale, cy + 20 * scale),
            size = Size(70 * scale, 30 * scale),
            cornerRadius = CornerRadius(15 * scale)
        )

        // Fist under chin
        drawCircle(
            color = SkinColor,
            radius = 7 * scale,
            center = Offset(cx, cy + 25 * scale)
        )
        // Arm going down
        drawRoundRect(
            color = ShirtColor.darken(0.1f),
            topLeft = Offset(cx - 5 * scale, cy + 28 * scale),
            size = Size(10 * scale, 22 * scale),
            cornerRadius = CornerRadius(4 * scale)
        )

        translate(top = headNod * scale) {
            drawHead(
                cx, cy, scale,
                blinkProgress = 0f,
                eyelidOverride = anims.eyelidClose
            )
            // Slightly furrowed eyebrows override (drawn more inward)
        }
    }
}

// ════════════════════════════════════════════
// WAITING
// ════════════════════════════════════════════

@Composable
private fun WaitingView(modifier: Modifier, size: Dp) {
    val anims = rememberWaitingAnims()
    val fingerTap by anims.fingerTap
    val headShake by anims.headShake

    Canvas(modifier = modifier.size(size, size * 1.4f)) {
        val scale = size.toPx() / 200f
        val cx = this.size.width / 2f
        val baseY = this.size.height * 0.4f

        // Desk
        drawDesk(cx, this.size.height * 0.72f, scale)

        // Body
        drawBody(cx, baseY + 40 * scale, scale)

        // Crossed arms
        val armY = baseY + 55 * scale
        // Left arm (going right-down)
        withTransform({
            rotate(-25f, pivot = Offset(cx - 25 * scale, armY))
        }) {
            drawRoundRect(
                color = ShirtColor.darken(0.1f),
                topLeft = Offset(cx - 35 * scale, armY),
                size = Size(50 * scale, 16 * scale),
                cornerRadius = CornerRadius(8 * scale)
            )
        }
        // Right arm (going left-down, on top)
        withTransform({
            rotate(25f, pivot = Offset(cx + 25 * scale, armY))
        }) {
            drawRoundRect(
                color = ShirtColor.darken(0.05f),
                topLeft = Offset(cx - 15 * scale, armY),
                size = Size(50 * scale, 16 * scale),
                cornerRadius = CornerRadius(8 * scale)
            )
        }
        // Tapping finger
        drawCircle(
            color = SkinColor,
            radius = 4 * scale,
            center = Offset(cx + 20 * scale, armY + fingerTap * scale)
        )

        // Head with impatient shake
        rotate(headShake, pivot = Offset(cx, baseY)) {
            drawHead(
                cx, baseY, scale,
                blinkProgress = 0f,
                eyelidOverride = 0.5f // half-lidded
            )
        }
    }
}

// ════════════════════════════════════════════
// RETURNED
// ════════════════════════════════════════════

@Composable
private fun ReturnedView(modifier: Modifier, size: Dp, onComplete: () -> Unit) {
    val anims = rememberReturnedAnims(onComplete)
    val headNod = anims.headNod.value
    val armRise = anims.armRise.value
    val smile = anims.smileAngle.value

    Canvas(modifier = modifier.size(size, size * 1.4f)) {
        val scale = size.toPx() / 200f
        val cx = this.size.width / 2f
        val baseY = this.size.height * 0.4f

        // Desk
        drawDesk(cx, this.size.height * 0.72f, scale)
        drawNotebook(cx - 10 * scale, this.size.height * 0.68f, scale, 0f)

        // Body
        drawBody(cx, baseY + 40 * scale, scale)

        // Arms returning to position
        val armOffset = (1f - armRise) * 15 * scale
        translate(top = armOffset) {
            drawWritingArm(cx, baseY + 50 * scale, scale, 0f)
        }
        drawRestingArm(cx, baseY + 50 * scale, scale)

        // Head with nod
        translate(top = headNod * scale) {
            drawHead(
                cx, baseY, scale,
                blinkProgress = 0f,
                showSmile = true,
                smileAngle = smile
            )
        }
    }
}

// ════════════════════════════════════════════
// SESSION COMPLETE
// ════════════════════════════════════════════

@Composable
private fun SessionCompleteView(modifier: Modifier, size: Dp) {
    val anims = rememberSessionCompleteAnims()
    val bounce = anims.bodyBounce.value
    val clap = anims.armClap.value
    val wiggle = anims.headWiggle.value
    val happy = anims.happyEyes.value
    val sparkle = anims.sparkleAlpha.value

    Canvas(modifier = modifier.size(size, size * 1.4f)) {
        val scale = size.toPx() / 200f
        val cx = this.size.width / 2f
        val baseY = this.size.height * 0.4f

        // Desk
        drawDesk(cx, this.size.height * 0.72f, scale)

        translate(top = bounce * scale) {
            // Body
            drawBody(cx, baseY + 40 * scale, scale)

            // Clapping arms — move toward center based on clap progress
            val armSpread = 20f * (1f - clap)
            // Left arm up
            drawRoundRect(
                color = ShirtColor.darken(0.1f),
                topLeft = Offset(cx - 30 * scale - armSpread * scale, baseY + 10 * scale),
                size = Size(20 * scale, 40 * scale),
                cornerRadius = CornerRadius(8 * scale)
            )
            drawCircle(
                color = SkinColor,
                radius = 6 * scale,
                center = Offset(cx - 20 * scale - armSpread * scale, baseY + 8 * scale)
            )
            // Right arm up
            drawRoundRect(
                color = ShirtColor.darken(0.1f),
                topLeft = Offset(cx + 10 * scale + armSpread * scale, baseY + 10 * scale),
                size = Size(20 * scale, 40 * scale),
                cornerRadius = CornerRadius(8 * scale)
            )
            drawCircle(
                color = SkinColor,
                radius = 6 * scale,
                center = Offset(cx + 20 * scale + armSpread * scale, baseY + 8 * scale)
            )

            // Head with wiggle
            rotate(wiggle, pivot = Offset(cx, baseY)) {
                drawHead(
                    cx, baseY, scale,
                    blinkProgress = 0f,
                    happyEyes = happy > 0.5f,
                    showSmile = true,
                    smileAngle = 30f
                )
            }
        }

        // Sparkles around character
        if (sparkle > 0f) {
            drawSparkles(cx, baseY, scale, sparkle)
        }
    }
}

private fun DrawScope.drawSparkles(cx: Float, cy: Float, scale: Float, alpha: Float) {
    val sparklePositions = listOf(
        Offset(cx - 60 * scale, cy - 40 * scale),
        Offset(cx + 55 * scale, cy - 30 * scale),
        Offset(cx - 50 * scale, cy + 20 * scale),
        Offset(cx + 60 * scale, cy + 10 * scale),
        Offset(cx - 20 * scale, cy - 60 * scale),
        Offset(cx + 25 * scale, cy - 55 * scale),
    )

    sparklePositions.forEach { pos ->
        drawStar(pos, 6 * scale * (0.5f + alpha * 0.7f), SoftGold.copy(alpha = alpha))
    }
}

private fun DrawScope.drawStar(center: Offset, size: Float, color: Color) {
    // 4-pointed star using lines from center
    drawLine(color, Offset(center.x, center.y - size), Offset(center.x, center.y + size), strokeWidth = 2f, cap = StrokeCap.Round)
    drawLine(color, Offset(center.x - size, center.y), Offset(center.x + size, center.y), strokeWidth = 2f, cap = StrokeCap.Round)
    // Diagonal smaller lines
    val dSize = size * 0.6f
    drawLine(color, Offset(center.x - dSize, center.y - dSize), Offset(center.x + dSize, center.y + dSize), strokeWidth = 1.5f, cap = StrokeCap.Round)
    drawLine(color, Offset(center.x + dSize, center.y - dSize), Offset(center.x - dSize, center.y + dSize), strokeWidth = 1.5f, cap = StrokeCap.Round)
}

// ════════════════════════════════════════════
// CHAI BREAK SIP
// ════════════════════════════════════════════

@Composable
private fun ChaiBreakSipView(modifier: Modifier, size: Dp) {
    val anims = rememberChaiBreakSipAnims()
    val headTilt by anims.headTilt
    val steamY1 by anims.steamOffset1
    val steamA1 by anims.steamAlpha1
    val steamY2 by anims.steamOffset2
    val steamA2 by anims.steamAlpha2
    val steamY3 by anims.steamOffset3
    val steamA3 by anims.steamAlpha3

    Canvas(modifier = modifier.size(size, size * 1.4f)) {
        val scale = size.toPx() / 200f
        val cx = this.size.width / 2f
        val baseY = this.size.height * 0.4f

        // Body
        drawBody(cx, baseY + 40 * scale, scale)

        // Both arms holding mug (centered in front of body)
        val mugY = baseY + 70 * scale
        // Left arm going to mug
        drawRoundRect(
            color = ShirtColor.darken(0.1f),
            topLeft = Offset(cx - 35 * scale, baseY + 50 * scale),
            size = Size(20 * scale, 35 * scale),
            cornerRadius = CornerRadius(8 * scale)
        )
        // Right arm going to mug
        drawRoundRect(
            color = ShirtColor.darken(0.1f),
            topLeft = Offset(cx + 15 * scale, baseY + 50 * scale),
            size = Size(20 * scale, 35 * scale),
            cornerRadius = CornerRadius(8 * scale)
        )

        // Hands
        drawCircle(color = SkinColor, radius = 6 * scale, center = Offset(cx - 18 * scale, mugY))
        drawCircle(color = SkinColor, radius = 6 * scale, center = Offset(cx + 18 * scale, mugY))

        // Chai mug
        drawChaiMug(cx, mugY, scale)

        // Steam
        drawSteamLine(cx - 5 * scale, mugY - 12 * scale, steamY1 * scale, steamA1, scale)
        drawSteamLine(cx, mugY - 12 * scale, steamY2 * scale, steamA2, scale)
        drawSteamLine(cx + 5 * scale, mugY - 12 * scale, steamY3 * scale, steamA3, scale)

        // Head tilting toward mug
        rotate(headTilt, pivot = Offset(cx, baseY)) {
            drawHead(
                cx, baseY, scale,
                blinkProgress = 0f,
                eyelidOverride = 0.4f, // content squint
                showSmile = true,
                smileAngle = 20f
            )
        }
    }
}

private fun DrawScope.drawChaiMug(cx: Float, cy: Float, scale: Float) {
    // Mug body
    drawRoundRect(
        color = WarmBrown,
        topLeft = Offset(cx - 12 * scale, cy - 10 * scale),
        size = Size(24 * scale, 22 * scale),
        cornerRadius = CornerRadius(3 * scale)
    )
    // Handle — arc on right
    drawArc(
        color = WarmBrown,
        startAngle = -60f,
        sweepAngle = 120f,
        useCenter = false,
        topLeft = Offset(cx + 10 * scale, cy - 6 * scale),
        size = Size(12 * scale, 14 * scale),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3 * scale)
    )
    // Chai liquid inside
    drawRoundRect(
        color = ChaiOrange.copy(alpha = 0.6f),
        topLeft = Offset(cx - 9 * scale, cy - 4 * scale),
        size = Size(18 * scale, 12 * scale),
        cornerRadius = CornerRadius(2 * scale)
    )
}

private fun DrawScope.drawSteamLine(
    x: Float,
    startY: Float,
    offsetY: Float,
    alpha: Float,
    scale: Float
) {
    if (alpha <= 0f) return

    val path = Path().apply {
        moveTo(x, startY + offsetY)
        cubicTo(
            x + 4 * scale, startY + offsetY - 8 * scale,
            x - 4 * scale, startY + offsetY - 16 * scale,
            x + 2 * scale, startY + offsetY - 24 * scale
        )
    }
    drawPath(
        path,
        color = Color(0xFFBBB5AE).copy(alpha = alpha),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f * scale, cap = StrokeCap.Round)
    )
}

// ════════════════════════════════════════════
// IDLE READING
// ════════════════════════════════════════════

@Composable
private fun IdleReadingView(modifier: Modifier, size: Dp) {
    val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "reading")

    val headTilt by transition.animateFloat(
        initialValue = 0f, targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOut), RepeatMode.Reverse),
        label = "head_tilt"
    )
    val eyeX by transition.animateFloat(
        initialValue = -2f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOut), RepeatMode.Reverse),
        label = "eye_x"
    )
    val pageFlip by transition.animateFloat(
        initialValue = 1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5000
                1f at 0
                1f at 4400
                0f at 4600
                1f at 4800
                1f at 5000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "page_flip"
    )

    Canvas(modifier = modifier.size(size, size * 1.4f)) {
        val scale = size.toPx() / 200f
        val cx = this.size.width / 2f
        val baseY = this.size.height * 0.4f

        // Body
        drawBody(cx, baseY + 40 * scale, scale)

        // Both arms raised holding book
        drawRoundRect(
            color = ShirtColor.darken(0.1f),
            topLeft = Offset(cx - 40 * scale, baseY + 30 * scale),
            size = Size(20 * scale, 40 * scale),
            cornerRadius = CornerRadius(8 * scale)
        )
        drawRoundRect(
            color = ShirtColor.darken(0.1f),
            topLeft = Offset(cx + 20 * scale, baseY + 30 * scale),
            size = Size(20 * scale, 40 * scale),
            cornerRadius = CornerRadius(8 * scale)
        )

        // Book
        val bookY = baseY + 25 * scale
        // Left page
        drawRoundRect(
            color = CreamWhite,
            topLeft = Offset(cx - 30 * scale, bookY),
            size = Size(30 * scale, 40 * scale),
            cornerRadius = CornerRadius(2 * scale)
        )
        // Right page (with page flip animation)
        drawRoundRect(
            color = CreamWhite,
            topLeft = Offset(cx, bookY),
            size = Size(30 * scale * pageFlip, 40 * scale),
            cornerRadius = CornerRadius(2 * scale)
        )
        // Book spine
        drawLine(
            color = WarmBrown,
            start = Offset(cx, bookY),
            end = Offset(cx, bookY + 40 * scale),
            strokeWidth = 2 * scale
        )
        // Book text lines
        for (i in 0..4) {
            val lineY = bookY + 8 * scale + i * 6 * scale
            drawLine(
                color = Color(0x30000000),
                start = Offset(cx - 25 * scale, lineY),
                end = Offset(cx - 5 * scale, lineY),
                strokeWidth = 0.5f * scale
            )
        }

        // Hands holding book
        drawCircle(color = SkinColor, radius = 5 * scale, center = Offset(cx - 30 * scale, bookY + 20 * scale))
        drawCircle(color = SkinColor, radius = 5 * scale, center = Offset(cx + 30 * scale, bookY + 20 * scale))

        // Head
        rotate(headTilt, pivot = Offset(cx, baseY)) {
            drawHead(cx, baseY, scale, blinkProgress = 0f, pupilXOffset = eyeX)
        }
    }
}

// ── Color utility ──

private fun Color.darken(factor: Float): Color {
    return Color(
        red = (red * (1f - factor)).coerceIn(0f, 1f),
        green = (green * (1f - factor)).coerceIn(0f, 1f),
        blue = (blue * (1f - factor)).coerceIn(0f, 1f),
        alpha = alpha
    )
}
