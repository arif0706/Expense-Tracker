package com.example.expensetracker.models

import android.net.Uri
import java.util.*

data class Expense(
    val date:Date?=null,
    val amount:String?=null,
    val purpose:String?=null,
    val memory:Memory?=Memory()
)

data class Memory(
    val desc:String?=null,
    val url:String?=null,
    val filepath: Uri? =null
)
