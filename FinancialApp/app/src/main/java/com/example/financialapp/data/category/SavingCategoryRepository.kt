package com.example.financialapp.data.category

import kotlinx.coroutines.flow.Flow

class SavingCategoryRepository(private val dao: SavingCategoryDao) {
    fun observeAll(): Flow<List<SavingCategory>> = dao.observeAll()

    suspend fun create(name: String, colorHex: String, goal: Double) =
        dao.upsert(SavingCategory(name = name, colorHex = colorHex, goalAmount = goal))

    suspend fun updateGoal(id: Long, newGoal: Double) {
        val current = dao.getById(id) ?: return
        dao.update(current.copy(goalAmount = newGoal))
    }
}
