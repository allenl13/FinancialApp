package com.example.financialapp.subscriptions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SubViewModel(app: Application): AndroidViewModel(app) {

    private val dao = SubDatabase.getDatabase(app).subDao()
    private val repository: SubRepository = SubRepository(dao)

    val readAllData: LiveData<List<SubEntity>> = repository.readAllData

    fun insert(sub: SubEntity) = viewModelScope.launch {
        repository.insert(sub)
    }

    fun update(sub: SubEntity) = viewModelScope.launch {
        repository.update(sub)
    }

    fun delete(sub: SubEntity) = viewModelScope.launch {
        repository.delete(sub)
    }


}