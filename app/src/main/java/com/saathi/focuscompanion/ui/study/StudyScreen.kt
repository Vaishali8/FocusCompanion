package com.saathi.focuscompanion.ui.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saathi.focuscompanion.R
import com.saathi.focuscompanion.data.model.SessionState
import com.saathi.focuscompanion.ui.character.CharacterState
import com.saathi.focuscompanion.ui.character.CharacterView
import com.saathi.focuscompanion.ui.room.RoomBackground
import com.saathi.focuscompanion.ui.theme.ChaiOrange
import com.saathi.focuscompanion.ui.theme.WarmBrown

@Composable
fun StudyScreen(
    viewModel: StudyViewModel,
    isInPipMode: Boolean = false
) {
    val profile by viewModel.profile.collectAsState()
    val sessionState by viewModel.sessionState.collectAsState()
    val characterState by viewModel.characterState.collectAsState()
    val timeRemaining by viewModel.timeRemainingSeconds.collectAsState()
    val totalTime by viewModel.totalTimeSeconds.collectAsState()
    val showDialog by viewModel.showCompletionDialog.collectAsState()

    if (isInPipMode) {
        // Simplified PiP view — just the character head
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CharacterView(
                state = characterState,
                size = 120.dp
            )
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Room background
        RoomBackground(city = profile?.city ?: "")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = profile?.companionName ?: "Saathi",
                    style = MaterialTheme.typography.headlineMedium,
                    color = WarmBrown,
                    fontWeight = FontWeight.Bold
                )
                val streak = profile?.currentStreakDays ?: 0
                if (streak > 0) {
                    Text(
                        text = "\uD83D\uDD25 $streak",
                        fontSize = 18.sp,
                        color = WarmBrown,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Character
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CharacterView(
                    state = characterState,
                    size = 180.dp,
                    onReturnComplete = {
                        viewModel.setCharacterState(CharacterState.IDLE_STUDYING)
                    }
                )
            }

            // Timer ring
            if (sessionState == SessionState.STUDYING || sessionState == SessionState.CHAI_BREAK) {
                TimerRing(
                    timeRemainingSeconds = timeRemaining,
                    totalTimeSeconds = totalTime
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (sessionState) {
                    SessionState.IDLE -> {
                        Button(
                            onClick = { viewModel.startStudySession() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChaiOrange,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .height(56.dp)
                                .width(200.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.shuru),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    SessionState.STUDYING -> {
                        Button(
                            onClick = { viewModel.endSession() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WarmBrown,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.bas_khatam),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    SessionState.CHAI_BREAK -> {
                        // Break timer shown, no action needed here
                    }
                    SessionState.COMPLETED -> {
                        // Handled by dialog
                    }
                }
            }
        }

        // Completion dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissCompletionDialog() },
                title = {
                    Text(
                        text = stringResource(R.string.ek_aur_round),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.ready_ho),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.startAnotherRound() },
                        colors = ButtonDefaults.buttonColors(containerColor = ChaiOrange)
                    ) {
                        Text(stringResource(R.string.haan))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.endSession() }) {
                        Text(
                            stringResource(R.string.nahi_ab_bas),
                            color = WarmBrown
                        )
                    }
                }
            )
        }
    }
}
