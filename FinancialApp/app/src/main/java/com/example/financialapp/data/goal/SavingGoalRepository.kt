package com.example.financialapp.data.goal

import kotlin.math.min

class SavingGoalRepository(private val dao: SavingGoalDao) {
    fun observeAll() = dao.observeAll()
    fun observeById(id: Long) = dao.observeById(id)

    suspend fun create(name: String, target: Double, dueDate: Long?, categoryId: Long?) =
        dao.upsert(SavingGoal(name = name.trim(), targetAmount = target, dueDate = dueDate, categoryId = categoryId))

    suspend fun updateTarget(id: Long, newTarget: Double) {
        dao.getById(id)?.let { cur ->
            val clampedSaved = min(cur.savedAmount, newTarget)
            dao.update(cur.copy(targetAmount = newTarget, savedAmount = clampedSaved))
        }
    }

    suspend fun updateSaved(id: Long, newSaved: Double) {
        dao.getById(id)?.let { cur -> dao.update(cur.copy(savedAmount = newSaved)) }
    }

    suspend fun updateName(id: Long, newName: String) {
        dao.getById(id)?.let { cur -> dao.update(cur.copy(name = newName.trim())) }
    }

    suspend fun updateDueDate(id: Long, newDue: Long?) {
        dao.getById(id)?.let { cur -> dao.update(cur.copy(dueDate = newDue)) }
    }

    suspend fun updateAll(id: Long, name: String?, target: Double?, due: Long?, clearDue: Boolean) {
        dao.getById(id)?.let { cur ->
            val newTarget = target ?: cur.targetAmount
            val clampedSaved = kotlin.math.min(cur.savedAmount, newTarget)
            val newDue = if (clearDue) null else (due ?: cur.dueDate)
            dao.update(
                cur.copy(
                    name = name?.trim()?.takeIf { it.isNotEmpty() } ?: cur.name,
                    targetAmount = newTarget,
                    savedAmount = clampedSaved,
                    dueDate = newDue
                )
            )
        }
    }

    suspend fun delete(id: Long) { dao.getById(id)?.let { dao.delete(it) } }
}
