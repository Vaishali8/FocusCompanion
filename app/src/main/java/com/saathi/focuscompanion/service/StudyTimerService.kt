package com.saathi.focuscompanion.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.saathi.focuscompanion.MainActivity
import com.saathi.focuscompanion.SaathiApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudyTimerService : Service() {

    private val binder = TimerBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _timeRemainingSeconds = MutableStateFlow(0)
    val timeRemainingSeconds: StateFlow<Int> = _timeRemainingSeconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var timerJob: Job? = null
    private var companionName: String = "Saathi"

    inner class TimerBinder : Binder() {
        fun getService(): StudyTimerService = this@StudyTimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getIntExtra(EXTRA_DURATION_SECONDS, 1200)
                companionName = intent.getStringExtra(EXTRA_COMPANION_NAME) ?: "Saathi"
                startTimer(duration)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_STICKY
    }

    fun startTimer(durationSeconds: Int) {
        _timeRemainingSeconds.value = durationSeconds
        _isRunning.value = true

        startForeground(NOTIFICATION_ID, createNotification())

        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (_timeRemainingSeconds.value > 0 && _isRunning.value) {
                delay(1000L)
                _timeRemainingSeconds.value -= 1
                // Update notification periodically
                if (_timeRemainingSeconds.value % 10 == 0) {
                    val nm = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
                    nm.notify(NOTIFICATION_ID, createNotification())
                }
            }

            if (_timeRemainingSeconds.value <= 0) {
                // Timer completed
                sendBroadcast(Intent(ACTION_TIMER_COMPLETE))
            }
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
    }

    fun stopTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(): Notification {
        val mins = _timeRemainingSeconds.value / 60
        val secs = _timeRemainingSeconds.value % 60
        val timeText = String.format("%02d:%02d", mins, secs)

        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, SaathiApplication.TIMER_CHANNEL_ID)
            .setContentTitle("$companionName padhai kar raha hai...")
            .setContentText("$timeText remaining")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setSilent(true)
            .setContentIntent(openIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.saathi.ACTION_START_TIMER"
        const val ACTION_PAUSE = "com.saathi.ACTION_PAUSE_TIMER"
        const val ACTION_STOP = "com.saathi.ACTION_STOP_TIMER"
        const val ACTION_TIMER_COMPLETE = "com.saathi.TIMER_COMPLETE"
        const val EXTRA_DURATION_SECONDS = "duration_seconds"
        const val EXTRA_COMPANION_NAME = "companion_name"
    }
}
