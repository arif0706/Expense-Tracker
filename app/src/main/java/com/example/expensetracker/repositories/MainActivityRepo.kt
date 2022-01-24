package com.example.expensetracker.repositories

import android.content.Context
import com.example.expensetracker.repositories.FirebaseNodes.Companion.TOTAL_AMOUNT
import com.example.expensetracker.repositories.FirebaseNodes.Companion.database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MainActivityRepo(
    private val userRepo: UserRepo,
    private val context:Context,
    private val expensesActivityRepo: ExpensesActivityRepo) {


    fun getTotalAmounts(callBack: MyCallBack){
        userRepo.getCurrentUser().uid?.let { database.child(it).child(TOTAL_AMOUNT).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value!=null) {
                    val list = snapshot.value as HashMap<*, *>
                    callBack.callBack(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }) }
    }

    fun getTotalAmountOf(date:String,callBack: MyCallBack){
        userRepo.getCurrentUser().uid?.let { database.child(it).child(TOTAL_AMOUNT).child(date).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?.let { it1 -> callBack.callBack(it1) }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }) }

    }



    interface MyCallBack{
        fun callBack(any: Any)
    }

}