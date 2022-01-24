package com.example.core.extentions

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type


val moshi= Moshi.Builder().build()

inline fun <reified T> String.objectify(): T? {
    return moshi.adapter(T::class.java).fromJson(this)
}

inline fun <reified T> String.objectifyToList(): List<T>? {
    val type: Type = Types.newParameterizedType(
        List::class.java,
        T::class.java
    )
    return moshi.adapter<List<T>>(type).fromJson(this)
}