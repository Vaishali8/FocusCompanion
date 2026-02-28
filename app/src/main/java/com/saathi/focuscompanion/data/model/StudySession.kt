package com.saathi.focuscompanion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val subject: String? = null,
    val completed: Boolean = false,
    val durationMinutes: Int = 0,
    val earlyExits: Int = 0
)
