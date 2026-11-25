package com.moto.gestos

import android.app.*
import android.content.*
import android.hardware.*
import android.hardware.camera2.*
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import kotlin.math.abs
import kotlin.math.sqrt

class GestureService : Service(), SensorEventListener {
    // Ajustes de sensibilidad
    private val CHOP_THRESHOLD = 17f   // m/s² (sube si se activa solo, baja si cuesta)
    private val TWIST_THRESHOLD = 6f   // rad/s

    private lateinit var sm: SensorManager
    private lateinit var cm: CameraManager
    private var camId: String? = null
    private var torchOn = false
    private var wl: PowerManager.WakeLock? = null

    private val g = FloatArray(3)
    private var lastSpike = 0L; private var spikes = 0; private var lastTrigger = 0L
    private var lastSign = 0; private var lastFlip = 0L; private var flips = 0

    private val torchCb = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(id: String, enabled: Boolean) { if (id == camId) torchOn = enabled }
    }

    override fun onCreate() {
        super.onCreate()
        val ch = "g"
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel(ch, "Gestos", NotificationManager.IMPORTANCE_MIN))
        val n = Notification.Builder(this, ch).setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle("Moto Gestos activo").setOngoing(true).build()
        if (Build.VERSION.SDK_INT >= 34)
            startForeground(1, n, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        else startForeground(1, n)

        cm = getSystemService(CameraManager::class.java)
        camId = cm.cameraIdList.firstOrNull {
            val c = cm.getCameraCharacteristics(it)
            c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true &&
                c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
        }
        cm.registerTorchCallback(torchCb, null)

        // Mantiene la CPU despierta para leer el sensor con la pantalla apagada
        wl = getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "motogestos:sensor").apply { acquire() }

        sm = getSystemService(SensorManager::class.java)
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME)
        if (getSharedPreferences("p", MODE_PRIVATE).getBoolean("cam", false))
            sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onSensorChanged(e: SensorEvent) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastTrigger < 1500) return
        if (e.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            for (k in 0..2) g[k] = 0.9f * g[k] + 0.1f * e.values[k]
            val x = e.values[0] - g[0]; val y = e.values[1] - g[1]; val z = e.values[2] - g[2]
            if (sqrt(x * x + y * y + z * z) > CHOP_THRESHOLD && now - lastSpike > 120) {
                if (spikes == 1 && now - lastSpike < 700) { spikes = 0; lastTrigger = now; toggleTorch() }
                else spikes = 1
                lastSpike = now
            }
        } else { // giroscopio: giro de muñeca sobre el eje largo del cel
            val w = e.values[1]
            if (abs(w) > TWIST_THRESHOLD) {
                val s = if (w > 0) 1 else -1
                if (s != lastSign) {
                    flips = if (now - lastFlip < 500) flips + 1 else 1
                    lastSign = s; lastFlip = now
                    if (flips >= 4) { flips = 0; lastTrigger = now; openCamera() }
                }
            }
        }
    }

    private fun toggleTorch() {
        val id = camId ?: return
        try { cm.setTorchMode(id, !torchOn); buzz() } catch (_: Exception) {}
    }

    private fun openCamera() {
        if (!Settings.canDrawOverlays(this)) return
        buzz()
        startActivity(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    @Suppress("DEPRECATION")
    private fun buzz() {
        (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(40, 255))
    }

    override fun onAccuracyChanged(s: Sensor?, a: Int) {}
    override fun onBind(i: Intent?): IBinder? = null
    override fun onStartCommand(i: Intent?, f: Int, s: Int) = START_STICKY

    override fun onDestroy() {
        sm.unregisterListener(this)
        cm.unregisterTorchCallback(torchCb)
        wl?.takeIf { it.isHeld }?.release()
        super.onDestroy()
    }
}
