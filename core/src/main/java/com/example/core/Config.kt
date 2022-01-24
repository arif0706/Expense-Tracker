package com.example.core

import com.example.core.extentions.objectify
import com.example.core.extentions.objectifyToList
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.concurrent.TimeUnit

class Config {

    val remoteConfig = Firebase.remoteConfig

    companion object{
        const val KEY_CATEGORIES="categories"
    }


    fun initialise(){
        val configSettings= remoteConfigSettings {
            minimumFetchIntervalInSeconds=TimeUnit.HOURS.toSeconds(24)
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    println("config params updated:${task.result}")
                }
                else{
                    println("config params failed")
                }
            }
    }

    inline fun <reified T> getObjectifiedValue(key:String) : T? =getValue<String>(key).objectify()

    inline fun <reified T> getObjectifiedList(key:String) :List<T>? =getValue<String>(key).objectifyToList()

    inline fun<reified T> getValue(key:String): T {
        return when(T::class){
            String::class -> remoteConfig.getString(key) as T
            Long::class -> remoteConfig.getLong(key) as T
            Double::class ->remoteConfig.getDouble(key) as T
            Boolean::class -> remoteConfig.getBoolean(key) as T

            else -> throw IllegalArgumentException("generic type not handled ${T::class.java.name}")
        }

    }
}