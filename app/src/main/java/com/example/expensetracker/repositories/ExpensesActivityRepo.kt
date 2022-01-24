package com.example.expensetracker.repositories

import android.content.Context
import com.example.expensetracker.models.ExpenseTransaction
import com.example.expensetracker.models.Memory
import com.example.expensetracker.repositories.FirebaseNodes.Companion.CATEGORY_AMOUNT
import com.example.expensetracker.repositories.FirebaseNodes.Companion.NODE_EXPENSES
import com.example.expensetracker.repositories.FirebaseNodes.Companion.STORAGE_PATH_UPLOADS
import com.example.expensetracker.repositories.FirebaseNodes.Companion.TOTAL_AMOUNT
import com.example.expensetracker.repositories.FirebaseNodes.Companion.database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ExpensesActivityRepo(
    private val userRepo: UserRepo,
    private val context:Context
){

    fun getExpensesByCategory(date:String,category: String,callBack: MyCallBack){

        if(category.toUpperCase()=="ALL")
        {
            getDayExpenses(date,callBack)
        }
        else {
            userRepo.getCurrentUser().uid?.let {
                database.child(it).child(NODE_EXPENSES).child(date)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val list = arrayListOf<ExpenseTransaction>()
                            for (postSnapShot in snapshot.children) {
                                val value = postSnapShot.getValue(ExpenseTransaction::class.java)


                                if (value?.category == category) {
                                    list.add(value)
                                }
                            }

                            callBack.callBack(list)
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }
        }
    }

    private fun getDayExpenses(date:String, callBack: MyCallBack){
        userRepo.getCurrentUser().uid?.let { database.child(it).child(NODE_EXPENSES).child(date).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                 val list= arrayListOf<ExpenseTransaction>()
                for(postSnapshot in snapshot.children){
                    postSnapshot.getValue(ExpenseTransaction::class.java)?.let { it1 -> list.add(it1) }
                }
                callBack.callBack(list)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }) }
    }



    fun addExpense(expense: ExpenseTransaction,date: String,callBack: MyCallBack) {

        val storageReference=FirebaseStorage.getInstance().reference.child(STORAGE_PATH_UPLOADS+System.currentTimeMillis()+"."+ "jpg")

        if(expense.memory?.imageBytes !=null){
            expense.memory.imageBytes.let {
                storageReference.putBytes(it)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnCompleteListener{
                            val newExpense=expense.copy(
                                memory = Memory(desc = expense.memory.desc, url = it.result.toString())
                            )
                            addExpenseToDB(newExpense,date,callBack)
                        }
                    }
            }
        }
        else{
            if(expense.memory?.desc?.isEmpty() == true) {
                val newExpense=expense.copy(memory = Memory())
                addExpenseToDB(newExpense,date, callBack)
            }
        }


    }

    private fun addExpenseToDB(expense: ExpenseTransaction, date:String, callBack: MyCallBack) {


        userRepo.getCurrentUser().uid?.let { database.child(it).child(NODE_EXPENSES).child(date).push().setValue(expense).addOnCompleteListener{
            callBack.callBack("Completed")
        }}

        userRepo.getCurrentUser().uid?.let { database.child(it).child(TOTAL_AMOUNT).child(date).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var currentAmount="0"

                if(snapshot.exists()) {
                    currentAmount = snapshot.value.toString()
                }

                val newAmount = currentAmount.toInt() + expense.amount?.toInt()!!
                userRepo.getCurrentUser().uid?.let {
                    database.child(it).child(TOTAL_AMOUNT).child(date).setValue(newAmount)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        }) }

        userRepo.getCurrentUser().uid?.let { database.child(it).child(CATEGORY_AMOUNT).child(expense.category.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var currentAmount="0"
                if(snapshot.exists()){
                    currentAmount=snapshot.value.toString()
                }

                val newAmount=currentAmount.toInt() + expense.amount?.toInt()!!

                database.child(it).child(CATEGORY_AMOUNT).child(expense.category.toString()).setValue(newAmount)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        }) }

    }


    interface MyCallBack{
        fun callBack(any: Any)
    }

}
