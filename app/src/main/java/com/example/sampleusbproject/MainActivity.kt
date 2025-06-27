package com.example.sampleusbproject

import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.sampleusbproject.data.remote.socket.SocketStatus
import com.example.sampleusbproject.databinding.ActivityMainBinding
import com.example.sampleusbproject.domain.interfaces.LockerBoardInterface
import com.example.sampleusbproject.usecases.PostomatSocketUseCase
import com.example.sampleusbproject.utils.AliveService
import com.example.sampleusbproject.utils.AppStatus
import com.example.sampleusbproject.utils.CommonPrefs
import com.example.sampleusbproject.utils.ScreensaverManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var screensaverManager: ScreensaverManager

    private lateinit var binding: ActivityMainBinding

    val viewModel: MainActivityViewModel by lazy {
        ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    @Inject
    lateinit var commonPrefs: CommonPrefs

    @Inject
    lateinit var postomatSocketUseCase: PostomatSocketUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        viewModel.connectLockerBoard()

        setContentView(binding.root)
        val intent = Intent(this, AliveService::class.java)
        startService(intent)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Оставляем только отступы слева и справа, убираем верхний и нижний
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        try {
            Settings.System.putInt(applicationContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255);
        } catch (e: Exception){
            Log.e("sdfsdfsdfsdf", "onCreate: ${e.message}", )
        }
        
        screensaverManager = ScreensaverManager(this,commonPrefs)


        setupNavigation()
        setupListeners()
        setupViews()
        connectSocket()
    }

    override fun onStart() {
        super.onStart()
        AppStatus.isInForeground = true
    }

    override fun onStop() {
        super.onStop()
        AppStatus.isInForeground = false
    }
    override fun onResume() {
        super.onResume()
        tryStartKioskMode()

        screensaverManager.start()
    }

    private fun tryStartKioskMode() {
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val isPermitted = dpm.isLockTaskPermitted(packageName)
        val isInLockTask = activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE

        Log.d("Kiosk", "isPermitted=$isPermitted, isInLockTask=$isInLockTask")

        if (isPermitted && !isInLockTask) {
            try {
                startLockTask()
                Log.i("Kiosk", "LockTask started")
            } catch (e: IllegalStateException) {
                Log.e("Kiosk", "Cannot start LockTask: ${e.message}")
            }
        } else {
            Log.w("Kiosk", "LockTask not permitted or already in LockTask")
        }
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navGraph =
            navHostFragment.navController.navInflater.inflate(R.navigation.app_navigation)
        
        navGraph.setStartDestination(
            if (commonPrefs.isAuthorized()) R.id.mainFragment else R.id.authFragment
        )
        
        navHostFragment.navController.graph = navGraph
    }

    private fun setupViews() {

    }

    private fun setupListeners() {

    }

    fun connectSocket() {
        lifecycleScope.launch {
            postomatSocketUseCase.connectToPostomatServer().collectLatest {
                when(it) {
                    is SocketStatus.Connected -> {
                        postomatSocketUseCase.requestPostomatInfo()
                        Timber.w("Socket Connected")
                    }
                    is SocketStatus.Disconnected -> {
                        Timber.w("Socket Disconnected ${it.reason}")
                    }
                    is SocketStatus.Error -> {
                        Timber.w("Socket Error ${it.error}")
                    }
                }
            }
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        screensaverManager.resetIdleTimer()
    }

    override fun onPause() {
        super.onPause()
        screensaverManager.stop()
    }
    override fun onDestroy() {
        super.onDestroy()
        stopLockTask()
        viewModel.disconnectLockerBoard()
    }
}