package com.example.expensetracker.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.core.platform.BaseViewHolder
import com.example.expensetracker.R
import com.example.expensetracker.ui.expenses.ExpensesActivityViewActions
import com.example.expensetracker.ui.expenses.ExpensesActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.sort_selection_sheet.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SortSelectionSheet:BottomSheetDialogFragment() {


    private val viewModel:ExpensesActivityViewModel by sharedViewModel()

    private lateinit var adapter:SortSelectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sort_selection_sheet,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val offsetFromTop=resources.getDimensionPixelOffset(R.dimen.sheet_top_offset)

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            expandedOffset=offsetFromTop
        }

        adapter= SortSelectionAdapter(requireContext()){
            dismiss()
            viewModel.handle(ExpensesActivityViewActions.CategoryClicked(it))
        }
        recycler_view.layoutManager=GridLayoutManager(requireContext(),2)
        recycler_view.adapter=adapter

        viewModel.viewState.observe(viewLifecycleOwner,{
            adapter.setData(it.sortList,it.category!!)
        })

    }
}


class SortSelectionAdapter(val context: Context, val callback:(String) -> Unit) :RecyclerView.Adapter<BaseViewHolder<String>>(){

    private val sortList = arrayListOf<String>()
    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String> {
        return SortSelectionViewHolder(inflater.inflate(R.layout.sort_selection_sheet_item,parent,false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<String>, position: Int) {

        val sortItem=sortList[position]
        holder.onBind(sortItem)

        holder.itemView.setOnClickListener{
            callback(sortItem)
        }

    }

    override fun getItemCount(): Int {
        return sortList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:List<String>,currentCategory:String){
        sortList.clear()
        sortList.addAll(list)
        notifyDataSetChanged()
    }

}
class SortSelectionViewHolder(itemView:View):BaseViewHolder<String>(itemView){

    private val tvSortItem=itemView.findViewById<TextView>(R.id.tv_sort_item)
    override fun onBind(model: String) {
        super.onBind(model)

        tvSortItem.text=model
    }


}