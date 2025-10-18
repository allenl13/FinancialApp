package com.example.financialapp.data.category

import kotlinx.coroutines.flow.Flow

class SavingCategoryRepository(private val dao: SavingCategoryDao) {
    fun observeAll(): Flow<List<SavingCategory>> = dao.observeAll()

    /** Create a category with name + color only (no goal amount). */
    suspend fun create(name: String, colorHex: String) =
        dao.upsert(SavingCategory(name = name.trim(), colorHex = colorHex, goalAmount = 0.0))

    /** Optional helper if you ever rename or recolor a category later. */
    suspend fun updateMeta(id: Long, name: String? = null, colorHex: String? = null) {
        val current = dao.getById(id) ?: return
        dao.update(
            current.copy(
                name = name?.trim()?.takeIf { it.isNotEmpty() } ?: current.name,
                colorHex = colorHex ?: current.colorHex
            )
        )
    }

    suspend fun delete(id: Long) { dao.getById(id)?.let { dao.delete(it) } }
}
