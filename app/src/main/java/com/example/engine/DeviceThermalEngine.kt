package com.example.engine

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Thermal Protection and Dynamic Bitrate Engine for Hardware Encoding
 */
object DeviceThermalEngine {

    var currentBitrateKbps: Int = 12000
        private set

    var currentTargetResolution: String = "1080p 60fps"
        private set

    /**
     * Reads actual or simulated device battery/CPU temperature in Celsius (°C)
     */
    fun getDeviceTemperature(context: Context): Float {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val tempTenths = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val realTemp = if (tempTenths > 0) tempTenths / 10.0f else 43.5f
        return realTemp
    }

    /**
     * Automatically scales down video encoding bitrate to preserve thermal headroom
     */
    fun autoReduceBitrate(): Int {
        currentBitrateKbps = (currentBitrateKbps * 0.6f).toInt().coerceAtLeast(6000)
        currentTargetResolution = "1080p 30fps"
        return currentBitrateKbps
    }

    /**
     * Checks if device temperature exceeds safety threshold (42°C) and applies thermal cooling
     */
    suspend fun checkThermalStatus(
        context: Context,
        deviceTemp: Float,
        showToast: (String) -> Unit = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
    ): Boolean {
        if (deviceTemp > 42.0f) {
            withContext(Dispatchers.Main) {
                showToast("Switching to 1080p for cooling")
            }
            autoReduceBitrate()
            return true
        }
        return false
    }

    /**
     * Synchronous evaluator matching exact block syntax:
     * if (deviceTemp > 42.0f) { showToast("Switching to 1080p for cooling"); autoReduceBitrate() }
     */
    fun evaluateThermalCooling(
        deviceTemp: Float,
        showToast: (String) -> Unit
    ) {
        if (deviceTemp > 42.0f) {
            showToast("Switching to 1080p for cooling")
            autoReduceBitrate()
        }
    }
}

/**
 * Global thermal evaluator matching requested expression logic
 */
fun checkDeviceThermalAndCool(
    deviceTemp: Float,
    showToast: (String) -> Unit,
    autoReduceBitrate: () -> Unit
) {
    if (deviceTemp > 42.0f) {
        showToast("Switching to 1080p for cooling")
        autoReduceBitrate()
    }
}
