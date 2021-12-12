package com.example.expensetracker.ui

import androidx.lifecycle.viewModelScope
import com.example.core.platform.*
import kotlinx.coroutines.launch

class MainActivityViewModel (
    mainActivityRepo:MainActivityRepo
)
    : BaseViewModel<EmptyViewEvents, MainActivityViewState>(MainActivityViewState())
{
    init {
        viewModelScope.launch {

            val dayToDayExpenses=mainActivityRepo.getDayToDayExpenses()
            setState {
                copy(dayToDayExpense= dayToDayExpenses)
            }
        }
    }
}


data class MainActivityViewState(
    val dayToDayExpense: List<Expense>?= listOf(),
) : ViewState

sealed class MainActivityViewActions : ViewModelAction {

}

sealed class MainActivityViewEvents : ViewEvents {

}