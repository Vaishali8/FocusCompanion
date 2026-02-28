package com.saathi.focuscompanion

import android.app.PictureInPictureModeChangedInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Consumer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saathi.focuscompanion.data.model.SessionState
import com.saathi.focuscompanion.pip.PipManager
import com.saathi.focuscompanion.service.StudyTimerService
import com.saathi.focuscompanion.ui.study.StudyScreen
import com.saathi.focuscompanion.ui.study.StudyViewModel
import com.saathi.focuscompanion.ui.theme.CreamWhite
import com.saathi.focuscompanion.ui.theme.SaathiTheme

class MainActivity : ComponentActivity() {

    private var studyViewModel: StudyViewModel? = null
    private var isInPipMode = mutableStateOf(false)
    private var timerService: StudyTimerService? = null
    private var serviceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val service = (binder as StudyTimerService.TimerBinder).getService()
            timerService = service
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
            serviceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        setContent {
            SaathiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CreamWhite
                ) {
                    SaathiApp(isInPipMode.value)
                }
            }
        }

        // PiP mode listener
        addOnPictureInPictureModeChangedListener(pipChangeListener)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == PipManager.ACTION_RETURN_FROM_PIP) {
            studyViewModel?.onReturnFromPip()
        }
    }

    private val pipChangeListener = Consumer<PictureInPictureModeChangedInfo> { info ->
        isInPipMode.value = info.isInPictureInPictureMode
        if (info.isInPictureInPictureMode) {
            studyViewModel?.onEnterPipMode()
        } else {
            studyViewModel?.onReturnFromPip()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        val vm = studyViewModel
        if (vm != null) {
            val sessionState = vm.sessionState.value
            if (sessionState == SessionState.STUDYING) {
                vm.onUserLeaveApp()
                PipManager.enterStudyPip(
                    this,
                    vm.profile.value?.companionName ?: "Saathi"
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Bind to timer service
        Intent(this, StudyTimerService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (serviceBound) {
            unbindService(serviceConnection)
            serviceBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOnPictureInPictureModeChangedListener(pipChangeListener)
    }

    fun setViewModel(vm: StudyViewModel) {
        studyViewModel = vm
    }

    fun startTimerService(durationSeconds: Int, companionName: String) {
        val intent = Intent(this, StudyTimerService::class.java).apply {
            action = StudyTimerService.ACTION_START
            putExtra(StudyTimerService.EXTRA_DURATION_SECONDS, durationSeconds)
            putExtra(StudyTimerService.EXTRA_COMPANION_NAME, companionName)
        }
        startForegroundService(intent)
    }

    fun stopTimerService() {
        val intent = Intent(this, StudyTimerService::class.java).apply {
            action = StudyTimerService.ACTION_STOP
        }
        startService(intent)
    }
}

@Composable
fun SaathiApp(isInPipMode: Boolean) {
    val navController = rememberNavController()
    val viewModel: StudyViewModel = viewModel()

    // Register ViewModel with activity for PiP callbacks
    val context = LocalContext.current
    DisposableEffect(viewModel) {
        (context as? MainActivity)?.setViewModel(viewModel)
        onDispose { }
    }

    val profile by viewModel.profile.collectAsState()
    val hasProfile = profile != null

    NavHost(
        navController = navController,
        startDestination = if (hasProfile) "study" else "study" // Simplified: skip onboarding for MVP steps 1-6
    ) {
        composable("study") {
            StudyScreen(
                viewModel = viewModel,
                isInPipMode = isInPipMode
            )
        }
    }
}
