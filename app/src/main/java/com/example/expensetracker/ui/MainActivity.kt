package com.example.expensetracker.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.R
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity:AppCompatActivity() {


    private val mainActivityViewModel :MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        add_expense.setOnClickListener{
            openDialog()
        }

        recycler_view.adapter=ExpenseReportAdapter(this){
            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
        }
        recycler_view.layoutManager=LinearLayoutManager(this)


        val sdf = SimpleDateFormat("ddMMyyyy",Locale.ENGLISH)
        val currentDate=sdf.format(Date())
        println("date  $currentDate")
    }

    private fun openDialog(){
        AddExpenseDialogFragment.display(supportFragmentManager)
    }
}