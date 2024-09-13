package com.example.practice.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathDashPathEffect
import android.graphics.PathMeasure
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.practice.ext.toDp
import kotlin.math.cos
import kotlin.math.sin

/**
 *   实现一个内部刻度为20个刻度的仪表盘view，并且当前指针指向第8个刻度
 */
class DashboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //仪表盘圆形半径
    private val radius = 150.toDp()

    //圆心坐标
    private lateinit var circleCenter: Pair<Int, Int>

    //仪表盘圆形矩形位置
    private lateinit var circleRect: RectF

    //外弧度路径
    private val circlePath = Path()

    private val outSideLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 3.toDp()
        style = Paint.Style.STROKE
    }

    private val scalePath = Path()
    private val markShape = Path().apply {
        addRect(0f, 0f, 3.toDp(), 10.toDp(), Path.Direction.CW)
    }
    private val sclaePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 10.toDp()
        style = Paint.Style.STROKE
    }

    private val lineLength = 120.toDp()


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scalePath.reset()
        circlePath.reset()
        initCircle()
        initScalePath()
    }

    private fun initScalePath() {
        val pathMeasure = PathMeasure(circlePath,false)
        val advance = (pathMeasure.length - 3.toDp()) / 20
        scalePath.addArc(circleRect, 150f, 240f)
        sclaePaint.pathEffect = PathDashPathEffect(markShape, advance, 0f, PathDashPathEffect.Style.ROTATE)
    }

    private fun initCircle() {
        circleCenter = Pair(width / 2, height / 2)
        val circleCenterX = circleCenter.first
        val circleCenterY = circleCenter.second
        circleRect = RectF(
            circleCenterX - radius,
            circleCenterY - radius,
            circleCenterX + radius,
            circleCenterY + radius
        )

        circlePath.addArc(circleRect, 150f, 240f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(circlePath, outSideLinePaint)
        canvas.drawPath(scalePath, sclaePaint)

        canvas.drawLine(
            circleCenter.first.toFloat(),
            circleCenter.second.toFloat(),
            getLineEndX(10),
            getLineEndY(10),
            outSideLinePaint
        )
    }

    private fun getLineEndY(markIndex: Int): Float {
        val perMarkAngle = 240f / 20
        val targetAngle = Math.toRadians(150f + perMarkAngle * (markIndex - 1).toDouble())
        return (circleCenter.second + lineLength * sin(targetAngle)).toFloat()
    }

    private fun getLineEndX(markIndex: Int): Float {
        val perMarkAngle = 240f / 20
        val targetAngle = Math.toRadians(150f + perMarkAngle * (markIndex - 1).toDouble())
        return (circleCenter.first + lineLength * cos(targetAngle)).toFloat()
    }
}