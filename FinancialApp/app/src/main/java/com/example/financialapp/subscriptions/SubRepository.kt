package com.example.financialapp.subscriptions

import androidx.lifecycle.LiveData

class SubRepository(private val subDao: SubDao) {

    val readAllData: LiveData<List<SubEntity>> = subDao.readAllData()

    suspend fun insert(sub: SubEntity){
        subDao.insert(sub)
    }

    suspend fun update(sub: SubEntity){
        subDao.update(sub)
    }

    suspend fun delete(sub: SubEntity){
        subDao.delete(sub)
    }

}