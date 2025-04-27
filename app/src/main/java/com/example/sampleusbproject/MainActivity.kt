package com.example.sampleusbproject

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.sampleusbproject.data.remote.socket.SocketStatus
import com.example.sampleusbproject.databinding.ActivityMainBinding
import com.example.sampleusbproject.usecases.PostomatSocketUseCase
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

    @Inject
    lateinit var commonPrefs: CommonPrefs

    @Inject
    lateinit var postomatSocketUseCase: PostomatSocketUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        screensaverManager = ScreensaverManager(this)
        setupNavigation()
        setupListeners()
        setupViews()
        connectSocket()
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

    private fun connectSocket() {
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

    override fun onResume() {
        super.onResume()
        screensaverManager.start()
        if (isScreenPinningAvailable(this)) {
            startScreenPinning()
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
        stopScreenPinning()
    }

    private fun isScreenPinningAvailable(context: Context): Boolean {
        val devicePolicyManager = this.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager?
        return devicePolicyManager?.isLockTaskPermitted(context.packageName) ?: false
    }

    // Запуск режима закрепления
    private fun startScreenPinning() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                // Программное закрепление экрана
                startLockTask()
                Toast.makeText(this, "Приложение закреплено", Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                // Обработка случая, когда у приложения недостаточно прав
                Toast.makeText(this, "Ошибка закрепления экрана", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Выход из режима закрепления
    private fun stopScreenPinning() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                stopLockTask()
                Toast.makeText(this, "Приложение откреплено", Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                Toast.makeText(this, "Ошибка открепления экрана", Toast.LENGTH_SHORT).show()
            }
        }
    }
}