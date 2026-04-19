package com.outrageousstorm.batterymonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    private val batteryReceiver = BatteryBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentFilter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        } else {
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        }
        registerReceiver(batteryReceiver, intentFilter, Context.RECEIVER_EXPORTED)

        setContent {
            BatteryMonitorApp(batteryReceiver)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
    }
}

@Composable
fun BatteryMonitorApp(receiver: BatteryBroadcastReceiver) {
    val batteryLevel = remember { mutableIntStateOf(0) }
    val temperature = remember { mutableIntStateOf(0) }
    val health = remember { mutableStateOf("Unknown") }
    val status = remember { mutableStateOf("Unknown") }
    val plugged = remember { mutableStateOf("Not charging") }

    LaunchedEffect(Unit) {
        while (true) {
            batteryLevel.intValue = receiver.level
            temperature.intValue = receiver.temperature
            health.value = receiver.health
            status.value = receiver.status
            plugged.value = receiver.plugged
            kotlinx.coroutines.delay(1000)
        }
    }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "🔋 Battery Monitor",
                    fontSize = 32.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Large battery % display
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color(0xFF1E1E1E), shape = MaterialTheme.shapes.large),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${batteryLevel.intValue}%",
                        fontSize = 64.sp,
                        color = when {
                            batteryLevel.intValue > 50 -> Color(0xFF4CAF50)
                            batteryLevel.intValue > 20 -> Color(0xFFFFC107)
                            else -> Color(0xFFFF5252)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Stats grid
                GridStats(
                    listOf(
                        "Temperature" to "${temperature.intValue}°C",
                        "Health" to health.value,
                        "Status" to status.value,
                        "Charging" to plugged.value
                    )
                )
            }
        }
    }
}

@Composable
fun GridStats(stats: List<Pair<String, String>>) {
    Column {
        stats.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { (label, value) ->
                    StatCard(label, value, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 18.sp, color = Color.White)
        }
    }
}

class BatteryBroadcastReceiver : BroadcastReceiver() {
    var level = 0
    var temperature = 0
    var health = "Unknown"
    var status = "Unknown"
    var plugged = "Not charging"

    override fun onReceive(context: Context, intent: Intent) {
        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10
        health = when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> "Unknown"
            else -> "Unknown"
        }
        status = when (intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0)) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
            else -> "Unknown"
        }
        val plug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        plugged = when {
            plug and BatteryManager.BATTERY_PLUGGED_AC != 0 -> "AC"
            plug and BatteryManager.BATTERY_PLUGGED_USB != 0 -> "USB"
            plug and BatteryManager.BATTERY_PLUGGED_WIRELESS != 0 -> "Wireless"
            else -> "Not charging"
        }
    }
}
