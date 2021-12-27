package com.example.expensetracker.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.R
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity:AppCompatActivity() {


    private val mainActivityViewModel :MainActivityViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        add_expense.setOnClickListener{
            openDialog()
        }

        toolbar_title.setOnClickListener{
            if(calendar_view.visibility==View.VISIBLE)
                calendar_view.visibility=View.GONE
            else
                calendar_view.visibility=View.VISIBLE
        }

    }

    private fun openDialog(){
        AddExpenseDialogFragment.display(supportFragmentManager)
    }
}