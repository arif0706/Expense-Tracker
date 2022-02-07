package com.example.expensetracker.ui.home

import androidx.lifecycle.viewModelScope
import com.example.core.Config
import com.example.expensetracker.CorePreferences
import com.example.core.platform.BaseViewModel
import com.example.core.platform.ViewEvents
import com.example.core.platform.ViewModelAction
import com.example.core.platform.ViewState
import com.example.expensetracker.models.Categories
import com.example.expensetracker.models.ExpenseTransaction
import com.example.expensetracker.repositories.ExpensesActivityRepo
import com.example.expensetracker.repositories.MainActivityRepo
import com.example.expensetracker.ui.Util
import com.example.expensetracker.ui.expensesGraph.GraphFragmentArgs
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val args : MainActivityArgs?,
    private val expensesActivityRepo: ExpensesActivityRepo,
    private val mainActivityRepo: MainActivityRepo,
    private val corePreferences: CorePreferences,
    private val config:Config

):BaseViewModel<MainActivityViewEvents, MainActivityViewState>(MainActivityViewState())
{
    init {


        viewModelScope.launch {
            _viewEvents.setValue(MainActivityViewEvents.OpenGraphFragment(args= GraphFragmentArgs(date = corePreferences.selectedDate)))
            setState { copy(date=corePreferences.selectedDate,category = corePreferences.category,categoryList = listOf()) }
        }

        mainActivityRepo.getTotalAmounts(object :MainActivityRepo.MyCallBack{
            override fun callBack(any: Any) {
                setState {
                    @Suppress("UNCHECKED_CAST")
                    copy(totalAmount = any as HashMap<String,Long>,categoryList = listOf())
                }
            }
        })
        setState {
            copy(categoryList = config.getObjectifiedValue<Categories>(Config.KEY_CATEGORIES)?.categories!!)
        }

    }



    fun handle(actions: MainActivityViewActions){

        when(actions){
            is MainActivityViewActions.DateClicked -> {
                corePreferences.selectedDate=actions.date
                _viewEvents.setValue(MainActivityViewEvents.OpenGraphFragment(args= GraphFragmentArgs(date = actions.date)))
            }
            is MainActivityViewActions.AddExpenseButtonClicked ->{
                _viewEvents.setValue(MainActivityViewEvents.OpenAddExpenseDialog)
            }

            is MainActivityViewActions.SaveFromDialogClicked ->{
                addAnExpense(actions.expense)
            }
            is MainActivityViewActions.TypingAmount ->{
                validateAmount(actions.amount)
            }
            is MainActivityViewActions.AddImageButtonClicked -> _viewEvents.setValue(
                MainActivityViewEvents.OpenGalleryAndPreview
            )
            is MainActivityViewActions.CloseImageButtonClicked -> _viewEvents.setValue(
                MainActivityViewEvents.CloseImage
            )

            is MainActivityViewActions.ProfileButtonClicked -> _viewEvents.setValue(MainActivityViewEvents.OpenProfileActivity)

            is MainActivityViewActions.ApplicationDestroyed -> corePreferences.selectedDate="Destroyed"

        }
    }


    private fun addAnExpense(expense: ExpenseTransaction) {

        println("category"+expense.category)
        if(expense.amount?.isEmpty() == true || expense.purpose?.isEmpty() == true || expense.category.isNullOrBlank()){
            _viewEvents.setValue(MainActivityViewEvents.ShowToast("Compulsory fill the amount, purpose and category"))
        }
        else {
            _viewEvents.setValue(MainActivityViewEvents.StartProgressDialog)
            if(args!=null) {
                expensesActivityRepo.addExpense(
                    expense,
                    args.date,
                    object : ExpensesActivityRepo.MyCallBack {
                        override fun callBack(any: Any) {
                            _viewEvents.setValue(MainActivityViewEvents.DismissProgressDialog)
                        }

                    })
            }
        }

    }
    private fun validateAmount(amount:String){

        if(amount.isNotEmpty()){
            val amount1= Util.commaSeparateAmount(amount)
            if(amount1 == "error"){
                _viewEvents.setValue(MainActivityViewEvents.ShowToast("Larger amount"))
            }
            else
                _viewEvents.setValue(MainActivityViewEvents.ShowModifiedAmount(amount1))
        }
    }


}
data class MainActivityViewState(
    val date: String? = null,
    val totalAmount: HashMap<String,Long > = hashMapOf(),
    val categoryList:List<String> = listOf(),
    val category:String?=null
):ViewState

sealed class MainActivityViewEvents:ViewEvents{

    class OpenGraphFragment(val args: GraphFragmentArgs) : MainActivityViewEvents()
    object OpenAddExpenseDialog: MainActivityViewEvents()


    class ShowModifiedAmount(val amount: String) : MainActivityViewEvents()
    class ShowToast(val message: String): MainActivityViewEvents()
    object OpenGalleryAndPreview : MainActivityViewEvents()
    object DismissProgressDialog: MainActivityViewEvents()
    object StartProgressDialog: MainActivityViewEvents()
    object CloseImage: MainActivityViewEvents()
    object OpenProfileActivity:MainActivityViewEvents()

    class SetCategories(val categoryList:List<String>):MainActivityViewEvents()


}
sealed class MainActivityViewActions:ViewModelAction{


    class SaveFromDialogClicked(val expense: ExpenseTransaction): MainActivityViewActions()
    class TypingAmount(val amount:String): MainActivityViewActions()
    object AddImageButtonClicked : MainActivityViewActions()
    object CloseImageButtonClicked : MainActivityViewActions()

    class DateClicked(val date:String):MainActivityViewActions()
    object AddExpenseButtonClicked : MainActivityViewActions()

    object ProfileButtonClicked:MainActivityViewActions()
    object ApplicationDestroyed : MainActivityViewActions()

}



