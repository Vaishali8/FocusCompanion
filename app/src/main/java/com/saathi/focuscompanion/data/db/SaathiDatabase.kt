package com.saathi.focuscompanion.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.saathi.focuscompanion.data.model.StudySession
import com.saathi.focuscompanion.data.model.UserProfile

@Database(
    entities = [UserProfile::class, StudySession::class],
    version = 1,
    exportSchema = false
)
abstract class SaathiDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun studySessionDao(): StudySessionDao

    companion object {
        @Volatile
        private var INSTANCE: SaathiDatabase? = null

        fun getDatabase(context: Context): SaathiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SaathiDatabase::class.java,
                    "saathi_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
