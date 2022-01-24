package com.example.expensetracker.ui.expensesGraph

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.ui.Util
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class LegendAdapter: RecyclerView.Adapter<LegendAdapter.LegendItemViewHolder>() {

    var list:ArrayList<PieEntry> = arrayListOf()
    var pieDataSet: PieDataSet?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegendItemViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.layout_graph_legend_item,parent,false)
        return LegendItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: LegendItemViewHolder, position: Int) {
        pieDataSet?.colors?.get(position)?.let { holder.onBind(list[position], it) }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class LegendItemViewHolder(view: View): RecyclerView.ViewHolder(view){

        private val ivLegendColor: ImageView =itemView.findViewById(R.id.iv_legend_color)
        private val ivLegendLabel: TextView =itemView.findViewById(R.id.tv_legend_label)
        private val ivLegendValue: TextView =itemView.findViewById(R.id.tv_legend_value)

        fun onBind(pieEntry: PieEntry, color: Int){
            ivLegendLabel.text=pieEntry.label
            ivLegendValue.text= Util.commaSeparateAmount(pieEntry.value.toInt().toString())
            ivLegendColor.backgroundTintList= ColorStateList.valueOf(color)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:ArrayList<PieEntry>, pieDataSet: PieDataSet){
        this.list.clear()
        this.list.addAll(list)
        this.pieDataSet=pieDataSet
        notifyDataSetChanged()

    }
}