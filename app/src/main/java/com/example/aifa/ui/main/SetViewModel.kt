package com.example.aifa.ui.main

import androidx.lifecycle.ViewModel
import com.example.aifa.Repository

class SetViewModel: ViewModel() {
    private val repository = Repository.get()
    val abs=repository.getSumWave()
    val liveFunds=repository.getLiveFunds()
}