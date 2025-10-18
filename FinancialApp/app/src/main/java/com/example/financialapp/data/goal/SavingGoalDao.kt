package com.example.financialapp.data.goal

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingGoalDao {
    @Query("""
        SELECT * FROM saving_goals
        ORDER BY CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, dueDate
    """)
    fun observeAll(): Flow<List<SavingGoal>>

    @Query("SELECT * FROM saving_goals WHERE id = :id")
    fun observeById(id: Long): Flow<SavingGoal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(goal: SavingGoal): Long

    @Update suspend fun update(goal: SavingGoal)
    @Delete suspend fun delete(goal: SavingGoal)

    @Query("SELECT * FROM saving_goals WHERE id = :id")
    suspend fun getById(id: Long): SavingGoal?
}