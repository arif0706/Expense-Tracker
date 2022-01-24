package com.example.expensetracker.ui.expensesGraph

import androidx.lifecycle.viewModelScope
import com.example.core.platform.BaseViewModel
import com.example.core.platform.ViewEvents
import com.example.core.platform.ViewModelAction
import com.example.core.platform.ViewState
import com.example.expensetracker.CorePreferences
import com.example.expensetracker.models.ExpenseTransaction
import com.example.expensetracker.repositories.ExpensesActivityRepo
import com.example.expensetracker.repositories.MainActivityRepo
import com.example.expensetracker.ui.Util
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch

class GraphFragmentViewModel(
    private val args:GraphFragmentArgs,
   private val expensesActivityRepo: ExpensesActivityRepo,
    private val mainActivityRepo: MainActivityRepo,
   private val corePreferences: CorePreferences
):BaseViewModel<GraphFragmentViewEvents,GraphFragmentViewState>(GraphFragmentViewState())

{
    init{
        viewModelScope.launch {

            setState { copy(isLoading = true) }
            getTotalAmounts(args.date)
            getTotalAmountOf(args.date)
        }
    }

    fun handle(action: GraphFragmentViewActions){
        when(action){
            is GraphFragmentViewActions.ViewTransactionButtonIsClicked -> _viewEvents.setValue(GraphFragmentViewEvents.OpenExpensesFragment(args.date,corePreferences.category))
        }

    }

    private fun getTotalAmountOf(date: String) {

        mainActivityRepo.getTotalAmountOf(date,object:MainActivityRepo.MyCallBack{
            override fun callBack(any: Any) {
                setState {
                    copy(totalAmount = Util.appendRupee(Util.commaSeparateAmount(any.toString())))
                }
            }
        })

    }

    private fun getTotalAmounts(date:String) {
        expensesActivityRepo.getExpensesByCategory(date,"All",object :ExpensesActivityRepo.MyCallBack{
            override fun callBack(any: Any) {
                val hash= hashMapOf<String,MutableList<ExpenseTransaction>>()
                Util.categories.forEach {
                    hash[it] = mutableListOf()
                }

                (any as List<*>).forEach {
                    it as ExpenseTransaction
                    hash[it.category]?.add(it)
                }

                val pieEntries= arrayListOf<PieEntry>()
                Util.categories.forEach { outerCategories ->
                    var sum=0
                    hash[outerCategories]?.forEach {
                         sum += it.amount?.let { it1 -> Util.commaLessAmount(it1) }?.toInt()
                             ?: 0
                    }

                    if(sum>0)
                        pieEntries.add(PieEntry(sum.toFloat(),outerCategories))
                }


                if(pieEntries.size>0) {
                    val list=pieEntries.sortedByDescending { it.value }
                    pieEntries.clear()
                    pieEntries.addAll(list)
                    setState {
                        copy(list = pieEntries, isLoading = false,isEmpty=false)
                    }
                }
                else{
                    setState {
                        copy(isEmpty=true,isLoading = false)
                    }
                }


            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        corePreferences.selectedDate=Util.getTodayDate()
    }
}
data class GraphFragmentViewState(
    val list : ArrayList<PieEntry> = arrayListOf(),
    val isLoading : Boolean=true,
    val isEmpty:Boolean=true,
    val totalAmount:String=""
):ViewState

sealed class GraphFragmentViewEvents:ViewEvents{
    class OpenExpensesFragment(val date:String,val category: String):GraphFragmentViewEvents()
}
sealed class GraphFragmentViewActions:ViewModelAction{
    object ViewTransactionButtonIsClicked : GraphFragmentViewActions()
}