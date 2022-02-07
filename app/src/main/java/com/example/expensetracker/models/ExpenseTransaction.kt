package com.example.expensetracker.models

import java.util.*

data class ExpenseTransaction(
    val id:String?=null,
    val date:Date?=null,
    val amount:String?=null,
    val purpose:String?=null,
    val memory:Memory?=Memory(),
    val category:String?=null,
)

data class Memory(
    val desc:String?=null,
    val url:String?=null,
    val imageBytes: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Memory

        if (imageBytes != null) {
            if (other.imageBytes == null) return false
            if (!imageBytes.contentEquals(other.imageBytes)) return false
        } else if (other.imageBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        return imageBytes?.contentHashCode() ?: 0
    }
}

