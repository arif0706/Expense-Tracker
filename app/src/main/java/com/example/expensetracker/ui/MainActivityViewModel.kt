package com.example.expensetracker.ui

import androidx.lifecycle.viewModelScope
import com.example.core.platform.BaseViewModel
import com.example.core.platform.ViewEvents
import com.example.core.platform.ViewModelAction
import com.example.core.platform.ViewState
import com.example.expensetracker.models.Expense
import com.example.expensetracker.repositories.MainActivityRepo
import kotlinx.coroutines.launch

class MainActivityViewModel (
    private val mainActivityRepo: MainActivityRepo
)
    : BaseViewModel<MainActivityViewEvents, MainActivityViewState>(MainActivityViewState())
{
    init {
        viewModelScope.launch {

            val dayToDayExpenses=mainActivityRepo.getDayToDayExpenses()
            setState {
                copy(dayToDayExpense= dayToDayExpenses)
            }
        }
    }
    fun handle(actions: MainActivityViewActions){
        when(actions){
            is MainActivityViewActions.AddExpenseButtonClicked->{}
            is MainActivityViewActions.SaveFromDialogClicked ->{
                addAnExpense(actions.expense)
            }
            is MainActivityViewActions.TypingAmount->{
                validateAmount(actions.amount)
            }
            is MainActivityViewActions.AddImageButtonClicked-> _viewEvents.setValue(MainActivityViewEvents.OpenGalleryAndPreview())
            is MainActivityViewActions.CloseImageButtonClicked -> _viewEvents.setValue(MainActivityViewEvents.CloseImage)
        }
    }

    private fun addAnExpense(expense: Expense) {

        if(expense.amount?.isEmpty() == true || expense.purpose?.isEmpty() == true){
            _viewEvents.setValue(MainActivityViewEvents.ShowToast("Compulsory fill the amount and purpose"))
        }
        else {
            _viewEvents.setValue(MainActivityViewEvents.StartProgressDialog)
            mainActivityRepo.addExpense(expense, object : MainActivityRepo.MyCallBack {
                override fun callBack(response: String) {
                    _viewEvents.setValue(MainActivityViewEvents.DismissProgressDialog)
                }

            })
        }

    }
    private fun validateAmount(amount:String){

        if(amount.isNotEmpty()){
            val amount1=ValidationUtil.commaSeparateAmount(amount)
            if(amount1 == "error"){
                _viewEvents.setValue(MainActivityViewEvents.ShowToast("Larger amount"))
            }
            else
                _viewEvents.setValue(MainActivityViewEvents.ShowModifiedAmount(amount1))
        }
    }

}


data class MainActivityViewState(
    val dayToDayExpense: List<Expense>?= listOf(),
) : ViewState


sealed class MainActivityViewEvents : ViewEvents {
    class ShowModifiedAmount(val amount: String) : MainActivityViewEvents()
    class ShowToast(val message: String):MainActivityViewEvents()
    class OpenGalleryAndPreview:MainActivityViewEvents()
    object DismissProgressDialog:MainActivityViewEvents()
    object StartProgressDialog:MainActivityViewEvents()
    object CloseImage:MainActivityViewEvents()
}


sealed class MainActivityViewActions : ViewModelAction {
    class SaveFromDialogClicked(val expense: Expense):MainActivityViewActions()
    object AddExpenseButtonClicked :MainActivityViewActions()
    class TypingAmount(val amount:String):MainActivityViewActions()
    class AddImageButtonClicked(): MainActivityViewActions()
    object CloseImageButtonClicked :MainActivityViewActions()
}