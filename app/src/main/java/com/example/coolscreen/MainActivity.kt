package com.example.coolscreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var toggleButton: Button
    private val prefs by lazy { getSharedPreferences("CoolScreenPrefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleButton = findViewById(R.id.btnToggle)
        
        // Восстанавливаем состояние кнопки
        val isRunning = prefs.getBoolean("is_running", false)
        updateButtonState(isRunning)

        toggleButton.setOnClickListener {
            // 1. Проверяем оверлей
            if (!Settings.canDrawOverlays(this)) {
                requestOverlayPermission()
                return@setOnClickListener
            }
            
            // 2. Проверяем уведомления (для Android 13+)
            if (Build.VERSION.SDK_INT >= 33) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
                    return@setOnClickListener
                }
            }

            val currentState = prefs.getBoolean("is_running", false)
            if (currentState) {
                stopOverlayService()
            } else {
                startOverlayService()
            }
        }

        // Авто-проверка при старте
        checkPermissions()
    }

    private fun checkPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
        }
        // Запрос уведомлений сразу при старте (удобнее для пользователя)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }

    private fun startOverlayService() {
        val intent = Intent(this, FilterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        
        prefs.edit().putBoolean("is_running", true).apply()
        updateButtonState(true)
    }

    private fun stopOverlayService() {
        val intent = Intent(this, FilterService::class.java)
        stopService(intent)
        
        prefs.edit().putBoolean("is_running", false).apply()
        updateButtonState(false)
    }

    private fun updateButtonState(isRunning: Boolean) {
        toggleButton.text = if (isRunning) "Выключить" else "Включить"
    }
}
