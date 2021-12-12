package com.example.expensetracker.ui

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class MainActivityRepo(private val firebaseDatabase: FirebaseDatabase){

    companion object{
        private const val NODE_USERS="USERS"
        private const val NODE_USERS_DETAILS="USER_DETAILS"
        private const val NODE_EXPENSES="USER_EXPENSES"
    }

    fun getDayToDayExpenses():List<Expense>{

        return listOf(Expense())
    }

    fun addExpenseCard(expense: Expense){

    }
    fun getLastDate():String{

        return ""
    }
}
