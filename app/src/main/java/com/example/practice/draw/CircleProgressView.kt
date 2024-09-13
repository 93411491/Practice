package com.example.practice.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.practice.R
import com.example.practice.ext.color
import com.example.practice.ext.toDp

class CircleProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val CIRCLE_RADIUS = 150
        private const val STROKE_WIDTH = 20
        private const val TEXT_SIZE = 40
        private const val MAX_VALUE = 100f
    }

    private var progress = 0f

    init {
        setOnClickListener {
            progress += 10
            progress %= MAX_VALUE
            invalidate()
        }
    }

    private val cicleStrokeWidth = STROKE_WIDTH.toDp()

    private val metrics = Paint.FontMetrics()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = cicleStrokeWidth
    }


    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = TEXT_SIZE.toDp()
    }

    private val circleRadius = CIRCLE_RADIUS.toDp()

    private var circleX = 0
    private var circleY = 0

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        circleX = width / 2
        circleY = height / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBottomCircle(canvas)
        drawCircleProgress(canvas)
        drawProgressText(canvas)
    }

    private fun drawProgressText(canvas: Canvas) {
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.getFontMetrics(metrics)
        val verticalOffset = (metrics.ascent + metrics.descent) / 2
        canvas.drawText("$progress %", circleX.toFloat(), circleY.toFloat() - verticalOffset, textPaint)
    }

    private fun drawCircleProgress(canvas: Canvas) {
        paint.color = color(R.color.red)
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(
            circleX - circleRadius,
            circleY - circleRadius,
            circleX + circleRadius,
            circleY + circleRadius,
            -90f,
            getProgressPercent() * 360f,
            false,
            paint
        )
    }

    private fun drawBottomCircle(canvas: Canvas) {
        paint.color = color(R.color.gray)
        canvas.drawCircle(circleX.toFloat(), circleY.toFloat(), circleRadius, paint)
    }

    private fun getProgressPercent() = progress / MAX_VALUE
}