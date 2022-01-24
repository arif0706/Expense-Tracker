package com.example.expensetracker.repositories

import com.example.expensetracker.models.User
import com.example.expensetracker.repositories.FirebaseNodes.Companion.CATEGORY_AMOUNT
import com.example.expensetracker.repositories.FirebaseNodes.Companion.NODE_USERS_DETAILS
import com.example.expensetracker.repositories.FirebaseNodes.Companion.database
import com.example.expensetracker.ui.Util
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class UserRepo {
    fun getCurrentUser(): User {
        val auth= Firebase.auth
        val user=auth.currentUser
        return User(uid = user?.uid,name = user?.displayName,email_id = user?.email)
    }

    fun setUserDetails(){
         getCurrentUser().uid?.let { database.child(it).child(NODE_USERS_DETAILS).setValue(getCurrentUser().copy(start_date = Util.getTodayDate())) }
    }

    fun getUserDetails(callBack: UserRepoCallBack){
        getCurrentUser().uid?.let{ database.child(it).child(NODE_USERS_DETAILS).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val value=snapshot.getValue<User>()
                value?.let { it1 -> callBack.callBack(it1) }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })}
    }


    fun signOutUser(){
        val auth=Firebase.auth;
        auth.signOut()
    }


    fun getCategoryExpensesAmounts(callBack: UserRepoCallBack){
        getCurrentUser().uid?.let { database.child(it).child(CATEGORY_AMOUNT).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var list:HashMap<*,*> = hashMapOf<String,Float>()
                if(snapshot.value!=null){
                    list= snapshot.value as HashMap<*, *>
                    callBack.callBack(list)
                }
                else
                    callBack.callBack(list)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }) }
    }

    interface UserRepoCallBack{
        fun callBack(any: Any)
    }
}
