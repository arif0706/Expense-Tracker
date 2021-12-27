package com.example.expensetracker.repositories

import android.content.Context
import android.net.Uri
import com.example.expensetracker.models.Expense
import com.example.expensetracker.models.Memory
import com.example.expensetracker.ui.ValidationUtil
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class MainActivityRepo(
    private val userRepo: UserRepo,
    private val context:Context
){

    companion object{
        private const val NODE_USERS="USERS"
        private const val NODE_USERS_DETAILS="USER_DETAILS"
        private const val NODE_EXPENSES="USER_EXPENSES"

        private const val STORAGE_PATH_UPLOADS="uploads/"
    }

    fun getDayToDayExpenses():List<Expense>{

        return listOf(Expense())
    }

    fun addExpense(expense: Expense,callBack: MyCallBack) {

        val storageReference=FirebaseStorage.getInstance().reference.child(STORAGE_PATH_UPLOADS+System.currentTimeMillis()+"."+ expense.memory?.filepath?.let { ValidationUtil.getFileExtension(context =context,uri = it) })

        if(expense.memory?.filepath!=null){
            expense.memory.filepath.let {
                storageReference.putFile(it)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnCompleteListener{
                            val newExpense=expense.copy(
                                memory = Memory(desc = expense.memory.desc,url = it.result.toString())
                            )
                            addExpenseToDB(newExpense,callBack)
                        }
                    }
            }
        }
        else{
            if(expense.memory?.desc?.isEmpty() == true) {
                val newExpense=expense.copy(memory = Memory())
                addExpenseToDB(newExpense, callBack)
            }
        }


    }

    private fun addExpenseToDB(newExpense: Expense,callBack: MyCallBack) {
        val database= Firebase.database.getReference(NODE_USERS)
        val currentDate=ValidationUtil.getTodayDate()
        userRepo.getCurrentUser().uid?.let { database.child(it).child(NODE_EXPENSES).child(currentDate).push().setValue(newExpense).addOnCompleteListener{
            callBack.callBack("Completed")
        }}


    }

    interface MyCallBack{
        fun callBack(response:String)
    }

}
