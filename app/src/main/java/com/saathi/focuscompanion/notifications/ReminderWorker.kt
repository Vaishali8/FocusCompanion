package com.saathi.focuscompanion.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.saathi.focuscompanion.SaathiApplication

class ReminderWorker(
    private val ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val companionName = inputData.getString("companion_name") ?: "Saathi"
        val streakDays = inputData.getInt("streak_days", 0)

        val messages = listOf(
            "$companionName intezaar kar raha hai. Padhai shuru karein?",
            "Aaj ka session baaki hai! Chai ready hai.",
            if (streakDays > 0) "Streak mat todna! $streakDays din ho gaye hain." else "Ek session shuru karo aaj!",
            "Ek session? Sirf 20 minute. $companionName tayaar hai."
        )

        val notification = NotificationCompat.Builder(ctx, SaathiApplication.REMINDER_CHANNEL_ID)
            .setContentTitle(companionName)
            .setContentText(messages.random())
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setAutoCancel(true)
            .build()

        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(2001, notification)

        return Result.success()
    }
}
