package com.example.aifa.ui.main

import androidx.lifecycle.ViewModel
import com.example.aifa.Repository
import com.example.aifa.database.Fund

class ListViewModel : ViewModel() {
    private val repository = Repository.get()
    val fundList = repository.getLiveFunds()
    fun removeFund(fund: Fund)=repository.removeFund(fund)
    fun updateFund(fund: Fund)=repository.updateFund(fund)
}
