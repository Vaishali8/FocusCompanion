package com.saathi.focuscompanion.data.repository

import com.saathi.focuscompanion.data.db.StudySessionDao
import com.saathi.focuscompanion.data.db.UserProfileDao
import com.saathi.focuscompanion.data.model.StudySession
import com.saathi.focuscompanion.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

class StudyRepository(
    private val userProfileDao: UserProfileDao,
    private val studySessionDao: StudySessionDao
) {
    val profile: Flow<UserProfile?> = userProfileDao.getProfile()
    val allSessions: Flow<List<StudySession>> = studySessionDao.getAllSessions()

    suspend fun getProfile(): UserProfile? = userProfileDao.getProfileSync()

    suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: UserProfile) {
        userProfileDao.updateProfile(profile)
    }

    suspend fun insertSession(session: StudySession): Long {
        return studySessionDao.insertSession(session)
    }

    suspend fun updateSession(session: StudySession) {
        studySessionDao.updateSession(session)
    }

    suspend fun getSession(id: Int): StudySession? {
        return studySessionDao.getSession(id)
    }

    suspend fun getSessionsForDay(startOfDay: Long, endOfDay: Long): List<StudySession> {
        return studySessionDao.getSessionsForDay(startOfDay, endOfDay)
    }

    suspend fun deleteAll() {
        studySessionDao.deleteAll()
    }
}
