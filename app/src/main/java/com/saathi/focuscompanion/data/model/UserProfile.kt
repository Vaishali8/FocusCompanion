package com.saathi.focuscompanion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val companionName: String = "Saathi",
    val city: String = "",
    val studyDurationMinutes: Int = 20,
    val breakDurationMinutes: Int = 5,
    val totalSessionsCompleted: Int = 0,
    val totalMinutesStudied: Int = 0,
    val currentStreakDays: Int = 0,
    val lastStudyDate: Long = 0L
)
