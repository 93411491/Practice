package com.example.practice.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View
import com.example.practice.R
import com.example.practice.ext.bitmap
import com.example.practice.ext.toDp

class CircleAvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val avatarWidth = 150.toDp()

    private val radius = avatarWidth / 2

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var centerX = 0f
    private var centerY = 0f

    private val xferMode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    private val bitmap = context.bitmap(R.drawable.avatar_rengwuxian, avatarWidth.toInt())

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.reset()
        val count = canvas.saveLayer(
            centerX,
            centerY,
            centerX + avatarWidth,
            centerY + avatarWidth,
            paint
        )
        canvas.drawCircle(centerX + radius, centerY + radius, radius, paint)
        paint.xfermode = xferMode

        canvas.drawBitmap(bitmap, centerX, centerY, paint)

        paint.xfermode = null

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5.toDp()
        canvas.restoreToCount(count)
        canvas.drawCircle(centerX + radius, centerY + radius, radius , paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = width /2 .toFloat()
        centerY = height / 2 .toFloat()
    }
}