package com.saathi.focuscompanion.ui.character

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay

// ── Idle Studying Animations ──

data class IdleStudyingAnims(
    val armYOffset: State<Float>,
    val headRotation: State<Float>,
    val penXOffset: State<Float>,
    val blinkProgress: State<Float>
)

@Composable
fun rememberIdleStudyingAnims(): IdleStudyingAnims {
    val transition = rememberInfiniteTransition(label = "idle_studying")

    val armYOffset = transition.animateFloat(
        initialValue = 0f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arm_y"
    )

    val headRotation = transition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "head_rot"
    )

    val penXOffset = transition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pen_x"
    )

    val blinkProgress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3000
                0f at 0
                0f at 2800
                1f at 2880
                1f at 2920
                0f at 3000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )

    return IdleStudyingAnims(armYOffset, headRotation, penXOffset, blinkProgress)
}

// ── Phone Caught (one-shot) Animations ──

data class PhoneCaughtAnims(
    val headRotation: Animatable<Float, AnimationVector1D>,
    val armDrop: Animatable<Float, AnimationVector1D>,
    val eyebrowRaise: Animatable<Float, AnimationVector1D>,
    val rightEyebrowRotation: Animatable<Float, AnimationVector1D>,
    val eyeWiden: Animatable<Float, AnimationVector1D>
)

@Composable
fun rememberPhoneCaughtAnims(): PhoneCaughtAnims {
    val headRotation = remember { Animatable(0f) }
    val armDrop = remember { Animatable(0f) }
    val eyebrowRaise = remember { Animatable(0f) }
    val rightEyebrowRotation = remember { Animatable(0f) }
    val eyeWiden = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Snap head upright
        headRotation.animateTo(0f, tween(200, easing = FastOutSlowInEasing))
        // Drop arm
        armDrop.animateTo(1f, tween(300, easing = EaseInOut))
        // Raise eyebrows
        eyebrowRaise.animateTo(6f, tween(200, easing = EaseInOut))
        // Right eyebrow skeptical
        rightEyebrowRotation.animateTo(-10f, tween(150))
        // Widen eyes
        eyeWiden.animateTo(1f, tween(150))
    }

    return PhoneCaughtAnims(headRotation, armDrop, eyebrowRaise, rightEyebrowRotation, eyeWiden)
}

// ── PiP Judging Animations ──

data class PipJudgingAnims(
    val pupilXOffset: State<Float>,
    val headTilt: State<Float>
)

@Composable
fun rememberPipJudgingAnims(): PipJudgingAnims {
    val transition = rememberInfiniteTransition(label = "pip_judging")

    val pupilXOffset = transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3500
                0f at 0
                8f at 1000
                8f at 3000
                0f at 3500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "pupil_x"
    )

    val headTilt = transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "head_tilt"
    )

    return PipJudgingAnims(pupilXOffset, headTilt)
}

// ── PiP Impatient Animations ──

data class PipImpatientAnims(
    val headNod: State<Float>,
    val eyelidClose: Float
)

@Composable
fun rememberPipImpatientAnims(): PipImpatientAnims {
    val transition = rememberInfiniteTransition(label = "pip_impatient")

    val headNod = transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "head_nod"
    )

    return PipImpatientAnims(headNod, 0.6f)
}

// ── Waiting Animations ──

data class WaitingAnims(
    val fingerTap: State<Float>,
    val headShake: State<Float>
)

@Composable
fun rememberWaitingAnims(): WaitingAnims {
    val transition = rememberInfiniteTransition(label = "waiting")

    val fingerTap = transition.animateFloat(
        initialValue = 0f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "finger_tap"
    )

    val headShake = transition.animateFloat(
        initialValue = 0f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "head_shake"
    )

    return WaitingAnims(fingerTap, headShake)
}

// ── Returned (one-shot) Animations ──

