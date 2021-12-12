package com.example.expensetracker.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R

class ExpenseReportAdapter(
    context:Context,
    val callback:(message:String)->Unit
)
    : RecyclerView.Adapter<ExpenseReportAdapter.CardHolder>() {


    private val layoutInflater=LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        return CardHolder(layoutInflater.inflate(R.layout.expense_report_card,parent,false))
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.itemView.setOnClickListener{
            callback(position.toString())
        }
    }

    override fun getItemCount(): Int {
        return 6
    }
    inner class CardHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

    }
}