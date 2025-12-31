package com.example.coolscreen

import android.Manifest
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. BASE: 0x060000FF (Классика)
        findViewById<Button>(R.id.btnBase).setOnClickListener { 
            startFilter(0x060000FF) 
        }

        // 2. FAVORITE: 0x0A040899 (Тот самый удачный компромисс)
        findViewById<Button>(R.id.btnFavorite).setOnClickListener { 
            startFilter(0x0A040899.toInt()) 
        }

        // 3. UPGRADE 1: 0x0C030680 (Alpha 12, Blue 128)
        // Тени как у FAVORITE, но холоднее
        findViewById<Button>(R.id.btnUpgrade1).setOnClickListener { 
            startFilter(0x0C030680.toInt()) 
        }

        // 4. UPGRADE 2: 0x0E03066E (Alpha 14, Blue 110)
        // Еще холоднее, риск "вуали"
        findViewById<Button>(R.id.btnUpgrade2).setOnClickListener { 
            startFilter(0x0E03066E.toInt()) 
        }

        // 5. GREEN TWEAK: 0x0C030880 (Alpha 12, G 8, B 128)
        // Чуть больше зелени в тенях
        findViewById<Button>(R.id.btnGreenTweak).setOnClickListener { 
            startFilter(0x0C030880.toInt()) 
        }


        // ВЫКЛЮЧИТЬ
        findViewById<Button>(R.id.btnOff).setOnClickListener { 
            stopFilter() 
        }

        checkPermissions()
    }

    private fun checkPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun startFilter(color: Int) {
        if (!Settings.canDrawOverlays(this)) {
            checkPermissions()
            return
        }
        val intent = Intent(this, FilterService::class.java)
        intent.putExtra("COLOR_VALUE", color)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopFilter() {
        stopService(Intent(this, FilterService::class.java))
    }
}
