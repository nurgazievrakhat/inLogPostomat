package com.example.sampleusbproject

import android.app.AlertDialog
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.sampleusbproject.admin.KioskManager
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
    lateinit var kioskManager: KioskManager

    @Inject
    lateinit var commonPrefs: CommonPrefs

    @Inject
    lateinit var postomatSocketUseCase: PostomatSocketUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.hide(WindowInsets.Type.systemBars())
            controller?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        // Держим экран всегда включенным
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        screensaverManager = ScreensaverManager(this)

        // Проверяем, является ли наше приложение Device Owner
        if (kioskManager.isDeviceOwner()) {
            // Включаем режим киоска
            kioskManager.enableKioskMode()

            // Запускаем режим блокировки задачи
            kioskManager.startLockTask(this)

            // Настраиваем автозапуск после перезагрузки
            kioskManager.setAutoStartAfterReboot()
        } else {
            // Приложение не является Device Owner, показываем инструкции
            showDeviceOwnerInstructions()
        }
        setupNavigation()
        setupListeners()
        setupViews()
        connectSocket()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (kioskManager.isDeviceOwner() && kioskManager.isInLockTaskMode()) {
            // Не делаем ничего, оставаем в приложении
            return
        }
    }
    private fun showDeviceOwnerInstructions() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Требуется настройка Device Owner")
            .setMessage("Чтобы использовать режим киоска, необходимо установить это приложение как Device Owner. Выполните следующие шаги:\n\n" +
                    "1. Подключите устройство к компьютеру\n" +
                    "2. Включите отладку по USB в настройках разработчика\n" +
                    "3. Выполните команду:\n" +
                    "adb shell dpm set-device-owner ${packageName}/.KioskDeviceAdminReceiver")
            .setPositiveButton("OK", null)
            .create()

        dialog.show()
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
        if (kioskManager.isDeviceOwner() && kioskManager.isInLockTaskMode()) {
            kioskManager.stopLockTask(this)
        }
    }
}