package com.example.expensetracker.ui.calendarView

import java.text.SimpleDateFormat
import java.util.*

data class CalendarDateModel(
    var date:Date,
    var isSelected:Boolean=false,
    var isPastDate:Boolean=false,
    var isCurrentDate:Boolean=false,
    var totalAmount:String = "0"
){
    val calendarDay:String get() = SimpleDateFormat("EE", Locale.getDefault()).format(date)

    val calendarDate:String get() {
        val cal=Calendar.getInstance()
        cal.time=date
        return cal[Calendar.DAY_OF_MONTH].toString()
    }

    val totalDate:String get() {
        return SimpleDateFormat("ddMMyyyy", Locale.ENGLISH).format(date)
    }
}
