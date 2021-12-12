package com.example.core.platform

sealed class Async<out T>(val complete:Boolean,val shouldLoad:Boolean) {

    open operator fun invoke(): T? = null

    companion object {

        fun <T> Success<*>.setMetadata(metadata: T) {
            this.metadata = metadata
        }
    }
}

object Uninitialized: Async<Nothing>(complete = false,shouldLoad = true),Incomplete

class Loading<out T>:Async<T>(complete = false,shouldLoad = true),Incomplete{
    override fun equals(other: Any?) = other is Loading<*>

    override fun hashCode(): Int = "Loading".hashCode()
}

data class Success<out T>(private val value:T):Async<T>(complete = true,shouldLoad = false){
    override operator fun invoke(): T? =value

    internal var metadata:Any?=null
}


data class Fail<out T>(val error:Throwable):Async<T>(complete = true,shouldLoad = true){
    override fun equals(other: Any?): Boolean {
        if (other !is Fail<*>) return false

        val otherError = other.error
        return error::class == otherError::class &&
                error.message == otherError.message &&
                error.stackTrace.firstOrNull() == otherError.stackTrace.firstOrNull()
    }

    override fun hashCode(): Int =
        arrayOf(error::class, error.message, error.stackTrace[0]).contentHashCode()
}

interface Incomplete

