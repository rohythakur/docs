package com.flipclock.aod

import android.service.dreams.DreamService
import android.view.WindowManager

class FlipClockDreamService : DreamService() {

    private var batteryReceiver: BatteryReceiver? = null
    private var flipClockView: FlipClockView? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        isInteractive = false
        isFullscreen = true

        @Suppress("DEPRECATION")
        window?.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        // Apply user-selected brightness to this window only
        val brightness = ClockPreferences.brightness(this)
        window?.attributes = window?.attributes?.also { it.screenBrightness = brightness }

        setContentView(R.layout.dream_flip_clock)

        flipClockView = findViewById<FlipClockView>(R.id.flipClockView).also { clock ->
            clock.setShowSeconds(ClockPreferences.showSeconds(this))
        }

        batteryReceiver = BatteryReceiver { level, isCharging ->
            flipClockView?.updateBattery(level, isCharging)
        }
        // Sticky broadcast — immediate callback with current battery level
        batteryReceiver?.register(this)

        flipClockView?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        flipClockView?.stop()
        batteryReceiver?.unregister(this)
        batteryReceiver = null
        flipClockView = null
    }
}
