package com.example.financialapp.data.category

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingCategoryDao {
    @Query("SELECT * FROM saving_categories ORDER BY name")
    fun observeAll(): Flow<List<SavingCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: SavingCategory): Long

    @Update
    suspend fun update(category: SavingCategory)

    @Query("SELECT * FROM saving_categories WHERE id = :id")
    suspend fun getById(id: Long): SavingCategory?

    @Delete
    suspend fun delete(category: SavingCategory)
}
