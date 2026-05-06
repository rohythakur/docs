package com.flipclock.aod

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.SeekBar
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {

    private lateinit var flipClockView: FlipClockView
    private lateinit var switchSeconds: SwitchCompat
    private lateinit var seekBrightness: SeekBar
    private lateinit var btnOpenSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flipClockView   = findViewById(R.id.previewFlipClock)
        switchSeconds   = findViewById(R.id.switchSeconds)
        seekBrightness  = findViewById(R.id.seekBrightness)
        btnOpenSettings = findViewById(R.id.btnOpenDaydreamSettings)

        val showSecs = ClockPreferences.showSeconds(this)
        val brightness = ClockPreferences.brightness(this)

        switchSeconds.isChecked = showSecs
        flipClockView.setShowSeconds(showSecs)
        seekBrightness.progress = (brightness * 100).toInt()
        applyWindowBrightness(brightness)

        switchSeconds.setOnCheckedChangeListener { _, checked ->
            ClockPreferences.setShowSeconds(this, checked)
            flipClockView.setShowSeconds(checked)
        }

        seekBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                val value = progress / 100f
                ClockPreferences.setBrightness(this@MainActivity, value)
                applyWindowBrightness(value)
            }
            override fun onStartTrackingTouch(bar: SeekBar) {}
            override fun onStopTrackingTouch(bar: SeekBar) {}
        })

        btnOpenSettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_DREAM_SETTINGS))
        }

        flipClockView.start()
    }

    override fun onStop() {
        super.onStop()
        flipClockView.stop()
    }

    override fun onRestart() {
        super.onRestart()
        flipClockView.start()
    }

    private fun applyWindowBrightness(value: Float) {
        window.attributes = window.attributes.also { it.screenBrightness = value }
    }
}
