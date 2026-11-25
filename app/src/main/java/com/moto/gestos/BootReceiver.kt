package com.moto.gestos

import android.content.*

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        if (c.getSharedPreferences("p", Context.MODE_PRIVATE).getBoolean("on", false))
            c.startForegroundService(Intent(c, GestureService::class.java))
    }
}
