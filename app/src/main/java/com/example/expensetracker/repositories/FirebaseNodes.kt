package com.example.expensetracker.repositories

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseNodes {
    companion object{
        const val NODE_USERS="USERS"
        const val NODE_USERS_DETAILS="USER_DETAILS"
        const val NODE_EXPENSES="USER_EXPENSES"

        const val STORAGE_PATH_UPLOADS="uploads/"
        const val TOTAL_AMOUNT="TOTAL_AMOUNT"
        const val CATEGORY_AMOUNT="CATEGORY_AMOUNT"

        val database= Firebase.database.getReference(NODE_USERS)

    }
}