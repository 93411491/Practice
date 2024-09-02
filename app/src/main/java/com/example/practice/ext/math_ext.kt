package com.example.practice.ext

import kotlin.math.cos
import kotlin.math.sin

fun Double.getCosX(angle: Double): Double {
    return this * cos(Math.toRadians(angle))
}

fun Int.getCosX(angle: Double): Double {
    return this.toDouble().getCosX(angle)
}

fun Float.getCosX(angle: Double): Double {
    return this.toDouble().getCosX(angle)
}

fun Double.getSinX(angle: Double): Double {
    return this * sin(Math.toRadians(angle))
}

fun Int.getSinX(angle: Double): Double {
    return this.toDouble().getSinX(angle)
}

fun Float.getSinX(angle: Double): Double {
    return this.toDouble().getSinX(angle)
}
