package com.example.financialapp.repo

import androidx.lifecycle.ViewModel

class MainViewModel (val repository: MainRepo): ViewModel(){
    constructor():this(MainRepo())

    fun loadData() = repository.items
}