package com.example.practice.ext

import android.util.TypedValue

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

// 扩展函数文件：DimensionExtensions.kt

// Float 转 dp
fun Float.toDp(): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    Resources.getSystem().displayMetrics
)

// Int 转 dp
fun Int.toDp(): Float = this.toFloat().toDp()

// Float 转 dp 并四舍五入为 Int
fun Float.toDpInt(): Int = this.toDp().toInt()

// Int 转 dp 并四舍五入为 Int
fun Int.toDpInt(): Int = this.toDp().toInt()

// Float 转 px
fun Float.toPx(): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_PX,
    this,
    Resources.getSystem().displayMetrics
)

// Int 转 px
fun Int.toPx(): Float = this.toFloat().toPx()

// Float 转 sp
fun Float.toSp(): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this,
    Resources.getSystem().displayMetrics
)

// Int 转 sp
fun Int.toSp(): Float = this.toFloat().toSp()

// 使用 Context 的扩展函数，可以在某些情况下更准确
fun Float.toDp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics
)

fun Int.toDp(context: Context): Float = this.toFloat().toDp(context)

// View 的扩展属性，方便在 View 中直接使用
val View.dp: (Float) -> Float
    get() = { it.toDp(context) }

val View.dpInt: (Float) -> Int
    get() = { it.toDp(context).toInt() }

fun View.color(@ColorRes color:Int) : Int {
    return ContextCompat.getColor(context, color)
}

fun String.toColor() : Int{
    return Color.parseColor(this)
}

fun View.toColor(colorResId: Int): Int {
    return ContextCompat.getColor(context, colorResId)
}

fun Context.toColor(colorResId: Int): Int {
    return ContextCompat.getColor(this, colorResId)
}