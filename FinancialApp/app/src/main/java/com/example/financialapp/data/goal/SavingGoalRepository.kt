package com.example.financialapp.data.goal

import kotlinx.coroutines.flow.Flow
import kotlin.math.min

class SavingGoalRepository(private val dao: SavingGoalDao) {
    fun observeAll(): Flow<List<SavingGoal>> = dao.observeAll()
    fun observeById(id: Long): Flow<SavingGoal?> = dao.observeById(id)

    suspend fun create(name: String, target: Double, dueDate: Long?, categoryId: Long?) =
        dao.upsert(
            SavingGoal(
                name = name.trim(),
                targetAmount = target,
                dueDate = dueDate,
                categoryId = categoryId
            )
        )

    /** Add a contribution to the saved amount (clamped to target). */
    suspend fun addSaving(id: Long, delta: Double) {
        if (delta <= 0) return
        dao.getById(id)?.let { cur ->
            val newSaved = min(cur.savedAmount + delta, cur.targetAmount)
            dao.update(cur.copy(savedAmount = newSaved))
        }
    }

    /** Bulk edit used by the Edit dialog (name, target, due; supports clear-due). */
    suspend fun updateAll(id: Long, name: String?, target: Double?, due: Long?, clearDue: Boolean) {
        dao.getById(id)?.let { cur ->
            val newTarget = target ?: cur.targetAmount
            val clampedSaved = min(cur.savedAmount, newTarget)
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
