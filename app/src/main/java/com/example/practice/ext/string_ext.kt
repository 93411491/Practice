package com.example.practice.ext

import android.util.Log

fun String.logd(tag: String) {
    Log.d(tag, this)
}

fun String.logI(tag: String){
    Log.i(tag, this)
}

fun String.logW(tag: String){
    Log.w(tag, this)
}

fun String.logE(tag: String){
    Log.e(tag, this)
}

fun String.logD(tag: String){
    Log.d(tag, this)
}
