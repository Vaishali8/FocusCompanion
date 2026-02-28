package com.saathi.focuscompanion.ui.study

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.saathi.focuscompanion.SaathiApplication
import com.saathi.focuscompanion.data.model.SessionState
import com.saathi.focuscompanion.data.model.StudySession
import com.saathi.focuscompanion.data.model.UserProfile
import com.saathi.focuscompanion.ui.character.CharacterState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as SaathiApplication).repository

    val profile: StateFlow<UserProfile?> = repository.profile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _sessionState = MutableStateFlow(SessionState.IDLE)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private val _characterState = MutableStateFlow(CharacterState.IDLE_STUDYING)
    val characterState: StateFlow<CharacterState> = _characterState.asStateFlow()

    private val _timeRemainingSeconds = MutableStateFlow(0)
    val timeRemainingSeconds: StateFlow<Int> = _timeRemainingSeconds.asStateFlow()

    private val _totalTimeSeconds = MutableStateFlow(0)
    val totalTimeSeconds: StateFlow<Int> = _totalTimeSeconds.asStateFlow()

    private val _earlyExits = MutableStateFlow(0)
    val earlyExits: StateFlow<Int> = _earlyExits.asStateFlow()

    private val _showCompletionDialog = MutableStateFlow(false)
    val showCompletionDialog: StateFlow<Boolean> = _showCompletionDialog.asStateFlow()

    private val _currentSessionId = MutableStateFlow<Long?>(null)

    private var timerJob: Job? = null
    private var isInPip = false

    fun startStudySession() {
        val prof = profile.value ?: return
        val durationSeconds = prof.studyDurationMinutes * 60
        _totalTimeSeconds.value = durationSeconds
        _timeRemainingSeconds.value = durationSeconds
        _sessionState.value = SessionState.STUDYING
        _characterState.value = CharacterState.IDLE_STUDYING
        _earlyExits.value = 0

        viewModelScope.launch {
            val session = StudySession(
                startTime = System.currentTimeMillis(),
                durationMinutes = prof.studyDurationMinutes
            )
            _currentSessionId.value = repository.insertSession(session)
        }

        startTimer()
    }

    fun startBreak() {
        val prof = profile.value ?: return
        val breakSeconds = prof.breakDurationMinutes * 60
        _totalTimeSeconds.value = breakSeconds
        _timeRemainingSeconds.value = breakSeconds
        _sessionState.value = SessionState.CHAI_BREAK
        _characterState.value = CharacterState.CHAI_BREAK_SIP
        startTimer()
    }

    fun startAnotherRound() {
        _showCompletionDialog.value = false
        startStudySession()
    }

    fun endSession() {
        _showCompletionDialog.value = false
        _sessionState.value = SessionState.IDLE
        _characterState.value = CharacterState.IDLE_STUDYING
        timerJob?.cancel()

        viewModelScope.launch {
            _currentSessionId.value?.let { id ->
                repository.getSession(id.toInt())?.let { session ->
                    repository.updateSession(
                        session.copy(
                            endTime = System.currentTimeMillis(),
                            completed = true,
                            earlyExits = _earlyExits.value
                        )
                    )
                }
            }
            // Update profile stats
            profile.value?.let { prof ->
                repository.updateProfile(
                    prof.copy(
                        totalSessionsCompleted = prof.totalSessionsCompleted + 1,
                        totalMinutesStudied = prof.totalMinutesStudied + prof.studyDurationMinutes,
                        lastStudyDate = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun dismissCompletionDialog() {
        _showCompletionDialog.value = false
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timeRemainingSeconds.value > 0) {
                delay(1000L)
                _timeRemainingSeconds.value -= 1
            }
            onTimerComplete()
        }
    }

    private fun onTimerComplete() {
        when (_sessionState.value) {
            SessionState.STUDYING -> {
                _characterState.value = CharacterState.SESSION_COMPLETE
                viewModelScope.launch {
                    delay(1500L) // Let the completion animation play
                    _sessionState.value = SessionState.CHAI_BREAK
                    startBreak()
                }
            }
            SessionState.CHAI_BREAK -> {
                _showCompletionDialog.value = true
            }
            else -> {}
        }
    }

    fun onUserLeaveApp() {
        if (_sessionState.value == SessionState.STUDYING) {
            _earlyExits.value += 1
            _characterState.value = CharacterState.PHONE_CAUGHT
        }
    }

    fun onEnterPipMode() {
        isInPip = true
        if (_sessionState.value == SessionState.STUDYING) {
            _characterState.value = CharacterState.PIP_JUDGING
            // After 60 seconds in PiP, switch to impatient
            viewModelScope.launch {
                delay(60_000L)
                if (isInPip && _sessionState.value == SessionState.STUDYING) {
                    _characterState.value = CharacterState.PIP_IMPATIENT
                }
            }
        }
    }

    fun onReturnFromPip() {
        isInPip = false
        if (_sessionState.value == SessionState.STUDYING) {
            _characterState.value = CharacterState.RETURNED
            viewModelScope.launch {
                delay(800L)
                _characterState.value = CharacterState.IDLE_STUDYING
            }
        }
    }

    fun setCharacterState(state: CharacterState) {
        _characterState.value = state
    }

    // Save/update profile from onboarding or settings
    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }

    fun updateTimeFromService(seconds: Int) {
        _timeRemainingSeconds.value = seconds
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
