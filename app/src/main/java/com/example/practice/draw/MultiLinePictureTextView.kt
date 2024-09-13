package com.example.practice.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.practice.R
import com.example.practice.ext.bitmap
import com.example.practice.ext.color
import com.example.practice.ext.toDp

class MultiLinePictureTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "MultiLinePictureTextView"
        private const val IMAGE_WIDTH = 150f
        private const val IMAGE_PADDING = 70f
    }

    private val content = """
            Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
        """.trimIndent()

    private val imageWidth = IMAGE_WIDTH.toDp()
    private val imagePadding = IMAGE_PADDING.toDp()
    private val measureWidth = floatArrayOf()

    private val picture = context.bitmap(R.drawable.avatar_rengwuxian, imageWidth.toInt())

    private val picturePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fontMetrics = Paint.FontMetrics()

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 20.toDp()
        color = color(R.color.gray)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textPaint.getFontMetrics(fontMetrics)
        canvas.drawBitmap(picture, width - imageWidth, imagePadding, picturePaint)
        var start = 0
        var verticalOffset = -fontMetrics.top
        while (start < content.length) {
            val measuredWidth = when {
                verticalOffset + fontMetrics.bottom < imagePadding || verticalOffset + fontMetrics.top > imagePadding + imageWidth -> width.toFloat()
                else -> width - imageWidth
            }
            val count =
                textPaint.breakText(
                    content,
                    start,
                    content.length,
                    true,
                    measuredWidth,
                    measureWidth
                )
            Log.d(
                TAG,
                "onDraw: content length=${content.length} start=$start ,end=${start + count} "
            )
            Log.d(TAG, "onDraw: verticalOffset=$verticalOffset")
            canvas.drawText(content, start, start + count, 0f, verticalOffset, textPaint)
            start+=count
            verticalOffset += textPaint.fontSpacing
        }
    }
}