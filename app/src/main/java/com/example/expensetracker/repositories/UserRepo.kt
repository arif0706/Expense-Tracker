package com.example.expensetracker.repositories

import com.example.expensetracker.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class UserRepo {
    fun getCurrentUser(): User {
        val auth= Firebase.auth
        val user=auth.currentUser
        return User(uid = user?.uid,name = user?.displayName,email_id = user?.email)
    }
}
