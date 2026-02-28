package com.saathi.focuscompanion.ui.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.saathi.focuscompanion.R
import com.saathi.focuscompanion.ui.theme.ChaiOrange
import com.saathi.focuscompanion.ui.theme.WarmBrown

private val subjects = listOf(
    "Physics", "Chemistry", "Maths", "Biology",
    "History", "Geography", "English", "Hindi",
    "Economics", "CS", "Other"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SubjectTagSheet(
    onDismiss: () -> Unit,
    onSave: (subject: String, topic: String?) -> Unit
) {
    var selectedSubject by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Kya padha aaj?",
                style = MaterialTheme.typography.headlineMedium,
                color = WarmBrown
            )

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjects.forEach { subject ->
                    FilterChip(
                        selected = selectedSubject == subject,
                        onClick = { selectedSubject = subject },
                        label = { Text(subject) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChaiOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text(stringResource(R.string.subject_prompt)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.abhi_nahi), color = WarmBrown)
                }
                Button(
                    onClick = {
                        if (selectedSubject.isNotEmpty()) {
                            onSave(selectedSubject, topic.ifBlank { null })
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ChaiOrange),
                    shape = RoundedCornerShape(12.dp),
                    enabled = selectedSubject.isNotEmpty()
                ) {
                    Text(stringResource(R.string.save_karo))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
