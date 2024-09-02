package com.example.practice.ext

sealed class BooleanExt<out T>()

data class TransferData<T>(val data: T) : BooleanExt<T>()

data object OtherWise : BooleanExt<Nothing>()

inline fun <T> Boolean.yes(block: () -> T): BooleanExt<T> {
    return when (this) {
        true -> TransferData(block())
        else -> OtherWise
    }
}

inline fun <T> BooleanExt<T>.otherWise(block: () -> T): T {
    return when (this) {
        is TransferData -> data
        else -> block()
    }
}