data class ReturnedAnims(
    val headNod: Animatable<Float, AnimationVector1D>,
    val armRise: Animatable<Float, AnimationVector1D>,
    val smileAngle: Animatable<Float, AnimationVector1D>,
    val complete: Boolean
)

@Composable
fun rememberReturnedAnims(onComplete: () -> Unit): ReturnedAnims {
    val headNod = remember { Animatable(0f) }
    val armRise = remember { Animatable(0f) }
    val smileAngle = remember { Animatable(0f) }
    val complete = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Nod
        headNod.animateTo(8f, tween(200, easing = EaseInOut))
        headNod.animateTo(0f, tween(200, easing = EaseInOut))
        // Arm back up
        armRise.animateTo(1f, tween(500, easing = EaseInOut))
        // Smile
        smileAngle.animateTo(30f, tween(300, easing = EaseInOut))
        delay(300)
        complete.value = true
        onComplete()
    }

    return ReturnedAnims(headNod, armRise, smileAngle, complete.value)
}

// ── Session Complete (one-shot) Animations ──

data class SessionCompleteAnims(
    val bodyBounce: Animatable<Float, AnimationVector1D>,
    val armClap: Animatable<Float, AnimationVector1D>,
    val headWiggle: Animatable<Float, AnimationVector1D>,
    val happyEyes: Animatable<Float, AnimationVector1D>,
    val sparkleAlpha: Animatable<Float, AnimationVector1D>
)

@Composable
fun rememberSessionCompleteAnims(): SessionCompleteAnims {
    val bodyBounce = remember { Animatable(0f) }
    val armClap = remember { Animatable(0f) }
    val headWiggle = remember { Animatable(0f) }
    val happyEyes = remember { Animatable(0f) }
    val sparkleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Start happy eyes
        happyEyes.animateTo(1f, tween(200))

        // Body bounce
        bodyBounce.animateTo(-10f, tween(200, easing = FastOutSlowInEasing))
        bodyBounce.animateTo(0f, tween(200))
        bodyBounce.animateTo(-6f, tween(150))
        bodyBounce.animateTo(0f, tween(150))

        // Head wiggle
        headWiggle.animateTo(10f, tween(200))
        headWiggle.animateTo(-10f, tween(200))
        headWiggle.animateTo(0f, tween(200))

        // Arm clap 3x
        repeat(3) {
            armClap.animateTo(1f, tween(100))
            armClap.animateTo(0f, tween(100))
        }

        // Sparkles
        sparkleAlpha.animateTo(1f, tween(300))
        delay(400)
        sparkleAlpha.animateTo(0f, tween(300))
    }

    return SessionCompleteAnims(bodyBounce, armClap, headWiggle, happyEyes, sparkleAlpha)
}

// ── Chai Break Sip Animations ──

data class ChaiBreakSipAnims(
    val headTilt: State<Float>,
    val steamOffset1: State<Float>,
    val steamAlpha1: State<Float>,
    val steamOffset2: State<Float>,
    val steamAlpha2: State<Float>,
    val steamOffset3: State<Float>,
    val steamAlpha3: State<Float>
)

@Composable
fun rememberChaiBreakSipAnims(): ChaiBreakSipAnims {
    val transition = rememberInfiniteTransition(label = "chai_break")

    val headTilt = transition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "head_tilt"
    )

    fun steamOffset(delay: Int): State<Float> = transition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(delay)
        ),
        label = "steam_offset_$delay"
    )

    fun steamAlpha(delay: Int): State<Float> = transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(delay)
        ),
        label = "steam_alpha_$delay"
    )

    return ChaiBreakSipAnims(
        headTilt = headTilt,
        steamOffset1 = steamOffset(0),
        steamAlpha1 = steamAlpha(0),
        steamOffset2 = steamOffset(400),
        steamAlpha2 = steamAlpha(400),
        steamOffset3 = steamOffset(800),
        steamAlpha3 = steamAlpha(800)
    )
}
