package com.example.expensetracker

import com.example.expensetracker.repositories.MainActivityRepo
import com.example.expensetracker.repositories.UserRepo
import com.example.expensetracker.ui.MainActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule= module {
    viewModel { MainActivityViewModel(get()) }
}

val repositoryModule= module {
    single { MainActivityRepo(get(),get()) }
    single { UserRepo() }
}


val appModules=
    arrayListOf(viewModelModule, repositoryModule)