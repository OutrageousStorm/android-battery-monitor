package com.outrageousstorm.batterymonitor

import android.app.Service
import android.content.Intent
import android.os.Debug
import android.os.IBinder
import kotlin.system.exitProcess

class BatteryMonitorService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            while (true) {
                val processes = Runtime.getRuntime().exec(arrayOf("sh", "-c", 
                    "top -n 1 | head -15")).inputStream.bufferedReader().readLines()
                
                processes.forEach { line ->
                    if (!line.contains("PID") && line.isNotBlank()) {
                        println("[Battery] $line")
                    }
                }
                
                Thread.sleep(2000)  // refresh every 2s
            }
        }.start()
        
        return START_STICKY
    }
}
