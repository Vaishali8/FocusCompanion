package com.saathi.focuscompanion.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.saathi.focuscompanion.data.model.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Insert
    suspend fun insertSession(session: StudySession): Long

    @Update
    suspend fun updateSession(session: StudySession)

    @Query("SELECT * FROM study_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE id = :id")
    suspend fun getSession(id: Int): StudySession?

    @Query("SELECT * FROM study_sessions WHERE startTime >= :startOfDay AND startTime < :endOfDay")
    suspend fun getSessionsForDay(startOfDay: Long, endOfDay: Long): List<StudySession>

    @Query("SELECT COUNT(*) FROM study_sessions WHERE subject = :subject AND completed = 1")
    suspend fun getCompletedSessionCountBySubject(subject: String): Int

    @Query("DELETE FROM study_sessions")
    suspend fun deleteAll()
}
