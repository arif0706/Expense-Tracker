package com.example.expensetracker.ui.expenses

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.platform.BaseViewHolder
import com.example.expensetracker.R
import com.example.expensetracker.models.ExpenseTransaction
import com.example.expensetracker.models.Memory
import com.example.expensetracker.ui.Util
import kotlinx.android.synthetic.main.activity_expenses.*

class ExpensesAdapter(val context: Context,
                      val singleClickCallback:(expense: Memory)->Unit,
                      val longClickCallback:(expense:ExpenseTransaction)->Unit
)
    : RecyclerView.Adapter<BaseViewHolder<ExpenseTransaction>>() {


    private val expensesList= arrayListOf<ExpenseTransaction>()
    private val inflater by lazy { LayoutInflater.from(context) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ExpenseTransaction> {
        return ExpenseViewHolder(inflater.inflate(R.layout.expense_view_card,parent,false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ExpenseTransaction>, position: Int) {

        val expense=expensesList[position]
        holder.onBind(expense)

        if(expense.memory!=null) {
            holder.itemView.setOnClickListener {
                singleClickCallback(expense.memory)
            }
        }
        holder.itemView.setOnLongClickListener{
            longClickCallback(expense)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return expensesList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:List<ExpenseTransaction>){
        expensesList.clear()
        expensesList.addAll(list)
        notifyDataSetChanged()
    }
}
class ExpenseViewHolder(itemView: View):BaseViewHolder<ExpenseTransaction>(itemView){
    private val tvAmount=itemView.findViewById<TextView>(R.id.tv_amount)
    private val tvPurpose=itemView.findViewById<TextView>(R.id.tv_purpose)
    private val ivImage=itemView.findViewById<ImageView>(R.id.iv_preview)
    override fun onBind(model: ExpenseTransaction) {
        tvAmount.text= model.amount?.let { Util.appendRupee(Util.commaSeparateAmount(it)) }
        tvPurpose.text=model.purpose
        Glide.with(itemView.context).load(model.memory?.url).into(ivImage)
    }
}