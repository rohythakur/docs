package com.flipclock.aod

import android.content.Context

object ClockPreferences {

    private const val PREFS_NAME = "flip_clock_prefs"
    private const val KEY_SHOW_SECONDS = "show_seconds"
    private const val KEY_BRIGHTNESS = "brightness"

    fun showSeconds(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_SHOW_SECONDS, true)

    fun setShowSeconds(ctx: Context, value: Boolean) =
        prefs(ctx).edit().putBoolean(KEY_SHOW_SECONDS, value).apply()

    fun brightness(ctx: Context): Float =
        prefs(ctx).getFloat(KEY_BRIGHTNESS, 0.5f)

    fun setBrightness(ctx: Context, value: Float) =
        prefs(ctx).edit().putFloat(KEY_BRIGHTNESS, value).apply()

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
