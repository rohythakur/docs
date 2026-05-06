package com.flipclock.aod

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Calendar

class FlipClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private lateinit var hourTens: FlipCardView
    private lateinit var hourUnits: FlipCardView
    private lateinit var minuteTens: FlipCardView
    private lateinit var minuteUnits: FlipCardView
    private lateinit var secondTens: FlipCardView
    private lateinit var secondUnits: FlipCardView
    private lateinit var secondsRow: LinearLayout
    private lateinit var batteryText: TextView
    private lateinit var batteryIcon: View

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    private var prevHt = -1; private var prevHu = -1
    private var prevMt = -1; private var prevMu = -1
    private var prevSt = -1; private var prevSu = -1

    private val tickRunnable = object : Runnable {
        override fun run() {
            tick()
            val nextSecond = 1000L - (System.currentTimeMillis() % 1000L)
            handler.postDelayed(this, nextSecond)
        }
    }

    init {
        inflate(context, R.layout.view_flip_clock_content, this)
        bindViews()
    }

    private fun bindViews() {
        hourTens    = findViewById(R.id.hourTens)
        hourUnits   = findViewById(R.id.hourUnits)
        minuteTens  = findViewById(R.id.minuteTens)
        minuteUnits = findViewById(R.id.minuteUnits)
        secondTens  = findViewById(R.id.secondTens)
        secondUnits = findViewById(R.id.secondUnits)
        secondsRow  = findViewById(R.id.secondsRow)
        batteryText = findViewById(R.id.batteryText)
        batteryIcon = findViewById(R.id.batteryIcon)
    }

    fun start() {
        if (isRunning) return
        isRunning = true
        tick()
        handler.post(tickRunnable)
    }

    fun stop() {
        isRunning = false
        handler.removeCallbacks(tickRunnable)
    }

    fun setShowSeconds(show: Boolean) {
        secondsRow.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun updateBattery(level: Int, isCharging: Boolean) {
        batteryText.text = if (isCharging) "⚡ $level%" else "$level%"
        batteryIcon.visibility = if (isCharging) View.VISIBLE else View.GONE
    }

    private fun tick() {
        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        val s = cal.get(Calendar.SECOND)

        val ht = h / 10; val hu = h % 10
        val mt = m / 10; val mu = m % 10
        val st = s / 10; val su = s % 10

        if (ht != prevHt) { hourTens.flipTo(ht);    prevHt = ht }
        if (hu != prevHu) { hourUnits.flipTo(hu);   prevHu = hu }
        if (mt != prevMt) { minuteTens.flipTo(mt);  prevMt = mt }
        if (mu != prevMu) { minuteUnits.flipTo(mu); prevMu = mu }
        if (secondsRow.visibility == View.VISIBLE) {
            if (st != prevSt) { secondTens.flipTo(st);  prevSt = st }
            if (su != prevSu) { secondUnits.flipTo(su); prevSu = su }
        }
    }
}
