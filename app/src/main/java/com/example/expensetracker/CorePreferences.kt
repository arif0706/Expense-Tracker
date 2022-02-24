package com.example.expensetracker

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.expensetracker.ui.Util

class CorePreferences(private val sharedPreferences: SharedPreferences) {
    companion object{
        const val CATEGORY="category"
        const val SELECTED_DATE="selected_date"
        const val START_DATE="start_date"
        const val TUTORIAL_PROMPT="tutorial_prompt"
    }


    var category:String
    set(value) {
        sharedPreferences.edit {
            putString(CATEGORY,value)
        }
    }
    get() {
        return sharedPreferences.getString(CATEGORY,"All")?:"All"
    }

    var selectedDate:String
    set(value){
        sharedPreferences.edit{
            putString(SELECTED_DATE,value)
        }
    }
    get(){
        if(sharedPreferences.getString(SELECTED_DATE,Util.getTodayDate())?:Util.getTodayDate()=="Destroyed"){
            selectedDate=Util.getTodayDate()
        }
        return sharedPreferences.getString(SELECTED_DATE,Util.getTodayDate())?:Util.getTodayDate()
    }

    var startDate:String
    set(value) {
        sharedPreferences.edit{
            putString(START_DATE,value)
        }
    }
    get(){
        return sharedPreferences.getString(START_DATE,"")?:""
    }
}