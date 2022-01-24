package com.example.expensetracker

import android.app.Application
import com.example.core.Config
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ExpenseTracker:Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(androidContext = this@ExpenseTracker)
            modules(
                    appModules
            )
        }
        val config:Config by inject()
        config.initialise()
    }
}