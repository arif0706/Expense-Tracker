package com.example.expensetracker.ui.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.example.core.platform.BaseViewModel
import com.example.core.platform.ViewEvents
import com.example.core.platform.ViewModelAction
import com.example.core.platform.ViewState
import com.example.expensetracker.CorePreferences
import com.example.expensetracker.models.User
import com.example.expensetracker.repositories.ExpensesActivityRepo
import com.example.expensetracker.repositories.UserRepo
import com.example.expensetracker.ui.Util
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch

class ProfileActivityViewModel(
    private val userRepo:UserRepo,
    private val expensesActivityRepo: ExpensesActivityRepo,
    private val corePreferences: CorePreferences
):BaseViewModel<ProfileActivityViewEvents,ProfileActivityViewState>(ProfileActivityViewState())
{
    init {

        setState { copy(isLoading = true) }
        getUserDetails()
        getExpensesDetails()
    }

    fun handle(actions: ProfileActivityViewActions){
        when(actions){
            is ProfileActivityViewActions.SwitchAccountClicked ->
                _viewEvents.setValue(ProfileActivityViewEvents.OpenSignOutDialog)

            is ProfileActivityViewActions.AlertDialogOkClicked-> {
                userRepo.signOutUser()
                _viewEvents.setValue(ProfileActivityViewEvents.OpenOnBoardingActivity)
            }
        }
    }


    private fun getExpensesDetails() {
        viewModelScope.launch {
            userRepo.getCategoryExpensesAmounts(object : UserRepo.UserRepoCallBack{
                @RequiresApi(Build.VERSION_CODES.N)
                override fun callBack(any: Any) {
                    val list : ArrayList<PieEntry> = arrayListOf()
                    var totalAmount=0
                    (any as HashMap<*, *>).forEach{
                        val l = (it.value as Long).toFloat()

                        list.add(PieEntry(l ,it.key as String))
                       totalAmount += (it.value as Long).toFloat().toInt()
                    }
                    setState { copy(graphList = list,isLoading = false,totalAmount = Util.appendRupee(Util.commaSeparateAmount(totalAmount.toString()))) }
                }
            })
        }
    }

    private fun getUserDetails() {
        viewModelScope.launch {
            userRepo.getUserDetails(object :UserRepo.UserRepoCallBack{
                override fun callBack(any: Any) {
                    setState { copy(user= any as User) }
                }
            })
        }
    }
}

data class ProfileActivityViewState(
    val user:User=User(),
    val graphList:ArrayList<PieEntry> = arrayListOf(),
    val totalAmount: String="",
    val isLoading: Boolean=true,
):ViewState

sealed class ProfileActivityViewEvents:ViewEvents{
    object OpenSignOutDialog: ProfileActivityViewEvents()
    object OpenOnBoardingActivity : ProfileActivityViewEvents()
}

sealed class ProfileActivityViewActions:ViewModelAction{
    object SwitchAccountClicked : ProfileActivityViewActions()
    object AlertDialogOkClicked :ProfileActivityViewActions()
}