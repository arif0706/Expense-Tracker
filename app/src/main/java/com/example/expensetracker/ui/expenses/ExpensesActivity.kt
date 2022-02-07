package com.example.expensetracker.ui.expenses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.extentions.activityArgs
import com.example.core.extentions.toBundle
import com.example.expensetracker.R
import com.example.expensetracker.models.ExpenseTransaction
import com.example.expensetracker.models.Memory
import com.example.expensetracker.ui.Util
import com.example.expensetracker.ui.home.SortSelectionSheet
import com.example.expensetracker.ui.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_expenses.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExpensesActivityArgs(
    val date:String,
    val category:String?="All"
):Parcelable

class ExpensesActivity : AppCompatActivity() {


    private lateinit var adapter: ExpensesAdapter

    companion object{

       fun launch(context:Context,date:String,category: String){
           val intent= newIntent(context,date,category)
           context.startActivity(intent)
       }

        private fun newIntent(
            context: Context,
            date: String,
            category: String
        ):Intent{
            val args= ExpensesActivityArgs(
                date=date,
                category=category
            )
            return Intent(context,ExpensesActivity::class.java)
                .apply {
                    putExtras(args.toBundle()!!)
                }
        }
    }

    private val args : ExpensesActivityArgs by activityArgs()

    private val viewModel:ExpensesActivityViewModel by viewModel(parameters = { parametersOf(args)})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_expenses)
        setAdapter()

        viewModel.viewState.observe(this){
            btn_filter.text=it.category
            tv_total_amount.text=it.totalAmount
            if(it.isLoading){
                progress_circular.visibility=View.VISIBLE
            }
            else{
                progress_circular.visibility=GONE
            }
            if(it.noData.isNotEmpty())
            {
                tv_message.visibility= VISIBLE
                tv_message.text=it.noData
                recycler_view.visibility=GONE
            }
            else {
                tv_message.visibility= GONE
                recycler_view.visibility=View.VISIBLE
                adapter.setData(it.dayToDayExpense)
            }
        }

        toolbar.title=Util.dateToUiFormat(args.date)
        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }

        viewModel.viewEvents.observe(this){
            when(it){
                is ExpenseActivityViewEvents.OpenFilterList-> openFilterSheet()
                is ExpenseActivityViewEvents.OpenFullImageDialog -> openFullImageDialog(it.memory)
            }
        }

        btn_filter.setOnClickListener{
            viewModel.handle(ExpensesActivityViewActions.FilterClicked)
        }

    }

    private fun openFullImageDialog(memory: Memory) {
        memory.desc?.let { FullImageDialog.display(supportFragmentManager,memory.url!!, it) }


    }

    private fun openFilterSheet() {
        SortSelectionSheet().show(supportFragmentManager,"OpenSortSelectionSheet")
    }


    private fun setAdapter(){
        adapter=ExpensesAdapter(this,
                {
                 viewModel.handle(ExpensesActivityViewActions.OpenFullImage(it))
                },
                {
                showDialog(it)
                }
        )
        recycler_view.layoutManager= LinearLayoutManager(this)
        recycler_view.adapter=adapter
    }

    private fun showDialog(expenseTransaction: ExpenseTransaction) {

        val alertDialog=MaterialAlertDialogBuilder(this)
        alertDialog.setTitle("Delete")
        alertDialog.setMessage("Do you want to delete this transaction?")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("ok",object :DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                viewModel.handle(ExpensesActivityViewActions.DeleteTransaction(expenseTransaction))
            }
        })
            .setNegativeButton("cancel"
            ) { _, _ -> }
            .show()
    }
}


