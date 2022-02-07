package com.example.expensetracker.ui.expenses

import androidx.lifecycle.viewModelScope
import com.example.core.Config
import com.example.expensetracker.CorePreferences
import com.example.core.platform.BaseViewModel
import com.example.core.platform.ViewEvents
import com.example.core.platform.ViewModelAction
import com.example.core.platform.ViewState
import com.example.expensetracker.models.Categories
import com.example.expensetracker.models.ExpenseTransaction
import com.example.expensetracker.models.Memory
import com.example.expensetracker.repositories.ExpensesActivityRepo
import com.example.expensetracker.ui.Util
import kotlinx.coroutines.launch

class ExpensesActivityViewModel (
    private val args:ExpensesActivityArgs,
    private val expensesActivityRepo: ExpensesActivityRepo,
    private val corePreferences: CorePreferences,
    private val config:Config

)
    : BaseViewModel<ExpenseActivityViewEvents, ExpensesActivityViewState>(ExpensesActivityViewState())
{
    init {
        fetchDayExpenses()
    }


    private fun fetchDayExpenses() {


         setState {copy(isLoading = true) }
         setState { copy(
             sortList = config.getObjectifiedValue<Categories>(Config.KEY_CATEGORIES)?.categories!!,
             category = corePreferences.category
         ) }

       viewModelScope.launch {

            corePreferences.category.let {
                expensesActivityRepo.getExpensesByCategory(args.date,category = it, object : ExpensesActivityRepo.MyCallBack {
                    override fun callBack(any: Any) {
                        val list= any as List<*>

                        if(list.isEmpty()){
                            setState {
                                copy(noData = "Sorry no transactions added",isLoading = false,totalAmount = Util.appendRupee("0"))
                            }
                        } else {
                                var totalAmount:Int=0
                            (list as List<ExpenseTransaction>).forEach {
                                totalAmount += it.amount?.let { it1 -> Util.commaLessAmount(it1) }?.toInt() ?: 0
                            }
                            setState {
                                @Suppress("UNCHECKED_CAST")
                                copy(dayToDayExpense = any as List<ExpenseTransaction>,isLoading = false,noData ="",totalAmount = Util.appendRupee(Util.commaSeparateAmount(totalAmount.toString())))
                            }
                        }
                    }
                })
            }
        }
    }

    fun handle(actions: ExpensesActivityViewActions){
        when(actions){
            is ExpensesActivityViewActions.CategoryClicked -> {
                corePreferences.category=actions.category
                fetchDayExpenses()
            }
            is ExpensesActivityViewActions.FilterClicked -> _viewEvents.setValue(ExpenseActivityViewEvents.OpenFilterList)

            is ExpensesActivityViewActions.OpenFullImage -> _viewEvents.setValue(ExpenseActivityViewEvents.OpenFullImageDialog(actions.memory))
            is ExpensesActivityViewActions.DeleteTransaction -> expensesActivityRepo.deleteTransaction(actions.expenseTransaction,args.date)
        }
    }



}


data class ExpensesActivityViewState(
    val dayToDayExpense: List<ExpenseTransaction> = mutableListOf(),
    val noData:String= "",
    val isLoading:Boolean=true,
    val category:String?=null,
    val sortList:List<String> = listOf(),
    val totalAmount:String=""

) : ViewState


sealed class ExpenseActivityViewEvents : ViewEvents {
    object OpenFilterList:ExpenseActivityViewEvents()
    class OpenFullImageDialog(val memory: Memory):ExpenseActivityViewEvents()
}


sealed class ExpensesActivityViewActions : ViewModelAction {

    class CategoryClicked(val category:String): ExpensesActivityViewActions()
    object  FilterClicked:ExpensesActivityViewActions()
    class OpenFullImage(val memory: Memory):ExpensesActivityViewActions()
    class DeleteTransaction(val expenseTransaction: ExpenseTransaction):ExpensesActivityViewActions()
}