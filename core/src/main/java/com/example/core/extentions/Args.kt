package com.example.core.extentions

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import java.lang.IllegalArgumentException
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

const val KEY_ARG="expense:arg"

fun Parcelable?.toBundle():Bundle?{
    return this?.let { Bundle().apply { putParcelable(KEY_ARG,it) } }
}


fun<V: Any> fragmentArgs() = object : ReadOnlyProperty<Fragment,V>{
    var value:V?=null
    override fun getValue(thisRef: Fragment, property: KProperty<*>): V {
        if(value == null){
            val args=thisRef.arguments ?: throw IllegalArgumentException("There are no fragment arguments!")
            val argUntyped=args.get(KEY_ARG)
            argUntyped?:throw IllegalArgumentException("MvRx arguments not found at key")

            @Suppress("UNCHECKED_CAST")
            value=argUntyped as V
        }
        return value ?:throw IllegalArgumentException("")
    }
}

fun <V : Any> activityArgs() = object : ReadOnlyProperty<Activity, V> {
    var value: V? = null

    override fun getValue(thisRef: Activity, property: KProperty<*>): V {
        if (value == null) {
            val args = thisRef.intent.extras ?: throw IllegalArgumentException("There are no fragment arguments!")
            val argUntyped = args.get(KEY_ARG)
            argUntyped ?: throw IllegalArgumentException("MvRx arguments not found at key MvRx.KEY_ARG!")
            @Suppress("UNCHECKED_CAST")
            value = argUntyped as V
        }
        return value ?: throw IllegalArgumentException("")
    }
}