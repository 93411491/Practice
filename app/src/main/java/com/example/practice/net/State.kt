package com.example.practice.net

sealed class State<out T>{
    data class Success<T>(val data: T) : State<T>()

    data class Error(val exception: Exception) : State<Nothing>()

    companion object {
        fun <T> success(data: T) : State<T> = Success(data)

        fun error(exception: Exception) : State<Nothing> = Error(exception)
    }

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    fun getOrNull()  = when(this){
        is Success -> data
        is Error -> null
    }
}
