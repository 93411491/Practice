sealed class State<out T> {
    data class Success<T>(val data: T) : State<T>()
    data class Error(val exception: Throwable) : State<Nothing>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(exception: Throwable) = Error(exception)
    }

    inline fun onSuccess(action: (T) -> Unit): State<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Throwable) -> Unit): State<T> {
        if (this is Error) action(exception)
        return this
    }

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
}
