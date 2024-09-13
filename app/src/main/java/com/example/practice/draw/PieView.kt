package com.example.practice.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.practice.ext.getCosX
import com.example.practice.ext.getSinX
import com.example.practice.ext.toColor
import com.example.practice.ext.toDp


/**
 * 自定义绘制一个饼图，内部分为4部分
 *  这四部分的角度按顺序为60，150，120，30
 *  颜色按顺序为#C21858，#324747，#00AC21,#112231
 *  可以将对应部分给抽出来
 */
class PieView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val angleList = listOf(60f, 150f, 120f, 30f)
    private val colorList = listOf("#C21858", "#008847", "#00AC21", "#112231")

    private val radius = 100F.toDp()

    private lateinit var rect: RectF

    private var forcusPart = 0

    init {
        setOnClickListener {
            setFoucsPart(++forcusPart % 4)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val circleCenterX = width / 2f
        val circleCenterY = height / 2f
        rect = RectF(
            circleCenterX - radius,
            circleCenterY - radius,
            circleCenterX + radius,
            circleCenterY + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var startAngle = 0f
        angleList.forEachIndexed { index, angle ->
            paint.color = colorList[index].toColor()
            if (isFoucsPart(index)){
                canvas.save()
                //移动距离为15dp
                val moveX = 15.toDp().getCosX((startAngle + angle / 2).toDouble())
                val moveY = 15.toDp().getSinX((startAngle + angle / 2).toDouble())
                canvas.translate(moveX.toFloat(), moveY.toFloat())
            }
            canvas.drawArc(
                rect, startAngle, angle, true, paint
            )
            if (isFoucsPart(index)) {
                canvas.restore()
            }
            startAngle += angle
        }
    }

    private fun isFoucsPart(index: Int) = forcusPart == index

    fun setFoucsPart(index: Int) {
        forcusPart = index
        invalidate()
    }
}
