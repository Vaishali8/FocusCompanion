package com.saathi.focuscompanion

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.saathi.focuscompanion.data.db.SaathiDatabase
import com.saathi.focuscompanion.data.repository.StudyRepository

class SaathiApplication : Application() {

    val database: SaathiDatabase by lazy { SaathiDatabase.getDatabase(this) }
    val repository: StudyRepository by lazy {
        StudyRepository(database.userProfileDao(), database.studySessionDao())
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val timerChannel = NotificationChannel(
            TIMER_CHANNEL_ID,
            "Study Timer",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows timer progress during study sessions"
        }

        val reminderChannel = NotificationChannel(
            REMINDER_CHANNEL_ID,
            "Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Study reminders from your Saathi"
        }

        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(timerChannel)
        nm.createNotificationChannel(reminderChannel)
    }

    companion object {
        const val TIMER_CHANNEL_ID = "study_timer"
        const val REMINDER_CHANNEL_ID = "reminders"
    }
}
