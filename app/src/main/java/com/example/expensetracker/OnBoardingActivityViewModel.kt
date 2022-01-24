package com.example.expensetracker

import com.example.core.platform.BaseViewModel
import com.example.core.platform.ViewEvents
import com.example.core.platform.ViewModelAction
import com.example.core.platform.ViewState
import com.example.expensetracker.models.User
import com.example.expensetracker.repositories.UserRepo

class OnBoardingActivityViewModel(
    private val corePreferences: CorePreferences,
    private val userRepo: UserRepo

):
    BaseViewModel<OnBoardingActivityViewEvents,OnBoardingActivityViewState>(
    OnBoardingActivityViewState()
) {
    fun updateUser() {
        userRepo.setUserDetails()
        userRepo.getUserDetails(object :UserRepo.UserRepoCallBack{
            override fun callBack(any: Any) {
                corePreferences.startDate= (any as User).start_date.toString()
            }
        })
    }


}
sealed class OnBoardingActivityViewEvents : ViewEvents{

}
sealed class OnBoardingActivityViewActions: ViewModelAction{

}

data class OnBoardingActivityViewState(
    val empty:String?=null
):ViewState