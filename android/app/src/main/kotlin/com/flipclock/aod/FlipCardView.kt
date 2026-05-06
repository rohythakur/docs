package com.flipclock.aod

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd

class FlipCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var currentDigit = 0
    private var nextDigit = 0
    private var flipProgress = 0f

    private var activeAnimator: ValueAnimator? = null

    // Top half: bright white; bottom half: slightly dimmer — classic flip clock shading
    private val topCardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val bottomCardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#E0E0E0") }
    private val dividerPaint = Paint().apply { color = Color.parseColor("#C0C0C0") }
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK }

    private val digitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A1A1A")
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    private val camera = Camera()
    private val matrix = Matrix()
    private val cardRect = RectF()
    private val cornerRadius = 12f

    companion object {
        private const val FLIP_DURATION_MS = 300L
        private val INTERPOLATOR = DecelerateInterpolator()
    }

    fun flipTo(digit: Int) {
        if (digit == currentDigit && flipProgress == 0f) return
        nextDigit = digit
        activeAnimator?.cancel()

        activeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = FLIP_DURATION_MS
            interpolator = INTERPOLATOR
            addUpdateListener {
                flipProgress = animatedValue as Float
                invalidate()
            }
            doOnEnd {
                currentDigit = nextDigit
                flipProgress = 0f
                invalidate()
            }
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cardRect.set(0f, 0f, w.toFloat(), h.toFloat())
        digitPaint.textSize = h * 0.62f
    }

    override fun onDraw(canvas: Canvas) {
        val midY = height / 2f

        if (flipProgress == 0f) {
            drawStaticCard(canvas, currentDigit)
            return
        }

        // Static halves always visible beneath the flipping flap
        drawHalf(canvas, nextDigit, top = true)      // top of next (shows in phase 2 background)
        drawHalf(canvas, currentDigit, top = false)  // bottom of current (shows in phase 1 background)

        if (flipProgress < 0.5f) {
            // Phase 1: top flap of current folds away (0° → -90°)
            val angle = flipProgress * 2f * 90f
            drawFlippingHalf(canvas, currentDigit, top = true, angle = -angle, midY = midY)
        } else {
            // Phase 2: bottom flap of next unfolds into view (90° → 0°)
            val angle = (1f - flipProgress) * 2f * 90f
            drawFlippingHalf(canvas, nextDigit, top = false, angle = angle, midY = midY)
        }
    }

    private fun drawStaticCard(canvas: Canvas, digit: Int) {
        val midY = height / 2f
        // Top half
        canvas.save()
        canvas.clipRect(0f, 0f, width.toFloat(), midY)
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, topCardPaint)
        drawDigit(canvas, digit)
        canvas.restore()
        // Bottom half
        canvas.save()
        canvas.clipRect(0f, midY, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, bottomCardPaint)
        drawDigit(canvas, digit)
        canvas.restore()
        // Divider
        canvas.drawRect(0f, midY - 1f, width.toFloat(), midY + 1f, dividerPaint)
    }

    private fun drawHalf(canvas: Canvas, digit: Int, top: Boolean) {
        val midY = height / 2f
        canvas.save()
        if (top) canvas.clipRect(0f, 0f, width.toFloat(), midY)
        else canvas.clipRect(0f, midY, width.toFloat(), height.toFloat())
        val paint = if (top) topCardPaint else bottomCardPaint
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, paint)
        drawDigit(canvas, digit)
        canvas.restore()
    }

    private fun drawFlippingHalf(
        canvas: Canvas, digit: Int, top: Boolean, angle: Float, midY: Float
    ) {
        canvas.save()
        if (top) canvas.clipRect(0f, 0f, width.toFloat(), midY)
        else canvas.clipRect(0f, midY, width.toFloat(), height.toFloat())

        camera.save()
        camera.rotateX(angle)
        camera.getMatrix(matrix)
        camera.restore()

        // Pivot rotation at vertical center of card
        matrix.preTranslate(-width / 2f, -midY)
        matrix.postTranslate(width / 2f, midY)
        canvas.concat(matrix)

        val paint = if (top) topCardPaint else bottomCardPaint
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, paint)
        drawDigit(canvas, digit)

        // Shadow deepens as card rotates
        shadowPaint.alpha = (Math.abs(angle) / 90f * 160).toInt()
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, shadowPaint)

        canvas.restore()
    }

    private fun drawDigit(canvas: Canvas, digit: Int) {
        val x = width / 2f
        val y = height / 2f - (digitPaint.descent() + digitPaint.ascent()) / 2f
        canvas.drawText(digit.toString(), x, y, digitPaint)
    }
}
