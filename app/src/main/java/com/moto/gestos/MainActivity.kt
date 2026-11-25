package com.moto.gestos

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val p = getSharedPreferences("p", MODE_PRIVATE)
        val d = resources.displayMetrics.density
        val pad = (20 * d).toInt()
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)

        fun refresh() {
            val i = Intent(this, GestureService::class.java)
            stopService(i)
            if (p.getBoolean("on", false)) startForegroundService(i)
        }

        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(pad, pad * 2, pad, pad) }
        root.addView(TextView(this).apply { text = "Moto Gestos"; textSize = 26f })
        root.addView(TextView(this).apply {
            text = "\nAgitar el cel 2 veces (movimiento de hachazo) = linterna"
            textSize = 15f
        })
        root.addView(Switch(this).apply {
            text = "Gestos activos"; textSize = 18f; isChecked = p.getBoolean("on", false)
            setPadding(0, pad, 0, pad)
            setOnCheckedChangeListener { _, on -> p.edit().putBoolean("on", on).apply(); refresh() }
        })
        root.addView(CheckBox(this).apply {
            text = "Girar la muñeca 2 veces = abrir cámara"; isChecked = p.getBoolean("cam", false)
            setOnCheckedChangeListener { _, on -> p.edit().putBoolean("cam", on).apply(); refresh() }
        })
        root.addView(Button(this).apply {
            text = "Permiso: mostrar sobre otras apps (para cámara)"
            setOnClickListener { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) }
        })
        root.addView(Button(this).apply {
            text = "Batería: sin restricciones (recomendado)"
            setOnClickListener { startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:$packageName"))) }
        })
        setContentView(ScrollView(this).apply { addView(root) })
    }
}
