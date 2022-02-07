package com.example.expensetracker

import android.preference.PreferenceManager
import com.example.core.Config
import com.example.expensetracker.repositories.ExpensesActivityRepo
import com.example.expensetracker.repositories.MainActivityRepo
import com.example.expensetracker.repositories.UserRepo
import com.example.expensetracker.ui.expenses.ExpensesActivityArgs
import com.example.expensetracker.ui.expenses.ExpensesActivityViewModel
import com.example.expensetracker.ui.expensesGraph.GraphFragmentArgs
import com.example.expensetracker.ui.expensesGraph.GraphFragmentViewModel
import com.example.expensetracker.ui.home.MainActivityArgs
import com.example.expensetracker.ui.home.MainActivityViewModel
import com.example.expensetracker.ui.profile.ProfileActivityViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule= module {
    viewModel { ( args: ExpensesActivityArgs) ->
        ExpensesActivityViewModel(
            args=args,
            get(),
            get(),
            get()
        )
    }

    viewModel {(args: MainActivityArgs)->
        MainActivityViewModel(
            args=args,
            get(),
            get(),
            get(),
            get()
        )
    }

    viewModel {
        OnBoardingActivityViewModel(
            get(),
            get()
        )
    }
    viewModel {
        ( args:GraphFragmentArgs ) ->
        GraphFragmentViewModel(
            args=args,
            get(),
            get(),
            get(),
            get()
        )
    }

    viewModel {
        ProfileActivityViewModel(
            get(),
            get(),
            get()
        )
    }
}

val repositoryModule= module {
    single { ExpensesActivityRepo(get(),get()) }
    single { UserRepo() }
    single { MainActivityRepo(get(),get(),get()) }
}

val module=module{
    single { CorePreferences(PreferenceManager.getDefaultSharedPreferences(androidApplication())) }
    single { Config() }
}


val appModules=
    arrayListOf(viewModelModule, repositoryModule, module)