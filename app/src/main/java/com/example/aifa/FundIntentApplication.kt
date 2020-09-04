package com.example.aifa

import android.app.Application

class FundIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Repository.initialize(this)
    }
}