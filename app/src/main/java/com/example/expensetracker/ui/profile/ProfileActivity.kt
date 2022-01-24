package com.example.expensetracker.ui.profile

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.expensetracker.OnBoardingActivity
import com.example.expensetracker.R
import com.example.expensetracker.ui.Util
import com.example.expensetracker.ui.expensesGraph.LegendAdapter
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

class ProfileActivity:AppCompatActivity() {

    private val viewModel : ProfileActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        viewModel.viewState.observe(this){

            tv_name.text=it.user.name
            tv_email.text=it.user.email_id
            if(it.isLoading){
                setEmptyGraph()
                pie_chart.centerText="Loading..."
            }
            else if(it.graphList.isEmpty()){
                setEmptyGraph()
                pie_chart.centerText="No Transactions"
            }
            if(it.graphList.isNotEmpty()){
                setGraph(it.graphList,it.totalAmount)
            }
        }


        viewModel.viewEvents.observe(this){
            when(it){
                is ProfileActivityViewEvents.OpenSignOutDialog -> showDialog()
                is ProfileActivityViewEvents.OpenOnBoardingActivity -> openOnBoardingActivity()
            }
        }

        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }


        btn_switch_account.setOnClickListener{
            viewModel.handle(ProfileActivityViewActions.SwitchAccountClicked)
        }
    }

    private fun openOnBoardingActivity() {
        startActivity(Intent(applicationContext,OnBoardingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }

    private fun showDialog() {
        val alertDialog=MaterialAlertDialogBuilder(this)
        alertDialog.setTitle("Switch Account")
        alertDialog.setMessage(resources.getString(R.string.sign_out_message))
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Ok",object :DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, p1: Int) {
                viewModel.handle(ProfileActivityViewActions.AlertDialogOkClicked)
                dialog?.dismiss()
            }
        })

            .setNegativeButton("Cancel"
            ) { _, _ -> }

            .show()
    }


    private fun setEmptyGraph() {
        Util.setEmptyGraph(pie_chart,this)
    }

    private fun setGraph(list: ArrayList<PieEntry>, totalAmount: String) {


        Util.prepareGraph(list,totalAmount,pie_chart,this)

        val adapter=LegendAdapter()
        adapter.setData(list,Util.getPieDataSet(list,this))
        rv_legends.layoutManager=GridLayoutManager(this,2)
        rv_legends.adapter=adapter
    }
}