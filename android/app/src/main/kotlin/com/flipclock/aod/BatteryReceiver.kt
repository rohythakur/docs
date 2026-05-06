package com.flipclock.aod

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatteryReceiver(
    private val onUpdate: (level: Int, isCharging: Boolean) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BATTERY_CHANGED) return

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        val percentage = if (scale > 0) level * 100 / scale else level
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        onUpdate(percentage, isCharging)
    }

    // ACTION_BATTERY_CHANGED is sticky — returns current state immediately on registration
    fun register(context: Context): Intent? =
        context.registerReceiver(this, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

    fun unregister(context: Context) {
        try {
            context.unregisterReceiver(this)
        } catch (_: IllegalArgumentException) {
            // not registered — safe to ignore
        }
    }
}
