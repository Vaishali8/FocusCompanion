package com.saathi.focuscompanion.ui.break_

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saathi.focuscompanion.ui.character.CharacterState
import com.saathi.focuscompanion.ui.character.CharacterView
import com.saathi.focuscompanion.ui.room.RoomBackground
import com.saathi.focuscompanion.ui.theme.CreamWhite
import com.saathi.focuscompanion.ui.theme.WarmBrown
import com.saathi.focuscompanion.util.funFacts
import kotlinx.coroutines.delay

@Composable
fun ChaiBreakScreen(
    timeRemainingSeconds: Int,
    city: String = ""
) {
    val funFact = remember { funFacts.random() }
    var showFact by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500L)
        showFact = true
    }

    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Box(modifier = Modifier.fillMaxSize()) {
        RoomBackground(city = city)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Timer (small, non-prominent)
            Text(
                text = timeText,
                style = MaterialTheme.typography.bodyLarge,
                color = WarmBrown.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Character sipping chai
            Box(contentAlignment = Alignment.TopCenter) {
                CharacterView(
                    state = CharacterState.CHAI_BREAK_SIP,
                    size = 220.dp
                )
                ChaiSteam(
                    modifier = Modifier.offset(y = 80.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Fun fact card
            AnimatedVisibility(
                visible = showFact,
                enter = slideInVertically(initialOffsetY = { it })
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = WarmBrown,
                        contentColor = CreamWhite
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Kya aap jaante hain?",
                            style = MaterialTheme.typography.labelLarge,
                            color = CreamWhite.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = funFact,
                            style = MaterialTheme.typography.bodyLarge,
                            color = CreamWhite
                        )
                    }
                }
            }
        }
    }
}
