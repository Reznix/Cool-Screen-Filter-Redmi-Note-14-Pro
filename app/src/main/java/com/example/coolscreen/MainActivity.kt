package com.example.coolscreen

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

  private lateinit var toggleButton: Button
private var serviceRunning = false

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    toggleButton = findViewById(R.id.btnToggle)
    toggleButton.setOnClickListener {
        if (serviceRunning) {
            stopOverlayService()
        } else {
            startOverlayService()
        }
    }

    // Проверьте, есть ли разрешение на отображение поверх других приложений
    if (!Settings.canDrawOverlays(this)) {
        // Перенаправить пользователя к настройкам
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        startActivity(intent)
    }
}

private fun startOverlayService() {
    val intent = Intent(this, FilterService::class.java)
    startForegroundService(intent)
    serviceRunning = true
    toggleButton.text = "Выключить"
}

private fun stopOverlayService() {
    val intent = Intent(this, FilterService::class.java)
    stopService(intent)
    serviceRunning = false
    toggleButton.text = "Включить"
}
