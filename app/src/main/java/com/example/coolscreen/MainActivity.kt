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

        // БАЗА: 0x060000FF (Alpha 6%, R 0, G 0, B 255)
        findViewById<Button>(R.id.btnBase).setOnClickListener { startFilter(0x060000FF) }

        // COOL: 0x090000AA (Alpha 9%, R 0, G 0, B 170)
        findViewById<Button>(R.id.btnCool).setOnClickListener { startFilter(0x090000AA.toInt()) }

        // COOL+: 0x0C000080 (Alpha 12%, R 0, G 0, B 128)
        findViewById<Button>(R.id.btnCoolPlus).setOnClickListener { startFilter(0x0C000080.toInt()) }

        // COOL-G: 0x0A040899 (Alpha 10%, R 4, G 8, B 153)
        // (Тут в запросе было 0x0A040899 как COOL-G, и 0x0A040899 как EXP 7 - они одинаковые)
        findViewById<Button>(R.id.btnCoolG).setOnClickListener { startFilter(0x0A040899.toInt()) }


        // --- ЭКСПЕРИМЕНТЫ ---

        // EXP 1: 0x070000DB (Alpha 7%, B 219)
        findViewById<Button>(R.id.btnExp1).setOnClickListener { startFilter(0x070000DB) }

        // EXP 2: 0x080000BF (Alpha 8%, B 191)
        findViewById<Button>(R.id.btnExp2).setOnClickListener { startFilter(0x080000BF.toInt()) }

        // EXP 3: 0x060010FF (Alpha 6%, G 16, B 255)
        findViewById<Button>(R.id.btnExp3).setOnClickListener { startFilter(0x060010FF) }

        // EXP 4: 0x061004FF (Alpha 6%, R 16, G 4, B 255)
        findViewById<Button>(R.id.btnExp4).setOnClickListener { startFilter(0x061004FF) }

        // EXP 5: 0x060810FF (Alpha 6%, R 8, G 16, B 255)
        findViewById<Button>(R.id.btnExp5).setOnClickListener { startFilter(0x060810FF) }

        // EXP 6: 0x0A020499 (Alpha 10%, R 2, G 4, B 153)
        findViewById<Button>(R.id.btnExp6).setOnClickListener { startFilter(0x0A020499.toInt()) }

        // EXP 7: 0x0A040899 (Дубль COOL-G, но пусть будет)
        findViewById<Button>(R.id.btnExp7).setOnClickListener { startFilter(0x0A040899.toInt()) }


        // ВЫКЛЮЧИТЬ
        findViewById<Button>(R.id.btnOff).setOnClickListener { stopFilter() }

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
