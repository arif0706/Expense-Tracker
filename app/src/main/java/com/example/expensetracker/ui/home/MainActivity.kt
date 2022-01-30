package com.example.expensetracker.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.example.expensetracker.R
import com.example.expensetracker.ui.Util
import com.example.expensetracker.ui.calendarView.CalendarView
import com.example.expensetracker.ui.expensesGraph.GraphFragment
import com.example.expensetracker.ui.expensesGraph.GraphFragmentArgs
import com.example.expensetracker.ui.profile.ProfileActivity
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class MainActivityArgs(
    val date:String
):Parcelable
class MainActivity:AppCompatActivity() {


    private val viewModel:MainActivityViewModel by viewModel(parameters = { parametersOf(MainActivityArgs(date=Util.getTodayDate()))})

    private var currentDate:String?=null

    private var totalAmounts : HashMap<String,Long> = hashMapOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.viewState.observe(this,{

            updateTotalAmounts(it.totalAmount)

        })

        viewModel.viewEvents.observe(this,{
            when(it){
                is MainActivityViewEvents.OpenGraphFragment ->
                    initializeFragment(args = it.args)

                is MainActivityViewEvents.OpenAddExpenseDialog->openDialog()

                is MainActivityViewEvents.OpenProfileActivity -> openProfileActivity()

                else -> {}
            }
        })



        btn_date.setOnClickListener{
            val calendarView=CalendarView(this)
            calendarView.setTotalAmounts(totalAmounts)
            calendarView.show()
            calendarView.setDateClickListener(object :CalendarView.DateClickListener{
                override fun onDateClick(date: String) {
                    viewModel.handle(MainActivityViewActions.DateClicked(date))
                }
            })
        }

        add_expense.setOnClickListener{
            viewModel.handle(MainActivityViewActions.AddExpenseButtonClicked)
        }

        toolbar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.profile -> {
                    viewModel.handle(MainActivityViewActions.ProfileButtonClicked)
                    return@setOnMenuItemClickListener  true
                }


            }
            false
        }


    }

    private fun openProfileActivity() {
        startActivity(Intent(this,ProfileActivity::class.java))
    }



    private fun updateTotalAmounts(totalAmount: HashMap<String, Long>){
        totalAmounts=totalAmount

    }

    private fun initializeFragment(args: GraphFragmentArgs) {

        currentDate=args.date

        btn_date.text=Util.dateToUiFormat(args.date)
        val graphFragment=GraphFragment.newInstance(args.date)
        supportFragmentManager.commit {
            replace(R.id.fragment_container, graphFragment, GraphFragment.TAG)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

        }
    }
    private fun openDialog() {
        AddExpenseDialogFragment.display(supportFragmentManager,currentDate)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.handle(MainActivityViewActions.ApplicationDestroyed)
    }
}